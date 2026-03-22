package dev.su5ed.mffs.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CoercionDeriverTopModel extends ModelBase {
    private final ModelRenderer tout;

    public CoercionDeriverTopModel() {
        this.textureWidth = 64;
        // Texture is 128x128 but content occupies only the top 128x64 region.
        this.textureHeight = 64;
        this.tout = new ModelRenderer(this, 24, 19);
        this.tout.addBox(-2.0F, 14.0F, -2.0F, 4, 1, 4);
        this.tout.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.tout.mirror = true;
    }

    public void render(float rotation, float scale) {
        GL11.glPushMatrix();
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        this.tout.render(scale);
        GL11.glPopMatrix();
    }
}
