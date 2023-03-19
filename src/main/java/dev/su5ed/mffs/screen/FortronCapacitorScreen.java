package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchTransferModePacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FortronCapacitorScreen extends FortronScreen<FortronCapacitorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/fortron_capacitor.png");

    public FortronCapacitorScreen(FortronCapacitorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(50, 76);
        this.frequencyLabelPos = IntIntPair.of(8, 63);
        this.fortronEnergyBarPos = IntIntPair.of(8, 115);
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IconCycleButton<>(this.width / 2 + 15, this.height / 2 - 37, 18, 18, 0, 0, 18, this.menu.blockEntity::getTransferMode,
            value -> Network.INSTANCE.sendToServer(new SwitchTransferModePacket(this.menu.blockEntity.getBlockPos(), value.next()))));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        poseStack.pushPose();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        this.font.draw(poseStack, ModUtil.translate("screen", "upgrade"), -95, 140, GuiColors.DARK_GREY);
        poseStack.popPose();

        this.font.draw(poseStack, ModUtil.translate("screen", "linked_devices", this.menu.blockEntity.getDevicesByFrequency().size()), 8, 28, GuiColors.DARK_GREY);
        this.font.draw(poseStack, ModUtil.translate("screen", "transmission_rate", this.menu.blockEntity.getTransmissionRate() * 10), 8, 40, GuiColors.DARK_GREY);
        this.font.draw(poseStack, ModUtil.translate("screen", "range", this.menu.blockEntity.getTransmissionRange()), 8, 52, GuiColors.DARK_GREY);

        this.font.draw(poseStack, ModUtil.translate("screen", "fortron.name"), 8, 95, GuiColors.DARK_GREY);
        this.font.draw(poseStack, ModUtil.translate("screen", "fortron.value", this.menu.blockEntity.fortronStorage.getEnergyStored(), this.menu.blockEntity.fortronStorage.getMaxEnergyStored()), 8, 105, GuiColors.DARK_GREY);

        // TODO Fortron cost
    }
}
