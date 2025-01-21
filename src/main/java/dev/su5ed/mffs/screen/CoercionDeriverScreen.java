package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.SwitchEnergyModePacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CoercionDeriverScreen extends FortronScreen<CoercionDeriverMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/coercion_deriver.png");

    public CoercionDeriverScreen(CoercionDeriverMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(30, 43);
        this.frequencyLabelPos = IntIntPair.of(8, 30);
        this.fortronEnergyBarPos = IntIntPair.of(8, 115);
        this.fortronEnergyBarWidth = 103;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new TextButton(this.width / 2 - 10, this.height / 2 - 28, 58, 20,
            () -> this.menu.blockEntity.getEnergyMode().translate(),
            button -> {
                EnergyMode mode = this.menu.blockEntity.getEnergyMode().next();
                this.menu.blockEntity.setEnergyMode(mode);
                Network.INSTANCE.sendToServer(new SwitchEnergyModePacket(this.menu.blockEntity.getBlockPos(), mode));
            })
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "upgrade"), -95, 140, GuiColors.DARK_GREY, false);
        poseStack.popPose();

        guiGraphics.drawString(this.font, ModUtil.translate("screen", "progress")
            .append(ModUtil.translate("screen", "progress." + (this.menu.blockEntity.isActive() ? "running" : "idle"))), 8, 70, GuiColors.DARK_GREY, false);

        final int energy = this.menu.blockEntity.fortronStorage.getStoredFortron();
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron.short", energy), 8, 105, GuiColors.DARK_GREY, false);
        final boolean inversed = this.menu.blockEntity.isInversed();
        final int displayFortron = this.menu.blockEntity.fortronProducedLastTick * ModUtil.TICKS_PER_SECOND;
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron_cost", inversed ? "-" : "+",  displayFortron)
            .withStyle(inversed ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN), 114, 117, GuiColors.DARK_GREY, false);
    }
}
