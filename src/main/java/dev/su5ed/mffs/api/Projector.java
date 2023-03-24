package dev.su5ed.mffs.api;

import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.api.module.ProjectorMode;
import dev.su5ed.mffs.api.security.BiometricIdentifierLink;
import dev.su5ed.mffs.util.inventory.InventorySlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @author Calclavia
 */
public interface Projector extends ModuleAcceptor, BiometricIdentifierLink {
    BlockEntity be();
    
    BlockState getCachedBlockState(BlockPos pos);

    /**
     * @return Is the projector value?
     */
    boolean isActive();

    /**
     * Gets the mode of the projector, mainly the shape and size of it.
     */
    Optional<ProjectorMode> getMode();

    ItemStack getModeStack();

    /**
     * Transformation information functions. Returns CACHED information unless the cache is cleared.
     */
    BlockPos getTranslation();

    BlockPos getPositiveScale();

    BlockPos getNegativeScale();

    int getRotationYaw();

    int getRotationPitch();

    int getRotationRoll();

    /**
     * Destroys a force field.
     */
    void destroyField();

    /**
     * Gets the unspecified, direction-unspecific module slots on the left side of the GUI.
     */
    Collection<InventorySlot> getUpgradeSlots();

    /**
     * Gets the slot IDs based on the direction given.
     */
    Collection<InventorySlot> getSlotsFromSide(Direction side);

    /**
     * * @return Gets all the blocks that are occupying the force field.
     */
    Collection<TargetPosPair> getCalculatedFieldPositions();

    /**
     * @return The speed in which a force field is constructed.
     */
    int getProjectionSpeed();

    /**
     * Gets the interior points of the projector. This might cause lag so call sparingly.
     */
    Set<BlockPos> getInteriorPoints();

    /**
     * * @return The amount of ticks this projector has existed in the world.
     */
    long getTicks();

    void schedule(int delay, Runnable runnable);

    boolean mergeIntoInventory(ItemStack stack);
}
