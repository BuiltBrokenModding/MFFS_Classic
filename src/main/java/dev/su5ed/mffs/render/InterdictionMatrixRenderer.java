package dev.su5ed.mffs.render;

import dev.su5ed.mffs.blockentity.InterdictionMatrixBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class InterdictionMatrixRenderer extends TileEntitySpecialRenderer<InterdictionMatrixBlockEntity> {
    private static final ResourceLocation BASE_SIDE =
        new ResourceLocation("mffs", "textures/block/interdiction_matrix_side_active.png");
    private static final ResourceLocation BASE_VERTICAL =
        new ResourceLocation("mffs", "textures/block/interdiction_matrix_vertical_active.png");
    private static final ResourceLocation EMISSIVE_SIDE =
        new ResourceLocation("mffs", "textures/block/interdiction_matrix_side_active_emissive.png");
    private static final ResourceLocation EMISSIVE_VERTICAL =
        new ResourceLocation("mffs", "textures/block/interdiction_matrix_vertical_active_emissive.png");

    private static final Map<ResourceLocation, ResourceLocation> EMISSIVE_MAP = new HashMap<>();

    static {
        EMISSIVE_MAP.put(BASE_SIDE, EMISSIVE_SIDE);
        EMISSIVE_MAP.put(BASE_VERTICAL, EMISSIVE_VERTICAL);
    }

    @Override
    public void render(InterdictionMatrixBlockEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (te.getWorld() != null) {
            CodeChickenLibEmissiveCompat.renderBlockEmissiveMulti(
                te.getWorld().getBlockState(te.getPos()), x, y, z, EMISSIVE_MAP, te.isActive());
        }
    }
}
