package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.ProjectorMenu;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends FortronScreen<ProjectorMenu> {
    public static final ResourceLocation BACKGROUND = MFFSMod.location("textures/gui/projector.png");

    public ProjectorScreen(ProjectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(48, 91);
        this.frequencyLabelPos = IntIntPair.of(9, 78);
        this.fortronEnergyBarPos = IntIntPair.of(8, 120);
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        drawWithTooltip(guiGraphics, 32, 20, GuiColors.DARK_GREY, "matrix");
        drawWithTooltip(guiGraphics, 8, 110, GuiColors.DARK_GREY, "fortron", this.menu.blockEntity.fortronStorage.getStoredFortron(), this.menu.blockEntity.fortronStorage.getFortronCapacity());
        int cost = this.menu.getClientFortronCost() * 20;
        if (cost > 0) {
            guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron_cost", "-", cost).withStyle(ChatFormatting.DARK_RED), 117, 121, GuiColors.DARK_GREY, false);
        }
    }
}
