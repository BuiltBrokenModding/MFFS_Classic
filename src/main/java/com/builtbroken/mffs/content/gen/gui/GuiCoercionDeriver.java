package com.builtbroken.mffs.content.gen.gui;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.core.network.packet.callback.PacketOpenGUI;
import com.builtbroken.mc.prefab.gui.buttons.GuiImageButton;
import com.builtbroken.mffs.MFFS;
import com.builtbroken.mffs.client.gui.base.GuiMFFS;
import com.builtbroken.mffs.content.gen.TileCoercionDeriver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class GuiCoercionDeriver extends GuiMFFS<TileCoercionDeriver>
{
    private static final int TABS = 4;

    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(MFFS.DOMAIN, References.GUI_DIRECTORY + "tile.coercion.deriver.png");

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
        mainWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(TileCoercionDeriver.GUI_MAIN, x, y, 0, 0).setTexture(GUI_BUTTONS))
                .setEnabled(id != TileCoercionDeriver.GUI_MAIN);
        upgradesWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(TileCoercionDeriver.GUI_UPGRADES, x, y + 19, 7, 0).setTexture(GUI_BUTTONS))
                .setEnabled(id != TileCoercionDeriver.GUI_UPGRADES);
        linksWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(TileCoercionDeriver.GUI_LINKS, x, y + 19 * 2, 6, 0).setTexture(GUI_BUTTONS))
                .setEnabled(id != TileCoercionDeriver.GUI_LINKS);
        settingsWindowButton = (GuiImageButton) addButton(GuiImageButton.newButton18(TileCoercionDeriver.GUI_SETTINGS, x, y + 19 * 3, 5, 0).setTexture(GUI_BUTTONS))
                .setEnabled(id != TileCoercionDeriver.GUI_SETTINGS);

        //TODO implement invert button
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        this.fontRendererObj.drawString(host.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(host.getInventoryName()) / 2, 6, 4210752);

        if (TileCoercionDeriver.GUI_MAIN == id)
        {
            //renderUniversalDisplay(85, 30, host.getBattery().getMaxBufferSize(), x, y, UnitDisplay.Unit.JOULES);

            final String fortron = "Fortron: " + host.getFortronEnergy() + "/" + host.getFortronCapacity() + "ml";
            drawString(fortron, 8, 105);

            fontRendererObj.drawString((host.outputPower ? EnumChatFormatting.RED + "-" : EnumChatFormatting.GREEN + "+") + (host.getFortronCreationRate() * 20) + "ml/s",
                    10 + fontRendererObj.getStringWidth(fortron), 105, 4210752);
        }
        else if (TileCoercionDeriver.GUI_UPGRADES == id)
        {
            drawStringCentered("Upgrades", this.xSize / 2, 20);
        }
        else if (TileCoercionDeriver.GUI_LINKS == id)
        {
            drawStringCentered("Connections", this.xSize / 2, 20);
            drawStringCentered("Not implemented", this.xSize / 2, 40);
        }
        else if (TileCoercionDeriver.GUI_SETTINGS == id)
        {
            drawStringCentered("Settings", this.xSize / 2, 20);
            drawStringCentered("Not implemented", this.xSize / 2, 40);
        }

        super.drawGuiContainerForegroundLayer(x, y);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouse_x, int mouse_y)
    {
        super.drawGuiContainerBackgroundLayer(f, mouse_x, mouse_y);

        if (TileCoercionDeriver.GUI_MAIN == id)
        {
            this.mc.renderEngine.bindTexture(GUI_TEXTURE);
            //------------------------------
            //Fortron tank render

            int x = guiLeft + 10;
            int y = guiTop + 20;

            //Background
            this.drawTexturedModalRect(x, y, 0, 167, 94, 50);

            //Get color
            Color color = Color.BLUE;
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0F);

            //Calculate size
            float volume_render = (float) host.getFortronEnergy() / (float) host.getFortronCapacity();
            int renderHeight = (int) Math.floor(50 * volume_render);

            //Render fluid
            this.drawTexturedModalRect(x + 1, y - 1 + (50 - renderHeight), 95 + 20, 167, 94, renderHeight);

            //Render overlay
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(x + 1, y, 95, 167, 20, 50);
            //------------------------------
        }
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
