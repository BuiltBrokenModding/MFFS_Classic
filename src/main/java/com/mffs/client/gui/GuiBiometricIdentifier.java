package com.mffs.client.gui;

import com.mffs.api.card.ICardIdentification;
import com.mffs.api.security.Permission;
import com.mffs.client.buttons.GuiPressableButton;
import com.mffs.common.container.BiometricContainer;
import com.mffs.common.tile.type.TileBiometricIdentifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class GuiBiometricIdentifier extends MFFSGui {

    private GuiTextField textFieldUsername;

    /**
     * @param player
     * @param bio
     */
    public GuiBiometricIdentifier(EntityPlayer player, TileBiometricIdentifier bio) {
        super(new BiometricContainer(player, bio), bio);
    }
    
    public TileBiometricIdentifier getEntity() {
        return (TileBiometricIdentifier) this.frequencyTile;
    }

    @Override
    public void initGui() {

        this.textFieldPos = new Vector2d(109.0D, 92.0D);

        super.initGui();


        this.textFieldUsername = new GuiTextField(this.fontRendererObj, 52, 18, 90, 12);

        this.textFieldUsername.setMaxStringLength(30);


        int x = 0;

        int y = 0;


        for (int i = 0; i < Permission.values().length; i++) {

            x++;

            this.buttonList.add(new GuiPressableButton(i + 1, this.width / 2 - 50 + 20 * x, this.height / 2 - 75 + 20 * y, new Vector2d(18.0D, 18 * i), this, Permission.values()[i].name()));


            if ((i % 3 == 0) && (i != 0)) {

                x = 0;

                y++;

            }

        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        TileBiometricIdentifier entity = getEntity();
        this.fontRendererObj.drawString(entity.getInventoryName(), this.xSize / 2 - this.fontRendererObj.getStringWidth(entity.getInventoryName()) / 2, 6, 4210752);


        drawTextWithTooltip("rights", "%1", 8, 32, x, y, 0);


        try {

            if (entity.getManipulatingCard() != null) {

                ICardIdentification idCard = (ICardIdentification) entity.getManipulatingCard().getItem();


                this.textFieldUsername.drawTextBox();


                if (idCard.getUsername(entity.getManipulatingCard()) != null) {


                    for (int i = 0; i < this.buttonList.size(); i++) {

                        if ((this.buttonList.get(i) instanceof GuiPressableButton)) {

                            GuiPressableButton button = (GuiPressableButton) this.buttonList.get(i);

                            button.enabled = true;


                            int permissionID = i - 1;


                            if (Permission.getPerm(permissionID) != null) {

                                if (idCard.hasPermission(entity.getManipulatingCard(), Permission.getPerm(permissionID))) {

                                    button.stuck = true;

                                } else {

                                    button.stuck = false;

                                }

                            }

                        }

                    }

                }

            } else {
                this.buttonList.forEach(button -> {
                    if ((button instanceof GuiPressableButton)) {

                        ((GuiPressableButton) button).enabled = false;

                    }
                });
            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        this.textFieldFrequency.drawTextBox();


        drawTextWithTooltip("master", 28, 90 + this.fontRendererObj.FONT_HEIGHT / 2, x, y);

        super.drawGuiContainerForegroundLayer(x, y);

    }

    @Override
    public void updateScreen() {

        super.updateScreen();
        TileBiometricIdentifier entity = getEntity();

        if (!this.textFieldUsername.isFocused()) {

            if (entity.getManipulatingCard() != null) {

                ICardIdentification idCard = (ICardIdentification) entity.getManipulatingCard().getItem();


                if (idCard.getUsername(entity.getManipulatingCard()) != null) {

                    this.textFieldUsername.setText(idCard.getUsername(entity.getManipulatingCard()));

                }

            }

        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {

        super.drawGuiContainerBackgroundLayer(f, x, y);


        drawSlot(87, 90);


        drawSlot(7, 45);

        drawSlot(7, 65);

        drawSlot(7, 90);


        for (int var4 = 0; var4 < 9; var4++) {

            drawSlot(8 + var4 * 18 - 1, 110);

        }

    }

    @Override
    protected void keyTyped(char par1, int par2) {

        if ((par1 != 'e') && (par1 != 'E')) {

            super.keyTyped(par1, par2);

        }


        this.textFieldUsername.textboxKeyTyped(par1, par2);


        try {

           // cpw.mods.fml.common.network.PacketDispatcher.sendPacketToServer(ModularForceFieldSystem.PACKET_TILE.getPacket(entity, new Object[]{Integer.valueOf(TileMFFS.TilePacketType.STRING.ordinal()), this.textFieldUsername.func_73781_b()}));

        } catch (NumberFormatException e) {
        }

    }

    @Override
    public void mouseClicked(int x, int y, int par3) {

        super.mouseClicked(x, y, par3);

        this.textFieldUsername.mouseClicked(x - this.containerWidth, y - this.containerHeight, par3);

    }

    @Override
    protected void actionPerformed(GuiButton guiButton) {

        super.actionPerformed(guiButton);


        if (guiButton.id > 0) {

           // cpw.mods.fml.common.network.PacketDispatcher.sendPacketToServer(ModularForceFieldSystem.PACKET_TILE.getPacket(entity, new Object[]{Integer.valueOf(TileMFFS.TilePacketType.TOGGLE_MODE.ordinal()), Integer.valueOf(guiButton.field_73741_f - 1)}));

        }

    }

}
