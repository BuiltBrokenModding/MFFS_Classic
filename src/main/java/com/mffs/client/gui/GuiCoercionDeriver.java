package com.mffs.client.gui;

import com.mffs.MFFS;
import com.mffs.api.gui.GuiSlotType;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.common.container.CoercionDeriverContainer;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.type.TileCoercionDeriver;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class GuiCoercionDeriver extends MFFSGui {

    public GuiCoercionDeriver(EntityPlayer player, TileCoercionDeriver tileentity) {
        super(new CoercionDeriverContainer(player, tileentity), tileentity);
    }

    public void initGui() {
        this.textFieldPos = new Vector2d(30.0D, 43.0D);
        super.initGui();
        //this.buttonList.add(new GuiButton(1, this.width / 2 - 10, this.height / 2 - 28, 58, 20, LanguageRegistry.instance().getStringLocalization("gui.deriver.derive")));
    }

    /**
     * Gets the tile entity represented.
     *
     * @return
     */
    private TileCoercionDeriver getEntity() {
        return ((TileCoercionDeriver) this.frequencyTile);
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        TileCoercionDeriver entity = getEntity();
        this.fontRendererObj.drawString(entity.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(entity.getInventoryName()) / 2, 6, 4210752);

        drawTextWithTooltip("frequency", "%1:", 8, 30, x, y);
        this.textFieldFrequency.drawTextBox();

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        drawTextWithTooltip("upgrade", -95, 140, x, y);
        GL11.glPopMatrix();

        /*if ((this.buttonList.get(1) instanceof GuiButton)) {
            if (!entity.isInversed) {
                ((GuiButton) this.buttonList.get(1)).displayString = LanguageRegistry.instance().getStringLocalization("gui.deriver.derive");
            } else {
                ((GuiButton) this.buttonList.get(1)).displayString = LanguageRegistry.instance().getStringLocalization("gui.deriver.integrate");
            }
        }*/

        renderUniversalDisplay(85, 30, entity.getWattage(), x, y, UnitDisplay.Unit.WATT);
        this.fontRendererObj.drawString(UnitDisplay.getDisplayShort(240L, UnitDisplay.Unit.VOLTAGE), 85, 40, 4210752);

        drawTextWithTooltip("progress", "%1: " + (entity.isActive() ? LanguageRegistry.instance().getStringLocalization("gui.deriver.running") : LanguageRegistry.instance().getStringLocalization("gui.deriver.idle")), 8, 70, x, y);
        drawTextWithTooltip("fortron", "%1: " + UnitDisplay.getDisplayShort(entity.getFortronEnergy(), UnitDisplay.Unit.LITER), 8, 105, x, y);

        this.fontRendererObj.drawString((entity.isInversed ? EnumChatFormatting.RED + "-" : EnumChatFormatting.GREEN + "+") + UnitDisplay.getDisplayShort(entity.getProductionRate() * 20, UnitDisplay.Unit.LITER) + "/s", 118, 117, 4210752);

        super.drawGuiContainerForegroundLayer(x, y);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        TileCoercionDeriver entity = getEntity();
        super.drawGuiContainerBackgroundLayer(f, x, y);


        drawSlot(153, 46);
        drawSlot(153, 66);
        drawSlot(153, 86);


        drawSlot(8, 40);


        drawSlot(8, 82, GuiSlotType.BATTERY);
        drawSlot(28, 82);

        drawBar(50, 84, 1.0F);


        drawForce(8, 115, entity.getFortronEnergy() > 0 ? ((float) entity.getFortronEnergy()) / entity.getFortronCapacity() : 0);
    }


    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        if (guibutton.id == 1) {
            MFFS.channel.sendToServer(new EntityToggle(getEntity(), EntityToggle.TOGGLE_STATE));
        }
    }
}
