package com.builtbroken.mffs.content.biometric;

import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiBiometricIdentifier extends GuiMFFS<TileBiometricIdentifier>
{

    /**
     * @param player
     * @param bio
     */
    public GuiBiometricIdentifier(EntityPlayer player, TileBiometricIdentifier bio)
    {
        super(new BiometricContainer(player, bio), bio);
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);
        this.fontRendererObj.drawString("Frequency", 33, 108, 4210752);
        this.fontRendererObj.drawString(EnumChatFormatting.AQUA + "id and Group Cards", 40, 25, 4210752);

        super.drawGuiContainerForegroundLayer(x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        drawContainerSlots();
    }
}
