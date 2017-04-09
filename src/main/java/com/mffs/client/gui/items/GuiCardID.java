package com.mffs.client.gui.items;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.gui.GuiContainerBase;
import com.mffs.ModularForcefieldSystem;
import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.Permission;
import com.mffs.client.gui.components.GuiScroll;
import com.mffs.common.net.packet.ItemByteToggle;
import com.mffs.common.net.packet.ItemStringToggle;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Created by Poopsicle360 on 7/16/2016.
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
    public GuiCardID(EntityPlayer player)
    {
        super(new ContainerDummy(player, null));
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
        int index = (int) (scroll.getBar() * (buttonList.size() - 1));
        int maxIndex = Math.min(index + 5, Permission.values().length - 1);
        final ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || !(stack.getItem() instanceof ICardIdentification))
        {
            player.closeScreen();
            return;
        }
        int xStart = 80;
        int yStart = 50;
        for (Object obj : buttonList)
        {
            if (obj instanceof GuiButton)
            {
                GuiButton button = (GuiButton) obj;
                if (button.id >= index && button.id <= maxIndex)
                {
                    Permission perm = Permission.getPerm(button.id);
                    ICardIdentification icard = (ICardIdentification) stack.getItem();

                    button.displayString = (stack != null && icard.hasPermission(stack, perm) ? ChatFormatting.GREEN : ChatFormatting.RED) + LanguageRegistry.instance().getStringLocalization("gui." + perm.name() + ".name");
                    button.xPosition = width / 2 - xStart;
                    button.yPosition = height / 2 - yStart + (button.id - index) * 20;
                    button.visible = true;
                }
                else
                {
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

        this.mc.renderEngine.bindTexture(SharedAssets.GUI_COMPONENTS_BARS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int yStart = 33;
        int xStart = xSize - 16;
        int topHeight = 20;
        int bottomHeight = 102;
        int totalSize = topHeight + bottomHeight;

        //Render background for scroll bar TODO make reusable
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yStart, 16, 0, 9, topHeight);
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yStart + topHeight, 16, 139 - bottomHeight, 9, bottomHeight);

        //Render scroll bar
        float heightP = Math.max(Permission.values().length - 6, 0) / (float) Permission.values().length;
        int height = (int) (heightP * totalSize);
        int yPos = Math.max((int) (scroll.getBar() * totalSize) - height + yStart, yStart);

        GL11.glColor4f(1.0F, 0, 0, 1.0F);
        drawTexturedModalRect(containerWidth + xStart, containerHeight + yPos, 16, 0, 9, 2 + height);

    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        if (button.id <= Permission.values().length)
        {
            ModularForcefieldSystem.channel.sendToServer(new ItemByteToggle(button.id));
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
        ModularForcefieldSystem.channel.sendToServer(new ItemStringToggle(textField.getText()));
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
}
