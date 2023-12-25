package dev.su5ed.mffs.item;

import dev.su5ed.mffs.api.module.ProjectorMode;
import net.minecraft.world.item.Item;

public class ProjectorModeItem extends Item {
    private final ProjectorMode projectorMode;
    
    public ProjectorModeItem(Properties properties, ProjectorMode projectorMode) {
        super(properties.stacksTo(1));
        
        this.projectorMode = projectorMode;
    }

    public ProjectorMode getProjectorMode() {
        return this.projectorMode;
    }
}
