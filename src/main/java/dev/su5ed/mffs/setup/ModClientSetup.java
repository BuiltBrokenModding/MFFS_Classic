package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.render.BiometricIdentifierRenderer;
import dev.su5ed.mffs.render.ClientRenderHandler;
import dev.su5ed.mffs.render.CoercionDeriverRenderer;
import dev.su5ed.mffs.render.LazyRendererFactory;
import dev.su5ed.mffs.render.ProjectorRenderer;
import dev.su5ed.mffs.render.model.BiometricIdentifierModel;
import dev.su5ed.mffs.render.model.CoercionDeriverTopModel;
import dev.su5ed.mffs.render.model.ForceCubeModel;
import dev.su5ed.mffs.render.model.ForceFieldBlockModelLoader;
import dev.su5ed.mffs.render.model.ForceTubeModel;
import dev.su5ed.mffs.render.model.ProjectorRotorModel;
import dev.su5ed.mffs.render.particle.BeamParticleProvider;
import dev.su5ed.mffs.render.particle.MovingHologramParticleProvider;
import dev.su5ed.mffs.screen.BiometricIdentifierScreen;
import dev.su5ed.mffs.screen.CoercionDeriverScreen;
import dev.su5ed.mffs.screen.FortronCapacitorScreen;
import dev.su5ed.mffs.screen.ProjectorScreen;
import dev.su5ed.mffs.util.projector.ModProjectorModes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public final class ModClientSetup {
    private static final Map<ProjectorMode, LazyRendererFactory> LAZY_RENDERERS = new HashMap<>();

    public static void renderLazy(ProjectorMode mode, BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        LazyRendererFactory factory = LAZY_RENDERERS.get(mode);
        if (factory != null) {
            factory.apply(be, modelFactory);
        }
    }

    private static void registerLazyRenderers() {
        LAZY_RENDERERS.put(ModProjectorModes.CUBE, ClientRenderHandler::renderCubeMode);
        LAZY_RENDERERS.put(ModProjectorModes.SPHERE, ClientRenderHandler::renderSphereMode);
        LAZY_RENDERERS.put(ModProjectorModes.TUBE, ClientRenderHandler::renderTubeMode);
        LAZY_RENDERERS.put(ModProjectorModes.PYRAMID, ClientRenderHandler::renderPyramidMode);
        LAZY_RENDERERS.put(ModProjectorModes.CYLINDER, ClientRenderHandler::renderCylinderMode);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.COERCION_DERIVER_MENU.get(), CoercionDeriverScreen::new);
            MenuScreens.register(ModMenus.FORTRON_CAPACITOR_MENU.get(), FortronCapacitorScreen::new);
            MenuScreens.register(ModMenus.PROJECTOR_MENU.get(), ProjectorScreen::new);
            MenuScreens.register(ModMenus.BIOMETRIC_IDENTIFIER_MENU.get(), BiometricIdentifierScreen::new);

            registerLazyRenderers();
        });
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), CoercionDeriverRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get(), BiometricIdentifierRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
        event.register(ModObjects.BEAM_PARTICLE.get(), new BeamParticleProvider());
        event.register(ModObjects.MOVING_HOLOGRAM_PARTICLE.get(), new MovingHologramParticleProvider());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorRotorModel.LAYER_LOCATION, ProjectorRotorModel::createBodyLayer);
        event.registerLayerDefinition(CoercionDeriverTopModel.LAYER_LOCATION, CoercionDeriverTopModel::createBodyLayer);
        event.registerLayerDefinition(ForceCubeModel.LAYER_LOCATION, ForceCubeModel::createBodyLayer);
        event.registerLayerDefinition(ForceTubeModel.LAYER_LOCATION, ForceTubeModel::createBodyLayer);
        event.registerLayerDefinition(BiometricIdentifierModel.LAYER_LOCATION, BiometricIdentifierModel::createBodyLayer);
    }
    
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            event.addSprite(BiometricIdentifierRenderer.HOLO_SCREEN_TEXTURE);
        }
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ForceFieldBlockModelLoader.NAME.getPath(), new ForceFieldBlockModelLoader());
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> 3473151, ModBlocks.FORCE_FIELD.get());
    }

    private ModClientSetup() {}
}
