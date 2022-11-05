package dev.su5ed.mffs.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumericEditBox extends EditBox {

    public NumericEditBox(Font pFont, int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pFont, pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.canConsumeInput() && Character.isDigit(codePoint)) {
            this.insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }
}
