package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.menu.FortronMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
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
        this.frequency.setResponder(this::onFrequencyChanged);
        this.frequency.setValue(Integer.toString(this.menu.blockEntity.fortronStorage.getFrequency()));
        addWidget(this.frequency);

        addRenderableWidget(new FortronChargeWidget(this.leftPos + this.fortronEnergyBarPos.leftInt(), this.topPos + this.fortronEnergyBarPos.rightInt(), this.fortronEnergyBarWidth, 11, Component.empty(),
            () -> this.menu.blockEntity.fortronStorage.getStoredFortron() / (double) this.menu.blockEntity.fortronStorage.getFortronCapacity()));
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
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawWithTooltip(poseStack, this.frequencyLabelPos.leftInt(), this.frequencyLabelPos.rightInt(), GuiColors.DARK_GREY, this.frequency.getMessage(), ModUtil.translate("screen", "frequency.tooltip"));
    }

    @Override
    public int getDisabledSlotColor() {
        int alpha = Math.min((int) (255 * this.menu.blockEntity.getAnimation() / 4F), 0x90);
        return GuiColors.DISABLED_SLOT_OVERLAY_RGB | alpha << 24;
    }

    private void onFrequencyChanged(String str) {
        int frequency = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.INSTANCE.sendToServer(new UpdateFrequencyPacket(this.menu.blockEntity.getBlockPos(), frequency));
    }
}
