package dev.su5ed.mffs.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class MovingHologramParticle extends Particle {
    private static final ResourceLocation PARTICLE_RESOURCE = new ResourceLocation("textures/particle/particles.png");

    private final ParticleColor color;

    public MovingHologramParticle(World world, Vec3d pos, ParticleColor color, int lifetime) {
        super(world, pos.x, pos.y, pos.z, 0, 0, 0);

        this.color = color;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        this.canCollide = false;
        this.particleMaxAge = lifetime;

        this.particleRed = color.getRed();
        this.particleGreen = color.getGreen();
        this.particleBlue = color.getBlue();

        // Distance cull
        Entity viewer = Minecraft.getMinecraft().player;
        int visibleDist = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 50 : 25;
        if (viewer != null && viewer.getDistance(this.posX, this.posY, this.posZ) > visibleDist) {
            this.particleMaxAge = 0;
        }

        // Expand bounding box to match the rendered 1×1 cube so culling
        // doesn't clip the particle when only part of it is on-screen.
        this.setBoundingBox(new AxisAlignedBB(
            pos.x - 0.5, pos.y, pos.z - 0.5,
            pos.x + 0.5, pos.y + 1, pos.z + 0.5
        ));
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            setExpired();
        }
    }

    @Override
    public int getFXLayer() {
        return 3; // Custom rendering
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks,
                               float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        Tessellator tessellator = Tessellator.getInstance();

        // Completion ratio: cube grows vertically from 0 to 1
        float completion = (this.particleAge + partialTicks) / (float) this.particleMaxAge;
        if (completion > 1.0F) completion = 1.0F;

        // Opacity: 0.5, fading in last 4 ticks
        float opacity = 0.5F;
        int remaining = this.particleMaxAge - this.particleAge;
        if (remaining <= 4) {
            opacity = 0.5F - (5 - remaining) * 0.1F;
        }
        if (opacity <= 0) return;

        GL11.glPushMatrix();

        // Position relative to camera
        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        GL11.glTranslatef(xx, yy, zz);

        // Scale slightly larger than block (1.01) and vertically by completion
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(1.1F, 1.05F * completion, 1.1F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // Translucent blending
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GlStateManager.disableLighting();
        // Polygon offset biases depth toward the camera so the overlay always
        // renders on top of a solid block at the same position (avoids Z-fighting).
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(-1.0F, -1.0F);

        float r = this.particleRed;
        float g = this.particleGreen;
        float b = this.particleBlue;
        float a = opacity;

        // Draw 6 faces of the cube (0,0,0) to (1,1,1)
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        // Bottom (y=0)
        buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 0, 1).color(r, g, b, a).endVertex();
        buffer.pos(0, 0, 1).color(r, g, b, a).endVertex();
        // Top (y=1)
        buffer.pos(0, 1, 0).color(r, g, b, a).endVertex();
        buffer.pos(0, 1, 1).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 1).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 0).color(r, g, b, a).endVertex();
        // North (z=0)
        buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(0, 1, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 0, 0).color(r, g, b, a).endVertex();
        // South (z=1)
        buffer.pos(0, 0, 1).color(r, g, b, a).endVertex();
        buffer.pos(1, 0, 1).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 1).color(r, g, b, a).endVertex();
        buffer.pos(0, 1, 1).color(r, g, b, a).endVertex();
        // West (x=0)
        buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(0, 0, 1).color(r, g, b, a).endVertex();
        buffer.pos(0, 1, 1).color(r, g, b, a).endVertex();
        buffer.pos(0, 1, 0).color(r, g, b, a).endVertex();
        // East (x=1)
        buffer.pos(1, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 0).color(r, g, b, a).endVertex();
        buffer.pos(1, 1, 1).color(r, g, b, a).endVertex();
        buffer.pos(1, 0, 1).color(r, g, b, a).endVertex();
        tessellator.draw();

        // Restore GL state
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

        GL11.glPopMatrix();

        // Restore vanilla particle texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_RESOURCE);
    }
}
