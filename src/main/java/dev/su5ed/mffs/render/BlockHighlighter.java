/*
 * Copyright (c) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package dev.su5ed.mffs.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 1.12.2 backport of BlockHighlighter.
 *
 * Modified version of DarkKronicle's BetterBlockOutline renderer.
 * SOURCE: https://github.com/DarkKronicle/BetterBlockOutline
 */
public final class BlockHighlighter {
    private static final float OUTLINE_WIDTH = 1.0F;
    private static final Color OUTLINE_COLOR = new Color(1, 1, 1, 0.5F);
    public static final Color LIGHT_GREEN = new Color(0, 1, 0, 0.15F);
    public static final Color LIGHT_RED   = new Color(1, 0, 0, 0.25F);

    /**
     * Highlight a single block position.
     *
     * @param partialTick render interpolation factor [0, 1)
     * @param pos         block to highlight
     * @param color       fill color (also used for outline base hue)
     */
    public static void highlightBlock(float partialTick, BlockPos pos, Color color) {
        AxisAlignedBB area = new AxisAlignedBB(pos);
        double[] cam = getCameraPos(partialTick);
        highlightAreaInternal(cam[0], cam[1], cam[2], area, color);
    }

    /**
     * Highlight the region between two block positions (no fill – outline only).
     *
     * @param partialTick render interpolation factor
     * @param from        first corner
     * @param to          second corner (inclusive)
     */
    public static void highlightArea(float partialTick, BlockPos from, BlockPos to) {
        double[] cam = getCameraPos(partialTick);
        double minX = Math.min(from.getX(), to.getX());
        double minY = Math.min(from.getY(), to.getY());
        double minZ = Math.min(from.getZ(), to.getZ());
        double maxX = Math.max(from.getX(), to.getX()) + 1;
        double maxY = Math.max(from.getY(), to.getY()) + 1;
        double maxZ = Math.max(from.getZ(), to.getZ()) + 1;
        highlightAreaInternal(cam[0], cam[1], cam[2], new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ), null);
    }

    /**
     * Highlight an arbitrary AABB with an optional fill colour.
     * Pass {@code null} for {@code fillColor} to draw the outline only.
     */
    public static void highlightArea(float partialTick, AxisAlignedBB area, @Nullable Color fillColor) {
        double[] cam = getCameraPos(partialTick);
        highlightAreaInternal(cam[0], cam[1], cam[2], area, fillColor);
    }

    /**
     * Pre-compute contour edges from a set of block positions so they can be rendered
     * every frame without re-doing the expensive neighbor/toggle logic.
     */
    public static PreparedOutline prepare(Collection<BlockPos> blocks) {
        if (blocks.isEmpty()) return PreparedOutline.EMPTY;

        Set<Long> occupied = new HashSet<>(blocks.size());
        for (BlockPos pos : blocks) {
            occupied.add(pos.toLong());
        }

        Set<Segment> edgesDown  = new HashSet<>();
        Set<Segment> edgesUp    = new HashSet<>();
        Set<Segment> edgesNorth = new HashSet<>();
        Set<Segment> edgesSouth = new HashSet<>();
        Set<Segment> edgesWest  = new HashSet<>();
        Set<Segment> edgesEast  = new HashSet<>();

        for (BlockPos pos : blocks) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            if (!occupied.contains(new BlockPos(x, y - 1, z).toLong())) toggleFaceEdgesDown(edgesDown, x, y, z);
            if (!occupied.contains(new BlockPos(x, y + 1, z).toLong())) toggleFaceEdgesUp(edgesUp, x, y, z);
            if (!occupied.contains(new BlockPos(x, y, z - 1).toLong())) toggleFaceEdgesNorth(edgesNorth, x, y, z);
            if (!occupied.contains(new BlockPos(x, y, z + 1).toLong())) toggleFaceEdgesSouth(edgesSouth, x, y, z);
            if (!occupied.contains(new BlockPos(x - 1, y, z).toLong())) toggleFaceEdgesWest(edgesWest, x, y, z);
            if (!occupied.contains(new BlockPos(x + 1, y, z).toLong())) toggleFaceEdgesEast(edgesEast, x, y, z);
        }

        Set<Segment> contour = new HashSet<>();
        contour.addAll(edgesDown);
        contour.addAll(edgesUp);
        contour.addAll(edgesNorth);
        contour.addAll(edgesSouth);
        contour.addAll(edgesWest);
        contour.addAll(edgesEast);

        return new PreparedOutline(Collections.unmodifiableList(new ArrayList<>(contour)));
    }

    /**
     * Render a pre-computed outline. All the expensive contour logic was already done
     * by {@link #prepare(Collection)} — this only submits GL vertices.
     */
    public static void renderPrepared(float partialTick, PreparedOutline outline, @Nullable Color fillColor) {
        if (outline.edges.isEmpty()) return;

        double[] cam = getCameraPos(partialTick);
        double camX = cam[0];
        double camY = cam[1];
        double camZ = cam[2];

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(OUTLINE_WIDTH);

        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        float r = OUTLINE_COLOR.red(), g = OUTLINE_COLOR.green(), b = OUTLINE_COLOR.blue(), a = OUTLINE_COLOR.alpha();
        for (Segment edge : outline.edges) {
            buf.pos(edge.x1 - camX, edge.y1 - camY, edge.z1 - camZ).color(r, g, b, a).endVertex();
            buf.pos(edge.x2 - camX, edge.y2 - camY, edge.z2 - camZ).color(r, g, b, a).endVertex();
        }
        tess.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Pre-computed contour edge data for a voxel shape. Created once via
     * {@link #prepare(Collection)}, then rendered cheaply every frame via
     * {@link #renderPrepared(float, PreparedOutline, Color)}.
     */
    public static final class PreparedOutline {
        static final PreparedOutline EMPTY = new PreparedOutline(Collections.emptyList());

        private final List<Segment> edges;

        private PreparedOutline(List<Segment> edges) {
            this.edges = edges;
        }

        public boolean isEmpty() {
            return this.edges.isEmpty();
        }
    }

    private static void toggleFaceEdgesDown(Set<Segment> set, int x, int y, int z) {
        toggleSegment(set, new Segment(x, y, z, x + 1, y, z));
        toggleSegment(set, new Segment(x + 1, y, z, x + 1, y, z + 1));
        toggleSegment(set, new Segment(x + 1, y, z + 1, x, y, z + 1));
        toggleSegment(set, new Segment(x, y, z + 1, x, y, z));
    }

    private static void toggleFaceEdgesUp(Set<Segment> set, int x, int y, int z) {
        int yy = y + 1;
        toggleSegment(set, new Segment(x, yy, z, x + 1, yy, z));
        toggleSegment(set, new Segment(x + 1, yy, z, x + 1, yy, z + 1));
        toggleSegment(set, new Segment(x + 1, yy, z + 1, x, yy, z + 1));
        toggleSegment(set, new Segment(x, yy, z + 1, x, yy, z));
    }

    private static void toggleFaceEdgesNorth(Set<Segment> set, int x, int y, int z) {
        toggleSegment(set, new Segment(x, y, z, x + 1, y, z));
        toggleSegment(set, new Segment(x + 1, y, z, x + 1, y + 1, z));
        toggleSegment(set, new Segment(x + 1, y + 1, z, x, y + 1, z));
        toggleSegment(set, new Segment(x, y + 1, z, x, y, z));
    }

    private static void toggleFaceEdgesSouth(Set<Segment> set, int x, int y, int z) {
        int zz = z + 1;
        toggleSegment(set, new Segment(x, y, zz, x + 1, y, zz));
        toggleSegment(set, new Segment(x + 1, y, zz, x + 1, y + 1, zz));
        toggleSegment(set, new Segment(x + 1, y + 1, zz, x, y + 1, zz));
        toggleSegment(set, new Segment(x, y + 1, zz, x, y, zz));
    }

    private static void toggleFaceEdgesWest(Set<Segment> set, int x, int y, int z) {
        toggleSegment(set, new Segment(x, y, z, x, y + 1, z));
        toggleSegment(set, new Segment(x, y + 1, z, x, y + 1, z + 1));
        toggleSegment(set, new Segment(x, y + 1, z + 1, x, y, z + 1));
        toggleSegment(set, new Segment(x, y, z + 1, x, y, z));
    }

    private static void toggleFaceEdgesEast(Set<Segment> set, int x, int y, int z) {
        int xx = x + 1;
        toggleSegment(set, new Segment(xx, y, z, xx, y + 1, z));
        toggleSegment(set, new Segment(xx, y + 1, z, xx, y + 1, z + 1));
        toggleSegment(set, new Segment(xx, y + 1, z + 1, xx, y, z + 1));
        toggleSegment(set, new Segment(xx, y, z + 1, xx, y, z));
    }

    private static void toggleSegment(Set<Segment> set, Segment edge) {
        if (!set.add(edge)) {
            set.remove(edge);
        }
    }

    private static final class Segment {
        private final int x1;
        private final int y1;
        private final int z1;
        private final int x2;
        private final int y2;
        private final int z2;

        private Segment(int x1, int y1, int z1, int x2, int y2, int z2) {
            if (compare(x1, y1, z1, x2, y2, z2) <= 0) {
                this.x1 = x1;
                this.y1 = y1;
                this.z1 = z1;
                this.x2 = x2;
                this.y2 = y2;
                this.z2 = z2;
            } else {
                this.x1 = x2;
                this.y1 = y2;
                this.z1 = z2;
                this.x2 = x1;
                this.y2 = y1;
                this.z2 = z1;
            }
        }

        private static int compare(int ax, int ay, int az, int bx, int by, int bz) {
            if (ax != bx) return Integer.compare(ax, bx);
            if (ay != by) return Integer.compare(ay, by);
            return Integer.compare(az, bz);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Segment)) return false;
            Segment other = (Segment) obj;
            return this.x1 == other.x1 && this.y1 == other.y1 && this.z1 == other.z1
                && this.x2 == other.x2 && this.y2 == other.y2 && this.z2 == other.z2;
        }

        @Override
        public int hashCode() {
            int result = Integer.hashCode(x1);
            result = 31 * result + Integer.hashCode(y1);
            result = 31 * result + Integer.hashCode(z1);
            result = 31 * result + Integer.hashCode(x2);
            result = 31 * result + Integer.hashCode(y2);
            result = 31 * result + Integer.hashCode(z2);
            return result;
        }
    }

    // -------------------------------------------------------------------------
    // Internal – all render calls end up here
    // -------------------------------------------------------------------------

    private static void highlightAreaInternal(double camX, double camY, double camZ,
                                              AxisAlignedBB area, @Nullable Color fillColor) {
        // Expand slightly to avoid Z-fighting with block faces
        double minX = area.minX - 0.001 - camX;
        double minY = area.minY - 0.001 - camY;
        double minZ = area.minZ - 0.001 - camZ;
        double maxX = area.maxX + 0.001 - camX;
        double maxY = area.maxY + 0.001 - camY;
        double maxZ = area.maxZ + 0.001 - camZ;

        // Set up GL state (see-through, no cull, no depth write)
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        // Draw filled faces (if fill colour provided)
        if (fillColor != null) {
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            drawBox(buf, minX, minY, minZ, maxX, maxY, maxZ, fillColor);
            tess.draw();
        }

        // Draw outline edges
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(OUTLINE_WIDTH);

        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        drawEdges(buf, minX, minY, minZ, maxX, maxY, maxZ, OUTLINE_COLOR);
        tess.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        // Restore GL state
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /** Draw the 6 faces of a box as quads (for the fill pass). */
    private static void drawBox(BufferBuilder buf,
                                double minX, double minY, double minZ,
                                double maxX, double maxY, double maxZ,
                                Color c) {
        float r = c.red(), g = c.green(), b = c.blue(), a = c.alpha();

        // West face (minX)
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        // East face (maxX)
        buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        // North face (minZ)
        buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        // South face (maxZ)
        buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        // Top face (maxY)
        buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        // Bottom face (minY)
        buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
    }

    /** Draw the 12 edges of a box as GL_LINES vertex pairs (for the outline pass). */
    private static void drawEdges(BufferBuilder buf,
                                  double minX, double minY, double minZ,
                                  double maxX, double maxY, double maxZ,
                                  Color c) {
        float r = c.red(), g = c.green(), b = c.blue(), a = c.alpha();
        // Bottom edges
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex(); buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex(); buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex(); buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex(); buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
        // Top edges
        buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex(); buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex(); buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex(); buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex(); buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        // Vertical edges
        buf.pos(minX, minY, minZ).color(r, g, b, a).endVertex(); buf.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, minZ).color(r, g, b, a).endVertex(); buf.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buf.pos(minX, minY, maxZ).color(r, g, b, a).endVertex(); buf.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buf.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex(); buf.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
    }

    /** Get the interpolated camera position for this render frame. */
    private static double[] getCameraPos(float partialTick) {
        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        return new double[]{
            e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTick,
            e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTick,
            e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTick
        };
    }

    // -------------------------------------------------------------------------
    // Color – equivalent to the reference's Color record
    // -------------------------------------------------------------------------

    /** Immutable float-component RGBA color. Mirrors the reference {@code record Color(...)}. */
    public static final class Color {
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;

        public Color(float red, float green, float blue, float alpha) {
            this.red   = red;
            this.green = green;
            this.blue  = blue;
            this.alpha = alpha;
        }

        public float red()   { return red; }
        public float green() { return green; }
        public float blue()  { return blue; }
        public float alpha() { return alpha; }

        /** Return a copy with a different alpha. */
        public Color withAlpha(float newAlpha) {
            return new Color(red, green, blue, newAlpha);
        }
    }

    private BlockHighlighter() {}
}
