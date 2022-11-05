package dev.su5ed.mffs.api.module;

public interface Module extends FortronCost {

    /**
     * Called before the projector projects a field.
     *
     * @param projector
     * @return True to stop projecting.
     */
    boolean onProject(IProjector projector, Set<Vector3> field);

    boolean onDestroy(IProjector projector, Set<Vector3> field);

    /**
     * Called right after the projector creates a force field block.
     *
     * @param projector
     * @param position
     * @return 0 - Do nothing; 1 - Skip this block and continue; 2 - Cancel rest of projection;
     */

    int onProject(IProjector projector, Vector3 position);

    /**
     * Called when an entity collides with a force field block.
     *
     * @return True to stop the default process of entity collision.
     */
    boolean onCollideWithForceField(World world, int x, int y, int z, Entity entity, ItemStack moduleStack);

    /**
     * Called in this module when it is being calculated by the projector. Called BEFORE
     * transformation is applied to the field.
     *
     * @return False if to prevent this position from being added to the projection que.
     */
    Set<Vector3> onPreCalculate(IFieldInteraction projector, Set<Vector3> calculatedField);

    /**
     * Called in this module when it is being calculated by the projector.
     *
     * @return False if to prevent this position from being added to the projection que.
     */
    void onCalculate(IFieldInteraction projector, Set<Vector3> fieldDefinition);

    /**
     * @param moduleStack
     * @return Does this module require ticking from the force field projector?
     */
    boolean requireTicks(ItemStack moduleStack);

}

