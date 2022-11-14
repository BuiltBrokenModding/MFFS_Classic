package dev.su5ed.mffs.util;

import net.minecraft.core.BlockPos;

public final class CalcUtil {
    public static BlockPos rotateByAngle(BlockPos pos, double yaw, double pitch) {
        return rotateByAngle(pos, yaw, pitch, 0.0D);
    }

    public static BlockPos rotateByAngle(BlockPos pos, double yaw, double pitch, double roll) {
        double yawRadians = Math.toRadians(yaw);
        double pitchRadians = Math.toRadians(pitch);
        double rollRadians = Math.toRadians(roll);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        
        double mulX = x * Math.cos(yawRadians) * Math.cos(pitchRadians) + z * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) - Math.sin(yawRadians) * Math.cos(rollRadians)) + y * (Math.cos(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) + Math.sin(yawRadians) * Math.sin(rollRadians));
        double mulZ = x * Math.sin(yawRadians) * Math.cos(pitchRadians) + z * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.sin(rollRadians) + Math.cos(yawRadians) * Math.cos(rollRadians)) + y * (Math.sin(yawRadians) * Math.sin(pitchRadians) * Math.cos(rollRadians) - Math.cos(yawRadians) * Math.sin(rollRadians));
        double mulY = -x * Math.sin(pitchRadians) + z * Math.cos(pitchRadians) * Math.sin(rollRadians) + y * Math.cos(pitchRadians) * Math.cos(rollRadians);
        
        return new BlockPos(mulX, mulY, mulZ);
    }
    
    private CalcUtil() {}
}
