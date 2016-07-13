package com.mffs.client.render.particles;

import com.mffs.api.vector.Vector3D;
import com.mffs.client.render.RenderBlockHandler;
import com.mffs.common.blocks.BlockForceField;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class MovingFortron extends EntityFX {
    public MovingFortron(World par1World, Vector3D position, float red, float green, float blue, int age) {

        super(par1World, position.x, position.y, position.z);

        setRBGColorF(red, green, blue);

        this.particleMaxAge = age;

        this.noClip = true;

    }


    @Override
    public void onUpdate() {

        this.prevPosX = this.posX;

        this.prevPosY = this.posY;

        this.prevPosZ = this.posZ;


        if (this.particleAge++ >= this.particleMaxAge) {

            setDead();

        }

    }

    @Override
    public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {

        tessellator.draw();


        GL11.glPushMatrix();

        float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * f - interpPosX);

        float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * f - interpPosY);

        float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * f - interpPosZ);

        GL11.glTranslated(xx, yy, zz);

        GL11.glScalef(1.01F, 1.01F, 1.01F);


        double completion = (double) this.particleAge / this.particleMaxAge;

        GL11.glTranslated(0.0D, (completion - 1.0D) / 2.0D, 0.0D);

        GL11.glScaled(1.0D, completion, 1.0D);

        float op = 0.5F;

        if (this.particleMaxAge - this.particleAge <= 4) {

            op = 0.5F - (5 - (this.particleMaxAge - this.particleAge)) * 0.1F;

        }

        GL11.glColor4d(this.particleRed, this.particleGreen, this.particleBlue, op * 2.0F);


        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(FMLClientHandler.instance().getClient().renderEngine.getResourceLocation(0));

        RenderBlockHandler.renderNormal(new RenderBlocks(), BlockForceField.BLOCK_FORCE_FIELD, 0);

        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glDisable(2881);
        GL11.glDisable(3042);

        GL11.glPopMatrix();

        tessellator.startDrawingQuads();

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(FortronBeam.PARTICLE_RESOURCE);

    }
}
