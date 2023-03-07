package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.render.model.ForceFieldBlockModelBuilder;
import dev.su5ed.mffs.render.model.ForceFieldBlockModelLoader;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import static dev.su5ed.mffs.MFFSMod.location;

final class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MFFSMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        machineBlock(ModBlocks.PROJECTOR.get());
        machineBlock(ModBlocks.COERCION_DERIVER.get());
        machineBlock(ModBlocks.FORTRON_CAPACITOR.get());
        simpleBlock(ModBlocks.BIOMETRIC_IDENTIFIER.get(), models().getExistingFile(location("biometric_identifier")));
        machineBlock(ModBlocks.INTERDICTION_MATRIX.get());

        simpleBlock(ModBlocks.FORCE_FIELD.get(), models().getBuilder("force_field")
            .customLoader(ForceFieldBlockModelBuilder::new)
            .setDefaultModel(ForceFieldBlockModelLoader.DEFAULT_MODEL)
            .end());
    }

    public void machineBlock(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        getVariantBuilder(block)
            .forAllStates(state -> {
                ResourceLocation modelLocation = state.getValue(ProjectorBlock.ACTIVE)
                    ? new ResourceLocation(id.getNamespace(), id.getPath() + "_active")
                    : id;

                return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modelLocation))
                    .build();
            });
    }
}
