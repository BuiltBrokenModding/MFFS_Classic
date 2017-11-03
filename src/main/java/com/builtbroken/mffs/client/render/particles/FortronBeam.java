package com.builtbroken.mffs.client.render.particles;

import com.builtbroken.mffs.ModularForceFieldSystem;
import com.builtbroken.mffs.api.vector.Vector3D;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class FortronBeam extends EntityFX
{

    public static final ResourceLocation PARTICLE_RESOURCE = new ResourceLocation("textures/particle/particles.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(ModularForceFieldSystem.DOMAIN, "textures/blocks/fortron.png");
    private float length = 0.0F;
    private float rotYaw = 0.0F;
    private float rotPitch = 0.0F;
    private float prevYaw = 0.0F;
    private float prevPitch = 0.0F;
    private Vector3D target = new Vector3D();
    private float endModifier = 1.0F;
    private boolean reverse = false;
    private boolean pulse = true;
    private int rotationSpeed = 20;
    private float prevSize = 0.0F;

    public FortronBeam(World par1World, Vector3D position, Vector3D target, float red, float green, float blue, int age)
    {
        super(par1World, position.x, position.y, position.z, 0.0D, 0.0D, 0.0D);

        setRGB(red, green, blue);

        setSize(0.02F, 0.02F);
        this.noClip = true;
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.target = target;
        float xd = (float) (this.posX - this.target.x);
        float yd = (float) (this.posY - this.target.y);
        float zd = (float) (this.posZ - this.target.z);
        this.length = ((float) new Vector3D(this).distance(this.target));
        double var7 = MathHelper.sqrt_double(xd * xd + zd * zd);
        this.rotYaw = ((float) (Math.atan2(xd, zd) * 180.0D / 3.141592653589793D));
        this.rotPitch = ((float) (Math.atan2(yd, var7) * 180.0D / 3.141592653589793D));
        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        this.particleMaxAge = age;

        EntityLivingBase renderentity = Minecraft.getMinecraft().thePlayer;

        int visibleDistance = 50;
        if (!Minecraft.getMinecraft().gameSettings.fancyGraphics)
        {
            visibleDistance = 25;
        }
        if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance)
        {
            this.particleMaxAge = 0;
        }
    }

    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.prevYaw = this.rotYaw;
        this.prevPitch = this.rotPitch;

        float xd = (float) (this.posX - this.target.x);
        float yd = (float) (this.posY - this.target.y);
        float zd = (float) (this.posZ - this.target.z);

        this.length = MathHelper.sqrt_float(xd * xd + yd * yd + zd * zd);

        double var7 = MathHelper.sqrt_double(xd * xd + zd * zd);

        this.rotYaw = ((float) (Math.atan2(xd, zd) * 180.0D / 3.141592653589793D));
        this.rotPitch = ((float) (Math.atan2(yd, var7) * 180.0D / 3.141592653589793D));
        if (this.particleAge++ >= this.particleMaxAge)
        {
            setDead();
        }
    }

    public void setRGB(float r, float g, float b)
    {
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5)
    {
        tessellator.draw();

        GL11.glPushMatrix();
        float var9 = 1.0F;
        float slide = (float) this.worldObj.getTotalWorldTime();
        float rot = (float) (this.worldObj.provider.getWorldTime() % (360 / this.rotationSpeed) * this.rotationSpeed) + this.rotationSpeed * f;

        float size = 1.0F;
        if (this.pulse)
        {
            size = Math.min(this.particleAge / 4.0F, 1.0F);
            size = this.prevSize + (size - this.prevSize) * f;
        }
        float op = 0.5F;
        if ((this.pulse) && (this.particleMaxAge - this.particleAge <= 4))
        {
            op = 0.5F - (4 - (this.particleMaxAge - this.particleAge)) * 0.1F;
        }
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glTexParameterf(3553, 10242, 10497.0F);
        GL11.glTexParameterf(3553, 10243, 10497.0F);

        GL11.glDisable(2884);

        float var11 = slide + f;
        if (this.reverse)
        {
            var11 *= -1.0F;
        }
        float var12 = -var11 * 0.2F - MathHelper.floor_float(-var11 * 0.1F);

        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDepthMask(false);

        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);
        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);
        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);
        GL11.glTranslated(xx, yy, zz);

        float ry = this.prevYaw + (this.rotYaw - this.prevYaw) * f;
        float rp = this.prevPitch + (this.rotPitch - this.prevPitch) * f;
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F + ry, 0.0F, 0.0F, -1.0F);
        GL11.glRotatef(rp, 1.0F, 0.0F, 0.0F);

        double var44 = -0.15D * size;
        double var17 = 0.15D * size;
        double var44b = -0.15D * size * this.endModifier;
        double var17b = 0.15D * size * this.endModifier;

        GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
        for (int t = 0; t < 3; t++)
        {
            double var29 = this.length * size * var9;
            double var31 = 0.0D;
            double var33 = 1.0D;
            double var35 = -1.0F + var12 + t / 3.0F;
            double var37 = this.length * size * var9 + var35;

            GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.setBrightness(200);
            tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, op);
            tessellator.addVertexWithUV(var44b, var29, 0.0D, var33, var37);
            tessellator.addVertexWithUV(var44, 0.0D, 0.0D, var33, var35);
            tessellator.addVertexWithUV(var17, 0.0D, 0.0D, var31, var35);
            tessellator.addVertexWithUV(var17b, var29, 0.0D, var31, var37);
            tessellator.draw();
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2884);

        GL11.glPopMatrix();

        tessellator.startDrawingQuads();
        this.prevSize = size;

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(PARTICLE_RESOURCE);
    }
}
