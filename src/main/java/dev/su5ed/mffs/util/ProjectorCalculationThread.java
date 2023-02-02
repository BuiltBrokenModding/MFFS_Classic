package dev.su5ed.mffs.util;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.module.Module;
import dev.su5ed.mffs.blockentity.ProjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A thread that allows multi-threading calculation of projector fields.
 *
 * @author Calclavia
 */
public class ProjectorCalculationThread extends Thread {
    private final ProjectorBlockEntity projector;
    @Nullable
    private final Runnable callBack;

    public ProjectorCalculationThread(ProjectorBlockEntity projector, @Nullable Runnable callBack) {
        this.projector = projector;
        this.callBack = callBack;
    }

    @Override
    public void run() {
        this.projector.isCalculating = true;

        try {
            Set<Vec3> exteriorPoints = this.projector.getMode().orElseThrow().getExteriorPoints(this.projector);

            BlockPos translation = this.projector.getTranslation();
            int rotationYaw = this.projector.getRotationYaw();
            int rotationPitch = this.projector.getRotationPitch();
            int rotationRoll = this.projector.getRotationRoll();

            StreamEx.of(exteriorPoints)
                .map(pos -> rotationYaw != 0 || rotationPitch != 0 || rotationRoll != 0 ? ModUtil.rotateByAngleVec(pos, rotationYaw, rotationPitch, rotationRoll) : pos)
                .map(pos -> {
                    BlockPos projPos = this.projector.getBlockPos();
                    return pos.add(projPos.getX(), projPos.getY(), projPos.getZ()).add(translation.getX(), translation.getY(), translation.getZ());
                })
                .filter(pos -> pos.y() <= this.projector.getLevel().getHeight())
                .forEach(pos -> this.projector.getCalculatedField().add(new BlockPos(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z))));

            for (Module module : this.projector.getModules()) {
                module.onCalculate(this.projector, this.projector.getCalculatedField());
            }
        } catch (Exception e) {
            MFFSMod.LOGGER.error("Error calculating force field", e);
        }

        this.projector.isCalculating = false;
        this.projector.isCalculated = true;

        if (this.callBack != null) {
            this.callBack.run();
        }
    }
}