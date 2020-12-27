package com.cutesmouse.mmusic.utils;

public class Timer {
    private long startTime;
    private long pauseStartTime;
    private long offset;
    private boolean isPaused;
    public Timer() {
        offset = 0L;
        isPaused = false;
    }
    public void start() {
        startTime = System.currentTimeMillis();
    }
    public void setPause(boolean pause) {
        if (pause == isPaused) return;
        if (pause) {
            pauseStartTime = System.currentTimeMillis();
        } else {
            offset += System.currentTimeMillis() - pauseStartTime;
        }
        isPaused = pause;
    }
    public long getTime() {
        return System.currentTimeMillis() - startTime - offset;
    }
}
