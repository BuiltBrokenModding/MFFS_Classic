package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModBlocks;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

public class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.FORCEFIELD_REPLACEABLE)
            .add(Blocks.SNOW, Blocks.VINE, Blocks.GRASS, Blocks.DEAD_BUSH, Blocks.GLOW_LICHEN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.TALL_GRASS, Blocks.KELP, Blocks.KELP_PLANT);
        StreamEx.of(ModBlocks.COERCION_DERIVER, ModBlocks.FORTRON_CAPACITOR, ModBlocks.PROJECTOR, ModBlocks.BIOMETRIC_IDENTIFIER)
            .map(RegistryObject::get)
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
