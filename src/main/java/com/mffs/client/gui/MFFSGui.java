package com.mffs.client.gui;

import com.mffs.MFFS;
import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.gui.GuiContainerBase;
import com.mffs.client.buttons.GuiIcon;
import com.mffs.common.TileMFFS;
import com.mffs.common.net.packet.ChangeFrequency;
import com.mffs.common.net.packet.EntityToggle;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector2d;

/**
 * @author Calclavia
 */
public class MFFSGui extends GuiContainerBase {

    public int animation;
    protected GuiTextField textFieldFrequency;
    protected Vector2d textFieldPos = new Vector2d();
    protected IBlockFrequency frequencyTile;

    /**
     * @param container
     */
    public MFFSGui(Container container) {
        super(container);
        ySize = 217;
    }

    /**
     * @param container
     * @param freq
     */
    public MFFSGui(Container container, IBlockFrequency freq) {
        this(container);
        this.frequencyTile = freq;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        this.buttonList.add(new GuiIcon(0, this.width / 2 - 82, this.height / 2 - 104, new ItemStack(Blocks.redstone_torch), null));
        Keyboard.enableRepeatEvents(true);
        if (this.frequencyTile != null) {
            this.textFieldFrequency = new GuiTextField(this.fontRendererObj, (int) this.textFieldPos.x, (int) this.textFieldPos.y, 50, 12);
            this.textFieldFrequency.setMaxStringLength(6);
            this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);

        if (this.textFieldFrequency != null) {

            if (!Character.isDigit(par1))
                return;
            this.textFieldFrequency.textboxKeyTyped(par1, par2);

            try {
                int newFrequency = Integer.parseInt(this.textFieldFrequency.getText());
                this.frequencyTile.setFrequency(newFrequency);
                this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
                MFFS.channel.sendToServer(new ChangeFrequency(((TileEntity) this.frequencyTile), newFrequency));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);

        if ((this.frequencyTile != null) && (guiButton.id == 0)) {
            MFFS.channel.sendToServer(new EntityToggle((TileEntity) frequencyTile, EntityToggle.REDSTONE_TOGGLE));
        }
    }


    @Override
    public void updateScreen() {
        super.updateScreen();

        if (this.textFieldFrequency != null) {
            if (!this.textFieldFrequency.isFocused()) {
                this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
            }
        }

        if ((this.frequencyTile instanceof TileMFFS)) {
            if ((this.buttonList.size() > 0) && (this.buttonList.get(0) != null)) {
                ((GuiIcon) this.buttonList.get(0)).setIndex(((TileMFFS) this.frequencyTile).isActive() ? 0 : 1);
            }
        }
    }


    @Override
    public void mouseClicked(int x, int y, int par3) {
        super.mouseClicked(x, y, par3);

        if (this.textFieldFrequency != null) {
            this.textFieldFrequency.mouseClicked(x - this.containerWidth, y - this.containerHeight, par3);
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if (this.textFieldFrequency != null) {
            if (func_146978_c((int) this.textFieldPos.x, (int) this.textFieldPos.y, this.textFieldFrequency.getWidth(), 12, mouseX, mouseY)) {
                this.tooltip = LanguageRegistry.instance().getStringLocalization("gui.frequency.tooltip");
            }
        }
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(var1, x, y);

        if ((this.frequencyTile instanceof IBiometricIdentifierLink)) {
            drawBulb(167, 4, ((IBiometricIdentifierLink) this.frequencyTile).getBiometricIdentifier() != null);
        }
    }
}
