package com.mffs.client.gui.items;

import com.mffs.MFFS;
import com.mffs.api.card.ICardIdentification;
import com.mffs.api.gui.GuiContainerBase;
import com.mffs.api.security.Permission;
import com.mffs.client.gui.components.GuiScroll;
import com.mffs.common.container.HotBarContainer;
import com.mffs.common.net.packet.ItemByteToggle;
import com.mffs.common.net.packet.ItemStringToggle;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Poopsicle360 on 7/16/2016.
 */
public class GuiCardID extends GuiItemBase {

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
        final ItemStack stack = player.getCurrentEquippedItem();
        if (stack == null || !(stack.getItem() instanceof ICardIdentification)) {
            player.closeScreen();
            return;
        }
        buttonList.forEach(button -> {
            if(button instanceof GuiButton) {
                GuiButton b = (GuiButton) button;
                if(b.id >= index && b.id <= maxIndex) {

                    Permission perm = Permission.getPerm(b.id);
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

        if(button.id <= Permission.values().length)
            MFFS.channel.sendToServer(new ItemByteToggle(button.id));
    }

    @Override
    public void keyTyped(char c, int p_73869_2_)
    {
        super.keyTyped(c, p_73869_2_);
        MFFS.channel.sendToServer(new ItemStringToggle(textField.getText()));
    }
}
