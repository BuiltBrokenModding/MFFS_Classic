package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.render.model.ForceFieldBlockModelBuilder;
import dev.su5ed.mffs.render.model.ForceFieldBlockModelLoader;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

final class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(PackOutput output, ExistingFileHelper helper) {
        super(output, MFFSMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        machineBlock(ModBlocks.PROJECTOR.get());
        machineBlock(ModBlocks.COERCION_DERIVER.get());
        machineBlock(ModBlocks.FORTRON_CAPACITOR.get());
        machineBlock(ModBlocks.BIOMETRIC_IDENTIFIER.get());
        machineBlock(ModBlocks.INTERDICTION_MATRIX.get());

        simpleBlock(ModBlocks.FORCE_FIELD.get(), models().getBuilder("force_field")
            .customLoader(ForceFieldBlockModelBuilder::new)
            .setDefaultModel(ForceFieldBlockModelLoader.DEFAULT_MODEL)
            .end());
    }

    public void machineBlock(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        getVariantBuilder(block)
            .forAllStates(state -> {
                ResourceLocation modelLocation = state.getValue(ProjectorBlock.ACTIVE)
                    ? ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_active")
                    : id;

                ConfiguredModel.Builder<?> builder = ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modelLocation));
                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    builder.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360);
                }
                return builder.build();
            });
    }
}
