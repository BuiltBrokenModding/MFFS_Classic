package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.jlib.data.science.units.UnitDisplay;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.packet.callback.PacketOpenGUI;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiCoercionDeriver extends GuiMFFS<TileCoercionDeriver>
{
    private static final int TABS = 4;

    public static final int MAIN_GUI_ID = 0;
    public static final int UPGRADE_GUI_ID = 1;
    public static final int LINKS_GUI_ID = 2;
    public static final int SETTINGS_GUI_ID = 3;

    private GuiImageButton mainWindowButton;
    private GuiImageButton upgradesWindowButton;
    private GuiImageButton linksWindowButton;
    private GuiImageButton settingsWindowButton;

    protected int id = 0;

    public GuiCoercionDeriver(EntityPlayer player, TileCoercionDeriver tileentity, int id)
    {
        super(new ContainerCoercionDeriver(player, tileentity, id), tileentity);
        this.id = id;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        int x = guiLeft - 18;
        int y = guiTop + 10;

        //Menu Tabs
        mainWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(MAIN_GUI_ID, x, y, 0, 0).setTexture(GUI_BUTTONS)).setEnabled(id != MAIN_GUI_ID);
        upgradesWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(UPGRADE_GUI_ID, x, y + 19, 7, 0).setTexture(GUI_BUTTONS)).setEnabled(id != UPGRADE_GUI_ID);
        linksWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(LINKS_GUI_ID, x, y + 19 * 2, 6, 0).setTexture(GUI_BUTTONS)).setEnabled(id != LINKS_GUI_ID);
        settingsWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(SETTINGS_GUI_ID, x, y + 19 * 3, 5, 0).setTexture(GUI_BUTTONS)).setEnabled(id != SETTINGS_GUI_ID);

        //TODO implement invert button
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);

        if (MAIN_GUI_ID == id)
        {
            renderUniversalDisplay(85, 30, host.getBattery().getMaxBufferSize(), x, y, UnitDisplay.Unit.JOULES);

            drawTextWithTooltip("fortron", "%1: " + host.getFortronCreationRate(), 8, 105, x, y);

            fontRendererObj.drawString((host.outputPower ? EnumChatFormatting.RED + "-" : EnumChatFormatting.GREEN + "+") + host.getFortronCreationRate(), 118, 117, 4210752);
        }
        else if (UPGRADE_GUI_ID == id)
        {
            drawTextWithTooltip("upgrade", 1, 140, x, y);
        }
        else if (LINKS_GUI_ID == id)
        {
            drawTextWithTooltip("WIP", 1, 140, x, y);
        }
        else if (SETTINGS_GUI_ID == id)
        {
            drawTextWithTooltip("WIP", 1, 140, x, y);
        }

        super.drawGuiContainerForegroundLayer(x, y);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
    {
        super.drawGuiContainerBackgroundLayer(f, x, y);

        //drawBar(50, 84, 1.0F);

        //drawForce(8, 115, host.getFortronEnergy() > 0 ? ((float) host.getFortronEnergy()) / host.getFortronCapacity() : 0);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        final int buttonId = button.id;
        if (buttonId >= 0 && buttonId < TABS && buttonId != id)
        {
            Engine.packetHandler.sendToServer(new PacketOpenGUI(host, buttonId));
        }
        else
        {
            super.actionPerformed(button);
        }
    }
}
