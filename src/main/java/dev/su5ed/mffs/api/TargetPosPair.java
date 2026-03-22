package dev.su5ed.mffs.api;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Desugar
public record TargetPosPair(BlockPos pos, Vec3d original) {}
