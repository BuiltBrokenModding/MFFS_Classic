package dev.su5ed.mffs.api.security;

import dev.su5ed.mffs.api.FieldInteraction;
import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.FortronCost;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface ProjectorMode extends FortronCost
{
	/**
	 * Called when the force field projector calculates the shape of the module.
	 * 
	 * @param projector - The Projector Object
	 */
	<T extends BlockEntity & FieldInteraction> Set<Vec3> getExteriorPoints(T projector);

	/**
	 * @return Gets all interior points. Not translated or rotated.
	 */
	Set<Vec3> getInteriorPoints(FieldInteraction projector);

	/**
	 * @return Is this specific position inside this force field?
	 */
	boolean isInField(FieldInteraction projector, Vec3 position);

	/**
	 * Called to render an object in front of the projection.
	 */
	void render(Projector projector, double x, double y, double z, float f, long ticks);
}
