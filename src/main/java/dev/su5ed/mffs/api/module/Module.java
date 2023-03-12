package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collection;

public interface Module {
    ModuleType<?> getType();

    void beforeSelect(Projector projector, Collection<? extends BlockPos> field);

    ProjectAction onSelect(Projector projector, BlockPos pos);

    /**
     * Called before the projector projects a field.
     *
     * @param projector the projector block entity
     */
    void beforeProject(Projector projector);

    /**
     * Called right after the projector creates a force field block.
     *
     * @param projector the projector block entity
     * @param pos       the projected position
     * @return the desired action
     */
    ProjectAction onProject(Projector projector, BlockPos pos);

    /**
     * Called when an entity collides with a force field block.
     *
     * @param level  the level being projected in
     * @param pos    the position being collided with
     * @param entity the entity colliding with the field
     * @return whether to stop the default process of entity collision
     */
    boolean onCollideWithForceField(Level level, BlockPos pos, Entity entity);

    /**
     * Called in this module when it is being calculated by the projector.
     *
     * @param projector       the projector
     * @param fieldDefinition the field block positions
     */
    void onCalculate(Projector projector, Collection<BlockPos> fieldDefinition);

    enum Category {
        MATRIX,
        FIELD,
        INTERDICTION
    }

    enum ProjectAction {
        /**
         * Keep projecting
         */
        PROJECT,
        /**
         * Skip this block and continue
         */
        SKIP,
        /**
         * Cancel the projection entirely
         */
        INTERRUPT
    }
}

