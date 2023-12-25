package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.util.FluidRegistryObject;
import dev.su5ed.mffs.util.ModFluidType.FluidProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static dev.su5ed.mffs.MFFSMod.location;

public final class ModFluids {
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MFFSMod.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, MFFSMod.MODID);

    public static final FluidRegistryObject FORTRON = new FluidRegistryObject(new FluidProperties()
        .density(1000)
        .lightLevel(8)
        .texture(location("fluid/fortron")));
    public static final DeferredHolder<FluidType, FluidType> FORTRON_FLUID_TYPE = FLUID_TYPES.register("fortron", FORTRON.fluidType());
    public static final DeferredHolder<Fluid, Fluid> FORTRON_FLUID = FLUIDS.register("fortron_fluid", FORTRON.sourceFluid());
    public static final DeferredHolder<Fluid, Fluid> FLOWING_FORTRON = FLUIDS.register("flowing_fortron", FORTRON.flowingFluid());

    public static void init(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }

    private ModFluids() {}
}
