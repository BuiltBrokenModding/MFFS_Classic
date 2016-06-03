package com.mffs.client;

import com.mffs.api.IBiometricIdentifierLink;
import com.mffs.api.IBlockFrequency;
import com.mffs.api.gui.GuiContainerBase;
import com.mffs.client.buttons.GuiIcon;
import com.mffs.model.TileMFFS;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector2d;

/**
 * Created by pwaln on 6/2/2016.
 */
public class MFFSGui extends GuiContainerBase {

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
        this.buttonList.add(new GuiIcon(0, this.width / 2 - 82, this.height / 2 - 104, new ItemStack(Blocks.redstone_torch), new ItemStack(Blocks.unlit_redstone_torch)));
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


            this.textFieldFrequency.textboxKeyTyped(par1, par2);

            try {
                int newFrequency = Math.max(0, Integer.parseInt(this.textFieldFrequency.getText()));
                this.frequencyTile.setFrequency(newFrequency);
                this.textFieldFrequency.setText(this.frequencyTile.getFrequency() + "");
                // PacketDispatcher.sendPacketToServer(ModularForceFieldSystem.PACKET_TILE.getPacket((TileEntity)this.frequencyTile, new Object[] { Integer.valueOf(TileMFFS.TilePacketType.FREQUENCY.ordinal()), Integer.valueOf(this.frequencyTile.getFrequency()) }));
            } catch (NumberFormatException e) {
            }
        }
    }


    @Override
    protected void actionPerformed(GuiButton guiButton) {
        super.actionPerformed(guiButton);

        if ((this.frequencyTile != null) && (guiButton.id == 0)) {
            // PacketDispatcher.sendPacketToServer(ModularForceFieldSystem.PACKET_TILE.getPacket((TileEntity)this.frequencyTile, new Object[] { Integer.valueOf(TileMFFS.TilePacketType.TOGGLE_ACTIVATION.ordinal()) }));
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
                ((GuiIcon) this.buttonList.get(0)).setIndex(((TileMFFS) this.frequencyTile).isActive() ? 1 : 0);
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
            if (this.isWithinBounds((int) this.textFieldPos.x, (int) this.textFieldPos.y, this.textFieldFrequency.getWidth(), 12, mouseX, mouseY)) {
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
