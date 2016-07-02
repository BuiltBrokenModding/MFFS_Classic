package com.mffs.client.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class ModelFortronCapacitor extends ModelBase {
    ModelRenderer corner1;
    ModelRenderer bottom;
    ModelRenderer top;
    ModelRenderer Rout;
    ModelRenderer corner2;
    ModelRenderer corner3;
    ModelRenderer corner4;
    ModelRenderer Bout;
    ModelRenderer Baout;
    ModelRenderer Fout;
    ModelRenderer Lout;
    ModelRenderer Core;
    ModelRenderer Tout;

    public ModelFortronCapacitor() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.corner1 = new ModelRenderer(this, 52, 0);
        this.corner1.addBox(3.0F, 14.0F, 3.0F, 3, 8, 3);
        this.corner1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.corner1.setTextureSize(64, 32);
        this.corner1.mirror = true;
        setRotation(this.corner1, 0.0F, 0.0F, 0.0F);
        this.bottom = new ModelRenderer(this, 0, 0);
        this.bottom.addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);
        this.bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bottom.setTextureSize(64, 32);
        this.bottom.mirror = true;
        setRotation(this.bottom, 0.0F, 0.0F, 0.0F);
        this.top = new ModelRenderer(this, 0, 0);
        this.top.addBox(-6.0F, 12.0F, -6.0F, 12, 2, 12);
        this.top.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top.setTextureSize(64, 32);
        this.top.mirror = true;
        setRotation(this.top, 0.0F, 0.0F, 0.0F);
        this.Rout = new ModelRenderer(this, 40, 14);
        this.Rout.addBox(-4.0F, 14.0F, -2.0F, 1, 4, 4);
        this.Rout.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Rout.setTextureSize(64, 32);
        this.Rout.mirror = true;
        setRotation(this.Rout, 0.0F, 0.0F, 0.0F);
        this.corner2 = new ModelRenderer(this, 52, 0);
        this.corner2.addBox(-6.0F, 14.0F, 3.0F, 3, 8, 3);
        this.corner2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.corner2.setTextureSize(64, 32);
        this.corner2.mirror = true;
        setRotation(this.corner2, 0.0F, 0.0F, 0.0F);
        this.corner3 = new ModelRenderer(this, 52, 0);
        this.corner3.addBox(-6.0F, 14.0F, -6.0F, 3, 8, 3);
        this.corner3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.corner3.setTextureSize(64, 32);
        this.corner3.mirror = true;
        setRotation(this.corner3, 0.0F, 0.0F, 0.0F);
        this.corner4 = new ModelRenderer(this, 52, 0);
        this.corner4.addBox(3.0F, 14.0F, -6.0F, 3, 8, 3);
        this.corner4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.corner4.setTextureSize(64, 32);
        this.corner4.mirror = true;
        setRotation(this.corner4, 0.0F, 0.0F, 0.0F);
        this.Bout = new ModelRenderer(this, 24, 19);
        this.Bout.addBox(-2.0F, 21.0F, -2.0F, 4, 1, 4);
        this.Bout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Bout.setTextureSize(64, 32);
        this.Bout.mirror = true;
        setRotation(this.Bout, 0.0F, 0.0F, 0.0F);
        this.Baout = new ModelRenderer(this, 24, 14);
        this.Baout.addBox(-2.0F, 14.0F, 3.0F, 4, 4, 1);
        this.Baout.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Baout.setTextureSize(64, 32);
        this.Baout.mirror = true;
        setRotation(this.Baout, 0.0F, 0.0F, 0.0F);
        this.Fout = new ModelRenderer(this, 24, 14);
        this.Fout.addBox(-2.0F, 14.0F, -4.0F, 4, 4, 1);
        this.Fout.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Fout.setTextureSize(64, 32);
        this.Fout.mirror = true;
        setRotation(this.Fout, 0.0F, 0.0F, 0.0F);
        this.Lout = new ModelRenderer(this, 40, 14);
        this.Lout.addBox(3.0F, 14.0F, -2.0F, 1, 4, 4);
        this.Lout.setRotationPoint(0.0F, 2.0F, 0.0F);
        this.Lout.setTextureSize(64, 32);
        this.Lout.mirror = true;
        setRotation(this.Lout, 0.0F, 0.0F, 0.0F);
        this.Core = new ModelRenderer(this, 0, 14);
        this.Core.addBox(-3.0F, 15.0F, -3.0F, 6, 6, 6);
        this.Core.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Core.setTextureSize(64, 32);
        this.Core.mirror = true;
        setRotation(this.Core, 0.0F, 0.0F, 0.0F);
        this.Tout = new ModelRenderer(this, 24, 19);
        this.Tout.addBox(-2.0F, 14.0F, -2.0F, 4, 1, 4);
        this.Tout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Tout.setTextureSize(64, 32);
        this.Tout.mirror = true;
        setRotation(this.Tout, 0.0F, 0.0F, 0.0F);
    }

    public void render(float f5) {
        this.corner1.render(f5);
        this.bottom.render(f5);
        this.top.render(f5);
        this.Rout.render(f5);
        this.corner2.render(f5);
        this.corner3.render(f5);
        this.corner4.render(f5);
        this.Bout.render(f5);
        this.Baout.render(f5);
        this.Fout.render(f5);
        this.Lout.render(f5);
        this.Core.render(f5);
        this.Tout.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
