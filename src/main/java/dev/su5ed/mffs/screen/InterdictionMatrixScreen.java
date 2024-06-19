package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.InterdictionMatrix;
import dev.su5ed.mffs.menu.InterdictionMatrixMenu;
import dev.su5ed.mffs.network.SwitchConfiscationModePacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class InterdictionMatrixScreen extends FortronScreen<InterdictionMatrixMenu> {
    public static final ResourceLocation BACKGROUND = MFFSMod.location("textures/gui/interdiction_matrix.png");

    public InterdictionMatrixScreen(InterdictionMatrixMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(110, 91);
        this.frequencyLabelPos = IntIntPair.of(8, 93);
        this.fortronEnergyBarPos = IntIntPair.of(8, 120);
        this.fortronEnergyBarWidth = 107;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new TextButton(this.width / 2 - 80, this.height / 2 - 65, 50, 20,
            () -> this.menu.blockEntity.getConfiscationMode().translation,
            button -> {
                InterdictionMatrix.ConfiscationMode mode = this.menu.blockEntity.getConfiscationMode().next();
                this.menu.blockEntity.setConfiscationMode(mode);
                PacketDistributor.sendToServer(new SwitchConfiscationModePacket(this.menu.blockEntity.getBlockPos(), mode));
            }
        ));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        guiGraphics.drawString(this.font, ModUtil.translate("screen", "warn_range", this.menu.blockEntity.getWarningRange()), 35, 19, GuiColors.DARK_GREY, false);
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "action_range", this.menu.blockEntity.getActionRange()), 100, 19, GuiColors.DARK_GREY, false);
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "filter_mode"), 9, 32, GuiColors.DARK_GREY, false);

        drawWithTooltip(guiGraphics, 8, 110, GuiColors.DARK_GREY, "fortron", this.menu.blockEntity.fortronStorage.getStoredFortron(), this.menu.blockEntity.fortronStorage.getFortronCapacity());
        guiGraphics.drawString(this.font, ModUtil.translate("screen", "fortron_cost", "-", this.menu.getClientFortronCost() * 20), 120, 121, ChatFormatting.DARK_RED.getColor(), false);
    }
}
