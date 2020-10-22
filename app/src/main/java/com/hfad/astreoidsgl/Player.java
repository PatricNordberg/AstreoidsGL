package com.hfad.astreoidsgl;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.os.SystemClock;

import com.hfad.astreoidsgl.shapes.Triangle;

import static com.hfad.astreoidsgl.CollisionDetection.areBoundingSpheresOverlapping;


public class Player extends GLEntity {

    private static final String TAG = "Player";
    static final float ROTATION_VELOCITY = 360f; //TODO: game play values!
    static final float THRUST = 8f;
    static final float DRAG = 0.99f;
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds. TODO: game play setting!
    private float _bulletCooldown = 0;

    public Player(final float x, final float y){
        super();
        _x = x;
        _y = y;
        _width = 8f; //TODO: gameplay values!
        _height = 12f;
        float vertices[] = { // in counterclockwise order:
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
        if(_game._inputs._pressingB){
            final float theta = _rotation*(float)Utils.TO_RAD;
            _velX += (float)Math.sin(theta) * THRUST;
            _velY -= (float)Math.cos(theta) * THRUST;
        }
        _velX *= DRAG;
        _velY *= DRAG;
        _bulletCooldown -= dt;
        if(_game._inputs._pressingA && _bulletCooldown <= 0){
            setColors(1, 0, 1, 1);
            if(_game.maybeFireBullet(this)){
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

}
