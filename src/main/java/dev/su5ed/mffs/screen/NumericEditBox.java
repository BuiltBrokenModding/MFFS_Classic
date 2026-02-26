package dev.su5ed.mffs.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;

public class NumericEditBox extends EditBox {

    public NumericEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        int codePoint = event.codepoint();
        if (canConsumeInput() && Character.isDigit(codePoint)) {
            insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }
}
