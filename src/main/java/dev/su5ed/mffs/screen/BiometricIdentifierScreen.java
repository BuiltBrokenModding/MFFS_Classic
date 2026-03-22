package dev.su5ed.mffs.screen;

import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.api.security.FieldPermission;
import dev.su5ed.mffs.menu.BiometricIdentifierMenu;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleFieldPermissionPacket;
import dev.su5ed.mffs.util.ModUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BiometricIdentifierScreen extends FortronScreen<BiometricIdentifierMenu> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/biometric_identifier.png");

    /**
     * Display order for the 7 permission buttons laid out in a 4+3 grid.
     */
    private static final FieldPermission[] PERM_DISPLAY_ORDER = {
        FieldPermission.WARP,
        FieldPermission.USE_BLOCKS,
        FieldPermission.PLACE_BLOCKS,
        FieldPermission.BYPASS_DEFENSE,
        FieldPermission.BYPASS_CONFISCATION,
        FieldPermission.REMOTE_CONTROL,
        FieldPermission.CONFIGURE_SECURITY_CENTER,
    };

    private final List<IconToggleButton> permissionButtons = new ArrayList<>();

    public BiometricIdentifierScreen(BiometricIdentifierMenu menu, InventoryPlayer playerInventory) {
        super(menu, playerInventory, BACKGROUND);
        this.frequencyBoxX = 109;
        this.frequencyBoxY = 92;
        this.frequencyLabelX = 87;
        this.frequencyLabelY = 80;
        this.fortronEnergyBarX = 87;
        this.fortronEnergyBarY = 66;
        this.fortronEnergyBarWidth = 82;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.permissionButtons.clear();
        // Row 0 holds the first 4 permissions, row 1 holds the remaining 3.
        for (int i = 0; i < PERM_DISPLAY_ORDER.length; i++) {
            int row = i < 4 ? 0 : 1;
            int col = i < 4 ? i : i - 4;
            FieldPermission permission = PERM_DISPLAY_ORDER[i];
            IconToggleButton widget = new IconToggleButton(
                this.width / 2 - 21 + 20 * (col + 1), this.height / 2 - 87 + 20 * row, 18, 18,
                ModUtil.translateTooltip(permission),
                18, 18 * permission.ordinal(),
                () -> ((BiometricIdentifierMenu) this.inventorySlots).hasPermission(permission),
                value -> togglePermission(permission, !value)
            );
            this.permissionButtons.add(widget);
            this.buttonList.add(widget);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
        // Permission buttons only visible when rightsSlot has an item
        BiometricIdentifierMenu menu = (BiometricIdentifierMenu) this.inventorySlots;
        for (IconToggleButton btn : this.permissionButtons) {
            btn.visible = !menu.blockEntity.rightsSlot.isEmpty();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawWithTooltip(28, 50, GuiColors.DARK_GREY, "rights");
        drawWithTooltip(28, 70, GuiColors.DARK_GREY, "copy");
        drawWithTooltip(28, 95, GuiColors.DARK_GREY, "master");
    }

    public void togglePermission(FieldPermission permission, boolean value) {
        Network.sendToServer(new ToggleFieldPermissionPacket(
            ((BiometricIdentifierMenu) this.inventorySlots).blockEntity.getPos(), permission, value));
    }
}

