package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.menu.CoercionDeriverMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleEnergyModePacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
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
                Network.INSTANCE.sendToServer(new ToggleEnergyModePacket(this.menu.blockEntity.getBlockPos(), mode));
            })
        );
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        poseStack.pushPose();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        this.font.draw(poseStack, ModUtil.translate("screen", "upgrade"), -95, 140, GuiColors.DARK_GREY);
        poseStack.popPose();

        this.font.draw(poseStack, ModUtil.translate("screen", "progress")
            .append(ModUtil.translate("screen", "progress." + (this.menu.blockEntity.isActive() ? "running" : "fidle"))), 8, 70, GuiColors.DARK_GREY);

        int energy = this.menu.blockEntity.fortronStorage.getStoredFortron();
        this.font.draw(poseStack, ModUtil.translate("screen", "fortron.short", energy), 8, 105, GuiColors.DARK_GREY);
        boolean inversed = this.menu.blockEntity.isInversed();
        this.font.draw(poseStack, ModUtil.translate("screen", "fortron_cost", inversed ? "-" : "+", this.menu.blockEntity.getProductionRate() * 20)
            .withStyle(inversed ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN), 114, 117, GuiColors.DARK_GREY);
    }
}
