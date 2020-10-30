package com.hfad.astreoidsgl.entities;

import android.opengl.GLES20;

import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

@SuppressWarnings({"SuspiciousNameCombination", "SpellCheckingInspection"})
public class Particles extends GLEntity {
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;


    public Particles(final float x, final float y, int points) {
        if (points < 3) {
            points = 3;
        }
        _x = x;
        _y = y;
        _width = 0.2f;
        _height = _width;
        velocityParticles();
        final double radius = _width * 0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }


    private void velocityParticles() {
        _velX = Utils.between(MIN_VEL * 1, MAX_VEL * 1);
        _velY = Utils.between(MIN_VEL * 1, MAX_VEL * 1);
        _velR = Utils.between(MIN_VEL * 2, MAX_VEL * 2);
    }


}

