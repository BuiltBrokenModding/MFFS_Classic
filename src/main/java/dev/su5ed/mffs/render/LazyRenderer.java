package dev.su5ed.mffs.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface LazyRenderer {
    void render(PoseStack poseStack, VertexConsumer buffer, int ticks, float partialTick);

    @Nullable
    Vec3 centerPos();
}
