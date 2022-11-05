package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.block.ProjectorBlock;
import dev.su5ed.mffs.setup.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

final class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MFFSMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.MACHINE_BLOCK.get());

        machineBlock(ModBlocks.PROJECTOR.get());
        machineBlock(ModBlocks.COERCION_DERIVER.get());
    }

    public void machineBlock(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        getVariantBuilder(block)
            .forAllStates(state -> {
                ResourceLocation modelLocation = state.getValue(ProjectorBlock.ENABLED)
                    ? new ResourceLocation(id.getNamespace(), id.getPath() + "_enabled")
                    : id;

                return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modelLocation))
                    .build();
            });
    }
}
