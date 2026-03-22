package dev.su5ed.mffs.item;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class IdentificationCardItem extends BaseItem {

    public IdentificationCardItem() {
        super(true); // show description
        setMaxStackSize(1);
    }

    /** Shift + right-click in air: toggle own identity on/off the card. */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (playerIn.isSneaking()) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
            if (card != null && !worldIn.isRemote) {
                if (card.getIdentity() != null) {
                    card.setIdentity(null);
                    playerIn.sendStatusMessage(
                        new TextComponentTranslation("info.mffs.identity_cleared"), true);
                } else {
                    setCardIdentity(card, playerIn, playerIn.getGameProfile());
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    /** Shift + right-click on a player entity: copy that player's profile to the card. */
    public static void onLivingEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        EntityPlayer player = (EntityPlayer) event.getEntity();
        Entity target = event.getTarget();
        if (player.isSneaking() && target instanceof EntityPlayer targetPlayer) {
            IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
            if (card != null) {
                if (!player.world.isRemote) {
                    setCardIdentity(card, player, targetPlayer.getGameProfile());
                }
                event.setCanceled(true);
            }
        }
    }

    private static void setCardIdentity(IdentificationCard card, EntityPlayer user, GameProfile profile) {
        card.setIdentity(profile);
        user.sendStatusMessage(
            new TextComponentTranslation("info.mffs.identity_set",
                new TextComponentString(profile.getName())
                    .setStyle(new Style().setColor(TextFormatting.GREEN))),
            true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void addInformationPre(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
                                     net.minecraft.client.util.ITooltipFlag flagIn) {
        super.addInformationPre(stack, worldIn, tooltip, flagIn);
        IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
        if (card != null) {
            GameProfile identity = card.getIdentity();
            if (identity != null) {
                tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.identity",
                    TextFormatting.GREEN + identity.getName()));
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.identity",
                    TextFormatting.YELLOW + I18n.format("info.mffs.identity.everyone")));
            }
            Set<FieldPermission> permsSet = new HashSet<>(card.getPermissions());
            if (!permsSet.isEmpty()) {
                tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info.mffs.perms"));
                for (FieldPermission perm : FieldPermission.values()) {
                    if (permsSet.contains(perm)) {
                        tooltip.add(TextFormatting.GREEN + "  " + I18n.format(
                            "info.mffs.field_permission." + perm.name().toLowerCase()));
                    }
                }
            }
        }
    }

    /** Provide IdentificationCard capability for item stacks of this type. */
    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new IdentificationCardProvider(stack);
    }

    // -----------------------------------------------------------------------
    // NBT storage helpers
    // -----------------------------------------------------------------------

    /**
     * Identity stored as GameProfile data (name + UUID strings).
     * Permissions stored as integer bitmask (bit N = FieldPermission.values()[N]).
     */
    public static class IdentificationCardHandler implements IdentificationCard {
        private static final String NBT_PROFILE_NAME = "profile_name";
        private static final String NBT_PROFILE_ID   = "profile_id";
        private static final String NBT_PERMISSIONS  = "permissions";

        private final ItemStack stack;

        public IdentificationCardHandler(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        @Nullable
        public GameProfile getIdentity() {
            NBTTagCompound tag = this.stack.getTagCompound();
            if (tag == null || !tag.hasKey(NBT_PROFILE_NAME)) return null;
            String name = tag.getString(NBT_PROFILE_NAME);
            String idStr = tag.getString(NBT_PROFILE_ID);
            UUID uuid = idStr.isEmpty() ? null : UUID.fromString(idStr);
            return new GameProfile(uuid, name.isEmpty() ? null : name);
        }

        @Override
        public void setIdentity(@Nullable GameProfile profile) {
            NBTTagCompound tag = ModUtil.getOrCreateTag(this.stack);
            if (profile == null) {
                tag.removeTag(NBT_PROFILE_NAME);
                tag.removeTag(NBT_PROFILE_ID);
            } else {
                tag.setString(NBT_PROFILE_NAME, profile.getName() != null ? profile.getName() : "");
                tag.setString(NBT_PROFILE_ID, profile.getId() != null ? profile.getId().toString() : "");
            }
        }

        @Override
        public boolean hasPermission(FieldPermission permission) {
            return getPermissionsSet().contains(permission);
        }

        @Override
        public void addPermission(FieldPermission permission) {
            Set<FieldPermission> set = getPermissionsSet();
            set.add(permission);
            savePermissions(set);
        }

        @Override
        public void removePermission(FieldPermission permission) {
            Set<FieldPermission> set = getPermissionsSet();
            set.remove(permission);
            savePermissions(set);
        }

        @Override
        public Collection<FieldPermission> getPermissions() {
            return getPermissionsSet();
        }

        @Override
        public void setPermissions(Collection<FieldPermission> permissions) {
            savePermissions(new HashSet<>(permissions));
        }

        @Override
        public boolean checkIdentity(EntityPlayer player) {
            GameProfile profile = getIdentity();
            return profile == null || player.getGameProfile().equals(profile);
        }

        @Override
        public void copyTo(IdentificationCard other) {
            other.setIdentity(getIdentity());
            other.setPermissions(getPermissions());
        }

        private Set<FieldPermission> getPermissionsSet() {
            NBTTagCompound tag = this.stack.getTagCompound();
            int bits = tag != null ? tag.getInteger(NBT_PERMISSIONS) : 0;
            Set<FieldPermission> set = new HashSet<>();
            FieldPermission[] values = FieldPermission.values();
            for (int i = 0; i < values.length && i < 32; i++) {
                if ((bits & (1 << i)) != 0) set.add(values[i]);
            }
            return set;
        }

        private void savePermissions(Set<FieldPermission> permissions) {
            int bits = 0;
            for (FieldPermission fp : permissions) {
                bits |= (1 << fp.ordinal());
            }
            ModUtil.getOrCreateTag(this.stack).setInteger(NBT_PERMISSIONS, bits);
        }
    }

    private static class IdentificationCardProvider implements ICapabilityProvider {
        private final ItemStack stack;
        private IdentificationCardHandler handler;

        IdentificationCardProvider(ItemStack stack) { this.stack = stack; }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == ModCapabilities.IDENTIFICATION_CARD;
        }

        @Override
        @Nullable
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == ModCapabilities.IDENTIFICATION_CARD && ModCapabilities.IDENTIFICATION_CARD != null) {
                if (this.handler == null) this.handler = new IdentificationCardHandler(this.stack);
                return ModCapabilities.IDENTIFICATION_CARD.cast(this.handler);
            }
            return null;
        }
    }
}
