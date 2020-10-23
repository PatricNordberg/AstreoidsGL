package com.hfad.astreoidsgl;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.hfad.astreoidsgl.audio.BackgroundMusic;
import com.hfad.astreoidsgl.audio.Jukebox;
import com.hfad.astreoidsgl.input.InputManager;
import com.hfad.astreoidsgl.shapes.Square;
import com.hfad.astreoidsgl.shapes.Triangle;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {


    private static final String TAG = "";
    private Border _border;
    private Player _player = null;
    public Jukebox _jukebox = null;
    private BackgroundMusic _backgroundMusic = null;

    GameConfig _config = new GameConfig();
    public InputManager _inputs = new InputManager(); //empty but valid default

    //storage
    private static final int BULLET_COUNT = (int)(Bullet.TIME_TO_LIVE/Player.TIME_BETWEEN_SHOTS)+1;
    Bullet[] _bullets = new Bullet[BULLET_COUNT];
    private static int STAR_COUNT = 100;
    private static int ASTEROID_COUNT = 10;
    private ArrayList<Star> _stars= new ArrayList();
    private ArrayList<Asteroid> _asteroids = new ArrayList();
    private ArrayList<Text> _texts = new ArrayList<>();

    private static final float BG_COLOR[] = {135/255f, 206/255f, 235/255f, 1f}; //RGBA

    public static long SECOND_IN_NANOSECONDS = 1000000000;
    public static long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    public static float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;

    final double dt = 0.01;
    double accumulator = 0.0;
    double currentTime = System.nanoTime()*NANOSECONDS_TO_SECONDS;

    public static float WORLD_WIDTH = 160; //80x80 - much smaller than the viewport.
    public static float WORLD_HEIGHT = 90;
    static float METERS_TO_SHOW_X = WORLD_WIDTH; //160m x 90m, the entire game world in view
    static float METERS_TO_SHOW_Y = WORLD_HEIGHT; //TODO: calculate to match screen aspect ratio

    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    private float[] _viewportMatrix = new float[4*4]; //In essence, it is our our Camera

    public volatile float mAngle;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    //private MyGLRenderer _renderer = null;

    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        GLEntity._game = this;
        setEGLContextClientVersion(2); //select OpenGL ES 2.0
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.
        _jukebox = new Jukebox(getContext());
        _backgroundMusic = new BackgroundMusic(getContext());
       // _backgroundMusic.loadBackgroundMusic(R.raw.background_music);

        final String s1 = "HELLO WORLD";
        final String s2 = "0123456789";
        final String s3 = ", - . : = ? [ ~" ;
        final String s4 = "ABCDEFGHIJKLMNOPQRSTUVXYZ" ;
        _texts.add(new Text(s1, 8, 8));
        _texts.add(new Text(s2, 8, 16));
        _texts.add(new Text(s3, 8, 24));
        _texts.add(new Text(s4, 8, 32));

        for(int i = 0; i < BULLET_COUNT; i++){
            _bullets[i] = new Bullet();
        }

        setRenderer(this);
    }


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLManager.buildProgram(); //compile, link and upload our GL program
        GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]); //set clear color
        // center the player in the world.
        _player = new Player(WORLD_WIDTH/2, 10); //y == 10
        // spawn Border at the center of the world now!
        _border = new Border(WORLD_WIDTH/2, WORLD_HEIGHT/2, WORLD_WIDTH, WORLD_HEIGHT);
        _jukebox.play(GameConfig.START_GAME);


        Random r = new Random();
        for(int i = 0; i < STAR_COUNT; i++){
            _stars.add(new Star(r.nextInt((int)WORLD_WIDTH), r.nextInt((int)WORLD_HEIGHT)));
        }
        final int minAsteroid = 11;
        final int maxAsteroid = 13;
        for(int i = 0; i < ASTEROID_COUNT; i++){
            _asteroids.add(new Asteroid(r.nextInt((int)WORLD_WIDTH),  r.nextInt((int)WORLD_HEIGHT), r.nextInt((maxAsteroid - minAsteroid) + 1) + minAsteroid));
        }


    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed!");

    }

    @Override
    public void onDrawFrame(final GL10 unused) {
        update(); //TODO: move updates away from the render thread...
        render();
    }
    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/

    private void update() {
        final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        accumulator += frameTime;
        while(accumulator >= dt){
            for(final Asteroid a : _asteroids){
                a.update(dt);

            }
            for(final Bullet b : _bullets){
                if(b.isDead()){ continue; } //skip
                b.update(dt);
            }
            _player.update(dt);

            collisionDetection();
            removeDeadEntities();
            //_updateCount++;
            accumulator -= dt;
        }
    }



    private void render(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //TODO: encapsulate this in a Camera-class, let it "position" itself relative to an entity
        final int offset = 0;
        final float left = 0;
        final float right = METERS_TO_SHOW_X;
        final float bottom = METERS_TO_SHOW_Y;
        final float top = 0;
        final float near = 0f;
        final float far = 1f;
        Matrix.orthoM(_viewportMatrix, offset, left, right, bottom, top, near, far);

        _border.render(_viewportMatrix);

        for(final Asteroid a : _asteroids){
            a.render(_viewportMatrix);

            for(final Text t : _texts){
                t.render(_viewportMatrix);
            }
            for(final Bullet b : _bullets){
                if(b.isDead()){ continue; } //skip
                b.render(_viewportMatrix);
            }
        }

        for(final Star s : _stars){
            s.render(_viewportMatrix);
        }
        _player.render(_viewportMatrix);
    }



    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void setControls(final InputManager input){
        _inputs = input;
    }

    public boolean maybeFireBullet(final GLEntity source){
        for(final Bullet b : _bullets) {
            if(b.isDead()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    private void collisionDetection(){
        for(final Bullet b : _bullets) {
            if(b.isDead()){ continue; } //skip dead bullets
            for(final Asteroid a : _asteroids) {
                if(b.isColliding(a)){
                    Asteroid.onHitLaser();
                    if(a.isDead()){continue;}
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                }
            }
        }
        for(final Asteroid a : _asteroids) {
            if(a.isDead()){continue;}
            if(_player.isColliding(a)){
                _player.onCollision(a);
                _player.onHitAsteroid();
                a.onCollision(_player);
            }
        }
    }

    public void removeDeadEntities(){
        Asteroid temp;
        final int count = _asteroids.size();
        for(int i = count-1; i >= 0; i--){
            temp = _asteroids.get(i);
            if(temp.isDead()){
                _asteroids.remove(i);
            }
        }
    }

}
