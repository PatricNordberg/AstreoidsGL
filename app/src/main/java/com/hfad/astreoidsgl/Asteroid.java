package com.hfad.astreoidsgl;
import android.opengl.GLES20;

public class Asteroid extends GLEntity {
    private static final float MAX_VEL = 14f;
    private static final float MIN_VEL = -14f;

    public Asteroid(final float x, final float y, int points){
        if(points < 3){ points = 3; } //triangles or more, please. :)
        _x = x;
        _y = y;
        _width = 12;
        _height = _width;
        _velX = Utils.between(MIN_VEL*2, MAX_VEL*2);
        _velY = Utils.between(MIN_VEL*2, MAX_VEL*2);
        _velR = Utils.between(MIN_VEL*4, MAX_VEL*4);
        final double radius = _width*0.5;
        final float[] verts = Mesh.generateLinePolygon(points, radius);
        _mesh = new Mesh(verts, GLES20.GL_LINES);
        _mesh.setWidthHeight(_width, _height);
    }

    public void onHitLaser() {
        HUD._laserHitAsteroid = true;
        _game._jukebox.play(GameConfig.EXPLOSION);
        GameConfig._score++;

    }

    @Override
    public void update(double dt) {
        _rotation++;
        super.update(dt);
    }


}
