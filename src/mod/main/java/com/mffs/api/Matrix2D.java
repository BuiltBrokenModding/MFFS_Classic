package com.mffs.api;

import javax.vecmath.Vector2d;

/**
 * Created by pwaln on 6/2/2016.
 * <p>
 * Create a 2x3 Matrix
 */
public class Matrix2D {

    /* The bottom of this matrix. */
    private Vector2d bottom;

    /* The top of this matrix */
    private Vector2d top;

    public Matrix2D(Vector2d b, Vector2d t) {
        this.bottom = b;
        this.top = t;
    }

    /**
     * Deteremines if we are within the Vector.
     *
     * @param v
     * @return
     */
    public boolean isWithin(Vector2d v) {
        return v.x > bottom.x && v.x < top.x && v.y > bottom.y && v.y < top.y;
    }

    public boolean isWithin(int x, int y) {
        return x > bottom.x && x < top.x && y > bottom.y && y < top.y;
    }
}
