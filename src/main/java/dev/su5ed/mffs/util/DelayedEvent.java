package dev.su5ed.mffs.util;

public abstract class DelayedEvent {
    public int ticks;

    public DelayedEvent(int ticks) {
        this.ticks = ticks;
    }

    protected abstract void onEvent();

    public void update() {
        this.ticks--;

        if (this.ticks <= 0) {
            this.onEvent();
        }
    }
}
