package dev.su5ed.mffs.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BeamParticle extends Particle {
    public static final ResourceLocation TEXTURE_NEUTRAL = new ResourceLocation("mffs", "textures/particle/fortron_neutral.png");
    private static final ResourceLocation PARTICLE_RESOURCE = new ResourceLocation("textures/particle/particles.png");
    private static final int ROTATION_SPEED = 20;

    private final ResourceLocation texture;
    private final float length;
    private final float rotYaw;
    private final float rotPitch;
    private float prevYaw;
    private float prevPitch;
    private float prevSize;
    // Offset from the midpoint (used for culling) back to the actual start position (used for rendering)
    private final double startOffsetX;
    private final double startOffsetY;
    private final double startOffsetZ;

    public BeamParticle(World world, Vec3d start, Vec3d target, ParticleColor color, int lifetime) {
        this(world, start, target, color, lifetime, TEXTURE_NEUTRAL);
    }

    public BeamParticle(World world, Vec3d start, Vec3d target, ParticleColor color, int lifetime, ResourceLocation texture) {
        // Position the particle at the midpoint between start and target so that
        // frustum culling (which uses posX/posY/posZ) keeps the beam visible
        super(world, (start.x + target.x) / 2.0, (start.y + target.y) / 2.0, (start.z + target.z) / 2.0, 0, 0, 0);

        this.texture = texture;
        // Store offset from midpoint to start for rendering
        this.startOffsetX = start.x - this.posX;
        this.startOffsetY = start.y - this.posY;
        this.startOffsetZ = start.z - this.posZ;

        setSize(0.02F, 0.02F);
        // Set bounding box to encompass both endpoints so the beam is not culled.
        this.setBoundingBox(new AxisAlignedBB(start, target));
        this.canCollide = false;
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

        this.particleRed = color.getRed();
        this.particleGreen = color.getGreen();
        this.particleBlue = color.getBlue();

        // Calculate beam geometry from the actual start position to target
        float xd = (float) (start.x - target.x);
        float yd = (float) (start.y - target.y);
        float zd = (float) (start.z - target.z);
        this.length = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
        this.rotYaw = (float) (Math.atan2(xd, zd) * 180.0 / Math.PI);
        double horiz = MathHelper.sqrt(xd * xd + zd * zd);
        this.rotPitch = (float) (Math.atan2(yd, horiz) * 180.0 / Math.PI);
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        this.particleMaxAge = lifetime;

        // Distance cull
        Entity viewer = Minecraft.getMinecraft().player;
        int visibleDist = Minecraft.getMinecraft().gameSettings.fancyGraphics ? 50 : 25;
        if (viewer != null && viewer.getDistance(this.posX, this.posY, this.posZ) > visibleDist) {
            this.particleMaxAge = 0;
        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        if (this.particleAge++ >= this.particleMaxAge) {
            setExpired();
        }
    }

    @Override
    public int getFXLayer() {
        // Layer 3 = custom rendering. renderParticle is called with Tessellator control.
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks,
                               float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        // End any active batch started by the particle engine
        Tessellator tessellator = Tessellator.getInstance();

        GL11.glPushMatrix();

        // Pulse size effect: ramp up over first 4 ticks
        float size = Math.min(this.particleAge / 4.0F, 1.0F);
        size = this.prevSize + (size - this.prevSize) * partialTicks;

        // Opacity: fade out over last 4 ticks
        float opacity = 0.5F;
        if (this.particleMaxAge - this.particleAge <= 4) {
            opacity = 0.5F - (4 - (this.particleMaxAge - this.particleAge)) * 0.1F;
        }

        // Bind beam texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glDisable(GL11.GL_CULL_FACE);

        // Scrolling V offset for animated flow
        float slide = this.world.getTotalWorldTime() + partialTicks;
        float vOffset = -slide * 0.2F - MathHelper.floor(-slide * 0.1F);

        // Additive blending for glow effect
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);

        // Full brightness lightmap (emissive) — ensures consistent glow
        // regardless of whether the projector's holographic shape is on screen
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        // Position relative to camera — offset from midpoint (posX/Y/Z) to actual beam start
        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX + this.startOffsetX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY + this.startOffsetY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ + this.startOffsetZ);
        GL11.glTranslatef(xx, yy, zz);

        // Orient beam toward target
        float ry = this.prevYaw + (this.rotYaw - this.prevYaw) * partialTicks;
        float rp = this.prevPitch + (this.rotPitch - this.prevPitch) * partialTicks;
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F + ry, 0.0F, 0.0F, -1.0F);
        GL11.glRotatef(rp, 1.0F, 0.0F, 0.0F);

        // Beam half-width
        double negWidth = -0.15 * size;
        double posWidth = 0.15 * size;
        double beamLength = this.length * size;

        // Spinning rotation
        int deg = 360 / ROTATION_SPEED;
        float rot = (float) (this.world.getTotalWorldTime() % deg) * ROTATION_SPEED + ROTATION_SPEED * partialTicks;
        GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);

        // Draw 3 quads rotated 60° apart (star cross-section)
        for (int t = 0; t < 3; t++) {
            double v0 = -1.0F + vOffset + t / 3.0F;
            double v1 = beamLength + v0;

            GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(negWidth, beamLength, 0).tex(1, v1).color(this.particleRed, this.particleGreen, this.particleBlue, opacity).endVertex();
            buffer.pos(negWidth, 0, 0).tex(1, v0).color(this.particleRed, this.particleGreen, this.particleBlue, opacity).endVertex();
            buffer.pos(posWidth, 0, 0).tex(0, v0).color(this.particleRed, this.particleGreen, this.particleBlue, opacity).endVertex();
            buffer.pos(posWidth, beamLength, 0).tex(0, v1).color(this.particleRed, this.particleGreen, this.particleBlue, opacity).endVertex();
            tessellator.draw();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        // Restore previous lightmap
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glPopMatrix();

        this.prevSize = size;

        // Restore vanilla particle texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_RESOURCE);
    }
}
