package dev.su5ed.mffs;

import dev.su5ed.mffs.render.BeamParticleProvider;
import dev.su5ed.mffs.render.ProjectorBlockRenderer;
import dev.su5ed.mffs.render.ProjectorRotorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModClientSetup {

    @SubscribeEvent
    public static void registerParticleFactory(ParticleFactoryRegisterEvent event) {
        BlockEntityRenderers.register(ModObjects.PROJECTOR_BLOCK_ENTITY.get(), ProjectorBlockRenderer::new);

        Minecraft.getInstance().particleEngine.register(ModObjects.BEAM_PARTICLE.get(), BeamParticleProvider::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorRotorModel.LAYER_LOCATION, ProjectorRotorModel::createBodyLayer);
    }
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ForgeModelBakery.addSpecialModel(ProjectorBlockRenderer.FORCE_CUBE_MODEL);
    }

    private ModClientSetup() {}
}
