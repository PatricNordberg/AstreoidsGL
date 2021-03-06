package com.hfad.astreoidsgl.entities;

import android.opengl.GLES20;

import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

@SuppressWarnings({"SuspiciousNameCombination", "SpellCheckingInspection"})
public class Particles extends GLEntity {

    private boolean _isActive;
    private float _startX;
    private float _startY;

    public Particles(final float x, final float y, int points) {
        if (points < 3) {
            points = 3;
        }
        _x = x;
        _y = y;
        _width = GameConfig.PARTICLE_SIZE;
        _height = _width;
        velocityParticles();
        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }


    private void velocityParticles() {
        _velX = Utils.between(GameConfig.MIN_VEL, GameConfig.MAX_VEL);
        _velY = Utils.between(GameConfig.MIN_VEL, GameConfig.MAX_VEL);
        _velR = Utils.between(GameConfig.MIN_VEL * GameConfig.PARTICLE_VEL_R_RANGE, GameConfig.MAX_VEL * GameConfig.PARTICLE_VEL_R_RANGE);
    }

    @Override
    public void render(float[] viewportMatrix) {
        if(!_isActive) return;
        super.render(viewportMatrix);
    }

    public boolean IsActive()
    {
        return _isActive;
    }
    public void dead() {
        _isActive = false;
    }
    public void respawn(float x, float y) {
        _isActive = true;
        _x = x;
        _y = y;
    }


}

