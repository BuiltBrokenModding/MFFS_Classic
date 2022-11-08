package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchTransferModePacket;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FortronCapacitorScreen extends BaseScreen<FortronCapacitorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/fortron_capacitor.png");
    public static final ResourceLocation GUI_BUTTONS = new ResourceLocation(MFFSMod.MODID, "textures/gui/buttons.png");

    private NumericEditBox frequency;

    public FortronCapacitorScreen(FortronCapacitorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new ToggleButton(this.width / 2 - 82, this.height / 2 - 104, this.menu.blockEntity::isActive,
            () -> Network.INSTANCE.sendToServer(new ToggleModePacket(this.menu.blockEntity.getBlockPos(), !this.menu.blockEntity.isActive()))
        ));
        addRenderableWidget(new IconCycleButton<>(this.width / 2 + 15, this.height / 2 - 37, 18, 18, GUI_BUTTONS, 0, 0, 18, this.menu.blockEntity::getTransferMode,
            value -> Network.INSTANCE.sendToServer(new SwitchTransferModePacket(this.menu.blockEntity.getBlockPos(), value.next()))));

        this.frequency = new NumericEditBox(this.font, this.leftPos + 50, this.topPos + 76, 50, 12, Component.literal("Frequency:"));
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
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        this.font.draw(poseStack, this.frequency.getMessage(), 8, 63, GuiColors.DARK_GREY);

        poseStack.pushPose();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        this.font.draw(poseStack, "Upgrade", -95, 140, GuiColors.DARK_GREY);
        poseStack.popPose();

        this.font.draw(poseStack, "Linked Devices: " + this.menu.blockEntity.getLinkedDevices().size(), 8, 28, GuiColors.DARK_GREY);
        this.font.draw(poseStack, "Transmission rate: " + this.menu.blockEntity.getTransmissionRate() * 10 + " L/s", 8, 40, GuiColors.DARK_GREY);
        this.font.draw(poseStack, "Range: " + this.menu.blockEntity.getTransmissionRange(), 8, 52, GuiColors.DARK_GREY);

        this.font.draw(poseStack, "Fortron:", 8, 95, GuiColors.DARK_GREY);
        this.font.draw(poseStack, this.menu.blockEntity.getFortronEnergy() + " L / " + this.menu.blockEntity.getFortronCapacity() + " L", 8, 105, GuiColors.DARK_GREY);

        // TODO Fortron cost
    }

    private void onFrequencyChanged(String str) {
        int frequency = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.INSTANCE.sendToServer(new UpdateFrequencyPacket(this.menu.blockEntity.getBlockPos(), frequency));
    }
}
