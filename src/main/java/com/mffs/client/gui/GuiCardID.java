package com.mffs.client.gui;

import com.mffs.api.card.ICardIdentification;
import com.mffs.api.gui.GuiContainerBase;
import com.mffs.api.security.Permission;
import com.mffs.client.gui.GuiScroll;
import com.mffs.common.container.HotBarContainer;
import com.mffs.common.container.PlayerContainer;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public class GuiCardID extends GuiContainerBase {

    /* Text field of this interface */
    private GuiTextField textField;

    /* The item this interface is interacting with */
    private EntityPlayer player;

    /* Scroll bar for this interface */
    private GuiScroll scroll = new GuiScroll(Math.max(Permission.values().length - 4, 0));

    /**
     *
     * @param player
     */
    public GuiCardID(EntityPlayer player) {
        super(new HotBarContainer(player));
        this.player = player;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        textField = new GuiTextField(fontRendererObj, 50, 30, 80, 15);
        textField.setMaxStringLength(20);
        textField.setText(((ICardIdentification)player.getCurrentEquippedItem().getItem()).getUsername(player.getCurrentEquippedItem()));
        for(int id = 0; id < Permission.values().length; id++)
            buttonList.add(new GuiButton(id, 0, 0, 160, 20, Permission.getPerm(id).name()));
    }

    /**
     * Handles mouse input.
     */
    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        scroll.handleMouseInput();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
    public void updateScreen() {
        int index = (int) (scroll.getBar() * (buttonList.size() - 1));
        int maxIndex = Math.min(index + 3, Permission.values().length - 1);
        buttonList.forEach(button -> {
            if(button instanceof GuiButton) {
                GuiButton b = (GuiButton) button;
                if(b.id >= index && b.id <= maxIndex) {

                    Permission perm = Permission.getPerm(b.id);
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (!(stack.getItem() instanceof ICardIdentification))
                        return;
                    ICardIdentification icard = (ICardIdentification) stack.getItem();
                    b.displayString = (stack != null && icard.hasPermission(stack, perm) ? ChatFormatting.GREEN : ChatFormatting.RED) + LanguageRegistry.instance().getStringLocalization("gui." + perm.name() + ".name");
                    b.xPosition = width / 2 - 80;
                    b.yPosition = height / 2 - 60 + (b.id - index) * 20;
                    b.visible = true;
                } else {
                    b.visible = false;
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        textField.drawTextBox();
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        //MFFS.channel.sendToServer(new ItemByteToggle());

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
    public void keyTyped(char c, int p_73869_2_)
    {
        if (p_73869_2_ == 1 || p_73869_2_ == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            super.keyTyped(c, p_73869_2_);
        }
        textField.textboxKeyTyped(c, p_73869_2_);
    }
}
