package dev.su5ed.mffs.item;

import com.mojang.authlib.GameProfile;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModDataComponentTypes;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IdentificationCardItem extends BaseItem {

    public IdentificationCardItem(Properties properties) {
        super(new ExtendedItemProperties(properties.stacksTo(1)).description());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(usedHand);

            IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD);
            if (!level.isClientSide()) {
                if (card.getIdentity() != null) {
                    card.setIdentity(null);
                    player.displayClientMessage(ModUtil.translate("info", "identity_cleared"), true);
                } else {
                    setCardIdentity(card, player, player.getGameProfile());
                }
            }
            return InteractionResult.CONSUME;
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
                if (!player.level().isClientSide()) {
                    setCardIdentity(card, player, targetPlayer.getGameProfile());
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static void setCardIdentity(IdentificationCard card, Player user, GameProfile profile) {
        card.setIdentity(profile);
        user.displayClientMessage(ModUtil.translate("info", "identity_set", Component.literal(profile.name()).withStyle(ChatFormatting.GREEN)), true);
    }

    @Override
    protected void appendHoverTextPre(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverTextPre(stack, context, tooltipDisplay, tooltipAdder, flag);

        IdentificationCard card = stack.getCapability(ModCapabilities.IDENTIFICATION_CARD);
        if (card != null) {
            GameProfile identity = card.getIdentity();
            if (identity != null) {
                tooltipAdder.accept(ModUtil.translate("info", "identity",
                    Component.literal(identity.name()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.DARK_GRAY));
            }
            List<FieldPermission> perms = List.copyOf(card.getPermissions());
            if (!perms.isEmpty()) {
                MutableComponent permsComponent = ModUtil.translate(perms.getFirst());
                for (int i = 1; i < perms.size(); i++) {
                    permsComponent.append(", ").append(ModUtil.translate(perms.get(i)));
                }
                tooltipAdder.accept(permsComponent.withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    public record IdentificationCardAttachment(ItemStack stack) implements IdentificationCard {
        private Set<FieldPermission> getPermissionsSet() {
            return new HashSet<>(this.stack.getOrDefault(ModDataComponentTypes.ID_CARD_PERMISSIONS, List.of()));
        }

        @Override
        public boolean hasPermission(FieldPermission permission) {
            return getPermissionsSet().contains(permission);
        }

        @Override
        public void addPermission(FieldPermission permission) {
            Set<FieldPermission> permissions = getPermissionsSet();
            permissions.add(permission);
            this.stack.set(ModDataComponentTypes.ID_CARD_PERMISSIONS, List.copyOf(permissions));
        }

        @Override
        public void removePermission(FieldPermission permission) {
            Set<FieldPermission> permissions = getPermissionsSet();
            permissions.remove(permission);
            this.stack.set(ModDataComponentTypes.ID_CARD_PERMISSIONS, List.copyOf(permissions));
        }

        @Override
        public Collection<FieldPermission> getPermissions() {
            return getPermissionsSet();
        }

        @Override
        public void setPermissions(Collection<FieldPermission> permissions) {
            this.stack.set(ModDataComponentTypes.ID_CARD_PERMISSIONS, List.copyOf(Set.copyOf(permissions)));
        }

        @Nullable
        @Override
        public GameProfile getIdentity() {
            return this.stack.get(ModDataComponentTypes.ID_CARD_PROFILE);
        }

        @Override
        public void setIdentity(GameProfile profile) {
            this.stack.set(ModDataComponentTypes.ID_CARD_PROFILE, profile);
        }

        @Override
        public boolean checkIdentity(Player player) {
            GameProfile profile = getIdentity();
            return profile == null || player.getGameProfile().equals(profile);
        }

        @Override
        public void copyTo(IdentificationCard other) {
            other.setIdentity(getIdentity());
            other.setPermissions(getPermissions());
        }
    }
}
