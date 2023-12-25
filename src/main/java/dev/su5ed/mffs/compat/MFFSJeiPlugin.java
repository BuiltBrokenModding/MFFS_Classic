package dev.su5ed.mffs.compat;

import dev.su5ed.mffs.screen.InterdictionMatrixScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import static dev.su5ed.mffs.MFFSMod.location;

@JeiPlugin
public class MFFSJeiPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_ID = location("jei_plugin");

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // Hide Fortron from JEI as it's not a crafting fluid
        // TODO Update when JEI is available
//        runtime.getIngredientManager().removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, List.of(new FluidStack(ModFluids.FORTRON_FLUID.get(), FluidType.BUCKET_VOLUME)));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(InterdictionMatrixScreen.class, new BasicGhostIngredientHandler<>());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
}
