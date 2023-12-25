package dev.su5ed.mffs.item;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentificationCardItem extends BaseItem {

    public IdentificationCardItem() {
        super(new ExtendedItemProperties(new Item.Properties().stacksTo(1)).description());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);

            IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD);
            if (!level.isClientSide) {
                if (card.getIdentity() != null) {
                    card.setIdentity(null);
                    player.displayClientMessage(ModUtil.translate("info", "identity_cleared"), true);
                } else {
                    setCardIdentity(card, player, player.getGameProfile());
                }
            }
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }

    public static void onLivingEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Player player = event.getEntity();
        Entity target = event.getTarget();
        if (player.isShiftKeyDown() && target instanceof Player targetPlayer) {
            IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD);
            if (card != null) {
                if (!player.level().isClientSide) {
                    setCardIdentity(card, player, targetPlayer.getGameProfile());
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
            }
        }
    }

    private static void setCardIdentity(IdentificationCard card, Player user, GameProfile profile) {
        card.setIdentity(profile);
        user.displayClientMessage(ModUtil.translate("info", "identity_set", Component.literal(profile.getName()).withStyle(ChatFormatting.GREEN)), true);
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverTextPre(stack, level, tooltipComponents, isAdvanced);

        IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD);
        if (card != null) {
            GameProfile identity = card.getIdentity();
            if (identity != null) {
                tooltipComponents.add(ModUtil.translate("info", "identity",
                    Component.literal(identity.getName()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_GRAY));
            }
            List<FieldPermission> perms = List.copyOf(card.getPermissions());
            if (!perms.isEmpty()) {
                MutableComponent permsComponent = ModUtil.translate(perms.get(0));
                for (int i = 1; i < perms.size(); i++) {
                    permsComponent.append(", ").append(ModUtil.translate(perms.get(i)));
                }
                tooltipComponents.add(permsComponent.withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    public static class IdentificationCardAttachment implements IdentificationCard {
        private GameProfile profile;
        private final Set<FieldPermission> permissions = new HashSet<>();

        @Override
        public boolean hasPermission(FieldPermission permission) {
            return this.permissions.contains(permission);
        }

        @Override
        public void addPermission(FieldPermission permission) {
            this.permissions.add(permission);
        }

        @Override
        public void removePermission(FieldPermission permission) {
            this.permissions.remove(permission);
        }

        @Override
        public Collection<FieldPermission> getPermissions() {
            return this.permissions;
        }

        @Override
        public void setPermissions(Collection<FieldPermission> permissions) {
            this.permissions.clear();
            this.permissions.addAll(permissions);
        }

        @Nullable
        @Override
        public GameProfile getIdentity() {
            return this.profile;
        }

        @Override
        public void setIdentity(GameProfile profile) {
            this.profile = profile;
        }

        @Override
        public boolean checkIdentity(Player player) {
            return this.profile == null || player.getGameProfile().equals(this.profile);
        }

        @Override
        public void copyTo(IdentificationCard other) {
            other.setIdentity(getIdentity());
            other.setPermissions(getPermissions());
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            if (this.profile != null) {
                tag.put("profile", NbtUtils.writeGameProfile(new CompoundTag(), this.profile));
                ListTag permissionsTag = new ListTag();
                for (FieldPermission permission : this.permissions) {
                    permissionsTag.add(StringTag.valueOf(permission.name()));
                }
                tag.put("permissions", permissionsTag);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if (tag.contains("profile")) {
                this.profile = NbtUtils.readGameProfile(tag.getCompound("profile"));
                ListTag permissionsTag = tag.getList("permissions", Tag.TAG_STRING);
                for (Tag permTag : permissionsTag) {
                    FieldPermission permission = ModUtil.getEnumConstantSafely(FieldPermission.class, permTag.getAsString());
                    if (permission != null) {
                        this.permissions.add(permission);
                    }
                }
            }
        }
    }
}
