package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.render.ClientRenderHandler;
import dev.su5ed.mffs.render.CoercionDeriverBlockRenderer;
import dev.su5ed.mffs.render.LazyRendererFactory;
import dev.su5ed.mffs.render.ProjectorBlockRenderer;
import dev.su5ed.mffs.render.model.CoercionDeriverTopModel;
import dev.su5ed.mffs.render.model.ForceCubeModel;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import dev.su5ed.mffs.render.particle.BeamParticleProvider;
import dev.su5ed.mffs.screen.CoercionDeriverScreen;
import dev.su5ed.mffs.screen.FortronCapacitorScreen;
import dev.su5ed.mffs.screen.ProjectorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public final class ModClientSetup {
    private static final Map<Item, LazyRendererFactory> LAZY_RENDERERS = new HashMap<>();

    public static void renderLazy(Item item, BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        LazyRendererFactory factory = LAZY_RENDERERS.get(item);
        if (factory != null) {
            factory.apply(be, modelFactory);
        }
    }

    private static void registerLazyRenderers() {
        LAZY_RENDERERS.put(ModItems.CUBE_MODE.get(), ClientRenderHandler::renderCubeMode);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.COERCION_DERIVER_MENU.get(), CoercionDeriverScreen::new);
            MenuScreens.register(ModMenus.FORTRON_CAPACITOR_MENU.get(), FortronCapacitorScreen::new);
            MenuScreens.register(ModMenus.PROJECTOR_MENU.get(), ProjectorScreen::new);

            registerLazyRenderers();
        });
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), ProjectorBlockRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), CoercionDeriverBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
        event.register(ModObjects.BEAM_PARTICLE.get(), new BeamParticleProvider());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorRotorModel.LAYER_LOCATION, ProjectorRotorModel::createBodyLayer);
        event.registerLayerDefinition(CoercionDeriverTopModel.LAYER_LOCATION, CoercionDeriverTopModel::createBodyLayer);
        event.registerLayerDefinition(ForceCubeModel.LAYER_LOCATION, ForceCubeModel::createBodyLayer);
    }

    private ModClientSetup() {}
}
