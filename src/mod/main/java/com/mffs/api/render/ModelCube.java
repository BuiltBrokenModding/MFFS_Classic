package com.mffs.api.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class ModelCube extends ModelBase {

    public static final ModelCube INSTNACE = new ModelCube();
    private ModelRenderer cube;

    public ModelCube() {
        this.cube = new ModelRenderer(this, 0, 0);
        int size = 16;
        this.cube.addBox(-size / 2, -size / 2, -size / 2, size, size, size);
        this.cube.setTextureSize(112, 70);
        this.cube.mirror = true;
    }

    public void render() {
        this.cube.render(0.0625F);
    }
}
