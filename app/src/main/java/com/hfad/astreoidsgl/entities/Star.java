package com.hfad.astreoidsgl.entities;

import android.graphics.Color;
import android.opengl.GLES20;

import com.hfad.astreoidsgl.Mesh;

import java.util.Random;

public class Star extends GLEntity {
    private static Mesh m = null; //Q&D pool
    Random r;
    public boolean _showIt;


    public Star(final float x, final float y){
        super();
        r = new Random();
        _x = x;
        _y = y;
        _color[0] = Color.red(Color.YELLOW) / 255f;
        _color[1] = Color.green(Color.YELLOW) / 255f;
        _color[2] = Color.blue(Color.YELLOW) / 255f;
        _color[3] = 1f;
        if(m == null) {
            final float[] vertices = {0, 0, 0};
            m = new Mesh(vertices, GLES20.GL_POINTS);
        }
        _mesh = m; //all Stars use the exact same Mesh instance.
    }

    @Override
    public void update(double dt) {
        int n = r.nextInt(1000);
        if(n == 0){
// Switch on or off
            if(_showIt){
                _showIt = false;
            }else{
                _showIt = true;
            }
        }
        super.update(dt);
    }


}
