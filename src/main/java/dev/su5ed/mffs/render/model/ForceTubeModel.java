package dev.su5ed.mffs.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static dev.su5ed.mffs.MFFSMod.location;

@SideOnly(Side.CLIENT)
public class ForceTubeModel extends ModelBase {
    // Uses same texture atlas as ForceCubeModel
    public static final ResourceLocation CORE_TEXTURE = location("textures/model/force_cube.png");

    // Left wall:   X from -8 to -7
    private final ModelRenderer left;
    // Right wall:  X from  7 to  8
    private final ModelRenderer right;
    // Top wall:    Y from -8 to -7
    private final ModelRenderer top;
    // Bottom wall: Y from  7 to  8
    private final ModelRenderer bottom;

    public ForceTubeModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.left = new ModelRenderer(this, 0, 0);
        this.left.addBox(-8.0F, -8.0F, -8.0F, 1, 16, 16);
        this.left.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.right = new ModelRenderer(this, 0, 0);
        this.right.addBox(7.0F, -8.0F, -8.0F, 1, 16, 16);
        this.right.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.top = new ModelRenderer(this, 0, 0);
        this.top.addBox(-7.0F, -8.0F, -8.0F, 14, 1, 16);
        this.top.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bottom = new ModelRenderer(this, 0, 0);
        this.bottom.addBox(-7.0F, 7.0F, -8.0F, 14, 1, 16);
        this.bottom.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    /** Render all four walls at the current GL matrix. */
    public void render(float scale) {
        this.left.render(scale);
        this.right.render(scale);
        this.top.render(scale);
        this.bottom.render(scale);
    }
}
