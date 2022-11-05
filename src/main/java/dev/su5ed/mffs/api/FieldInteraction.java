package dev.su5ed.mffs.api;

import dev.su5ed.mffs.api.module.ModuleAcceptor;
import dev.su5ed.mffs.api.security.ProjectorMode;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface FieldInteraction extends ModuleAcceptor, Activatable
{
	/**
	 * Gets the mode of the projector, mainly the shape and size of it.
	 */
	ProjectorMode getMode();

	ItemStack getModeStack();

	/**
	 * Gets the slot IDs based on the direction given.
	 */
	int[] getSlotsBasedOnDirection(Direction direction);

	/**
	 * Gets the unspecified, direction-unspecific module slots on the left side of the GUI.
	 */
	int[] getModuleSlots();

	/**
	 * @param module - The module instance.
	 * @param direction - The direction facing.
	 * @return Gets the amount of modules based on the side.
	 */
	int getSidedModuleCount(Module module, Direction... direction);

	/**
	 * Transformation information functions. Returns CACHED information unless the cache is cleared.
	 * Note that these are all RELATIVE to the projector's position.
	 */
	Vec3 getTranslation();

	Vec3 getPositiveScale();

	Vec3 getNegativeScale();

	int getRotationYaw();

	int getRotationPitch();

	/**
	 * * @return Gets all the absolute block coordinates that are occupying the force field.
	 */
	Set<Vec3> getCalculatedField();

	/**
	 * Gets the absolute interior points of the projector. This might cause lag so call sparingly.
	 */
	Set<Vec3> getInteriorPoints();

	/**
	 * Force field calculation flags.
	 */
	void setCalculating(boolean bool);

	void setCalculated(boolean bool);

	/**
	 * @return Gets the facing direction. Always returns the front side of the block.
	 */
	Direction getDirection();
}
