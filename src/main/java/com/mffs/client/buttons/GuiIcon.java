package com.mffs.client.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * @author Calclavia
 */
public class GuiIcon extends GuiButton {

    public static RenderItem itemRenderer = new RenderItem();

    public ItemStack[] itemStacks;
    private int index = 0;

    /**
     * @param par1
     * @param par2
     * @param par3
     * @param itemStacks
     */
    public GuiIcon(int par1, int par2, int par3, ItemStack... itemStacks) {
        super(par1, par2, par3, 20, 20, "");
        this.itemStacks = itemStacks;
    }

    /**
     * @param ind
     */
    public void setIndex(int ind) {
        if (ind >= 0 && ind < itemStacks.length) {
            this.index = ind;
        }
    }

    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        super.drawButton(par1Minecraft, par2, par3);

        if (this.enabled && (this.itemStacks[this.index] != null)) {
            int yDisplacement = 0;

            //if ((this.itemStacks[this.index].getItem() == Blocks.unlit_redstone_torch.) || (this.itemStacks[this.index].field_77993_c == Block.field_72035_aQ.field_71990_ca)) {
            //    yDisplacement = 0;
            //} else
            if ((this.itemStacks[this.index].getItem() instanceof ItemBlock)) {
                yDisplacement = 3;
            }

            drawItemStack(this.itemStacks[this.index], this.xPosition, this.yPosition + yDisplacement);
        }
    }

    protected void drawItemStack(ItemStack itemStack, int x, int y) {
        x += 2;
        y--;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 500.0F;
        itemRenderer.zLevel = 500.0F;
        if (itemStack != null) {
            itemRenderer.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, itemStack, x, y);
            itemRenderer.renderItemOverlayIntoGUI(fontRenderer, mc.renderEngine, itemStack, x, y);
        }
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }
}
