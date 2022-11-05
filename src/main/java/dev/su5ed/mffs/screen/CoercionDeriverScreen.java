package dev.su5ed.mffs.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.su5ed.mffs.MFFSMod;
import dev.su5ed.mffs.blockentity.CoercionDeriverBlockEntity.EnergyMode;
import dev.su5ed.mffs.container.CoercionDeriverContainer;
import dev.su5ed.mffs.network.Network;
import dev.su5ed.mffs.network.ToggleEnergyModePacket;
import dev.su5ed.mffs.network.ToggleModePacket;
import dev.su5ed.mffs.network.UpdateFrequencyPacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public class CoercionDeriverScreen extends AbstractContainerScreen<CoercionDeriverContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MFFSMod.MODID, "textures/gui/coercion_deriver.png");
    public static final ResourceLocation COMPONENTS = new ResourceLocation(MFFSMod.MODID, "textures/gui/components.png");

    private NumericEditBox frequency;

    public CoercionDeriverScreen(CoercionDeriverContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.height = this.imageHeight = 217;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        addRenderableWidget(new ToggleButton(this.width / 2 - 82, this.height / 2 - 104, this.menu.blockEntity::isEnabled,
            () -> Network.INSTANCE.sendToServer(new ToggleModePacket(this.menu.blockEntity.getBlockPos(), !this.menu.blockEntity.isEnabled()))
        ));
        addRenderableWidget(new TextButton(this.width / 2 - 10, this.height / 2 - 28, 58, 20,
            () -> Component.literal(this.menu.blockEntity.getEnergyMode() == EnergyMode.DERIVE ? "Derive" : "Integrate"),
            button -> {
                EnergyMode mode = this.menu.blockEntity.getEnergyMode().next();
                this.menu.blockEntity.setEnergyMode(mode);
                Network.INSTANCE.sendToServer(new ToggleEnergyModePacket(this.menu.blockEntity.getBlockPos(), mode));
            })
        );

        this.frequency = new NumericEditBox(this.font, this.leftPos + 30, this.topPos + 43, 50, 12, Component.literal("Frequency:"));
        this.frequency.setCanLoseFocus(true);
        this.frequency.setBordered(true);
        this.frequency.setEditable(true);
        this.frequency.setMaxLength(6);
        this.frequency.setResponder(this::onFrequencyChanged);
        this.frequency.setValue(Integer.toString(this.menu.getFrequency()));
        addWidget(this.frequency);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.frequency.tick();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderFg(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        blit(poseStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        drawForce(poseStack, 8, 115, this.menu.getEnergy() / (float) this.menu.getCapacity());
    }
    
    public void renderFg(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.frequency.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int pMouseX, int pMouseY) {
        this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, GuiColors.DARK_GREY);
        this.font.draw(poseStack, this.frequency.getMessage(), 8, 30, GuiColors.DARK_GREY);
        
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
        this.font.draw(poseStack, "Upgrade", -95, 140, GuiColors.DARK_GREY);
        poseStack.popPose();
        
        this.font.draw(poseStack, "Progress: " + (this.menu.blockEntity.isEnabled() ? "Running" : "Idle"), 8, 70, GuiColors.DARK_GREY);
        
        int energy = this.menu.blockEntity.getCapability(ForgeCapabilities.ENERGY)
            .map(IEnergyStorage::getEnergyStored)
            .orElseThrow();
        this.font.draw(poseStack, "Fortron: " + energy + " FE", 8, 105, GuiColors.DARK_GREY); // TODO production rate
    }
    
    private void onFrequencyChanged(String str) {
        int frequency = str.isEmpty() ? 0 : Integer.parseInt(str);
        Network.INSTANCE.sendToServer(new UpdateFrequencyPacket(this.menu.blockEntity.getBlockPos(), frequency));
    }

    protected void drawForce(PoseStack poseStack, int x, int y, float scale) {
        RenderSystem.setShaderTexture(0, COMPONENTS);

        if (scale > 0) {
            blit(poseStack, this.leftPos + x, this.topPos + y, 54, 11, (int) (scale * 107.0f), 11);
        }
    }
}
