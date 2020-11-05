package com.hfad.astreoidsgl.entities;

public class LargeAsteroid extends Asteroid {


    public LargeAsteroid(float x, float y, int points) {
        super(x, y, points);

    }

    @Override
    protected void setupChildAsteroids() {
        for (int i = 0; i < get_child_asteroids_amount(); i++) {
            Asteroid asteroid = new MediumAsteroid(_x, _y, 0);
            asteroid.dead();
            _childAsteroids.add(asteroid);
        }
    }

    @Override
    public float get_width() {
        return 14;
    }

    @Override
    public float get_height() {
        return 14;
    }

    @Override
    public float get_min_velocity() {
        return -10;
    }

    @Override
    public float get_max_velocity() {
        return 20;
    }

    @Override
    public int get_score_points() {
        return 1;
    }

    @Override
    public int get_particle_amount() {
        return 40;
    }

    @Override
    public int get_child_asteroids_amount() {
        return 3;
    }
}
