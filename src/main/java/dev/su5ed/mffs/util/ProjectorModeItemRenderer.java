package dev.su5ed.mffs.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface ProjectorModeItemRenderer {
    void render(PoseStack poseStack, MultiBufferSource bufferSource, long ticks);
}
