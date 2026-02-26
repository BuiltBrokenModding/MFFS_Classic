package dev.su5ed.mffs.blockentity;

import com.google.common.base.Suppliers;
import dev.su5ed.mffs.util.CustomEnergyStorage;
import dev.su5ed.mffs.util.SidedEnergyWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class ElectricTileEntity extends ModularBlockEntity {
    public final CustomEnergyStorage energy;
    private final Map<Direction, Supplier<EnergyHandler>> sidedEnergyCap;

    protected ElectricTileEntity(BlockEntityType<? extends BaseBlockEntity> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);

        this.energy = new CustomEnergyStorage(capacity, Integer.MAX_VALUE, this::isActive, this::setChanged);
        Set<Direction> inputSides = getEnergyInputSides();
        Set<Direction> outputSides = getEnergyOutputSides();
        this.sidedEnergyCap = StreamEx.of(inputSides)
            .append(outputSides)
            .distinct()
            .<Supplier<EnergyHandler>>mapToEntry(side -> Suppliers.memoize(
                () -> new SidedEnergyWrapper(this.energy, side == null || inputSides.contains(side), side == null || outputSides.contains(side))))
            .toMap();
    }

    @Nullable
    public EnergyHandler getEnergy(Direction side) {
        Supplier<EnergyHandler> supplier = this.sidedEnergyCap.get(side);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Charges electric item.
     */
    public void chargeItemFromSelf(ItemStack stack) {
        EnergyHandler receiver = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        if (receiver != null) {
            EnergyHandlerUtil.move(this.energy, receiver, this.energy.getAmountAsInt(), null);
        }
    }

    /**
     * Discharges electric item.
     */
    public void dischargeItemIntoSelf(ItemStack stack) {
        EnergyHandler source = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        if (source != null) {
            EnergyHandlerUtil.move(source, this.energy, this.energy.getRequestedEnergy(), null);
        }
    }

    protected void outputEnergyToNearbyTiles() {
        for (Direction direction : getEnergyOutputSides()) {
            if (this.energy.getAmountAsInt() > 0) {
                EnergyHandler handler = this.level.getCapability(Capabilities.Energy.BLOCK, this.worldPosition.relative(direction), direction.getOpposite());
                EnergyHandlerUtil.move(this.energy, handler, Integer.MAX_VALUE, null);
            }
        }
    }

    public Set<Direction> getEnergyInputSides() {
        return Collections.emptySet();
    }

    public Set<Direction> getEnergyOutputSides() {
        return Collections.emptySet();
    }

    @Override
    protected void saveTag(ValueOutput output) {
        super.saveTag(output);

        output.putChild("energy", this.energy);
    }

    @Override
    protected void loadTag(ValueInput input) {
        super.loadTag(input);

        input.child("energy").ifPresent(this.energy::deserialize);
    }
}
