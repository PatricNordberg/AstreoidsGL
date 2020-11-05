package com.hfad.astreoidsgl.entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.hfad.astreoidsgl.CollisionDetection;
import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.HUD;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

import java.util.Random;

import static com.hfad.astreoidsgl.CollisionDetection.areBoundingSpheresOverlapping;

@SuppressWarnings({"SpellCheckingInspection", "UnusedAssignment"})
public class Player extends GLEntity {

    private static final String TAG = "Player";
    final Random r = new Random();
    public int _score = 0;
    private float _bulletCooldown = 0;
    private float _hyperspaceCooldown = 0;
    private float _boostCooldown = 0;
    public boolean _isDead = false;
    public int _health = 0;


    public Player(final float x, final float y) {
        super();
        _health = GameConfig.STARTING_HEALTH;
        _score = GameConfig.STARTING_SCORE;
        _x = x;
        _y = y;
        _width = GameConfig._playerWidth;
        _height = GameConfig._playerHeight;
        float[] vertices = { // in counterclockwise order:
                0.0f, 0.5f, 0.0f,    // top
                -0.5f, -0.5f, 0.0f,    // bottom left
                0.5f, -0.5f, 0.0f    // bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
    }


    @Override
    public void update(double dt) {
        _rotation += (dt * GameConfig.ROTATION_VELOCITY) * _game._inputs._horizontalFactor;
        _velX *= GameConfig.DRAG;
        _velY *= GameConfig.DRAG;

        _boostCooldown -= dt;
        if (_game._inputs._pressingBoost && _boostCooldown <= 0) {
            final float theta = _rotation * (float) Utils.TO_RAD;
            _velX += (float) Math.sin(theta) * GameConfig.THRUST;
            _velY -= (float) Math.cos(theta) * GameConfig.THRUST;
            _game._jukebox.play(GameConfig.BOOST);
            //todo: implement effects for boost - fire - like bullet but in the other y-direction
            _boostCooldown = GameConfig.TIME_BETWEEN_BOOST;
        }

        _hyperspaceCooldown -= dt;
        if (_game._inputs._pressingHyperspace && _hyperspaceCooldown <= 0) {
            _game._jukebox.play(GameConfig.HYPERSPACE);
            _y = r.nextInt((int) GameConfig.WORLD_HEIGHT);
            _x = r.nextInt((int) GameConfig.WORLD_WIDTH);
            _hyperspaceCooldown = GameConfig.TIME_BETWEEN_HYPERSPACE;
        }

        _bulletCooldown -= dt;
        if (_game._inputs._pressingLaser && _bulletCooldown <= 0) {
            setColors(1, 0, 1, 1);
            if (_game.maybeFireBullet(this)) {
                _game._jukebox.play(GameConfig.LASER);
                _bulletCooldown = GameConfig.TIME_BETWEEN_SHOTS;
            }
        } else {
            setColors(1.0f, 1, 1, 1);
        }
        super.update(dt);
    }

    @Override
    public void render(float[] viewportMatrix) {
        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
    }


    @Override
    public boolean isColliding(final GLEntity that) {
        if (!areBoundingSpheresOverlapping(this, that)) {
            return false;
        }
        final PointF[] shipHull = getPointList();
        final PointF[] asteroidHull = that.getPointList();
        if (CollisionDetection.polygonVsPolygon(shipHull, asteroidHull)) {
            return true;
        }
        return CollisionDetection.polygonVsPoint(asteroidHull, _x, _y); //finally, check if we're inside the asteroid
    }

    public void onHitAsteroid() {
        HUD._playerIsHit = true;
        _game._jukebox.play(GameConfig.HURT);
        _health--;
        if (_health == 0) {
            _isDead = true;
        }
    }

    @Override
    public void respawn() {
        _health = GameConfig.STARTING_HEALTH;
        _score = GameConfig.STARTING_SCORE;
        _isDead = false;
    }

}