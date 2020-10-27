package com.hfad.astreoidsgl.entities;
import android.opengl.GLES20;

import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.HUD;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

public class Particles extends GLEntity {
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;


    public Particles(final float x, final float y, int points){

        if(points < 3){ points = 3; } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = 0.2f;
        _height = _width;
        velocityParticless(1, 2);
        final double radius = _width*0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    //random astroids generator
    /*
    private void randomAstroid() {
        int r = new Random().nextInt(3);
        switch (r) {
            case 0: largeAstroid();
            break;
            case 1:  mediumAstroid();
                break;
            case 2: smallParticles();
                break;

        }
    }

     */



    private void velocityParticless(int i, int i2) {
        _velX = Utils.between(MIN_VEL * i, MAX_VEL * i);
        _velY = Utils.between(MIN_VEL * i, MAX_VEL * i);
        _velR = Utils.between(MIN_VEL * i2, MAX_VEL * i2);
    }



    @Override
    public void update(double dt) {

        super.update(dt);
    }


}

