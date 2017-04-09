package com.mffs.common.items.card.id;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.mffs.ModularForcefieldSystem;
import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.Permission;
import com.mffs.client.gui.components.GuiScroll;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by Poopsicle360 on 7/16/2016.
 * Edited by DarkCow on 4/9/2017
 */
public class GuiCardID extends GuiContainerBase
{
    private GuiTextField textField;

    /* The item this interface is interacting with */
    private EntityPlayer player;

    /* Scroll bar for this interface */
    private GuiScroll scroll = new GuiScroll(Math.max(Permission.values().length - 6, 0));

    /**
     * @param player
     */
    public GuiCardID(EntityPlayer player, int slot)
    {
        super(new ContainerCardID(player, player.inventory, slot));
        this.baseTexture = SharedAssets.GUI__MC_EMPTY_FILE;
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        //Create and init user name box
        textField = new GuiTextField(fontRendererObj, 50, 6, 118, 15);
        textField.setMaxStringLength(200);
        textField.setText(((ICardIdentification) player.getCurrentEquippedItem().getItem()).getUsername(player.getCurrentEquippedItem()));

        //Create and init permission buttons
        for (int id = 0; id < Permission.values().length; id++)
        {
            //TODO replace buttons with check boxes to make it more efficient to understand
            //      check box would be a string prefix and a button looking like a checkbox
            buttonList.add(new GuiButton(id, 0, 0, 150, 20, Permission.getPerm(id).name()));
        }
    }

    /**
     * Handles mouse input.
     */
    @Override
    public void handleMouseInput()
    {
        super.handleMouseInput();
        scroll.handleMouseInput();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen()
    {
        final ItemStack stack = player.getCurrentEquippedItem();

        //Exit screen if we are no longer holding an item, solves for player death or inventory clear actions
        if (stack == null || stack.getItem() != ModularForcefieldSystem.itemCardID)
        {
            player.closeScreen();
            return;
        }

        //Get index and max index
        int index = (int) (scroll.getBar() * (buttonList.size() - 1));
        int maxIndex = Math.min(index + 5, Permission.values().length - 1);

        //Cached position offsets
        final int xStart = 80;
        final int yStart = 50;

        //Loop buttons and reset position based on scroll
        for (Object obj : buttonList)
        {
            if (obj instanceof GuiButton)
            {
                GuiButton button = (GuiButton) obj;
                if (button.id >= index && button.id <= maxIndex)
                {
                    //Get permission
                    Permission perm = Permission.getPerm(button.id);
                    //Get card as interface
                    ICardIdentification icard = (ICardIdentification) stack.getItem();

                    //Set button name to match permission node and state,
                    button.displayString = (stack != null && icard.hasPermission(stack, perm) ? ChatFormatting.GREEN : ChatFormatting.RED) + LanguageRegistry.instance().getStringLocalization("gui." + perm.name() + ".name");

                    //Update position to match scroll position
                    button.xPosition = width / 2 - xStart;
                    button.yPosition = height / 2 - yStart + (button.id - index) * 20;

                    //Set visible
                    button.visible = true;
                }
                else
                {
                    //Set not visible if not in scroll area
                    button.visible = false;
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        drawString("Name:", 10, 10);
        textField.drawTextBox();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

        //Set texture and reset color
        this.mc.renderEngine.bindTexture(SharedAssets.GUI_COMPONENTS_BARS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        //Position cache
        final int yStart = 33;
        final int xStart = xSize - 16;

        //Size cache
        final int topHeight = 20;
        final int bottomHeight = 102;
        final int totalSize = topHeight + bottomHeight;

        //Render background for scroll bar TODO make reusable
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yStart, 16, 0, 9, topHeight);
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yStart + topHeight, 16, 139 - bottomHeight, 9, bottomHeight);

        //Render scroll bar
        float heightP = Math.max(Permission.values().length - 6, 0) / (float) Permission.values().length;
        int height = (int) (heightP * totalSize);
        int yPos = Math.max((int) (scroll.getBar() * totalSize) - height + yStart, yStart);

        //Set color to red and render texture
        GL11.glColor4f(1.0F, 0, 0, 1.0F);
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yPos, 16, 0, 9, 2 + height);

    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (stack != null && stack.getItem() == ModularForcefieldSystem.itemCardID)
        {
            if (button.id >= 0 && button.id < Permission.values().length)
            {
                boolean state = ModularForcefieldSystem.itemCardID.hasPermission(stack, Permission.values()[button.id]);
                ModularForcefieldSystem.itemCardID.sendPermPacket(Minecraft.getMinecraft().thePlayer, button.id, !state);
            }
        }
    }

    @Override
    public void keyTyped(char c, int keyID)
    {
        if (!textField.isFocused() || keyID == Keyboard.KEY_ESCAPE)
        {
            super.keyTyped(c, keyID);
        }
        textField.textboxKeyTyped(c, keyID);
        if (textField.isFocused() && keyID == Keyboard.KEY_RETURN)
        {
            sendUsernamePacket();
        }
        onGuiClosed();
    }

    /**
     * Called to send username packet to server
     */
    protected void sendUsernamePacket()
    {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (stack != null && stack.getItem() == ModularForcefieldSystem.itemCardID)
        {
            String text = textField.getText();
            if (text == null)
            {
                text = "";
                textField.setText("");
            }
            else
            {
                text = text.trim();
                textField.setText(text);
            }
            ModularForcefieldSystem.itemCardID.sendUserNamePacket(Minecraft.getMinecraft().thePlayer, text);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int par3)
    {
        super.mouseClicked(x, y, par3);
        if (textField != null)
        {
            textField.mouseClicked(x - this.containerWidth, y - this.containerHeight, par3);
        }
    }

    @Override
    public void onGuiClosed()
    {
        sendUsernamePacket();
        super.onGuiClosed();
    }
}
