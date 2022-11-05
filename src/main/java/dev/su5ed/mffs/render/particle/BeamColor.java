package dev.su5ed.mffs.render.particle;

public enum BeamColor {
    BLUE(0.6F, 0.6F, 1),
    RED;
    
    private final float red;
    private final float green;
    private final float blue;

    BeamColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }
}
