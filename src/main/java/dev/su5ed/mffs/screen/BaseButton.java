package dev.su5ed.mffs.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseButton extends GuiButton {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(200);

    private final Runnable onPress;

    public BaseButton(int x, int y, int width, int height, Runnable onPress) {
        super(NEXT_ID.getAndIncrement(), x, y, width, height, "");
        this.onPress = onPress;
    }

    /** Called by BaseScreen.actionPerformed() when this button is clicked. */
    public void firePress() {
        if (this.enabled) {
            this.onPress.run();
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y
            && mouseX < this.x + this.width && mouseY < this.y + this.height;
        renderFg(mc, mouseX, mouseY, partialTicks);
    }

    protected abstract void renderFg(Minecraft mc, int mouseX, int mouseY, float partialTicks);
}
