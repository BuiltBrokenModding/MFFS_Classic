package dev.su5ed.mffs.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public final class TranslucentVertexConsumer extends VertexConsumerWrapper {
    private final VertexConsumer wrapped;
    private final int alpha;

    public TranslucentVertexConsumer(VertexConsumer wrapped, int alpha) {
        super(wrapped);
        this.wrapped = wrapped;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this.wrapped.setColor(red, green, blue, alpha * this.alpha / 0xFF);
    }

    @Override
    public VertexConsumer setColor(float red, float green, float blue, float alpha) {
        return super.setColor(red, green, blue, alpha * this.alpha / 0xFF);
    }

    @Override
    public VertexConsumer setColor(int packedColor) {
        return super.setColor(ARGB.color(ARGB.alpha(packedColor) * this.alpha / 0xFF, ARGB.red(packedColor), ARGB.green(packedColor), ARGB.blue(packedColor)));
    }
}
