package com.mffs.client.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class ModelForceFieldProjector extends ModelBase
{
    ModelRenderer top;
    ModelRenderer axle;
    ModelRenderer bottom;
    ModelRenderer thingfront;
    ModelRenderer thingback;
    ModelRenderer thingright;
    ModelRenderer thingleft;
    ModelRenderer attacherbig1;
    ModelRenderer attacherbig2;
    ModelRenderer attachersmall3;
    ModelRenderer attachersmall4;
    ModelRenderer attachersmall2;
    ModelRenderer attachersmall_1;
    ModelRenderer corner1;
    ModelRenderer corner2;
    ModelRenderer corner3;
    ModelRenderer corner4;
    ModelRenderer lense;
    ModelRenderer lensesidefront;
    ModelRenderer lensesideback;
    ModelRenderer lensesideright;
    ModelRenderer lensesideleft;
    ModelRenderer lensecorner1;
    ModelRenderer lensecorner2;
    ModelRenderer lensecorner3;
    ModelRenderer lensecorner4;

    public ModelForceFieldProjector()
    {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.top = new ModelRenderer(this, 0, 0);
        this.top.addBox(-8.0F, -4.0F, -8.0F, 16, 2, 16);
        this.top.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.top.setTextureSize(128, 64);
        this.top.mirror = true;
        setRotation(this.top, 0.0F, 0.0F, 0.0F);
        this.axle = new ModelRenderer(this, 16, 26);
        this.axle.addBox(-1.0F, -2.0F, -1.0F, 2, 8, 2);
        this.axle.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.axle.setTextureSize(128, 64);
        this.axle.mirror = true;
        setRotation(this.axle, 0.0F, 0.0F, 0.0F);
        this.bottom = new ModelRenderer(this, 0, 44);
        this.bottom.addBox(-8.0F, 6.0F, -8.0F, 16, 2, 16);
        this.bottom.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.bottom.setTextureSize(128, 64);
        this.bottom.mirror = true;
        setRotation(this.bottom, 0.0F, 0.0F, 0.0F);
        this.thingfront = new ModelRenderer(this, 0, 20);
        this.thingfront.addBox(-2.0F, -2.0F, -7.0F, 4, 8, 4);
        this.thingfront.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.thingfront.setTextureSize(128, 64);
        this.thingfront.mirror = true;
        setRotation(this.thingfront, 0.0F, 0.0F, 0.0F);
        this.thingback = new ModelRenderer(this, 0, 20);
        this.thingback.addBox(-2.0F, -2.0F, 3.0F, 4, 8, 4);
        this.thingback.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.thingback.setTextureSize(128, 64);
        this.thingback.mirror = true;
        setRotation(this.thingback, 0.0F, 0.0F, 0.0F);
        this.thingright = new ModelRenderer(this, 0, 20);
        this.thingright.addBox(-6.0F, -2.0F, -2.0F, 4, 8, 4);
        this.thingright.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.thingright.setTextureSize(128, 64);
        this.thingright.mirror = true;
        setRotation(this.thingright, 0.0F, 0.0F, 0.0F);
        this.thingleft = new ModelRenderer(this, 0, 20);
        this.thingleft.addBox(2.0F, -2.0F, -2.0F, 4, 8, 4);
        this.thingleft.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.thingleft.setTextureSize(128, 64);
        this.thingleft.mirror = true;
        setRotation(this.thingleft, 0.0F, 0.0F, 0.0F);
        this.attacherbig1 = new ModelRenderer(this, 16, 20);
        this.attacherbig1.addBox(-7.0F, -1.0F, -3.0F, 14, 1, 6);
        this.attacherbig1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attacherbig1.setTextureSize(128, 64);
        this.attacherbig1.mirror = true;
        setRotation(this.attacherbig1, 0.0F, 0.0F, 0.0F);
        this.attacherbig2 = new ModelRenderer(this, 16, 20);
        this.attacherbig2.addBox(-7.0F, 4.0F, -3.0F, 14, 1, 6);
        this.attacherbig2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attacherbig2.setTextureSize(128, 64);
        this.attacherbig2.mirror = true;
        setRotation(this.attacherbig2, 0.0F, 0.0F, 0.0F);
        this.attachersmall3 = new ModelRenderer(this, 16, 36);
        this.attachersmall3.addBox(-3.0F, -1.0F, -8.0F, 6, 1, 5);
        this.attachersmall3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attachersmall3.setTextureSize(128, 64);
        this.attachersmall3.mirror = true;
        setRotation(this.attachersmall3, 0.0F, 0.0F, 0.0F);
        this.attachersmall4 = new ModelRenderer(this, 16, 36);
        this.attachersmall4.addBox(-3.0F, 4.0F, -8.0F, 6, 1, 5);
        this.attachersmall4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attachersmall4.setTextureSize(128, 64);
        this.attachersmall4.mirror = true;
        setRotation(this.attachersmall4, 0.0F, 0.0F, 0.0F);
        this.attachersmall2 = new ModelRenderer(this, 16, 36);
        this.attachersmall2.addBox(-3.0F, 4.0F, 3.0F, 6, 1, 5);
        this.attachersmall2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attachersmall2.setTextureSize(128, 64);
        this.attachersmall2.mirror = true;
        setRotation(this.attachersmall2, 0.0F, 0.0F, 0.0F);
        this.attachersmall_1 = new ModelRenderer(this, 16, 36);
        this.attachersmall_1.addBox(-3.0F, -1.0F, 3.0F, 6, 1, 5);
        this.attachersmall_1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.attachersmall_1.setTextureSize(128, 64);
        this.attachersmall_1.mirror = true;
        setRotation(this.attachersmall_1, 0.0F, 0.0F, 0.0F);
        this.corner1 = new ModelRenderer(this, 38, 32);
        this.corner1.addBox(6.0F, -2.0F, -8.0F, 2, 8, 2);
        this.corner1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.corner1.setTextureSize(128, 64);
        this.corner1.mirror = true;
        setRotation(this.corner1, 0.0F, 0.0F, 0.0F);
        this.corner2 = new ModelRenderer(this, 46, 32);
        this.corner2.addBox(6.0F, -2.0F, 6.0F, 2, 8, 2);
        this.corner2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.corner2.setTextureSize(128, 64);
        this.corner2.mirror = true;
        setRotation(this.corner2, 0.0F, 0.0F, 0.0F);
        this.corner3 = new ModelRenderer(this, 0, 32);
        this.corner3.addBox(-8.0F, -2.0F, 6.0F, 2, 8, 2);
        this.corner3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.corner3.setTextureSize(128, 64);
        this.corner3.mirror = true;
        setRotation(this.corner3, 0.0F, 0.0F, 0.0F);
        this.corner4 = new ModelRenderer(this, 8, 32);
        this.corner4.addBox(-8.0F, -2.0F, -8.0F, 2, 8, 2);
        this.corner4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.corner4.setTextureSize(128, 64);
        this.corner4.mirror = true;
        setRotation(this.corner4, 0.0F, 0.0F, 0.0F);
        this.lense = new ModelRenderer(this, 96, 0);
        this.lense.addBox(-4.0F, -5.0F, -4.0F, 8, 1, 8);
        this.lense.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lense.setTextureSize(128, 64);
        this.lense.mirror = true;
        setRotation(this.lense, 0.0F, 0.0F, 0.0F);
        this.lensesidefront = new ModelRenderer(this, 64, 5);
        this.lensesidefront.addBox(-3.0F, -6.0F, -5.0F, 6, 2, 1);
        this.lensesidefront.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensesidefront.setTextureSize(128, 64);
        this.lensesidefront.mirror = true;
        setRotation(this.lensesidefront, 0.0F, 0.0F, 0.0F);
        this.lensesideback = new ModelRenderer(this, 64, 5);
        this.lensesideback.addBox(-3.0F, -6.0F, 4.0F, 6, 2, 1);
        this.lensesideback.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensesideback.setTextureSize(128, 64);
        this.lensesideback.mirror = true;
        setRotation(this.lensesideback, 0.0F, 0.0F, 0.0F);
        this.lensesideright = new ModelRenderer(this, 64, 8);
        this.lensesideright.addBox(-5.0F, -6.0F, -3.0F, 1, 2, 6);
        this.lensesideright.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensesideright.setTextureSize(128, 64);
        this.lensesideright.mirror = true;
        setRotation(this.lensesideright, 0.0F, 0.0F, 0.0F);
        this.lensesideleft = new ModelRenderer(this, 64, 8);
        this.lensesideleft.addBox(4.0F, -6.0F, -3.0F, 1, 2, 6);
        this.lensesideleft.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensesideleft.setTextureSize(128, 64);
        this.lensesideleft.mirror = true;
        setRotation(this.lensesideleft, 0.0F, 0.0F, 0.0F);
        this.lensecorner1 = new ModelRenderer(this, 64, 16);
        this.lensecorner1.addBox(3.0F, -6.0F, -4.0F, 1, 2, 1);
        this.lensecorner1.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensecorner1.setTextureSize(128, 64);
        this.lensecorner1.mirror = true;
        setRotation(this.lensecorner1, 0.0F, 0.0F, 0.0F);
        this.lensecorner2 = new ModelRenderer(this, 64, 16);
        this.lensecorner2.addBox(3.0F, -6.0F, 3.0F, 1, 2, 1);
        this.lensecorner2.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensecorner2.setTextureSize(128, 64);
        this.lensecorner2.mirror = true;
        setRotation(this.lensecorner2, 0.0F, 0.0F, 0.0F);
        this.lensecorner3 = new ModelRenderer(this, 64, 16);
        this.lensecorner3.addBox(-4.0F, -6.0F, 3.0F, 1, 2, 1);
        this.lensecorner3.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensecorner3.setTextureSize(128, 64);
        this.lensecorner3.mirror = true;
        setRotation(this.lensecorner3, 0.0F, 0.0F, 0.0F);
        this.lensecorner4 = new ModelRenderer(this, 64, 16);
        this.lensecorner4.addBox(-4.0F, -6.0F, -4.0F, 1, 2, 1);
        this.lensecorner4.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.lensecorner4.setTextureSize(128, 64);
        this.lensecorner4.mirror = true;
        setRotation(this.lensecorner4, 0.0F, 0.0F, 0.0F);
    }

    public void render(float rotation, float f5)
    {
        this.top.render(f5);
        this.axle.render(f5);
        this.bottom.render(f5);

        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        this.thingfront.render(f5);
        this.attachersmall3.render(f5);
        this.thingback.render(f5);
        this.thingright.render(f5);
        this.thingleft.render(f5);
        this.attacherbig1.render(f5);
        this.attacherbig2.render(f5);
        this.attachersmall4.render(f5);
        this.attachersmall2.render(f5);
        this.attachersmall_1.render(f5);
        GL11.glPopMatrix();

        this.corner1.render(f5);
        this.corner2.render(f5);
        this.corner3.render(f5);
        this.corner4.render(f5);
        this.lense.render(f5);
        this.lensesidefront.render(f5);
        this.lensesideback.render(f5);
        this.lensesideright.render(f5);
        this.lensesideleft.render(f5);
        this.lensecorner1.render(f5);
        this.lensecorner2.render(f5);
        this.lensecorner3.render(f5);
        this.lensecorner4.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
