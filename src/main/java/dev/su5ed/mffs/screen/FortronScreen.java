package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;

public abstract class FortronScreen<T extends FortronMenu<?>> extends BaseScreen<T> {
    // Positions relative to guiLeft/guiTop
    protected int frequencyBoxX = 0, frequencyBoxY = 0;
    protected int frequencyLabelX = 0, frequencyLabelY = 0;
    protected int fortronEnergyBarX = 0, fortronEnergyBarY = 0;
    protected int fortronEnergyBarWidth = 0;

    protected NumericEditBox frequency;
    private FortronChargeWidget chargeWidget;
    private ToggleButton toggleButton;

    public FortronScreen(T menu, InventoryPlayer playerInventory, ResourceLocation background) {
        super(menu, playerInventory, background);
    }

    @Override
    public void initGui() {
        super.initGui();

        // Toggle active button (top-left area)
        this.toggleButton = new ToggleButton(
            this.width / 2 - 82, this.height / 2 - 104,
            this.inventorySlots instanceof FortronMenu<?> fm ? fm.blockEntity::isActive : () -> false,
            () -> {
                boolean newActive = !(((FortronMenu<?>) this.inventorySlots).blockEntity.isActive());
                Network.sendToServer(new ToggleModePacket(
                    ((FortronMenu<?>) this.inventorySlots).blockEntity.getPos(), newActive));
            }
        );
        this.buttonList.add(this.toggleButton);

        // Frequency edit box
        this.frequency = new NumericEditBox(
            this.fontRenderer,
            this.guiLeft + this.frequencyBoxX, this.guiTop + this.frequencyBoxY,
            50, 12,
            ModUtil.translate("screen", "frequency")
        );
        this.frequency.setCanLoseFocus(true);
        this.frequency.setEnableBackgroundDrawing(true);
        this.frequency.setEnabled(true);
        this.frequency.setMaxStringLength(6);
        updateFrequencyValue();
        ((FortronMenu<?>) this.inventorySlots).setFrequencyChangeListener(this::updateFrequencyValue);

        // Fortron charge widget
        this.chargeWidget = new FortronChargeWidget(
            this.guiLeft + this.fortronEnergyBarX, this.guiTop + this.fortronEnergyBarY,
            this.fortronEnergyBarWidth, 11,
            () -> {
                FortronMenu<?> fm = (FortronMenu<?>) this.inventorySlots;
                double cap = fm.blockEntity.fortronStorage.getFortronCapacity();
                return cap > 0 ? fm.blockEntity.fortronStorage.getStoredFortron() / cap : 0.0;
            }
        );
        this.buttonList.add(this.chargeWidget);
    }

    private void updateFrequencyValue() {
        this.frequency.setResponder(null);
        this.frequency.setValue(Integer.toString(((FortronMenu<?>) this.inventorySlots).blockEntity.fortronStorage.getFrequency()));
        this.frequency.setResponder(this::onFrequencyChanged);
    }

    private void onFrequencyChanged() {
        String str = this.frequency.getValue();
        int freq = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.sendToServer(new UpdateFrequencyPacket(((FortronMenu<?>) this.inventorySlots).blockEntity.getPos(), freq));
    }

    @Override
    public void renderFg(int mouseX, int mouseY, float partialTick) {
        this.frequency.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        // Frequency label with tooltip
        drawWithTooltip(
            this.frequencyLabelX, this.frequencyLabelY, GuiColors.DARK_GREY,
            ModUtil.translate("screen", "frequency"),
            ModUtil.translate("screen", "frequency.tooltip")
        );
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.frequency.isFocused()) {
            this.frequency.textboxKeyTyped(typedChar, keyCode);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.frequency.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.frequency.updateCursorCounter();
    }
}

