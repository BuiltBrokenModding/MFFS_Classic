package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import one.util.streamex.StreamEx;

import java.util.concurrent.CompletableFuture;

public class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, MFFSMod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.FORCEFIELD_REPLACEABLE)
            .add(Blocks.SNOW, Blocks.VINE, Blocks.SHORT_GRASS, Blocks.DEAD_BUSH, Blocks.GLOW_LICHEN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.TALL_GRASS, Blocks.KELP, Blocks.KELP_PLANT);
        StreamEx.of(ModBlocks.COERCION_DERIVER, ModBlocks.FORTRON_CAPACITOR, ModBlocks.PROJECTOR, ModBlocks.BIOMETRIC_IDENTIFIER, ModBlocks.INTERDICTION_MATRIX)
            .map(DeferredBlock::get)
            .forEach(block -> {
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                tag(BlockTags.NEEDS_IRON_TOOL).add(block);
            });
    }

    @Override
    public String getName() {
        return MFFSMod.NAME + " Block Tags";
    }
}
