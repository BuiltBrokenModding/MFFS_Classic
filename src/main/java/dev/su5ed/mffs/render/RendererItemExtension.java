package dev.su5ed.mffs.render;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Supplier;

public class RendererItemExtension implements IClientItemExtensions {
    private final Supplier<BlockEntityWithoutLevelRenderer> renderer;

    public RendererItemExtension(Supplier<BlockEntityWithoutLevelRenderer> renderer) {
        this.renderer = renderer;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return this.renderer.get();
    }
}
