package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.ProjectorMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;

public abstract class ProjectorModeItem extends Item implements ProjectorMode {

    public ProjectorModeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isInField(Projector projector, BlockPos position) {
        return false;
    }
}
