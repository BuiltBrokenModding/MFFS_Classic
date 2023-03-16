package dev.su5ed.mffs.api.module;

import dev.su5ed.mffs.api.Projector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;

public interface ProjectorMode {
    /**
     * Called when the force field projector calculates the shape of the module.
     *
     * @param projector - The Projector Object
     */
    Set<Vec3> getExteriorPoints(Projector projector);

    /**
     * @return Gets all interior points. Not translated or rotated.
     */
    Set<Vec3> getInteriorPoints(Projector projector);

    /**
     * @return Is this specific position inside this force field?
     */
    boolean isInField(Projector projector, Vec3 position);
}
