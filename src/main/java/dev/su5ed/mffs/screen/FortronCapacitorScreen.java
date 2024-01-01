package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.FortronCapacitorMenu;
import dev.su5ed.mffs.network.SwitchTransferModePacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

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
            value -> PacketDistributor.SERVER.noArg().send(new SwitchTransferModePacket(this.menu.blockEntity.getBlockPos(), value.next()))));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "upgrade"), -95, 140, GuiColors.DARK_GREY, false);
        poseStack.popPose();

        guiGraphics.drawString(this.font, ModUtil.translate("screen", "linked_devices", this.menu.blockEntity.getDevicesByFrequency().size()), 8, 28, GuiColors.DARK_GREY, false);
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "transmission_rate", this.menu.blockEntity.getTransmissionRate() * 10), 8, 40, GuiColors.DARK_GREY, false);
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "range", this.menu.blockEntity.getTransmissionRange()), 8, 52, GuiColors.DARK_GREY, false);

        guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron.name"), 8, 95, GuiColors.DARK_GREY, false);
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron.value", this.menu.blockEntity.fortronStorage.getStoredFortron(), this.menu.blockEntity.fortronStorage.getFortronCapacity()), 8, 105, GuiColors.DARK_GREY, false);

        // TODO Fortron cost
    }
}
