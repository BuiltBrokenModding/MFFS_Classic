package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.menu.InterdictionMatrixMenu;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModModules;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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

    public InterdictionMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.INTERDICTION_MATRIX_BLOCK_ENTITY.get(), pos, state);

        this.secondaryCard = addSlot("secondaryCard", InventorySlot.Mode.BOTH, ModUtil::isCard, this::onFrequencySlotChanged);
        this.upgradeSlots = createUpgradeSlots(8, Module.Category.INTERDICTION);
        this.bannedItemSlots = IntStreamEx.range(9)
            .mapToObj(i -> addSlot("banned_item_" + i, InventorySlot.Mode.NONE))
            .toList();
    }

    public int getWarningRange() {
        return getModuleCount(ModModules.WARN) + getActionRange() + 3;
    }

    public int getActionRange() {
        return getModuleCount(ModModules.SCALE);
    }

    @Override
    public BlockEntity be() {
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

    public void setConfiscationMode(ConfiscationMode confiscationMode) {
        this.confiscationMode = confiscationMode;
    }

    @Override
    protected void addModuleSlots(List<? super InventorySlot> list) {
        super.addModuleSlots(list);
        list.addAll(this.upgradeSlots);
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (getTicks() % 10 == 0 && (isActive() || this.frequencySlot.getItem().is(ModItems.INFINITE_POWER_CARD.get()))) {
            if (this.fortronStorage.extractFortron(getFortronCost() * 10, true) > 0) {
                this.fortronStorage.extractFortron(getFortronCost() * 10, false);
                scan();
            }
        }
    }

    @Override
    protected float getAmplifier() {
        return Math.max(Math.min(getActionRange() / 20, 10), 1);
    }

    public void scan() {
        BiometricIdentifier identifier = getBiometricIdentifier();
        AABB emptyBounds = new AABB(this.worldPosition, this.worldPosition.offset(1, 1, 1));

        List<LivingEntity> warningList = this.level.getEntitiesOfClass(LivingEntity.class, emptyBounds.inflate(getWarningRange(), getWarningRange(), getWarningRange()));
        List<LivingEntity> actionList = this.level.getEntitiesOfClass(LivingEntity.class, emptyBounds.inflate(getActionRange(), getActionRange(), getActionRange()));

        for (LivingEntity entity : warningList) {
            if (entity instanceof Player player && !actionList.contains(entity) && !canPlayerBypass(identifier, player) && this.level.random.nextInt(3) == 0) {
                player.displayClientMessage(ModUtil.translate("info", "interdiction_matrix.warning", getDisplayName()).withStyle(ChatFormatting.RED), false);
            }
        }

        if (this.level.random.nextInt(3) == 0) {
            for (LivingEntity entity : actionList) {
                applyAction(entity);
            }
        }
    }

    public void applyAction(LivingEntity target) {
        // Check for security permission to see if this player should be ignored.
        if (target instanceof Player player) {
            BiometricIdentifier identifier = getBiometricIdentifier();
            if (canPlayerBypass(identifier, player) || MFFSConfig.COMMON.interactCreative.get() && player.isCreative()) {
                return;
            }
        }

        for (ItemStack stack : getModuleStacks()) {
            if (stack.getCapability(ModCapabilities.INTERDICTION_MATRIX_MODULE).map(m -> m.onDefend(this, target) || target.isDeadOrDying()).orElse(false)) {
                break;
            }
        }
    }

    public boolean canPlayerBypass(BiometricIdentifier identifier, Player player) {
        return identifier != null && identifier.isAccessGranted(player, FieldPermission.BYPASS_CONFISCATION);
    }

    @Override
    protected void loadCommonTag(CompoundTag tag) {
        super.loadCommonTag(tag);

        this.confiscationMode = ConfiscationMode.valueOf(tag.getString("confiscationMode"));
    }

    @Override
    protected void saveCommonTag(CompoundTag tag) {
        super.saveCommonTag(tag);

        tag.putString("confiscationMode", this.confiscationMode.name());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new InterdictionMatrixMenu(containerId, this.worldPosition, player, playerInventory);
    }
}
