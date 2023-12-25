package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class FortronScreen<T extends FortronMenu<?>> extends BaseScreen<T> {
    protected IntIntPair frequencyBoxPos = IntIntPair.of(0, 0);
    protected IntIntPair frequencyLabelPos = IntIntPair.of(0, 0);
    protected IntIntPair fortronEnergyBarPos = IntIntPair.of(0, 0);
    protected int fortronEnergyBarWidth;

    private NumericEditBox frequency;

    public FortronScreen(T menu, Inventory playerInventory, Component title, ResourceLocation background) {
        super(menu, playerInventory, title, background);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ToggleButton(this.width / 2 - 82, this.height / 2 - 104, this.menu.blockEntity::isActive,
            () -> Network.INSTANCE.sendToServer(new ToggleModePacket(this.menu.blockEntity.getBlockPos(), !this.menu.blockEntity.isActive()))));

        this.frequency = new NumericEditBox(this.font, this.leftPos + this.frequencyBoxPos.leftInt(), this.topPos + this.frequencyBoxPos.rightInt(), 50, 12, ModUtil.translate("screen", "frequency"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setBordered(true);
        this.frequency.setEditable(true);
        this.frequency.setMaxLength(6);
        updateFrequencyValue();
        addWidget(this.frequency);
        this.menu.setFrequencyChangeListener(this::updateFrequencyValue);

        addRenderableWidget(new FortronChargeWidget(this.leftPos + this.fortronEnergyBarPos.leftInt(), this.topPos + this.fortronEnergyBarPos.rightInt(), this.fortronEnergyBarWidth, 11, Component.empty(),
            () -> this.menu.blockEntity.fortronStorage.getStoredFortron() / (double) this.menu.blockEntity.fortronStorage.getFortronCapacity()));
    }

    private void updateFrequencyValue() {
        this.frequency.setResponder(null);
        this.frequency.setValue(Integer.toString(this.menu.blockEntity.fortronStorage.getFrequency()));
        this.frequency.setResponder(this::onFrequencyChanged);
    }

    // TODO Test edit box tick

    @Override
    public void renderFg(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.frequency.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        drawWithTooltip(guiGraphics, this.frequencyLabelPos.leftInt(), this.frequencyLabelPos.rightInt(), GuiColors.DARK_GREY, this.frequency.getMessage(), ModUtil.translate("screen", "frequency.tooltip"));
    }

    private void onFrequencyChanged(String str) {
        int frequency = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.INSTANCE.sendToServer(new UpdateFrequencyPacket(this.menu.blockEntity.getBlockPos(), frequency));
    }
}
