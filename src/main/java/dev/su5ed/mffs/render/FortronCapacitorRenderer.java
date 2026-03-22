package dev.su5ed.mffs.render;

import dev.su5ed.mffs.blockentity.FortronCapacitorBlockEntity;
import dev.su5ed.mffs.compat.CodeChickenLibEmissiveCompat;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// Renders the emissive blue-accent overlay on the Fortron Capacitor when active.

@SideOnly(Side.CLIENT)
public class FortronCapacitorRenderer extends TileEntitySpecialRenderer<FortronCapacitorBlockEntity> {
    private static final ResourceLocation TEXTURE_EMISSIVE =
        new ResourceLocation("mffs", "textures/model/fortron_capacitor_emissive.png");

    @Override
    public void render(FortronCapacitorBlockEntity te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (te.getWorld() != null) {
            CodeChickenLibEmissiveCompat.renderBlockEmissive(
                te.getWorld().getBlockState(te.getPos()), x, y, z, TEXTURE_EMISSIVE, te.isActive());
        }
    }
}
