package com.builtbroken.mffs.api.vector;

import com.builtbroken.jlib.data.vector.IPos3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Matrix2d
{
    public Vector3D min;
    public Vector3D max;

    public Matrix2d()
    {
        this(new Vector3D(), new Vector3D());
    }

    public Matrix2d(double minX, double minY, double size)
    {
        this.min = new Vector3D(minX, minY, 0);
        this.max = new Vector3D(minX + size, minY + size, 0);
    }

    public Matrix2d(Vector2d min, double radius)
    {
        this.min = new Vector3D(min.x, min.y, 0);
        this.max = new Vector3D(min.x + radius, min.y + radius, 0);
    }

    public Matrix2d(IPos3D min, IPos3D max)
    {
        this.min = min instanceof Vector3D ? (Vector3D) min : new Vector3D(min);
        this.max = max instanceof Vector3D ? (Vector3D) max : new Vector3D(max);
    }

    public Matrix2d(Vector2d min, Vector2d max)
    {
        this.min = new Vector3D(min.x, min.y, 0);
        this.max = new Vector3D(max.x, max.y, 0);
    }

    public Matrix2d(AxisAlignedBB aabb)
    {
        this.min = new Vector3D(aabb.minX, aabb.minY, aabb.minZ);
        this.max = new Vector3D(aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public AxisAlignedBB toAABB()
    {
        return AxisAlignedBB.getBoundingBox(this.min.x, this.min.y, this.min.z, this.max.x, this.max.y, this.max.z);
    }

    public boolean isIn(Vector3D point)
    {
        return (point.x > this.min.x) && (point.x < this.max.x) && (point.y > this.min.y) && (point.y < this.max.y) && (point.z > this.min.z) && (point.z < this.max.z);
    }

    public boolean isIn(Matrix2d region)
    {
        return (region.max.z > this.min.z) && (region.min.z < this.max.z);
    }

    public boolean isIn(double xP, double zP)
    {
        return (xP >= this.min.x) && (zP <= this.max.y)
                && xP <= this.max.x && zP >= min.y;
    }

    public void expand(Vector3D difference)
    {
        this.min.subtract(difference);
        this.max.add(difference);
    }

    public List<Vector3D> getVectors()
    {
        List<Vector3D> vectors = new ArrayList();
        for (int x = (int) Math.floor(this.min.x); x < (int) Math.floor(this.max.x); x++)
        {
            for (int y = (int) Math.floor(this.min.y); x < (int) Math.floor(this.max.y); y++)
            {
                for (int z = (int) Math.floor(this.min.z); x < (int) Math.floor(this.max.z); z++)
                {
                    vectors.add(new Vector3D(x, y, z));
                }
            }
        }
        return vectors;
    }

    public List<Vector3D> getVectors(Vector3D center, int radius)
    {
        List<Vector3D> vectors = new ArrayList();
        for (int x = (int) Math.floor(this.min.x); x < (int) Math.floor(this.max.x); x++)
        {
            for (int y = (int) Math.floor(this.min.y); x < (int) Math.floor(this.max.y); y++)
            {
                for (int z = (int) Math.floor(this.min.z); x < (int) Math.floor(this.max.z); z++)
                {
                    Vector3D vec = new Vector3D(x, y, z);
                    if (vec.distance(x, y, z) <= radius)
                    {
                        vectors.add(vec);
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
