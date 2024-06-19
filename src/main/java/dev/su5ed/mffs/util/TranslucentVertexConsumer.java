package dev.su5ed.mffs.util;

import com.mojang.blaze3d.vertex.VertexConsumer;

public record TranslucentVertexConsumer(VertexConsumer wrapped, int alpha) implements VertexConsumer {
    @Override
    public VertexConsumer addVertex(float v, float v1, float v2) {
        return this.wrapped.addVertex(v, v1, v2);
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        return this.wrapped.setColor(red, green, blue, alpha * this.alpha / 0xFF);
    }

    @Override
    public VertexConsumer setUv(float v, float v1) {
        return this.wrapped.setUv(v, v1);
    }

    @Override
    public VertexConsumer setUv1(int i, int i1) {
        return this.wrapped.setUv1(i, i1);
    }

    @Override
    public VertexConsumer setUv2(int i, int i1) {
        return this.wrapped.setUv2(i, i1);
    }

    @Override
    public VertexConsumer setNormal(float v, float v1, float v2) {
        return this.wrapped.setNormal(v, v1, v2);
    }
}
