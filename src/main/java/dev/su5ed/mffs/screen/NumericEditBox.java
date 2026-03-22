package dev.su5ed.mffs.screen;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.ITextComponent;

public class NumericEditBox extends GuiTextField {
    private Runnable responder;

    public NumericEditBox(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
        super(0, font, x, y, width, height);
    }

    public void setResponder(Runnable responder) {
        this.responder = responder;
    }

    public void setValue(String text) {
        setText(text);
    }

    public String getValue() {
        return getText();
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        // Allow control characters (typedChar == 0), backspace, and digits
        if (typedChar == 0 || keyCode == 14 /* Keyboard.KEY_BACK */ || Character.isDigit(typedChar)) {
            boolean changed = super.textboxKeyTyped(typedChar, keyCode);
            if (changed && this.responder != null) {
                this.responder.run();
            }
            return changed;
        }
        return false;
    }
}
