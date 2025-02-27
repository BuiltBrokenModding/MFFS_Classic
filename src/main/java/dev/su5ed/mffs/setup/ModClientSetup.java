package dev.su5ed.mffs.setup;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ForceFieldBlockImpl;
import dev.su5ed.mffs.render.*;
import dev.su5ed.mffs.render.model.*;
import dev.su5ed.mffs.render.particle.BeamParticleProvider;
import dev.su5ed.mffs.render.particle.MovingHologramParticleProvider;
import dev.su5ed.mffs.screen.*;
import dev.su5ed.mffs.util.ModFluidType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static dev.su5ed.mffs.MFFSMod.location;

@EventBusSubscriber(modid = MFFSMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ModClientSetup {
    private static final Map<Item, LazyRendererFactory> LAZY_RENDERERS = new HashMap<>();

    public static void renderLazy(Item item, BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        LazyRendererFactory factory = LAZY_RENDERERS.get(item);
        if (factory != null) {
            factory.apply(be, modelFactory);
        }
    }

    public static boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    private static void registerLazyRenderers() {
        LAZY_RENDERERS.put(ModItems.CUBE_MODE.get(), ProjectorModeRenderer::renderCubeMode);
        LAZY_RENDERERS.put(ModItems.SPHERE_MODE.get(), ProjectorModeRenderer::renderSphereMode);
        LAZY_RENDERERS.put(ModItems.TUBE_MODE.get(), ProjectorModeRenderer::renderTubeMode);
        LAZY_RENDERERS.put(ModItems.PYRAMID_MODE.get(), ProjectorModeRenderer::renderPyramidMode);
        LAZY_RENDERERS.put(ModItems.CYLINDER_MODE.get(), ProjectorModeRenderer::renderCylinderMode);
        LAZY_RENDERERS.put(ModItems.CUSTOM_MODE.get(), ProjectorModeRenderer::renderCustomMode);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerLazyRenderers();
            RenderPostProcessor.initRenderTarget();
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.COERCION_DERIVER_MENU.get(), CoercionDeriverScreen::new);
        event.register(ModMenus.FORTRON_CAPACITOR_MENU.get(), FortronCapacitorScreen::new);
        event.register(ModMenus.PROJECTOR_MENU.get(), ProjectorScreen::new);
        event.register(ModMenus.BIOMETRIC_IDENTIFIER_MENU.get(), BiometricIdentifierScreen::new);
        event.register(ModMenus.INTERDICTION_MATRIX_MENU.get(), InterdictionMatrixScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), ProjectorRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.COERCION_DERIVER_BLOCK_ENTITY.get(), CoercionDeriverRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.BIOMETRIC_IDENTIFIER_BLOCK_ENTITY.get(), BiometricIdentifierRenderer::new);
        event.registerBlockEntityRenderer(ModObjects.FORCE_FIELD_BLOCK_ENTITY.get(), ForceFieldBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModObjects.BEAM_PARTICLE.get(), new BeamParticleProvider());
        event.registerSpecial(ModObjects.MOVING_HOLOGRAM_PARTICLE.get(), new MovingHologramParticleProvider());
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorRotorModel.LAYER_LOCATION, ProjectorRotorModel::createBodyLayer);
        event.registerLayerDefinition(CoercionDeriverTopModel.LAYER_LOCATION, CoercionDeriverTopModel::createBodyLayer);
        event.registerLayerDefinition(ForceCubeModel.LAYER_LOCATION, ForceCubeModel::createBodyLayer);
        event.registerLayerDefinition(ForceTubeModel.LAYER_LOCATION, ForceTubeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterLoaders event) {
        event.register(ForceFieldBlockModelLoader.NAME, new ForceFieldBlockModelLoader());
    }

    @SubscribeEvent
    public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        Block forceFieldBlock = ModBlocks.FORCE_FIELD.get();
        event.register((state, level, pos, tintIndex) -> {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                BlockState camo = be.getModelData().get(ForceFieldBlockImpl.CAMOUFLAGE_BLOCK);
                if (camo != null && !camo.is(forceFieldBlock)) {
                    return event.getBlockColors().getColor(camo, level, pos, tintIndex);
                }
            }
            return 3473151;
        }, forceFieldBlock);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(location("post_process"), (ResourceManagerReloadListener) resourceManager -> RenderPostProcessor.reloadPostProcessPass());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ModFluidType.FluidRenderInfo properties = ModFluids.FORTRON.getProperties();
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return properties.stillTexture();
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return properties.flowingTexture();
            }

            @Nullable
            @Override
            public ResourceLocation getOverlayTexture() {
                return properties.overlayTexture();
            }

            @Nullable
            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                return properties.renderOverlayTexture();
            }

            @Override
            public int getTintColor() {
                return properties.tintColor();
            }
        }, ModFluids.FORTRON_FLUID_TYPE);
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        event.registerShader(new ShaderProgram(MFFSMod.location("post/glitch_blit"), DefaultVertexFormat.NEW_ENTITY, ShaderDefines.EMPTY));
    }

    private ModClientSetup() {}
}
