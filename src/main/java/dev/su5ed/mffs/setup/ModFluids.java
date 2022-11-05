package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.util.FluidContainer;
import dev.su5ed.mffs.util.ModFluidType.FluidProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static dev.su5ed.mffs.MFFSMod.location;

public final class ModFluids {
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MFFSMod.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MFFSMod.MODID);

    public static final FluidContainer FORTRON = new FluidContainer(new FluidProperties()
            .density(1000)
            .texture(location("fluid_fortron")));
    public static final RegistryObject<FluidType> FORTRON_FLUID_TYPE = FLUID_TYPES.register("fortron", FORTRON.fluidType());
    public static final RegistryObject<Fluid> FORTRON_FLUID = FLUIDS.register("fortron_fluid", FORTRON.sourceFluid());
    public static final RegistryObject<Fluid> FLOWING_FORTRON = FLUIDS.register("flowing_fortron", FORTRON.flowingFluid());

    public static void init(final IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
    }

    private ModFluids() {}
}
