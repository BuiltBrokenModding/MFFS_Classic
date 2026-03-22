package dev.su5ed.mffs.render.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static dev.su5ed.mffs.MFFSMod.location;

@SideOnly(Side.CLIENT)
public class ForceCubeModel extends ModelBase {
    public static final ResourceLocation CORE_TEXTURE = location("textures/model/force_cube.png");

    // texOffs(0,0), addBox(-8,-8,-8, 16,16,16), texture 64x32
    private final ModelRenderer root;

    public ForceCubeModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.root = new ModelRenderer(this, 0, 0);
        this.root.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        this.root.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    /** Render at the current GL matrix with the given model scale (typically 0.0625F). */
    public void render(float scale) {
        this.root.render(scale);
    }
}
