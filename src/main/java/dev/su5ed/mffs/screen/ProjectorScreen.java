package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.menu.ProjectorMenu;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends FortronScreen<ProjectorMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/projector.png");

    public ProjectorScreen(ProjectorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);
        
        // TODO Slot tooltips
        this.frequencyBoxPos = IntIntPair.of(48, 91);
        this.frequencyLabelPos = IntIntPair.of(8, 76);
        this.fortronEnergyBarPos = IntIntPair.of(8, 120);
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);
        
        this.font.draw(poseStack, "Matrix", 32, 20, GuiColors.DARK_GREY);
        this.font.draw(poseStack, "Fortron: " + this.menu.blockEntity.fortronStorage.getStoredFortron() + " L / " + this.menu.blockEntity.fortronStorage.getFortronCapacity() + " L", 8, 110, GuiColors.DARK_GREY);
        int cost = this.menu.getClientFortronCost() * 20;
        if (cost > 0) {
            this.font.draw(poseStack, Component.literal("-" + cost + " L").withStyle(ChatFormatting.DARK_RED), 117, 121, GuiColors.DARK_GREY);
        }
    }
}
