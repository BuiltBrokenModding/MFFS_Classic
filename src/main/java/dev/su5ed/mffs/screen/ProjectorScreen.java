package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends FortronScreen<ProjectorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/projector.png");

    public ProjectorScreen(ProjectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(48, 91);
        this.frequencyLabelPos = IntIntPair.of(9, 78);
        this.fortronEnergyBarPos = IntIntPair.of(8, 120);
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawWithTooltip(poseStack, 32, 20, GuiColors.DARK_GREY, "matrix");
        drawWithTooltip(poseStack, 8, 110, GuiColors.DARK_GREY, "fortron", this.menu.blockEntity.fortronStorage.getStoredFortron(), this.menu.blockEntity.fortronStorage.getFortronCapacity());
        int cost = this.menu.getClientFortronCost() * 20;
        if (cost > 0) {
            this.font.draw(poseStack, ModUtil.translate("screen", "fortron_cost", '-', cost).withStyle(ChatFormatting.DARK_RED), 117, 121, GuiColors.DARK_GREY);
        }
    }
}
