/*
 * Copyright (c) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package dev.su5ed.mffs.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Modified version of DarkKronicle's BetterBlockOutline renderer.<br>
 * <b>SOURCE</b>: <a href="https://github.com/DarkKronicle/BetterBlockOutline">DarkKronicle's BetterBlockOutline</a><br>
 * <ul>
 *     <li>
 *         <a href="https://github.com/DarkKronicle/BetterBlockOutline/blob/607b0c3b280af1516a91aac41457c6cf0abf3508/src/main/java/io/github/darkkronicle/betterblockoutline/renderers/BasicOutlineRenderer.java">BasicOutlineRenderer</a>
 *     </li>
 *     <li>
 *         <a href="https://github.com/DarkKronicle/BetterBlockOutline/blob/607b0c3b280af1516a91aac41457c6cf0abf3508/src/main/java/io/github/darkkronicle/betterblockoutline/util/RenderingUtil.java">RenderingUtil</a>
 *     </li>
 * </ul>
 */
public final class BlockHighlighter {
    private static final float OUTLINE_WIDTH = 1.0F;
    private static final Color OUTLINE_COLOR = new Color(1, 1, 1, 0.5F);
    public static final Color LIGHT_GREEN = new Color(0, 1, 0, 0.15F);
    public static final Color LIGHT_RED = new Color(1, 0, 0, 0.25F);

    public static void highlightBlock(PoseStack pose, Vec3 cameraPos, BlockPos pos, Color color) {
        highlightArea(pose, cameraPos, Shapes.create(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), color);
    }

    public static void highlightArea(PoseStack pose, Vec3 cameraPos, BlockPos from, BlockPos to) {
        AABB area = AABB.encapsulatingFullBlocks(from, to);
        VoxelShape shape = Shapes.create(area);
        highlightArea(pose, cameraPos, shape, null);
    }

    public static void highlightArea(PoseStack pose, Vec3 cameraPos, VoxelShape shape, @Nullable Color fillColor) {
        Tesselator tessellator = Tesselator.getInstance();

        // Setup rendering
        RenderSystem.setShader(CoreShaders.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.disableDepthTest(); // See through
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        pose.pushPose();
        pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        // Allow glass and other translucent/transparent objects to render properly
        if (fillColor != null) {
            drawOutlineBoxes(tessellator, pose, fillColor, shape);
        }
        drawOutlineLines(tessellator, pose, OUTLINE_COLOR, shape);
        pose.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    /**
     * Draws boxes for an outline. Depth and blending should be set before this is called.
     */
    private static void drawOutlineBoxes(Tesselator tessellator, PoseStack matrices, Color color, VoxelShape outline) {
        PoseStack.Pose entry = matrices.last();

        BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // Divide into each edge and draw all of them
        outline.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            // Fix Z fighting
            minX -= .001;
            minY -= .001;
            minZ -= .001;
            maxX += .001;
            maxY += .001;
            maxZ += .001;
            drawBox(entry, buffer, (float) minX, (float) minY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, color);
        });
        MeshData data = buffer.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
    }

    /**
     * Renders an outline, sets shader and smooth lines.
     * Before calling blend and depth should be set
     */
    private static void drawOutlineLines(Tesselator tessellator, PoseStack matrices, Color color, VoxelShape outline) {
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.setShader(CoreShaders.RENDERTYPE_LINES);
        RenderSystem.lineWidth(OUTLINE_WIDTH);

        BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        drawOutlineLine(matrices.last(), buffer, color, outline);

        // Revert some changes
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    /**
     * Draws an outline. Setup should be done before this method is called.
     */
    private static void drawOutlineLine(PoseStack.Pose entry, BufferBuilder buffer, Color color, VoxelShape outline) {
        outline.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            // Fix Z fighting
            minX -= .001;
            minY -= .001;
            minZ -= .001;
            maxX += .001;
            maxY += .001;
            maxZ += .001;
            drawLine(entry, buffer, new Vector3d(minX, minY, minZ), new Vector3d(maxX, maxY, maxZ), color);
        });
        MeshData data = buffer.build();
        if (data != null) {
            BufferUploader.drawWithShader(data);
        }
    }

    private static void drawBox(PoseStack.Pose entry, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Color color) {
        Matrix4f position = entry.pose();

        float r = color.red();
        float g = color.green();
        float b = color.blue();
        float a = color.alpha();

        // West
        buffer.addVertex(position, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, maxY, minZ).setColor(r, g, b, a);

        // East
        buffer.addVertex(position, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, maxZ).setColor(r, g, b, a);

        // North
        buffer.addVertex(position, maxX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, minZ).setColor(r, g, b, a);

        // South
        buffer.addVertex(position, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, maxY, maxZ).setColor(r, g, b, a);

        // Top
        buffer.addVertex(position, minX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, maxY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, maxY, minZ).setColor(r, g, b, a);

        // Bottom
        buffer.addVertex(position, maxX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, minY, maxZ).setColor(r, g, b, a);
        buffer.addVertex(position, minX, minY, minZ).setColor(r, g, b, a);
        buffer.addVertex(position, maxX, minY, minZ).setColor(r, g, b, a);
    }

    /**
     * Gets the normal line from a starting and ending point.
     *
     * @param start Starting point
     * @param end   Ending point
     * @return Normal line
     */
    private static Vector3d getNormalAngle(Vector3d start, Vector3d end) {
        double xLength = end.x - start.x;
        double yLength = end.y - start.y;
        double zLength = end.z - start.z;
        double distance = Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
        xLength /= distance;
        yLength /= distance;
        zLength /= distance;
        return new Vector3d(xLength, yLength, zLength);
    }

    /**
     * This method doesn't do any of the {@link RenderSystem} setting up. Should be setup before call.
     *
     * @param entry  Matrix entry
     * @param buffer Buffer builder that is already setup
     * @param start  Starting point
     * @param end    Ending point
     * @param color  Color to render
     */
    private static void drawLine(PoseStack.Pose entry, BufferBuilder buffer, Vector3d start, Vector3d end, Color color) {
        Vector3d normal = getNormalAngle(start, end);
        float red = color.red();
        float green = color.green();
        float blue = color.blue();
        float alpha = color.alpha();

        buffer.addVertex(entry.pose(), (float) start.x, (float) start.y, (float) start.z)
            .setColor(red, green, blue, alpha)
            .setNormal(entry, (float) normal.x, (float) normal.y, (float) normal.z);

        buffer.addVertex(entry.pose(), (float) end.x, (float) end.y, (float) end.z)
            .setColor(red, green, blue, alpha)
            .setNormal(entry, (float) normal.x, (float) normal.y, (float) normal.z);
    }

    public record Color(float red, float green, float blue, float alpha) {
        public Color withAlpha(float alpha) {
            return new Color(this.red, this.green, this.blue, alpha);
        }
    }

    private BlockHighlighter() {}
}

