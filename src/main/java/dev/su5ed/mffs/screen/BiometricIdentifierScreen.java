package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.menu.BiometricIdentifierMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleFieldPermissionPacket;
import dev.su5ed.mffs.util.ModUtil;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class BiometricIdentifierScreen extends FortronScreen<BiometricIdentifierMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/biometric_identifier.png");

    private final List<Widget> permissionButtons = new ArrayList<>();

    public BiometricIdentifierScreen(BiometricIdentifierMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, BACKGROUND);

        this.frequencyBoxPos = IntIntPair.of(109, 92);
        this.frequencyLabelPos = IntIntPair.of(87, 80);
        this.fortronEnergyBarPos = IntIntPair.of(87, 66);
        this.fortronEnergyBarWidth = 82;
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0, x = 0, y = 0; i < FieldPermission.values().length; i++) {
            x++;
            FieldPermission permission = FieldPermission.values()[i];
            Widget widget = addWidget(new IconToggleButton(this, this.width / 2 - 21 + 20 * x, this.height / 2 - 87 + 20 * y, 18, 18, ModUtil.translateTooltip(permission), 18, 18 * i,
                () -> this.menu.hasPermission(permission), value -> togglePermission(permission, !value)));
            this.permissionButtons.add(widget);
            if (i % 3 == 0 && i != 0) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        if (!this.menu.blockEntity.rightsSlot.isEmpty()) {
            this.permissionButtons.forEach(button -> button.render(poseStack, mouseX, mouseY, partialTick));
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawWithTooltip(poseStack, 28, 50, GuiColors.DARK_GREY, "rights");
        drawWithTooltip(poseStack, 28, 70, GuiColors.DARK_GREY, "copy");
        drawWithTooltip(poseStack, 28, 95, GuiColors.DARK_GREY, "master");
    }

    public void togglePermission(FieldPermission permission, boolean value) {
        Network.INSTANCE.sendToServer(new ToggleFieldPermissionPacket(this.menu.blockEntity.getBlockPos(), permission, value));
    }
}
