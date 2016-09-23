package com.mffs.client.gui;

import com.mffs.ModularForcefieldSystem;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.client.buttons.GuiIcon;
import com.mffs.client.gui.base.GuiMatrix;
import com.mffs.common.container.entity.ForceFieldProjectorContainer;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.TileFieldMatrix;
import com.mffs.common.tile.type.TileForceFieldProjector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
//TODO: Add a seperate disintigration / camoflauge slot
public class GuiForceFieldProjector extends GuiMatrix {

    /**
     * @param player
     * @param entity
     */
    public GuiForceFieldProjector(EntityPlayer player, TileForceFieldProjector entity) {
        super(new ForceFieldProjectorContainer(player, entity), entity);
    }

    @Override
    public void initGui() {
        this.textFieldPos = new Vector2d(30, 115);
        super.initGui();
        this.buttonList.add(new GuiIcon(1, this.width / 2 - 110, this.height / 2 - 82, null, new ItemStack(Items.compass)));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        TileFieldMatrix proj = getMatrix();
        this.fontRendererObj.drawString(proj.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(proj.getInventoryName()) / 2, 6, 4210752);

        this.fontRendererObj.drawString("Filters", 20, 20, 4210752);

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();

        this.textFieldFrequency.drawTextBox();

        int cost = proj.getFortronCost() * 20;
        this.fontRendererObj.drawString(EnumChatFormatting.RED + (cost > 0 ? "-" : "") + UnitDisplay.getDisplayShort(cost, UnitDisplay.Unit.LITER) + "/s", 120, 119, 4210752);
        super.drawGuiContainerForegroundLayer(x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        drawSlot(7, 113); //Frequency slot

        for(int x1 = 0; x1 < 2; x1++)
            for(int y1 = 0; y1 < 3; y1++)
                drawSlot(20 + 18 * x1, 30 + 18 * y1);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == 1) {
            ModularForcefieldSystem.channel.sendToServer(new EntityToggle(getMatrix(), EntityToggle.ABSOLUTE_TOGGLE));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiIcon) this.buttonList.get(1)).setIndex(getMatrix().isAbs ? 1 : 0);
    }
}
