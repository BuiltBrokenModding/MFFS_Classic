package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.ModObjects;
import dev.su5ed.mffs.block.ProjectorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

final class BlockStateGen extends BlockStateProvider {

    public BlockStateGen(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, MFFSMod.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModObjects.MACHINE_BLOCK.get());
        
        machineBlock(ModObjects.PROJECTOR_BLOCK.get());
    }

    public void machineBlock(Block block) {
        ResourceLocation id = block.getRegistryName();
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
