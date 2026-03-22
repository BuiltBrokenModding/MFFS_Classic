package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.module.InterdictionMatrixModule;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.network.IMAZoneSyncPacket;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class InterdictionMatrixBlockEntity extends ModularBlockEntity implements InterdictionMatrix {
    public final InventorySlot secondaryCard;
    public final List<InventorySlot> upgradeSlots;
    public final List<InventorySlot> bannedItemSlots;

    private ConfiscationMode confiscationMode = ConfiscationMode.BLACKLIST;
    /** Tracks the last known active state so we can detect changes in tickServer. */
    private boolean prevActive = false;

    public InterdictionMatrixBlockEntity() {
        super();

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH,
            stack -> ModUtil.isCard(stack) || stack.getItem() == ModItems.INFINITE_POWER_CARD,
            this::onFrequencySlotChanged);
        this.upgradeSlots = createUpgradeSlots(8, Module.Category.INTERDICTION, stack -> {});
        this.bannedItemSlots = IntStreamEx.range(9)
            .mapToObj(i -> addVirtualSlot("banned_item_" + i))
            .toList();
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == ModCapabilities.INTERDICTION_MATRIX) return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == ModCapabilities.INTERDICTION_MATRIX) return (T) this;
        return super.getCapability(capability, facing);
    }

    public int getWarningRange() {
        return getModuleCount(ModModules.WARN) + getActionRange() + 3;
    }

    public int getActionRange() {
        return getModuleCount(ModModules.SCALE);
    }

    @Override
    public dev.su5ed.mffs.blockentity.BaseBlockEntity be() {
        return this;
    }

    @Override
    public Collection<ItemStack> getFilteredItems() {
        return StreamEx.of(this.bannedItemSlots)
            .remove(InventorySlot::isEmpty)
            .map(InventorySlot::getItem)
            .toList();
    }

    @Override
    public ConfiscationMode getConfiscationMode() {
        return this.confiscationMode;
    }

    @Override
    public ITextComponent getTitle() {
        return new TextComponentTranslation("tile.mffs.interdiction_matrix.name");
    }

    public void setConfiscationMode(ConfiscationMode confiscationMode) {
        this.confiscationMode = confiscationMode;
        markDirty();
        sendZoneSync();
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
        // Module slots changed — ranges and zone type may have shifted; re-sync clients.
        sendZoneSync();
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return MFFSConfig.interdictionMatrixInitialTankCapacity;
    }

    @Override
    protected int getCapacityBoostPerModule() {
        return MFFSConfig.interdictionMatrixTankCapacityPerModule;
    }

    @Override
    public void tickServer() {
        super.tickServer();

        boolean active  = isActive();
        boolean powered = active || this.frequencySlot.getItem().getItem() == ModItems.INFINITE_POWER_CARD;

        // Push zone data to nearby clients when active state changes, and as a heartbeat
        // every 200 ticks (10 s) so players who walk into range pick up the zone config.
        if (active != this.prevActive) {
            this.prevActive = active;
            sendZoneSync();
        } else if (powered && getTicks() % 200 == 0) {
            sendZoneSync();
        }

        // Actions (confiscation, damage, etc.) on the configurable action tick rate.
        if (getTicks() % Math.max(1, MFFSConfig.interdictionMatrixActionTickRate) == 0 && powered) {
            int extracted = this.fortronStorage.extractFortron(getFortronCost() * Math.max(1, MFFSConfig.interdictionMatrixActionTickRate), true);
            if (extracted > 0) {
                this.fortronStorage.extractFortron(getFortronCost() * Math.max(1, MFFSConfig.interdictionMatrixActionTickRate), false);
                scanActions();
            }
        }
    }

    @Override
    protected float getAmplifier() {
        return Math.max(Math.min(getActionRange() / 20, 10), 1);
    }

    /**
     * Returns 1 when the IM is active, has at least one biometric-requiring module
     * (Anti-Personnel, Block Alter, or Block Access), but no active Biometric Identifier
     * is linked. Used to drive the GUI warning indicator via a synced data slot.
     */
    public int getBiometricWarningFlag() {
        if (!isActive()) return 0;
        if (!hasModule(ModModules.ANTI_PERSONNEL)
                && !hasModule(ModModules.BLOCK_ALTER)
                && !hasModule(ModModules.BLOCK_ACCESS)) return 0;
        BiometricIdentifier identifier = getBiometricIdentifier();
        return (identifier == null || !identifier.isActive()) ? 1 : 0;
    }

    /** Encodes the zone type as a byte for the sync packet. */
    private byte getZoneTypeByte() {
        if (hasModule(ModModules.ANTI_PERSONNEL)) return IMAZoneSyncPacket.ZONE_KILL;
        if (hasModule(ModModules.CONFISCATION))   return IMAZoneSyncPacket.ZONE_CONFISCATION;
        return IMAZoneSyncPacket.ZONE_DEFENSE;
    }

    /** Broadcasts current zone configuration to all nearby clients, respecting bypass permissions. */
    private void sendZoneSync() {
        if (this.world == null || this.world.isRemote) return;
        boolean active   = isActive();
        int actionRange  = active ? getActionRange()  : 0;
        int warningRange = active ? getWarningRange() : 0;
        double sendRadius = Math.max(warningRange + 32, 48);
        double cx = this.pos.getX() + 0.5;
        double cy = this.pos.getY() + 0.5;
        double cz = this.pos.getZ() + 0.5;
        BiometricIdentifier identifier = getBiometricIdentifier();
        byte zoneType = getZoneTypeByte();
        for (net.minecraft.entity.player.EntityPlayerMP player :
                this.world.getPlayers(net.minecraft.entity.player.EntityPlayerMP.class, p -> true)) {
            // Only send to players within the broadcast radius
            if (player.getDistanceSq(cx, cy, cz) > sendRadius * sendRadius) continue;
            boolean playerActive = active && !canPlayerBypass(identifier, player);
            int pActionRange  = playerActive ? actionRange  : 0;
            int pWarningRange = playerActive ? warningRange : 0;
            Network.sendTo(new IMAZoneSyncPacket(this.pos, pActionRange, pWarningRange, zoneType, playerActive), player);
        }
    }

    /** Sends current zone configuration to a single player (e.g. on login), respecting bypass. */
    public void sendZoneSyncTo(net.minecraft.entity.player.EntityPlayerMP player) {
        if (this.world == null || this.world.isRemote) return;
        boolean active   = isActive();
        boolean bypass   = canPlayerBypass(getBiometricIdentifier(), player);
        boolean playerActive = active && !bypass;
        int actionRange  = playerActive ? getActionRange()  : 0;
        int warningRange = playerActive ? getWarningRange() : 0;
        Network.sendTo(
            new IMAZoneSyncPacket(this.pos, actionRange, warningRange, getZoneTypeByte(), playerActive),
            player);
    }

    /** Applies zone effects (confiscation, damage, etc.). Called on the Fortron drain cycle. */
    public void scanActions() {
        AxisAlignedBB emptyBounds = new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1));
        List<EntityLivingBase> actionList = this.world.getEntitiesWithinAABB(EntityLivingBase.class, emptyBounds.grow(getActionRange(), getActionRange(), getActionRange()));

        for (EntityLivingBase entity : actionList) {
            applyAction(entity);
        }
    }

    public void applyAction(EntityLivingBase target) {
        if (target instanceof EntityPlayer player) {
            BiometricIdentifier identifier = getBiometricIdentifier();
            if (canPlayerBypass(identifier, player) || MFFSConfig.interactCreative && player.capabilities.isCreativeMode) {
                return;
            }
        }
        for (Module module : getModuleInstances()) {
            if (module instanceof InterdictionMatrixModule interdictionModule && interdictionModule.onDefend(this, target) || target.isDead) {
                break;
            }
        }
    }

    public boolean canPlayerBypass(BiometricIdentifier identifier, EntityPlayer player) {
        return identifier != null && identifier.isAccessGranted(player, FieldPermission.BYPASS_CONFISCATION);
    }

    @Override
    protected void loadCommonTag(NBTTagCompound compound) {
        super.loadCommonTag(compound);

        String modeName = compound.getString("confiscationMode");
        if (!modeName.isEmpty()) {
            try { this.confiscationMode = ConfiscationMode.valueOf(modeName); } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    protected void saveCommonTag(NBTTagCompound compound) {
        super.saveCommonTag(compound);

        compound.setString("confiscationMode", this.confiscationMode.name());
    }
}
