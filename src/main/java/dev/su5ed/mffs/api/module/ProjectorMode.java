package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.api.Projector;
import dev.su5ed.mffs.api.module.FortronCost;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public interface ProjectorMode
{
	/**
	 * Called when the force field projector calculates the shape of the module.
	 * 
	 * @param projector - The Projector Object
	 */
	<T extends BlockEntity & Projector> Set<BlockPos> getExteriorPoints(T projector);

	/**
	 * @return Gets all interior points. Not translated or rotated.
	 */
	Set<BlockPos> getInteriorPoints(Projector projector);

	/**
	 * @return Is this specific position inside this force field?
	 */
	boolean isInField(Projector projector, BlockPos position);

	/**
	 * Called to render an object in front of the projection.
	 */
	void render(Projector projector, BlockPos pos, float f, long ticks);
}
