package dev.su5ed.mffs.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class TextButton extends Button {
    private final Supplier<Component> messageSupplier;

    public TextButton(int x, int y, int width, int height, Supplier<Component> messageSupplier, OnPress onPress) {
        super(x, y, width, height, null, onPress, s -> Component.empty());

        this.messageSupplier = messageSupplier;
    }

    @Override
    public Component getMessage() {
        return this.messageSupplier.get();
    }
}
