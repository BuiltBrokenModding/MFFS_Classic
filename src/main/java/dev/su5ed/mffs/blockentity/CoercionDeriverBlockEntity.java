package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.util.Fortron;
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
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class CoercionDeriverBlockEntity extends ElectricTileEntity implements MenuProvider {
    private static final int DEFAULT_FE_CAPACITY = 5000000;
    public static final int FUEL_PROCESS_TIME = 10 * 20;
    public static final int PRODUCTION_MULTIPLIER = 4;
    /**
     * Ratio from FE to Fortron. Multiply FE by this value to convert to Fortron.
     */
    public static final float FE_FORTRON_RATIO = 0.0025f;
    public static final int ENERGY_LOSS = 1;
    
    private static final int SLOT_BATTERY = 1;
    private static final int SLOT_FUEL = 2;
    private static final int[] SLOT_UPGRADE = {3, 4, 5};

    private int processTime;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state, DEFAULT_FE_CAPACITY);
        
        this.energy.setMaxTransfer(getWattage());
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }
    
    public int getWattage() {
        return (int) (DEFAULT_FE_CAPACITY + DEFAULT_FE_CAPACITY * (getModuleCount(ModItems.SPEED_MODULE.get()) / 8.0f));
    }
    
    public boolean isInversed() {
        return this.energyMode == EnergyMode.INTEGRATE;
    }

    @Override
    public int getSizeInventory() {
        return 6;
    }

    @Override
    public int getBaseFortronTankCapacity() {
        return 30;
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
       
        if (isActive()) {
            if (isInversed() && MFFSConfig.COMMON.enableElectricity.get()) {
                if (this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
                    int withdrawnElectricity = (int) (requestFortron(getProductionRate() / 20, FluidAction.EXECUTE) / FE_FORTRON_RATIO);
                    // Inject electricity from Fortron.
                    this.energy.receiveEnergy(withdrawnElectricity * ENERGY_LOSS, true);
                }

                recharge(this.items.getStackInSlot(SLOT_BATTERY));
                produce();
            } else {
                if (getFortronEnergy() < getFortronCapacity()) {
                    // Convert Electricity to Fortron
                    discharge(this.items.getStackInSlot(SLOT_BATTERY));

                    if (this.energy.canExtract() || !MFFSConfig.COMMON.enableElectricity.get() && hasFuel()) {
                        // Fill Fortron
                        provideFortron(getProductionRate(), FluidAction.EXECUTE);
                        this.energy.extractEnergy();

                        // Use fuel
                        if (this.processTime == 0 && hasFuel()) {
                            this.items.getStackInSlot(SLOT_FUEL).shrink(1);
                            this.processTime = FUEL_PROCESS_TIME * Math.max(getModuleCount(ModItems.SCALE_MODULE.get()) / 20, 1);
                        }

                        if (this.processTime > 0) {
                            // We are processing.
                            this.processTime--;

                            if (this.processTime < 1) {
                                this.processTime = 0;
                            }
                        } else {
                            this.processTime = 0;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return The Fortron production rate per tick!
     */
    public int getProductionRate() {
        if (isActive()) {
            int production = (int) ((float) getWattage() / 20f * FE_FORTRON_RATIO * MFFSConfig.COMMON.fortronProductionMultiplier.get());

            if (this.processTime > 0) {
                production *= PRODUCTION_MULTIPLIER;
            }

            return production;
        }
        return 0;
    }
    
    public boolean hasFuel() {
        return !this.items.getStackInSlot(SLOT_FUEL).isEmpty();
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

    @Override
    protected void saveTag(CompoundTag tag) {
        super.saveTag(tag);

        tag.putInt("processTime", this.processTime);
        tag.putString("energyMode", this.energyMode.name());
    }

    @Override
    protected void loadTag(CompoundTag tag) {
        super.loadTag(tag);
        
        this.processTime = tag.getInt("processTime");
        this.energyMode = EnergyMode.valueOf(tag.getString("energyMode"));
    }
    
    @Override
    public Set<Direction> getInputSides() {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public Set<Direction> getOutputSides() {
        return EnumSet.allOf(Direction.class);
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
