package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CoercionDeriverBlockEntity extends AnimatedBlockEntity implements MenuProvider {
    private static final int CAPACITY = 5000000;

    private final ItemStackHandler itemHandler = new ItemStackHandler(6);
    private final LazyOptional<IItemHandler> items = LazyOptional.of(() -> itemHandler);
    
    private final EnergyStorage energyStorage = new CustomEnergyStorage(CAPACITY, 10000, this::isEnabled, this::setChanged);
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state);
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }

    @Override
    public InteractionResult use(Player player, InteractionHand hand, BlockHitResult hit) {
        if (!this.level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, this, this.worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tickServer() {
        super.tickServer();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.energy.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CoercionDeriverContainer(containerId, this.worldPosition, player, inventory);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.items.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        
        this.energyStorage.deserializeNBT(tag.get("energy"));
        this.energyMode = EnergyMode.valueOf(tag.getString("energyMode"));
    }

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);
        
        tag.put("energy", this.energyStorage.serializeNBT());
        tag.putString("energyMode", this.energyMode.name());
    }
    
    public enum EnergyMode {
        DERIVE,     // FE -> FORT
        INTEGRATE;  // FORT -> FE
        
        private static final EnergyMode[] VALUES = values();
        
        public EnergyMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }
}
