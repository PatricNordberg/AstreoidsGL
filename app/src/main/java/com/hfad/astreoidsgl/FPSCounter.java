package com.hfad.astreoidsgl;

public class FPSCounter {
    long _startTime = System.nanoTime();
    int _frames = 0;
    private String _fps = "";

    public void logFrame() {
        _frames++;
        if (System.nanoTime() - _startTime >= 1000000000) {
            _fps = String.valueOf(_frames);
            _frames = 0;
            _startTime = System.nanoTime();
        }
    }

    public String get_fps() {
        return _fps;
    }
}
