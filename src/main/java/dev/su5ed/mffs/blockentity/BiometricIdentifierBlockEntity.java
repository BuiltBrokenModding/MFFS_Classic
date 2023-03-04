package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.api.card.IdentificationCard;
import dev.su5ed.mffs.api.security.BiometricIdentifier;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.menu.BiometricIdentifierMenu;
import dev.su5ed.mffs.setup.ModCapabilities;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.ModUtil;
import dev.su5ed.mffs.util.inventory.CopyingIdentificationCard;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BiometricIdentifierBlockEntity extends FortronBlockEntity implements BiometricIdentifier {
    public final InventorySlot masterSlot;
    public final InventorySlot rightsSlot;
    public final InventorySlot copySlot;
    public final List<InventorySlot> identitySlots;

    private final LazyOptional<BiometricIdentifier> identifierCap = LazyOptional.of(() -> this);

    public BiometricIdentifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get(), pos, state);

        // TODO: Restrict slot access to GUI
        this.masterSlot = addSlot("master", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard);
        this.rightsSlot = addSlot("rights", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard);
        this.copySlot = addSlot("copy", InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard, this::copyCard);
        this.identitySlots = IntStreamEx.range(9)
            .mapToObj(i -> addSlot("identity_" + i, InventorySlot.Mode.BOTH, ModUtil::isIdentificationCard))
            .toList();
    }

    @Override
    public LazyOptional<IdentificationCard> getManipulatingCard() {
        return this.rightsSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD)
            .lazyMap(card -> this.copySlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD)
                .<IdentificationCard>map(copy -> new CopyingIdentificationCard(card, copy))
                .orElse(card));
    }

    private void copyCard(ItemStack stack) {
        this.rightsSlot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD)
            .ifPresent(card -> stack.getCapability(ModCapabilities.IDENTIFICATION_CARD)
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
    public boolean isAccessGranted(Player player, FieldPermission permission) {
        return !isActive() || canOpBypass(player) || StreamEx.of(this.masterSlot)
            .append(this.identitySlots)
            .anyMatch(slot -> slot.getItem().getCapability(ModCapabilities.IDENTIFICATION_CARD)
                .map(card -> card.checkIdentity(player))
                .orElse(false));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.BIOMETRIC_IDENTIFIER) {
            return this.identifierCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BiometricIdentifierMenu(containerId, this.worldPosition, player, playerInventory);
    }

    public static boolean canOpBypass(Player player) {
        return player instanceof ServerPlayer serverPlayer && MFFSConfig.COMMON.allowOpBiometryOverride.get() && serverPlayer.server.getPlayerList().isOp(player.getGameProfile());
    }
}
