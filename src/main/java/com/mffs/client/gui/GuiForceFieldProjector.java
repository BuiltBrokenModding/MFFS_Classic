package com.mffs.client.gui;

import com.mffs.MFFS;
import com.mffs.api.gui.GuiSlotType;
import com.mffs.api.utils.UnitDisplay;
import com.mffs.api.vector.Matrix2d;
import com.mffs.client.buttons.GuiIcon;
import com.mffs.common.container.ForceFieldProjectorContainer;
import com.mffs.common.net.packet.EntityToggle;
import com.mffs.common.tile.type.TileForceFieldProjector;
import cpw.mods.fml.common.registry.LanguageRegistry;
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
public class GuiForceFieldProjector extends MFFSGui {

    /**
     * @param player
     * @param entity
     */
    public GuiForceFieldProjector(EntityPlayer player, TileForceFieldProjector entity) {
        super(new ForceFieldProjectorContainer(player, entity), entity);
    }

    @Override
    public void initGui() {
        this.textFieldPos = new Vector2d(48, 91);
        super.initGui();
        this.buttonList.add(new GuiIcon(1, this.width / 2 - 82, this.height / 2 - 82, null, new ItemStack(Items.compass)));

        this.tooltips.put(new Matrix2d(new Vector2d(117, 44), new Vector2d(135, 62)), LanguageRegistry.instance().getStringLocalization("gui.projector.mode"));

        this.tooltips.put(new Matrix2d(new Vector2d(90, 17), new Vector2d(108, 35)), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));
        this.tooltips.put(new Matrix2d(new Vector2d(144, 17), new Vector2d(162, 35)), LanguageRegistry.instance().getStringLocalization("gui.projector.up"));

        this.tooltips.put(new Matrix2d(new Vector2d(90, 71), new Vector2d(108, 89)), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));
        this.tooltips.put(new Matrix2d(new Vector2d(144, 71), new Vector2d(162, 89)), LanguageRegistry.instance().getStringLocalization("gui.projector.down"));

        String north = LanguageRegistry.instance().getStringLocalization("gui.projector.north");
        String south = LanguageRegistry.instance().getStringLocalization("gui.projector.south");
        String west = LanguageRegistry.instance().getStringLocalization("gui.projector.west");
        String east = LanguageRegistry.instance().getStringLocalization("gui.projector.east");

        if (!getProj().isAbs) {
            north = LanguageRegistry.instance().getStringLocalization("gui.projector.front");
            south = LanguageRegistry.instance().getStringLocalization("gui.projector.back");
            west = LanguageRegistry.instance().getStringLocalization("gui.projector.left");
            east = LanguageRegistry.instance().getStringLocalization("gui.projector.right");
        }

        this.tooltips.put(new Matrix2d(new Vector2d(108, 17), new Vector2d(126, 35)), north);
        this.tooltips.put(new Matrix2d(new Vector2d(126, 17), new Vector2d(144, 35)), north);

        this.tooltips.put(new Matrix2d(new Vector2d(108, 71), new Vector2d(126, 107)), south);
        this.tooltips.put(new Matrix2d(new Vector2d(126, 71), new Vector2d(144, 107)), south);

        this.tooltips.put(new Matrix2d(new Vector2d(90, 35), new Vector2d(126, 53)), west);
        this.tooltips.put(new Matrix2d(new Vector2d(90, 53), new Vector2d(126, 71)), west);

        this.tooltips.put(new Matrix2d(new Vector2d(144, 35), new Vector2d(162, 53)), east);
        this.tooltips.put(new Matrix2d(new Vector2d(144, 53), new Vector2d(162, 71)), east);
    }

    public TileForceFieldProjector getProj() {
        return (TileForceFieldProjector) frequencyTile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        TileForceFieldProjector proj = getProj();
        this.fontRendererObj.drawString(proj.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(proj.getInventoryName()) / 2, 6, 4210752);

        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        this.fontRendererObj.drawString(proj.getDirection().name(), -82, 10, 4210752);
        GL11.glPopMatrix();

        drawTextWithTooltip("matrix", 40, 20, x, y);
        this.textFieldFrequency.drawTextBox();

        drawTextWithTooltip("fortron", "%1: " + UnitDisplay.getDisplayShort(proj.getFortronEnergy(), UnitDisplay.Unit.LITER) + "/" + UnitDisplay.getDisplay(proj.getFortronCapacity(), UnitDisplay.Unit.LITER, UnitDisplay.UnitPrefix.MILLI), 8, 110, x, y);
        int cost = proj.getFortronCost() * 20;
        this.fontRendererObj.drawString(EnumChatFormatting.RED + (cost > 0 ? "-" : "") + UnitDisplay.getDisplayShort(cost, UnitDisplay.Unit.LITER) + "/s", 118, 121, 4210752);
        super.drawGuiContainerForegroundLayer(x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);
        TileForceFieldProjector entity = getProj();
        drawSlot(9, 88);
        drawSlot(27, 88);

        drawSlot(117, 44, GuiSlotType.NONE, 1, 0.4F, 0.4F);

        for (int xSlot = 0; xSlot < 4; xSlot++) {
            for (int ySlot = 0; ySlot < 4; ySlot++) {
                if (!(xSlot >= 1 && xSlot <= 2 && ySlot >= 1 && ySlot <= 2)) {
                    GuiSlotType type = GuiSlotType.NONE;

                    if ((xSlot == 0) && (ySlot == 0)) {
                        type = GuiSlotType.ARR_UP_LEFT;
                    } else if ((xSlot == 0) && (ySlot == 3)) {
                        type = GuiSlotType.ARR_DOWN_LEFT;
                    } else if ((xSlot == 3) && (ySlot == 0)) {
                        type = GuiSlotType.ARR_UP_RIGHT;
                    } else if ((xSlot == 3) && (ySlot == 3)) {
                        type = GuiSlotType.ARR_DOWN_RIGHT;
                    } else if (ySlot == 0) {
                        type = GuiSlotType.ARR_UP;
                    } else if (ySlot == 3) {
                        type = GuiSlotType.ARR_DOWN;
                    } else if (xSlot == 0) {
                        type = GuiSlotType.ARR_LEFT;
                    } else if (xSlot == 3) {
                        type = GuiSlotType.ARR_RIGHT;
                    }

                    drawSlot(90 + 18 * xSlot, 17 + 18 * ySlot, type);
                }
            }
        }


        for (int xSlot = 0; xSlot < 3; xSlot++) {

            for (int ySlot = 0; ySlot < 2; ySlot++) {

                drawSlot(30 + 18 * xSlot, 35 + 18 * ySlot);
            }
        }
        drawForce(8, 120, entity.getFortronEnergy() > 0 ? ((float) entity.getFortronEnergy()) / entity.getFortronCapacity() : 0);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);
        if (guiButton.id == 1) {
            MFFS.channel.sendToServer(new EntityToggle(getProj(), EntityToggle.ABSOLUTE_TOGGLE));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        ((GuiIcon) this.buttonList.get(1)).setIndex(getProj().isAbs ? 1 : 0);
    }
}
