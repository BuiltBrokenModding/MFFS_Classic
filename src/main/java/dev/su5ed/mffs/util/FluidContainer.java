package dev.su5ed.mffs.util;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class FluidContainer {
    private final Supplier<FluidType> fluidType;
    private final Lazy<Fluid> sourceFluid;
    private final Lazy<Fluid> flowingFluid;

    public FluidContainer(ModFluidType.FluidProperties properties) {
        this.fluidType = () -> new ModFluidType(properties);
        
        ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(this.fluidType, this::getSourceFluid, this::getFlowingFluid);
        this.sourceFluid = Lazy.of(() -> new ForgeFlowingFluid.Source(fluidProperties));
        this.flowingFluid = Lazy.of(() -> new ForgeFlowingFluid.Flowing(fluidProperties));
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
