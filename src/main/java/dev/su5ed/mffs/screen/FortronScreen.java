package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class FortronScreen<T extends FortronMenu<?>> extends BaseScreen<T> {
    protected IntIntPair frequencyBoxPos = IntIntPair.of(0, 0);
    protected IntIntPair frequencyLabelPos = IntIntPair.of(0, 0);

    private NumericEditBox frequency;

    public FortronScreen(T menu, Inventory playerInventory, Component title, ResourceLocation background) {
        super(menu, playerInventory, title, background);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ToggleButton(this.width / 2 - 82, this.height / 2 - 104, () -> this.menu.blockEntity.isActive(),
            () -> Network.INSTANCE.sendToServer(new ToggleModePacket(this.menu.blockEntity.getBlockPos(), !this.menu.blockEntity.isActive()))));

        this.frequency = new NumericEditBox(this.font, this.leftPos + this.frequencyBoxPos.leftInt(), this.topPos + this.frequencyBoxPos.rightInt(), 50, 12, Component.literal("Frequency:"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setBordered(true);
        this.frequency.setEditable(true);
        this.frequency.setMaxLength(6);
        this.frequency.setResponder(this::onFrequencyChanged);
        this.frequency.setValue(Integer.toString(this.menu.getFrequency()));
        addWidget(this.frequency);

        addRenderableWidget(new FortronChargeWidget(this.leftPos + 8, this.topPos + 115, 107, 11, Component.empty(),
            () -> this.menu.blockEntity.getFortronEnergy() / (double) this.menu.blockEntity.getFortronCapacity()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.frequency.tick();
    }

    @Override
    public void renderFg(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.frequency.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int pMouseX, int pMouseY) {
        super.renderLabels(poseStack, pMouseX, pMouseY);

        this.font.draw(poseStack, this.frequency.getMessage(), this.frequencyLabelPos.leftInt(), this.frequencyLabelPos.rightInt(), GuiColors.DARK_GREY);
    }

    private void onFrequencyChanged(String str) {
        int frequency = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.INSTANCE.sendToServer(new UpdateFrequencyPacket(this.menu.blockEntity.getBlockPos(), frequency));
    }
}
