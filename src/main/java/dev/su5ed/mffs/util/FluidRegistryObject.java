package dev.su5ed.mffs.util;

import com.google.common.base.Suppliers;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.function.Supplier;

public class FluidRegistryObject {
    private final Supplier<FluidType> fluidType;
    private final Supplier<Fluid> sourceFluid;
    private final Supplier<Fluid> flowingFluid;

    public FluidRegistryObject(ModFluidType.FluidProperties properties) {
        this.fluidType = Suppliers.memoize(() -> new ModFluidType(properties));

        BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(this.fluidType, this::getSourceFluid, this::getFlowingFluid);
        this.sourceFluid = Suppliers.memoize(() -> new BaseFlowingFluid.Source(fluidProperties));
        this.flowingFluid = Suppliers.memoize(() -> new BaseFlowingFluid.Flowing(fluidProperties));
    }

    public Supplier<FluidType> fluidType() {
        return this.fluidType;
    }

    public Supplier<Fluid> sourceFluid() {
        return this.sourceFluid;
    }

    public Supplier<Fluid> flowingFluid() {
        return this.flowingFluid;
    }

    public Fluid getSourceFluid() {
        return this.sourceFluid.get();
    }

    public Fluid getFlowingFluid() {
        return this.flowingFluid.get();
    }
}
