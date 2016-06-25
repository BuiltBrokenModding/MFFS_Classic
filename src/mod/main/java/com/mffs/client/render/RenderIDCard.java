package com.mffs.client.render;

import com.mffs.api.card.ICardIdentification;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class RenderIDCard implements IItemRenderer {
    /**
     * Checks if this renderer should handle a specific item's render type
     *
     * @param item The item we are trying to render
     * @param type A render type to check if this renderer handles
     * @return true if this renderer should handle the given render type,
     * otherwise false
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    /**
     * Checks if certain helper functionality should be executed for this renderer.
     * See ItemRendererHelper for more info
     *
     * @param type   The render type
     * @param item   The ItemStack being rendered
     * @param helper The type of helper functionality to be ran
     * @return True to run the helper functionality, false to not.
     */
    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    /**
     * Called to do the actual rendering, see ItemRenderType for details on when specific
     * types are run, and what extra data is passed into the data parameter.
     *
     * @param type      The render type
     * @param itemStack The ItemStack being rendered
     * @param data      Extra Type specific data
     */
    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemStack, Object... data) {
        if ((itemStack.getItem() instanceof ICardIdentification)) {
            ICardIdentification card = (ICardIdentification) itemStack.getItem();

            GL11.glPushMatrix();
            GL11.glDisable(2884);

            transform(type);
            renderItemIcon(itemStack.getIconIndex());

            if (type != IItemRenderer.ItemRenderType.INVENTORY) {

                GL11.glTranslatef(0.0F, 0.0F, -5.0E-4F);
            }

            renderPlayerFace(getSkinFace(card.getUsername(itemStack)));

            GL11.glEnable(2884);
            GL11.glPopMatrix();
        }
    }


    private void transform(IItemRenderer.ItemRenderType type) {
        float scale = 0.0625F;

        if (type != IItemRenderer.ItemRenderType.INVENTORY) {
            GL11.glScalef(scale, -scale, -scale);
            GL11.glTranslatef(20.0F, -16.0F, 0.0F);
            GL11.glRotatef(180.0F, 1.0F, 1.0F, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
        }
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            GL11.glTranslatef(20.0F, 0.0F, 0.0F);
            GL11.glRotatef((float) Minecraft.getSystemTime() / 12.0F % 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-8.0F, 0.0F, 0.0F);
            GL11.glTranslated(0.0D, 2.0D * Math.sin(Minecraft.getSystemTime() / 512.0D % 360.0D), 0.0D);
        }
    }

    private ResourceLocation getSkinFace(String name) {
        try {
            ResourceLocation resourcelocation;

            if ((name != null) && (!name.isEmpty())) {
                resourcelocation = AbstractClientPlayer.getLocationSkin(name);
                AbstractClientPlayer.getDownloadImageSkin(resourcelocation, name);
                return resourcelocation;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void renderPlayerFace(ResourceLocation resourcelocation) {
        if (resourcelocation != null) {
            Vector2d translation = new Vector2d(9.0D, 5.0D);
            int xSize = 4;
            int ySize = 4;

            int topLX = (int) translation.x;
            int topRX = (int) translation.x + xSize;
            int botLX = (int) translation.x;
            int botRX = (int) translation.x + xSize;

            int topLY = (int) translation.y;
            int topRY = (int) translation.y;
            int botLY = (int) translation.y + ySize;
            int botRY = (int) translation.y + ySize;

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(resourcelocation);


            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glBegin(7);

            GL11.glTexCoord2f(0.125F, 0.25F);
            GL11.glVertex2f(topLX, topLY);

            GL11.glTexCoord2f(0.125F, 0.5F);
            GL11.glVertex2f(botLX, botLY);

            GL11.glTexCoord2f(0.25F, 0.5F);
            GL11.glVertex2f(botRX, botRY);

            GL11.glTexCoord2f(0.25F, 0.25F);
            GL11.glVertex2f(topRX, topRY);

            GL11.glEnd();

            GL11.glBegin(7);

            GL11.glTexCoord2f(0.625F, 0.25F);
            GL11.glVertex2f(topLX, topLY);

            GL11.glTexCoord2f(0.625F, 0.5F);
            GL11.glVertex2f(botLX, botLY);

            GL11.glTexCoord2f(0.75F, 0.5F);
            GL11.glVertex2f(botRX, botRY);

            GL11.glTexCoord2f(0.75F, 0.25F);
            GL11.glVertex2f(topRX, topRY);

            GL11.glEnd();
        }
    }

    private void renderItemIcon(IIcon icon) {
        GL11.glBegin(7);

        GL11.glTexCoord2f(icon.getMinU(), icon.getMinV());
        GL11.glVertex2f(0.0F, 0.0F);

        GL11.glTexCoord2f(icon.getMinU(), icon.getMaxV());
        GL11.glVertex2f(0.0F, 16.0F);

        GL11.glTexCoord2f(icon.getMaxU(), icon.getMaxV());
        GL11.glVertex2f(16.0F, 16.0F);

        GL11.glTexCoord2f(icon.getMaxU(), icon.getMinV());
        GL11.glVertex2f(16.0F, 0.0F);

        GL11.glEnd();
    }

    private void renderItem3D(EntityLiving par1EntityLiving, ItemStack par2ItemStack, int par3) {
        IIcon icon = par1EntityLiving.getItemIcon(par2ItemStack, par3);

        if (icon == null) {
            GL11.glPopMatrix();
            return;
        }

        Tessellator tessellator = Tessellator.instance;
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(32826);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
        GL11.glDisable(32826);
    }
}
