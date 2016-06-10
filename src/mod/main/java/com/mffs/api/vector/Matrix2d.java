package com.mffs.api.vector;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.vec.Vector3;
import com.mffs.api.utils.Util;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class Matrix2d
{
    public Vector3 min;
    public Vector3 max;

    public Matrix2d()
    {
        this(new Vector3(), new Vector3());
    }

    public Matrix2d(Vector3 min, Vector3 max)
    {
        this.min = min;
        this.max = max;
    }

    public Matrix2d(AxisAlignedBB aabb)
    {
        this.min = new Vector3(aabb.minX, aabb.minY, aabb.minZ);
        this.max = new Vector3(aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public AxisAlignedBB toAABB()
    {
        return AxisAlignedBB.getBoundingBox(this.min.x, this.min.y, this.min.z, this.max.x, this.max.y, this.max.z);
    }

    public boolean isIn(Vector3 point)
    {
        return (point.x > this.min.x) && (point.x < this.max.x) && (point.y > this.min.y) && (point.y < this.max.y) && (point.z > this.min.z) && (point.z < this.max.z);
    }

    public boolean isIn(Matrix2d region)
    {
        return (region.max.z > this.min.z) && (region.min.z < this.max.z);
    }

    public boolean isIn(double xP, double zP) {
        return (xP >= this.min.x) && (zP <= this.max.z)
                && xP <= this.max.x && zP >= min.z;
    }

    public void expand(Vector3 difference)
    {
        this.min.subtract(difference);
        this.max.add(difference);
    }

    public List<Vector3> getVectors()
    {
        List<Vector3> vectors = new ArrayList();
        for (int x = (int) Math.floor(this.min.x); x < (int) Math.floor(this.max.x); x++) {
            for (int y = (int) Math.floor(this.min.y); x < (int) Math.floor(this.max.y); y++) {
                for (int z = (int) Math.floor(this.min.z); x < (int) Math.floor(this.max.z); z++) {
                    vectors.add(new Vector3(x, y, z));
                }
            }
        }
        return vectors;
    }

    public List<Vector3> getVectors(Vector3 center, int radius)
    {
        List<Vector3> vectors = new ArrayList();
        for (int x = (int) Math.floor(this.min.x); x < (int) Math.floor(this.max.x); x++) {
            for (int y = (int) Math.floor(this.min.y); x < (int) Math.floor(this.max.y); y++) {
                for (int z = (int) Math.floor(this.min.z); x < (int) Math.floor(this.max.z); z++)
                {
                    Vector3 vector3 = new Vector3(x, y, z);
                    if (Util.getDist(center, vector3)<= radius) {
                        vectors.add(vector3);
                    }
                }
            }
        }
        return vectors;
    }

    public List<Entity> getEntities(World world, Class<? extends Entity> entityClass)
    {
        return world.getEntitiesWithinAABB(entityClass, toAABB());
    }

    public List<Entity> getEntitiesExlude(World world, Entity entity)
    {
        return world.getEntitiesWithinAABBExcludingEntity(entity, toAABB());
    }

    public List<Entity> getEntities(World world)
    {
        return getEntities(world, Entity.class);
    }
}
