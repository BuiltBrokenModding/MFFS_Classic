package dev.su5ed.mffs.datagen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.setup.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class BlockTagsGen extends BlockTagsProvider {

    public BlockTagsGen(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, MFFSMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModTags.FORCEFIELD_REPLACEABLE).add(Blocks.SNOW, Blocks.VINE, Blocks.TALL_GRASS, Blocks.DEAD_BUSH);
    }

    @Override
    public String getName() {
        return MFFSMod.NAME + " Block Tags";
    }
}
