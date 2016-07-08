package com.mffs.api.vector;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class Vector3D
        implements Cloneable {
    public double x;
    public double y;
    public double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D() {
        this(0.0D, 0.0D, 0.0D);
    }

    public Vector3D(Vector3D vector) {
        this(vector.x, vector.y, vector.z);
    }

    public Vector3D(double amount) {
        this(amount, amount, amount);
    }

    public Vector3D(Entity par1) {
        this(par1.posX, par1.posY, par1.posZ);
    }

    public Vector3D(TileEntity par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord);
    }

    public Vector3D(Vec3 par1) {
        this(par1.xCoord, par1.yCoord, par1.zCoord);
    }

    public Vector3D(MovingObjectPosition par1) {
        this(par1.blockX, par1.blockY, par1.blockZ);
    }

    public Vector3D(ChunkCoordinates par1) {
        this(par1.posX, par1.posY, par1.posZ);
    }

    public Vector3D(ForgeDirection direction) {
        this(direction.offsetX, direction.offsetY, direction.offsetZ);
    }

    public Vector3D(NBTTagCompound nbt) {
        this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }

    public Vector3D(float rotationYaw, float rotationPitch) {
        this(Math.cos(Math.toRadians(rotationYaw + 90.0F)), Math.sin(Math.toRadians(-rotationPitch)), Math.sin(Math.toRadians(rotationYaw + 90.0F)));
    }

    public static double distance(TileEntity e, double x, double y, double z) {
        x = e.xCoord - x;
        y = e.yCoord - y;
        z = e.zCoord - y;
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public static Vector3D fromCenter(Entity e) {
        return new Vector3D(e.posX, e.posY - e.yOffset + e.height / 2.0F, e.posZ);
    }

    public static Vector3D fromCenter(TileEntity e) {
        return new Vector3D(e.xCoord + 0.5D, e.yCoord + 0.5D, e.zCoord + 0.5D);
    }

    public static double distance(Vector3D vec1, Vector3D vec2) {
        return vec1.distance(vec2);
    }

    public static Vector3D translate(Vector3D first, Vector3D second) {
        return first.clone().translate(second);
    }

    public static Vector3D translate(Vector3D translate, double addition) {
        return translate.clone().translate(addition);
    }

    public static Vector3D scale(Vector3D vec, double amount) {
        return vec.scale(amount);
    }

    public static Vector3D scale(Vector3D vec, Vector3D amount) {
        return vec.scale(amount);
    }

    public static Vector3D translateMatrix(double[] matrix, Vector3D translation) {
        double x = translation.x * matrix[0] + translation.y * matrix[1] + translation.z * matrix[2] + matrix[3];
        double y = translation.x * matrix[4] + translation.y * matrix[5] + translation.z * matrix[6] + matrix[7];
        double z = translation.x * matrix[8] + translation.y * matrix[9] + translation.z * matrix[10] + matrix[11];
        translation.x = x;
        translation.y = y;
        translation.z = z;
        return translation;
    }

    public static double[] getRotationMatrix(float angle, Vector3D axis) {
        return axis.getRotationMatrix(angle);
    }

    public static Vector3D getDeltaPositionFromRotation(float rotationYaw, float rotationPitch) {
        return new Vector3D(rotationYaw, rotationPitch);
    }

    public static double getAngle(Vector3D vec1, Vector3D vec2) {
        return vec1.getAngle(vec2);
    }

    public static double anglePreNorm(Vector3D vec1, Vector3D vec2) {
        return Math.acos(vec1.clone().dotProduct(vec2));
    }

    public static Vector3D UP() {
        return new Vector3D(0.0D, 1.0D, 0.0D);
    }

    public static Vector3D DOWN() {
        return new Vector3D(0.0D, -1.0D, 0.0D);
    }

    public static Vector3D NORTH() {
        return new Vector3D(0.0D, 0.0D, -1.0D);
    }

    public static Vector3D SOUTH() {
        return new Vector3D(0.0D, 0.0D, 1.0D);
    }

    public static Vector3D WEST() {
        return new Vector3D(-1.0D, 0.0D, 0.0D);
    }

    public static Vector3D EAST() {
        return new Vector3D(1.0D, 0.0D, 0.0D);
    }

    public static Vector3D ZERO() {
        return new Vector3D(0.0D, 0.0D, 0.0D);
    }

    public static Vector3D CENTER() {
        return new Vector3D(0.5D, 0.5D, 0.5D);
    }

    public int intX() {
        return (int) Math.floor(this.x);
    }

    public int intY() {
        return (int) Math.floor(this.y);
    }

    public int intZ() {
        return (int) Math.floor(this.z);
    }

    public float floatX() {
        return (float) this.x;
    }

    public float floatY() {
        return (float) this.y;
    }

    public float floatZ() {
        return (float) this.z;
    }

    public Vector3D clone() {
        return new Vector3D(this);
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(intX(), intY(), intZ());
    }

    public int getBlockMetadata(IBlockAccess world) {
        return world.getBlockMetadata(intX(), intY(), intZ());
    }

    public TileEntity getTileEntity(IBlockAccess world) {
        return world.getTileEntity(intX(), intY(), intZ());
    }

    public boolean setBlock(World world, Block block, int metadata, int notify) {
        return world.setBlock(intX(), intY(), intZ(), block, metadata, notify);
    }

    public boolean setBlock(World world, Block block, int metadata) {
        return setBlock(world, block, metadata, 3);
    }

    public boolean setBlock(World world, Block block) {
        return setBlock(world, block, 0);
    }

    public Vec3 toVec3() {
        return Vec3.createVectorHelper(this.x, this.y, this.z);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setDouble("x", this.x);
        nbt.setDouble("y", this.y);
        nbt.setDouble("z", this.z);
        return nbt;
    }

    public ForgeDirection toForgeDirection() {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
            if ((this.x == direction.offsetX) && (this.y == direction.offsetY) && (this.z == direction.offsetZ)) {
                return direction;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    public double getMagnitude() {
        return Math.sqrt(getMagnitudeSquared());
    }

    public double getMagnitudeSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3D normalize() {
        double d = getMagnitude();
        if (d != 0.0D) {
            scale(1.0D / d);
        }
        return this;
    }

    public double distance(double x, double y, double z) {
        Vector3D difference = clone().difference(x, y, z);
        return difference.getMagnitude();
    }

    public double distance(Vector3D compare) {
        return distance(compare.x, compare.y, compare.z);
    }

    public double distance(Entity entity) {
        return distance(entity.posX, entity.posY, entity.posZ);
    }

    public Vector3D invert() {
        scale(-1.0D);
        return this;
    }

    public Vector3D translate(ForgeDirection side, double amount) {
        return translate(new Vector3D(side).scale(amount));
    }

    public Vector3D translate(ForgeDirection side) {
        return translate(side, 1.0D);
    }

    @Deprecated
    public Vector3D modifyPositionFromSide(ForgeDirection side, double amount) {
        return translate(side, amount);
    }

    @Deprecated
    public Vector3D modifyPositionFromSide(ForgeDirection side) {
        return translate(side);
    }

    public Vector3D translate(Vector3D addition) {
        this.x += addition.x;
        this.y += addition.y;
        this.z += addition.z;
        return this;
    }

    public Vector3D translate(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3D translate(double addition) {
        this.x += addition;
        this.y += addition;
        this.z += addition;
        return this;
    }

    public Vector3D add(Vector3D amount) {
        return translate(amount);
    }

    public Vector3D add(double amount) {
        return translate(amount);
    }

    public Vector3D subtract(Vector3D amount) {
        return translate(amount.clone().invert());
    }

    public Vector3D subtract(double amount) {
        return translate(-amount);
    }

    public Vector3D subtract(double x, double y, double z) {
        return difference(x, y, z);
    }

    public Vector3D difference(Vector3D amount) {
        return translate(amount.clone().invert());
    }

    public Vector3D difference(double amount) {
        return translate(-amount);
    }

    public Vector3D difference(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3D scale(double amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
        return this;
    }

    public Vector3D scale(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector3D scale(Vector3D amount) {
        this.x *= amount.x;
        this.y *= amount.y;
        this.z *= amount.z;
        return this;
    }

    public Vector3D max(Vector3D other) {
        return new Vector3D(Math.max(this.x, other.x), Math.max(this.y, other.y), Math.max(this.z, other.z));
    }

    public Vector3D min(Vector3D other) {
        return new Vector3D(Math.min(this.x, other.x), Math.min(this.y, other.y), Math.min(this.z, other.z));
    }

    public Vector3D round() {
        return new Vector3D(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3D ceil() {
        return new Vector3D(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
    }

    public Vector3D floor() {
        return new Vector3D(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public Vector3D toRound() {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);
        return this;
    }

    public Vector3D toCeil() {
        this.x = Math.ceil(this.x);
        this.y = Math.ceil(this.y);
        this.z = Math.ceil(this.z);
        return this;
    }

    public Vector3D toFloor() {
        this.x = Math.floor(this.x);
        this.y = Math.floor(this.y);
        this.z = Math.floor(this.z);
        return this;
    }

    public List<Entity> getEntitiesWithin(World worldObj, Class<? extends Entity> par1Class) {
        return worldObj.getEntitiesWithinAABB(par1Class, AxisAlignedBB.getBoundingBox(intX(), intY(), intZ(), intX() + 1, intY() + 1, intZ() + 1));
    }

    public Vector3D midPoint(Vector3D pos) {
        return new Vector3D((this.x + pos.x) / 2.0D, (this.y + pos.y) / 2.0D, (this.z + pos.z) / 2.0D);
    }

    public Vector3D toCrossProduct(Vector3D compare) {
        double newX = this.y * compare.z - this.z * compare.y;
        double newY = this.z * compare.x - this.x * compare.z;
        double newZ = this.x * compare.y - this.y * compare.x;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    public Vector3D crossProduct(Vector3D compare) {
        return clone().toCrossProduct(compare);
    }

    public Vector3D xCrossProduct() {
        return new Vector3D(0.0D, this.z, -this.y);
    }

    public Vector3D zCrossProduct() {
        return new Vector3D(-this.y, this.x, 0.0D);
    }

    public double dotProduct(Vector3D vec2) {
        return this.x * vec2.x + this.y * vec2.y + this.z * vec2.z;
    }

    public Vector3D getPerpendicular() {
        if (this.z == 0.0D) {
            return zCrossProduct();
        }
        return xCrossProduct();
    }

    public boolean isZero() {
        return equals(ZERO());
    }

    public Vector3D rotate(float angle, Vector3D axis) {
        return translateMatrix(getRotationMatrix(angle, axis), this);
    }

    public double[] getRotationMatrix(float angle) {
        double[] matrix = new double[16];
        Vector3D axis = clone().normalize();
        double x = axis.x;
        double y = axis.y;
        double z = axis.z;
        angle = (float) (angle * 0.0174532925D);
        float cos = (float) Math.cos(angle);
        float ocos = 1.0F - cos;
        float sin = (float) Math.sin(angle);
        matrix[0] = (x * x * ocos + cos);
        matrix[1] = (y * x * ocos + z * sin);
        matrix[2] = (x * z * ocos - y * sin);
        matrix[4] = (x * y * ocos - z * sin);
        matrix[5] = (y * y * ocos + cos);
        matrix[6] = (y * z * ocos + x * sin);
        matrix[8] = (x * z * ocos + y * sin);
        matrix[9] = (y * z * ocos - x * sin);
        matrix[10] = (z * z * ocos + cos);
        matrix[15] = 1.0D;
        return matrix;
    }

    public void rotate(double yaw, double pitch, double roll) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);
        double rollRadians = Math.toRadians(roll);

        double x = this.x;
        double y = this.y;
        double z = this.z;

        this.x = (x * Math.cos(yawRadians) * Math.cos(pitchRadians) + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians)) + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians)));
        this.z = (x * Math.sin(yawRadians) * Math.cos(pitchRadians) + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians)) + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians)));
        this.y = (-x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians) + y * Math.cos(pitchRadians) * Math.cos(rollRadians));
    }

    public void rotate(double yaw, double pitch) {
        rotate(yaw, pitch, 0.0D);
    }

    public void rotate(double yaw) {
        double yawRadians = Math.toRadians(yaw);

        double x = this.x;
        double z = this.z;
        if (yaw != 0.0D) {
            this.x = (x * Math.cos(yawRadians) - z * Math.sin(yawRadians));
            this.z = (x * Math.sin(yawRadians) + z * Math.cos(yawRadians));
        }
    }

    public double getAngle(Vector3D vec2) {
        return anglePreNorm(clone().normalize(), vec2.clone().normalize());
    }

    public double anglePreNorm(Vector3D vec2) {
        return Math.acos(dotProduct(vec2));
    }

    public MovingObjectPosition rayTrace(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
        Vector3D lookVector = getDeltaPositionFromRotation(rotationYaw, rotationPitch);
        Vector3D reachPoint = clone().translate(lookVector.clone().scale(reachDistance));
        return rayTrace(world, reachPoint, collisionFlag);
    }

    public MovingObjectPosition rayTrace(World world, Vector3D reachPoint, boolean collisionFlag) {
        MovingObjectPosition pickedBlock = rayTraceBlocks(world, reachPoint.clone(), collisionFlag);
        MovingObjectPosition pickedEntity = rayTraceEntities(world, reachPoint.clone());
        if (pickedBlock == null) {
            return pickedEntity;
        }
        if (pickedEntity == null) {
            return pickedBlock;
        }
        double dBlock = distance(new Vector3D(pickedBlock.hitVec));
        double dEntity = distance(new Vector3D(pickedEntity.hitVec));
        if (dEntity < dBlock) {
            return pickedEntity;
        }
        return pickedBlock;
    }

    public MovingObjectPosition rayTrace(World world, boolean collisionFlag, double reachDistance) {
        return rayTrace(world, 0.0F, 0.0F, collisionFlag, reachDistance);
    }

    public MovingObjectPosition rayTraceBlocks(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
        Vector3D lookVector = getDeltaPositionFromRotation(rotationYaw, rotationPitch);
        Vector3D reachPoint = clone().translate(lookVector.clone().scale(reachDistance));
        return rayTraceBlocks(world, reachPoint, collisionFlag);
    }

    public MovingObjectPosition rayTraceBlocks(World world, Vector3D vec, boolean collisionFlag) {
        return world.rayTraceBlocks(toVec3(), vec.toVec3(), collisionFlag);
    }

    @Deprecated
    public MovingObjectPosition rayTraceEntities(World world, float rotationYaw, float rotationPitch, boolean collisionFlag, double reachDistance) {
        return rayTraceEntities(world, rotationYaw, rotationPitch, reachDistance);
    }

    public MovingObjectPosition rayTraceEntities(World world, float rotationYaw, float rotationPitch, double reachDistance) {
        return rayTraceEntities(world, getDeltaPositionFromRotation(rotationYaw, rotationPitch).scale(reachDistance));
    }

    public MovingObjectPosition rayTraceEntities(World world, Vector3D target) {
        MovingObjectPosition pickedEntity = null;
        Vec3 startingPosition = toVec3();
        Vec3 look = target.toVec3();
        double reachDistance = distance(target);
        Vec3 reachPoint = Vec3.createVectorHelper(startingPosition.xCoord + look.xCoord * reachDistance, startingPosition.yCoord + look.yCoord * reachDistance, startingPosition.zCoord + look.zCoord * reachDistance);

        double checkBorder = 1.1D * reachDistance;
        AxisAlignedBB boxToScan = AxisAlignedBB.getBoundingBox(-checkBorder, -checkBorder, -checkBorder, checkBorder, checkBorder, checkBorder).offset(this.x, this.y, this.z);

        List<Entity> entitiesHit = world.getEntitiesWithinAABBExcludingEntity(null, boxToScan);
        double closestEntity = reachDistance;
        if ((entitiesHit == null) || (entitiesHit.isEmpty())) {
            return null;
        }
        for (Entity entityHit : entitiesHit) {
            if ((entityHit != null) && (entityHit.canBeCollidedWith()) && (entityHit.boundingBox != null)) {
                float border = entityHit.getCollisionBorderSize();
                AxisAlignedBB aabb = entityHit.boundingBox.expand(border, border, border);
                MovingObjectPosition hitMOP = aabb.calculateIntercept(startingPosition, reachPoint);
                if (hitMOP != null) {
                    if (aabb.isVecInside(startingPosition)) {
                        if ((0.0D < closestEntity) || (closestEntity == 0.0D)) {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            if (pickedEntity != null) {
                                pickedEntity.hitVec = hitMOP.hitVec;
                                closestEntity = 0.0D;
                            }
                        }
                    } else {
                        double distance = startingPosition.distanceTo(hitMOP.hitVec);
                        if ((distance < closestEntity) || (closestEntity == 0.0D)) {
                            pickedEntity = new MovingObjectPosition(entityHit);
                            pickedEntity.hitVec = hitMOP.hitVec;
                            closestEntity = distance;
                        }
                    }
                }
            }
        }
        return pickedEntity;
    }

    public MovingObjectPosition rayTraceEntities(World world, Entity target) {
        return rayTraceEntities(world, new Vector3D(target));
    }

    public int hashCode() {
        long x = Double.doubleToLongBits(this.x);
        long y = Double.doubleToLongBits(this.y);
        long z = Double.doubleToLongBits(this.z);
        int hash = (int) (x ^ x >>> 32);
        hash = 31 * hash + (int) (y ^ y >>> 32);
        hash = 31 * hash + (int) (z ^ z >>> 32);
        return hash;
    }

    public boolean equals(Object o) {
        if ((o instanceof Vector3D)) {
            Vector3D vector3D = (Vector3D) o;
            return (this.x == vector3D.x) && (this.y == vector3D.y) && (this.z == vector3D.z);
        }
        return false;
    }

    public String toString() {
        return "Vector3D [" + this.x + "," + this.y + "," + this.z + "]";
    }
}
