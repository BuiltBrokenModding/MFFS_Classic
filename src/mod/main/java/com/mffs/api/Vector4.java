package com.mffs.api;

import net.minecraft.util.Vec3;

/**
 * Created by pwaln on 5/29/2016.
 */
public class Vector4 extends Vec3 {

    /* The dimension of this vector */
    private int dimension;

    /**
     *
     * @param x The X coordinate of this vector.
     * @param y The Y coordinate of this vector.
     * @param z The Z coordinate of this vector.
     * @param dim The dimension Id associated with this vector.
     */
    public Vector4(double x, double y, double z, int dim) {
        super(x, y, z);
        this.dimension = dim;
    }

    /**
     * Gets the dimension.
     * @return
     */
    public int getDimension() { return dimension;}
}
