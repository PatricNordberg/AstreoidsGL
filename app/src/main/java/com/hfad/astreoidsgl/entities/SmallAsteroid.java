package com.hfad.astreoidsgl.entities;

public class SmallAsteroid extends Asteroid {
    public SmallAsteroid(float x, float y, int points) {
        super(x, y, points);
    }

    @Override
    protected void setupChildAsteroids() {

    }

    @Override
    public float get_width() {
        return 4;
    }

    @Override
    public float get_height() {
        return 4;
    }

    @Override
    public float get_min_velocity() {
        return -30;
    }

    @Override
    public float get_max_velocity() {
        return 40;
    }

    @Override
    public int get_score_points() {
        return 3;
    }

    @Override
    public int get_particle_amount() {
        return 20;
    }

    @Override
    public int get_child_asteroids_amount() {
        return 0;
    }
}
