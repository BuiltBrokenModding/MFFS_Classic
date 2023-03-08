package dev.su5ed.mffs.item;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentificationCardItem extends Item {

    public IdentificationCardItem() {
        super(ModItems.itemProperties().stacksTo(1));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new IdentificationCardCapability();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);
            return stack.getCapability(ModCapabilities.IDENTIFICATION_CARD)
                .map(card -> {
                    if (!level.isClientSide) {
                        if (card.getIdentity() != null) {
                            card.setIdentity(null);
                            player.displayClientMessage(ModUtil.translate("info", "identity_cleared"), true);
                        } else {
                            setCardIdentity(card, player, player.getGameProfile());
                        }
                    }
                    return InteractionResultHolder.consume(player.getItemInHand(usedHand));
                })
                .orElseGet(() -> InteractionResultHolder.pass(stack));
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (player.isShiftKeyDown() && interactionTarget instanceof Player targetPlayer) {
            return stack.getCapability(ModCapabilities.IDENTIFICATION_CARD)
                .map(card -> {
                    if (!player.level.isClientSide) {
                        setCardIdentity(card, player, targetPlayer.getGameProfile());
                    }
                    return InteractionResult.SUCCESS;
                })
                .orElse(InteractionResult.PASS);
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    private void setCardIdentity(IdentificationCard card, Player user, GameProfile profile) {
        card.setIdentity(profile);
        user.displayClientMessage(ModUtil.translate("info", "identity_set").append(Component.literal(profile.getName()).withStyle(ChatFormatting.GREEN)), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);

        stack.getCapability(ModCapabilities.IDENTIFICATION_CARD).ifPresent(card -> {
            GameProfile identity = card.getIdentity();
            if (identity != null) {
                tooltipComponents.add(ModUtil.translate("info", "identity").withStyle(ChatFormatting.GRAY).append(Component.literal(identity.getName()).withStyle(ChatFormatting.GREEN)));
            } else {
                tooltipComponents.add(ModUtil.translate("info", "unidentified").withStyle(ChatFormatting.GRAY));
            }
            List<FieldPermission> perms = List.copyOf(card.getPermissions());
            if (!perms.isEmpty()) {
                MutableComponent permsComponent = ModUtil.translate(perms.get(0));
                for (int i = 1; i < perms.size(); i++) {
                    permsComponent.append(", ").append(ModUtil.translate(perms.get(i)));
                }
                tooltipComponents.add(permsComponent.withStyle(ChatFormatting.DARK_GRAY));
            }
        });
    }

    public static class IdentificationCardCapability implements ICapabilityProvider, IdentificationCard, INBTSerializable<CompoundTag> {
        private final LazyOptional<IdentificationCard> optional = LazyOptional.of(() -> this);

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
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return ModCapabilities.IDENTIFICATION_CARD.orEmpty(cap, this.optional);
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