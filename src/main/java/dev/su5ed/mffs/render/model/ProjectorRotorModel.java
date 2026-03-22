package dev.su5ed.mffs.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ProjectorRotorModel extends ModelBase {
    // Rotating parts
    private final ModelRenderer axle;
    private final ModelRenderer thingfront;
    private final ModelRenderer thingback;
    private final ModelRenderer thingright;
    private final ModelRenderer thingleft;
    private final ModelRenderer attacherbig1;
    private final ModelRenderer attacherbig2;
    private final ModelRenderer attachersmall1;
    private final ModelRenderer attachersmall2;
    private final ModelRenderer attachersmall3;
    private final ModelRenderer attachersmall4;

    public ProjectorRotorModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;

        // Axle: center shaft
        this.axle = new ModelRenderer(this, 16, 26);
        this.axle.addBox(-1.0F, -2.0F, -1.0F, 2, 8, 2);
        this.axle.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Four "thing" parts (front, back, right, left)
        this.thingfront = new ModelRenderer(this, 0, 20);
        this.thingfront.addBox(-2.0F, -2.0F, -7.0F, 4, 8, 4);
        this.thingfront.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.thingback = new ModelRenderer(this, 0, 20);
        this.thingback.addBox(-2.0F, -2.0F, 3.0F, 4, 8, 4);
        this.thingback.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.thingright = new ModelRenderer(this, 0, 20);
        this.thingright.addBox(2.0F, -2.0F, -2.0F, 4, 8, 4);
        this.thingright.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.thingleft = new ModelRenderer(this, 0, 20);
        this.thingleft.addBox(-6.0F, -2.0F, -2.0F, 4, 8, 4);
        this.thingleft.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Big attachers (top/bottom plates, wide)
        this.attacherbig1 = new ModelRenderer(this, 16, 20);
        this.attacherbig1.addBox(-7.0F, -1.0F, -3.0F, 14, 1, 6);
        this.attacherbig1.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.attacherbig2 = new ModelRenderer(this, 16, 20);
        this.attacherbig2.addBox(-7.0F, 4.0F, -3.0F, 14, 1, 6);
        this.attacherbig2.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Small attachers (cross pieces)
        this.attachersmall1 = new ModelRenderer(this, 16, 36);
        this.attachersmall1.addBox(-3.0F, -1.0F, 3.0F, 6, 1, 5);
        this.attachersmall1.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.attachersmall2 = new ModelRenderer(this, 16, 36);
        this.attachersmall2.addBox(-3.0F, 4.0F, 3.0F, 6, 1, 5);
        this.attachersmall2.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.attachersmall3 = new ModelRenderer(this, 16, 36);
        this.attachersmall3.addBox(-3.0F, -1.0F, -8.0F, 6, 1, 5);
        this.attachersmall3.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.attachersmall4 = new ModelRenderer(this, 16, 36);
        this.attachersmall4.addBox(-3.0F, 4.0F, -8.0F, 6, 1, 5);
        this.attachersmall4.setRotationPoint(0.0F, 16.0F, 0.0F);
    }

    /**
     * Render the rotating rotor with the given Y-axis rotation.
     * @param rotation rotation angle in degrees
     * @param scale model scale (typically 0.0625F = 1/16)
     */
    public void render(float rotation, float scale) {
        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

        this.axle.render(scale);
        this.thingfront.render(scale);
        this.thingback.render(scale);
        this.thingright.render(scale);
        this.thingleft.render(scale);
        this.attacherbig1.render(scale);
        this.attacherbig2.render(scale);
        this.attachersmall1.render(scale);
        this.attachersmall2.render(scale);
        this.attachersmall3.render(scale);
        this.attachersmall4.render(scale);

        GL11.glPopMatrix();
    }
}
