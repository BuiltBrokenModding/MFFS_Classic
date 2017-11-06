package com.builtbroken.mffs.content.projector;

import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mffs.api.utils.UnitDisplay;
import com.builtbroken.mffs.client.gui.base.GuiMatrix;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiForceFieldProjector extends GuiMatrix<TileForceFieldProjector>
{
    GuiImageButton buttonAbsoluteDirection;
    /**
     * @param player
     * @param entity
     */
    public GuiForceFieldProjector(EntityPlayer player, TileForceFieldProjector entity)
    {
        super(new ForceFieldProjectorContainer(player, entity), entity);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonAbsoluteDirection = addButton(GuiImageButton.newButtonEmpty(1, this.width / 2 - 110, this.height / 2 - 82));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);

        this.fontRendererObj.drawString("Filters", 20, 20, 4210752);

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();

        int cost = host.getFortronCost() * 20;
        this.fontRendererObj.drawString(EnumChatFormatting.RED + (cost > 0 ? "-" : "")
                + UnitDisplay.getDisplayShort(cost, UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI) + "/s", 120, 119, 4210752);
        super.drawGuiContainerForegroundLayer(x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        drawContainerSlots();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        super.actionPerformed(guiButton);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        //((GuiIcon) this.buttonList.get(1)).setIndex(host.useAbsoluteDirection ? 1 : 0);
    }
}
