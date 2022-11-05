package dev.su5ed.mffs.blockentity;

import dev.su5ed.mffs.MFFSConfig;
import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.setup.ModItems;
import dev.su5ed.mffs.setup.ModObjects;
import dev.su5ed.mffs.setup.ModTags;
import dev.su5ed.mffs.util.Fortron;
import dev.su5ed.mffs.util.FortronModule;
import dev.su5ed.mffs.util.FrequencyCard;
import dev.su5ed.mffs.util.ModCompatibility;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

public class CoercionDeriverBlockEntity extends ElectricTileEntity implements MenuProvider {
    private static final int CAPACITY = 5000000;
    /**
     * The amount of KiloWatts this machine uses.
     */
    private static final int DEFAULT_WATTAGE = 5000000;
    public static final int FUEL_PROCESS_TIME = 10 * 20;
    public static final int PRODUCTION_MULTIPLIER = 4;
    /**
     * Ratio from UE to Fortron. Multiply J by this value to convert to Fortron.
     */
    public static final float UE_FORTRON_RATIO = 0.001f;
    public static final int ENERGY_LOSS = 1;

    private static final int SLOT_FREQUENCY = 0;
    private static final int SLOT_BATTERY = 1;
    private static final int SLOT_FUEL = 2;
    private static final int[] SLOT_UPGRADE = {3, 4, 5};

    private final ItemStackHandler items = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();
            if (ArrayUtils.contains(SLOT_UPGRADE, slot)) {
                return item instanceof FortronModule;
            }
            return switch (slot) {
                case SLOT_FREQUENCY -> item instanceof FrequencyCard;
                case SLOT_BATTERY -> ModCompatibility.isEnergyItem(stack);
                case SLOT_FUEL -> stack.is(ModTags.FORTRON_CATALYST);
                default -> false;
            };
        }
    };
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> this.items);

    private int processTime;
    private EnergyMode energyMode = EnergyMode.DERIVE;

    public CoercionDeriverBlockEntity(BlockPos pos, BlockState state) {
        super(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), pos, state, CAPACITY);
        
        this.energy.setMaxTransfer(getWattage());
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    public void setEnergyMode(EnergyMode energyMode) {
        this.energyMode = energyMode;
    }
    
    public int getWattage() {
        return (int) (DEFAULT_WATTAGE + DEFAULT_WATTAGE * (getModuleCount(ModItems.SPEED_MODULE.get()) / 8.0f));
    }
    
    public int getModuleCount(Item module) {
        return 0; // TODO
    }
    
    public boolean isInversed() {
        return this.energyMode == EnergyMode.INTEGRATE;
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
       
        if (isEnabled()) {
            if (isInversed() && MFFSConfig.COMMON.enableElectricity.get()) {
                if (this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
                    int withdrawnElectricity = (int) (requestFortron(getProductionRate() / 20, FluidAction.EXECUTE) / UE_FORTRON_RATIO);
                    // Inject electricity from Fortron.
                    this.energy.receiveEnergy(withdrawnElectricity * ENERGY_LOSS, true);
                }

                recharge(this.items.getStackInSlot(SLOT_BATTERY));
                produce();
            } else {
                if (this.getFortronEnergy() < this.getFortronCapacity()) {
                    // Convert Electricity to Fortron
                    this.discharge(this.items.getStackInSlot(SLOT_BATTERY));

                    if (this.energy.canExtract() || !MFFSConfig.COMMON.enableElectricity.get() && hasFuel()) {
                        // Fill Fortron
                        this.fortronTank.fill(Fortron.getFortron(getProductionRate()), FluidAction.EXECUTE);
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
        if (isEnabled()) {
            int production = (int) ((float) getWattage() / 20f * UE_FORTRON_RATIO * MFFSConfig.COMMON.fortronProductionMultiplier.get());

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemCap.cast();
        }
        return super.getCapability(cap, side);
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
