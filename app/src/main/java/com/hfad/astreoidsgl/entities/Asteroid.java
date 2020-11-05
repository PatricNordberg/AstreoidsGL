package com.hfad.astreoidsgl.entities;

import android.opengl.GLES20;

import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.HUD;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

import java.util.ArrayList;

public abstract class Asteroid extends GLEntity {

    public abstract float get_width();
    public abstract float get_height();
    public abstract float get_min_velocity();
    public abstract float get_max_velocity();
    public abstract int get_score_points();
    public abstract int get_particle_amount();
    public abstract int get_child_asteroids_amount();

    private final ArrayList<Particles> _smallParticles = new ArrayList();
    protected final ArrayList<Asteroid> _childAsteroids = new ArrayList();
    private final float _particleVisibilityTime = 1000;
    private double _currentTime = 0;
    private boolean _isActive = false;
    private boolean _particlesVisible = false;


    public Asteroid(final float x, final float y, int points) {

        if (points < 3) {
            points = 3;
        } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = get_width();
        _height = get_height();
        velocityAsteroids();
        setupParticles();
        setupChildAsteroids();
        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    private void setupParticles()
    {
        for (int i = 0; i < get_particle_amount(); i++) {
            Particles particle = new Particles(_x, _y, 0);
            particle.dead();
            _smallParticles.add(particle);
        }
    }
    protected abstract void setupChildAsteroids();


    /*
    private void velocityAsteroids(int i, int i2) {
        _velX = Utils.between(GameConfig.MIN_VEL * i, GameConfig.MAX_VEL * i);
        _velY = Utils.between(GameConfig.MIN_VEL * i, GameConfig.MAX_VEL * i);
        _velR = Utils.between(GameConfig.MIN_VEL * i2, GameConfig.MAX_VEL * i2);
    }

     */

    private void velocityAsteroids() {
        _velX = Utils.between(get_min_velocity() , get_max_velocity());
        _velY = Utils.between(get_min_velocity() , get_max_velocity());
        _velR = Utils.between(get_min_velocity() , get_max_velocity());
    }

    public void onHitLaser(Asteroid a) {
        HUD._laserHitAsteroid = true;
        _game._jukebox.play(GameConfig.EXPLOSION);
        _game._player._score += get_score_points();
        _currentTime = System.currentTimeMillis();
        spawnParticles();
        spawnChildAsteroids();
    }

    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
        updateParticles(dt);
        checkParticlesVisibility();
    }

    @Override
    public void render(float[] viewportMatrix) {
        renderParticles(viewportMatrix);

        if(!_isActive) return;
        super.render(viewportMatrix);
    }

    public ArrayList<Asteroid> get_child_asteroids() {
        return _childAsteroids;
    }

    public void dead() {
        _isActive = false;
    }
    public void respawn(float x, float y) {
        _isActive = true;
        _x = x;
        _y = y;
    }

    private void checkParticlesVisibility() {
        if(!_particlesVisible) return;

        double getTime = System.currentTimeMillis();
        if (getTime - _currentTime > _particleVisibilityTime) {
            hideParticles();
        }
    }
    private void updateParticles(double dt)
    {
        for (Particles particle:_smallParticles) {
            particle.update(dt);
        }
    }
    private void renderParticles(float[] viewportMatrix) {
        for (Particles particle:_smallParticles) {
            particle.render(viewportMatrix);
        }
    }

    public void SetActive(boolean isActive) {
        _isActive = isActive;
    }
    public boolean IsActive(){
        return _isActive;
    }

    private void spawnParticles(){
        for (Particles particle:_smallParticles) {
            particle.respawn(_x, _y);
        }
        _particlesVisible = true;
    }
    private void hideParticles() {
        for (Particles particle:_smallParticles) {
            particle.dead();
        }
        _particlesVisible = false;
    }
    private void spawnChildAsteroids(){
        for (Asteroid asteroid:_childAsteroids) {
            asteroid.Respawn(_x, _y);
        }
    }
    private void hideChildAsteroids() {
        for (Asteroid asteroid:_childAsteroids) {
            asteroid.SetActive(false);
        }
    }
    private void Respawn(float x, float y){
        _isActive = true;
        _x = x;
        _y = y;
    }

}
