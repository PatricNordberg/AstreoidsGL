package com.hfad.astreoidsgl.entities;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.hfad.astreoidsgl.CollisionDetection;
import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

import static com.hfad.astreoidsgl.CollisionDetection.areBoundingSpheresOverlapping;

@SuppressWarnings("SpellCheckingInspection")
public class Bullet extends GLEntity {
    private static final Mesh BULLET_MESH = new Mesh(Mesh.POINT, GLES20.GL_POINTS); //Q&D pool, Mesh.POINT is just [0,0,0] float array
    public float _ttl = GameConfig.TIME_TO_LIVE;

    public Bullet() {
        setColors(1, 0, 1, 1);
        _mesh = BULLET_MESH; //all bullets use the exact same mesh
    }

    public void fireFrom(GLEntity source) {
        final float theta = source._rotation * (float) Utils.TO_RAD;
        _x = source._x + (float) Math.sin(theta) * (source._width * 0.5f);
        _y = source._y - (float) Math.cos(theta) * (source._height * 0.5f);
        _velX = source._velX;
        _velY = source._velY;
        _velX += (float) Math.sin(theta) * GameConfig.SPEED;
        _velY -= (float) Math.cos(theta) * GameConfig.SPEED;
        _ttl = GameConfig.TIME_TO_LIVE;
    }


    @Override
    public boolean isDead() {
        return _ttl < 1;
    }


    @Override
    public void update(double dt) {
        if (_ttl > 0) {
            _ttl -= dt;
            super.update(dt);
        }
    }

    @Override
    public void render(final float[] viewportMatrix) {
        if (_ttl > 0) {
            super.render(viewportMatrix);
        }
    }

    @Override
    public boolean isColliding(final GLEntity that) {
        if (!areBoundingSpheresOverlapping(this, that)) { //quick rejection
            return false;
        }
        final PointF[] asteroidVerts = that.getPointList(); //TODO: breaking the law of demeter!
        return CollisionDetection.polygonVsPoint(asteroidVerts, _x, _y);
    }

}