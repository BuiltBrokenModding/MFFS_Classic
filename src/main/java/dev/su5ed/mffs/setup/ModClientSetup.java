package dev.su5ed.mffs.setup;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ForceFieldBlockImpl;
import dev.su5ed.mffs.render.*;
import dev.su5ed.mffs.render.model.*;
import dev.su5ed.mffs.render.particle.*;
import dev.su5ed.mffs.screen.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(modid = MFFSMod.MODID, value = Dist.CLIENT)
public final class ModClientSetup {
    private static final Map<Item, LazyRendererFactory> LAZY_RENDERERS = new HashMap<>();

    public static void renderLazy(Item item, BlockEntity be, Function<ModelLayerLocation, ModelPart> modelFactory) {
        LazyRendererFactory factory = LAZY_RENDERERS.get(item);
        if (factory != null) {
            factory.apply(be, modelFactory);
        }
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
        event.enqueueWork(ModClientSetup::registerLazyRenderers);
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
    public static void registerBlockColor(RegisterColorHandlersEvent.BlockTintSources event) {
        Block forceFieldBlock = ModBlocks.FORCE_FIELD.get();
        event.register(List.of(new ForceFieldBlockTintSource()), forceFieldBlock);
    }

    @SubscribeEvent
    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(ModRenderPipeline.HOLO_TRIANGLE);
        event.registerPipeline(ModRenderPipeline.HOLO_TEXTURED_TRIANGLE);
        event.registerPipeline(ModRenderPipeline.HOLO_QUAD);
        event.registerPipeline(ModRenderPipeline.BLOCK_FILL);
        event.registerPipeline(ModRenderPipeline.BLOCK_OUTLINE);
        event.registerPipeline(ModRenderPipeline.HOLO_ENTITY);
        event.registerPipeline(ModRenderPipeline.BEAM_PARTICLE);
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> models = event.getBakingResult().blockStateModels();
        ModBlocks.FORCE_FIELD.get().getStateDefinition().getPossibleStates().forEach(state ->
            models.computeIfPresent(state, (location, model) -> new ForceFieldBlockModel(model)));
    }

    @SubscribeEvent
    public static void registerParticleGroups(RegisterParticleGroupsEvent event) {
        event.register(ModParticleRenderType.BEAM, BeamParticleGroup::new);
        event.register(ModParticleRenderType.HOLO, MovingHolograpParticleGroup::new);
    }

    private static class ForceFieldBlockTintSource implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            return 0xFF34fEFF;
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                BlockState camo = be.getModelData().get(ForceFieldBlockImpl.CAMOUFLAGE_BLOCK);
                if (camo != null && !camo.is(ModBlocks.FORCE_FIELD)) {
                    BlockTintSource source = Minecraft.getInstance().getBlockColors().getTintSource(camo, 0);
                    if (source != null) {
                        return source.colorInWorld(state, level, pos);
                    }
                }
            }
            return color(state);
        }
    }

    private ModClientSetup() {
    }
}
