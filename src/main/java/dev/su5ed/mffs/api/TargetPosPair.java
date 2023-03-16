package dev.su5ed.mffs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public record TargetPosPair(BlockPos pos, Vec3 original) {}
