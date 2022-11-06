package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.api.FieldInteraction;
import dev.su5ed.mffs.api.Projector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface Module extends FortronCost {

    /**
     * Called before the projector projects a field.
     *
     * @param projector
     * @return True to stop projecting.
     */
    boolean onProject(Projector projector, Set<Vec3> field);

    boolean onDestroy(Projector projector, Set<Vec3> field);

    /**
     * Called right after the projector creates a force field block.
     *
     * @param projector
     * @param position
     * @return 0 - Do nothing; 1 - Skip this block and continue; 2 - Cancel rest of projection;
     */

    int onProject(Projector projector, Vec3 position);

    /**
     * Called when an entity collides with a force field block.
     *
     * @return True to stop the default process of entity collision.
     */
    boolean onCollideWithForceField(Level level, int x, int y, int z, Entity entity, ItemStack moduleStack);

    /**
     * Called in this module when it is being calculated by the projector. Called BEFORE
     * transformation is applied to the field.
     *
     * @return False if to prevent this position from being added to the projection que.
     */
    Set<Vec3> onPreCalculate(FieldInteraction projector, Set<Vec3> calculatedField);

    /**
     * Called in this module when it is being calculated by the projector.
     *
     * @return False if to prevent this position from being added to the projection que.
     */
    void onCalculate(FieldInteraction projector, Set<Vec3> fieldDefinition);

    /**
     * @param moduleStack
     * @return Does this module require ticking from the force field projector?
     */
    boolean requireTicks(ItemStack moduleStack);

}

