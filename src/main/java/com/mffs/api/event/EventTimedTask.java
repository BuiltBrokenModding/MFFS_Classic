package com.mffs.api.event;

/**
 * Created by pwaln on 6/24/2016.
 */
public class EventTimedTask {

    /* The number of ticks until execution */
    private short ticks;

    /* The amount of times this task is to be executed */
    private byte runAmount;

    /* The action to be executed */
    private Runnable action;

    /**
     * Constructor.
     *
     * @param tick The number of ticks till execution.
     * @param run  The action to be run upon execution.
     */
    public EventTimedTask(int tick, Runnable run) {
        this.action = run;
        this.ticks = (short) tick;
        this.runAmount = 1;
    }

    /**
     * Constructor.
     *
     * @param tick     The number of ticks till execution.
     * @param runCount The amount of times to execute this task.
     * @param runnable The action to be run upon execution.
     */
    public EventTimedTask(int tick, int runCount, Runnable runnable) {
        this.action = runnable;
        this.ticks = (short) tick;
        this.runAmount = (byte) runCount;
    }

    /**
     * Ticks the current amount of ticks down.
     *
     * @return If has executed or not.
     */
    public boolean tick() {
        if (--ticks <= 0) {
            execute();
            runAmount--;
            return true;
        }
        return false;
    }

    /**
     * Gets if this task has executions remaining.
     *
     * @return
     */
    public boolean isActive() {
        return runAmount >= 1;
    }

    /**
     * Execute the desired action.
     */
    public void execute() {
        if (action != null) {
            action.run();
            ;
        }
    }
}
