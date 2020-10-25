package com.hfad.astreoidsgl;

import android.util.Log;

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;
    private String fps = "";

    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
             fps = String.valueOf(frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    public String getFPS() {
        return fps;
    }
}
