package com.hfad.astreoidsgl.entities;

public class LargeAsteroid extends Asteroid {


    public LargeAsteroid(float x, float y, int points) {
        super(x, y, points);

    }

    @Override
    public float get_width() {
        return 0;
    }

    @Override
    public float get_height() {
        return 0;
    }
}
