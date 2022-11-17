package dev.su5ed.mffs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class HashBlockPos extends BlockPos {

    public HashBlockPos(Vec3i pVector) {
        super(pVector);
    }

    @Override
    public int hashCode() {
        return ("X:" + (double) getX() + "Y:" + (double) getY() + "Z:" + (double) getZ()).hashCode();
    }
}
