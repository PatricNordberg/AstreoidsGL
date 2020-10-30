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

public class Player extends GLEntity {

    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 360f; //TODO: game play values!
    static final float THRUST = 40f;
    static final float DRAG = 0.99f;
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds. TODO: game play setting!
    private float _bulletCooldown = 0;
    public static final float TIME_BETWEEN_HYPERSPACE = 0.75f; //seconds. TODO: game play setting!
    private float _hyperspaceCooldown = 0;
    public static final float TIME_BETWEEN_BOOST = 0.75f; //seconds. TODO: game play setting!
    private float _boostCooldown = 0;
    Random r = new Random();


    public Player(final float x, final float y){
        super();
        _x = x;
        _y = y;
        _width = 4f; //TODO: gameplay values!
        _height = 8f;
        float[] vertices = { // in counterclockwise order:
                0.0f,  0.5f, 0.0f, 	// top
                -0.5f, -0.5f, 0.0f,	// bottom left
                0.5f, -0.5f, 0.0f  	// bottom right
        };
        _mesh = new Mesh(vertices, GLES20.GL_TRIANGLES);
        _mesh.setWidthHeight(_width, _height);
        _mesh.flipY();
    }



    @Override
    public void update(double dt){
        _rotation += (dt*ROTATION_VELOCITY) * _game._inputs._horizontalFactor;
        _boostCooldown -= dt;
        if(_game._inputs._pressingBoost && _boostCooldown <= 0){
            final float theta = _rotation*(float) Utils.TO_RAD;
            _velX += (float)Math.sin(theta) * THRUST;
            _velY -= (float)Math.cos(theta) * THRUST;
            _game._jukebox.play(GameConfig.BOOST); //todo; fps drop, implement cooldown?
            //todo: implement effects for boost - fire - bullet åt andra håller bara
            _boostCooldown = TIME_BETWEEN_BOOST;
        }

        _hyperspaceCooldown -= dt;
        if(_game._inputs._pressingHyperspace && _hyperspaceCooldown <=0){
            _game._jukebox.play(GameConfig.HYPERSPACE);
            _y = r.nextInt((int) GameConfig.WORLD_HEIGHT);
            _x = r.nextInt((int) GameConfig.WORLD_WIDTH);
            _hyperspaceCooldown = TIME_BETWEEN_HYPERSPACE;
        }

        _velX *= DRAG;
        _velY *= DRAG;
        _bulletCooldown -= dt;
        if(_game._inputs._pressingLaser && _bulletCooldown <= 0){
            setColors(1, 0, 1, 1);
            if(_game.maybeFireBullet(this)){
                _game._jukebox.play(GameConfig.LASER);
                _bulletCooldown = TIME_BETWEEN_SHOTS;
            }
        }else{
            setColors(1.0f, 1, 1,1);
        }
        super.update(dt);
    }

    @Override
    public void render(float[] viewportMatrix) {
        //ask the super class (GLEntity) to render us
        super.render(viewportMatrix);
    }


    @Override
    public boolean isColliding(final GLEntity that){
        if(!areBoundingSpheresOverlapping(this, that)){
            return false;
        }
        final PointF[] shipHull = getPointList();
        final PointF[] asteroidHull = that.getPointList();
        if(CollisionDetection.polygonVsPolygon(shipHull, asteroidHull)){
            return true;
        }
        return CollisionDetection.polygonVsPoint(asteroidHull, _x, _y); //finally, check if we're inside the asteroid
    }

    public void onHitAsteroid() {
        HUD._playerIsHit = true;
        _game._jukebox.play(GameConfig.HURT);
        GameConfig._health--;
    }

}