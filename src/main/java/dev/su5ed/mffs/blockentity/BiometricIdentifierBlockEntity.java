package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.CopyingIdentificationCard;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import one.util.streamex.IntStreamEx;

import java.util.List;
import java.util.Optional;

public class BiometricIdentifierBlockEntity extends FortronBlockEntity implements BiometricIdentifier {
    public final InventorySlot masterSlot;
    public final InventorySlot rightsSlot;
    public final InventorySlot copySlot;
    public final List<InventorySlot> identitySlots;

    public BiometricIdentifierBlockEntity() {
        super();

        this.masterSlot = addSlot("master", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard);
        this.rightsSlot = addSlot("rights", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard);
        this.copySlot = addSlot("copy", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard, this::copyCard);
        this.identitySlots = IntStreamEx.range(9)
            .mapToObj(i -> addSlot("identity_" + i, InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard))
            .toList();
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == ModCapabilities.BIOMETRIC_IDENTIFIER) return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (capability == ModCapabilities.BIOMETRIC_IDENTIFIER) return (T) this;
        return super.getCapability(capability, facing);
    }

    @Override
    public Optional<IdentificationCard> getManipulatingCard() {
        return Optional.ofNullable(this.rightsSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null))
            .map(card -> Optional.ofNullable(this.copySlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null))
                .<IdentificationCard>map(copy -> new CopyingIdentificationCard(card, copy))
                .orElse(card));
    }

    private void copyCard(ItemStack stack) {
        Optional.ofNullable(this.rightsSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null))
            .ifPresent(card -> Optional.ofNullable(stack.getCapability(ModCapabilities.IDENTIFICATION_CARD, null))
                .ifPresent(card::copyTo));
    }

    @Override
    public void setActive(boolean active) {
        if (!this.masterSlot.isEmpty() || !active) {
            super.setActive(active);
        }
    }

    @Override
    public boolean isActive() {
        return !this.masterSlot.isEmpty() && super.isActive();
    }

    @Override
    protected void animate() {
        super.animate();

        if (!isActive()) {
            this.animation = 0;
        }
    }

    @Override
    public boolean isAccessGranted(EntityPlayer player, FieldPermission permission) {
        if (!isActive()) return false;
        if (canOpBypass(player)) return true;

        // The master card holder has all permissions implicitly.
        IdentificationCard masterCard = this.masterSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
        if (masterCard != null && masterCard.checkIdentity(player)) return true;

        // Named cards (specific identity) take priority over blank wildcard cards.
        // First pass: look for a card that explicitly names this player.
        for (InventorySlot slot : this.identitySlots) {
            IdentificationCard card = slot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
            if (card != null && card.getIdentity() != null && card.checkIdentity(player)) {
                // Found a card that specifically targets this player — its permissions are definitive.
                return card.hasPermission(permission);
            }
        }

        // Second pass: no named card matched; fall back to blank (everyone) wildcard cards.
        for (InventorySlot slot : this.identitySlots) {
            IdentificationCard card = slot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD, null);
            if (card != null && card.getIdentity() == null) {
                return card.hasPermission(permission);
            }
        }

        return false;
    }

    public static boolean canOpBypass(EntityPlayer player) {
        return player instanceof EntityPlayerMP serverPlayer
            && MFFSConfig.allowOpBiometryOverride
            && serverPlayer.server.getPlayerList().canSendCommands(serverPlayer.getGameProfile());
    }
}
