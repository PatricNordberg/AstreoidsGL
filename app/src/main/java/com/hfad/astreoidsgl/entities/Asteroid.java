package com.hfad.astreoidsgl.entities;
import android.opengl.GLES20;

import com.hfad.astreoidsgl.GameConfig;
import com.hfad.astreoidsgl.HUD;
import com.hfad.astreoidsgl.Mesh;
import com.hfad.astreoidsgl.Utils;

public class Asteroid extends GLEntity {
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;


    public Asteroid(final float x, final float y, int points){

        if(points < 3){ points = 3; } //triangles or more, please. :)
        _x = x;
        _y = y;
        //randomAstroid();
        selectAstroidSize();
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
            case 2: smallAsteroid();
                break;

        }
    }

     */

    private void selectAstroidSize() {
    int i = _game._asteroids.size();
            if (i < 4){
                largeAstroid();
            }
            if (i >= 4 && i<8){
                mediumAstroid();
            }
            if (i >= 8 && i <= 10){
                smallAsteroid();
            }
        }





    private void largeAstroid() {
        _width = 12;
        _height = _width;
        velocityAsteroids(1, 2);
    }

    private void mediumAstroid() {
        _width = 8;
        _height = _width;
        velocityAsteroids(2, 4);
    }

    private void smallAsteroid() {
        _width = 4;
        _height = _width;
        velocityAsteroids(3, 3);
    }

    private void velocityAsteroids(int i, int i2) {
        _velX = Utils.between(MIN_VEL * i, MAX_VEL * i);
        _velY = Utils.between(MIN_VEL * i, MAX_VEL * i);
        _velR = Utils.between(MIN_VEL * i2, MAX_VEL * i2);
    }

    public void onHitLaser(Asteroid a) {
        HUD._laserHitAsteroid = true;
        _game._jukebox.play(GameConfig.EXPLOSION);

        if (a._width == 12){
            GameConfig._score++;
        }
        if (a._width == 8){
            GameConfig._score+=2;
        }
        if (a._width == 4){
            GameConfig._score+=3;
        }



    }

    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
    }


}
