package com.hfad.astreoidsgl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hfad.astreoidsgl.audio.BackgroundMusic;
import com.hfad.astreoidsgl.audio.Jukebox;
import com.hfad.astreoidsgl.entities.Asteroid;
import com.hfad.astreoidsgl.entities.Border;
import com.hfad.astreoidsgl.entities.Bullet;
import com.hfad.astreoidsgl.entities.GLEntity;
import com.hfad.astreoidsgl.entities.LargeAsteroid;
import com.hfad.astreoidsgl.entities.MediumAsteroid;
import com.hfad.astreoidsgl.entities.Particles;
import com.hfad.astreoidsgl.entities.Player;
import com.hfad.astreoidsgl.entities.SmallAsteroid;
import com.hfad.astreoidsgl.entities.Star;
import com.hfad.astreoidsgl.input.InputManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


@SuppressWarnings({"unchecked", "SameParameterValue", "rawtypes", "SpellCheckingInspection"})
public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final String TAG = "";
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    private static DecimalFormat df = new DecimalFormat("0");
    public final int levelNumber = 1;
    public final ArrayList<Asteroid> _asteroids = new ArrayList();
    public final ArrayList<Asteroid> _asteroidsToRemove = new ArrayList();
    public final ArrayList<Asteroid> _asteroidsToAdd = new ArrayList();
    final Bullet[] _bullets = new Bullet[GameConfig.BULLET_COUNT];
    private final ArrayList<Star> _stars = new ArrayList();
    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    protected final float[] _viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera

    final double dt = 0.01;
    final Random r = new Random();

    //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    public Jukebox _jukebox = null;
    public InputManager _inputs = new InputManager(); //empty but valid default
    public volatile float mAngle;
    public Player _player = null;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
    FPSCounter _fpsCounter = null;
    private Border _border;
    private HUD _hud = null;
    private BackgroundMusic _backgroundMusic = null;
    private float previousX;
    private float previousY;
    private volatile boolean _isRunning = false;


    //private MyGLRenderer _renderer = null;
    public boolean _gameOver;


    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private void init() {
        _fpsCounter = new FPSCounter();
        GLEntity._game = this;
        setEGLContextClientVersion(2); //select OpenGL ES 2.0
        setPreserveEGLContextOnPause(true); //context *may* be preserved and thus *may* avoid slow reloads when switching apps.
        // we always re-create the OpenGL context in onSurfaceCreated, so we're safe either way.
        _jukebox = new Jukebox(getContext());
        _backgroundMusic = new BackgroundMusic(getContext());
        _backgroundMusic.loadBackgroundMusic(R.raw.background_music);
        spawnPlayerAndAsteroids();
        _hud = new HUD(this.getContext(), _player, _fpsCounter, levelNumber);

        for (int i = 0; i < GameConfig.BULLET_COUNT; i++) {
            _bullets[i] = new Bullet();
        }

        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLManager.buildProgram(); //compile, link and upload our GL program
        //GLES20.glClearColor(BG_COLOR[0], BG_COLOR[1], BG_COLOR[2], BG_COLOR[3]); //set clear color
        GLES20.glClearColor(GameConfig.BG_COLOR[0], GameConfig.BG_COLOR[1], GameConfig.BG_COLOR[2], GameConfig.BG_COLOR[3]); //set clear color
        // center the player in the world.

        // spawn Border at the center of the world now!
        _border = new Border(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        _jukebox.play(GameConfig.START_GAME);

        for (int i = 0; i < GameConfig.STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) GameConfig.WORLD_WIDTH), r.nextInt((int) GameConfig.WORLD_HEIGHT)));
        }

    }

    private void spawnPlayerAndAsteroids() {
        //player start position
        _player = new Player(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2);

        int worldWidth = 0;
        int worldHeight = 0;
        int point = 0;

        for (int i = 0; i < GameConfig.SMALLER_ASTEROIDS_COUNT; i++) {

            worldWidth = r.nextInt((int) GameConfig.WORLD_WIDTH);
            worldHeight = r.nextInt((int) GameConfig.WORLD_HEIGHT);
            point = r.nextInt((GameConfig.MAX_ASTEROID_POINTS - GameConfig.MIN_ASTEROID_POINTS) + 1) + GameConfig.MIN_ASTEROID_POINTS;

            Asteroid smallAsteroid = new SmallAsteroid(worldWidth, worldHeight, point);
            smallAsteroid.SetActive(true);
            _asteroidsToAdd.add(smallAsteroid);
        }
        for (int i = 0; i < GameConfig.MEDIUM_ASTEROIDS_COUNT; i++) {

            worldWidth = r.nextInt((int) GameConfig.WORLD_WIDTH);
            worldHeight = r.nextInt((int) GameConfig.WORLD_HEIGHT);
            point = r.nextInt((GameConfig.MAX_ASTEROID_POINTS - GameConfig.MIN_ASTEROID_POINTS) + 1) + GameConfig.MIN_ASTEROID_POINTS;

            Asteroid mediumAsteroid = new MediumAsteroid(worldWidth, worldHeight, point);
            mediumAsteroid.SetActive(true);
            _asteroidsToAdd.add(mediumAsteroid);
            _asteroidsToAdd.addAll(mediumAsteroid.get_child_asteroids());
        }
        for (int i = 0; i < GameConfig.LARGE_ASTEROIDS_COUNT; i++) {

            worldWidth = r.nextInt((int) GameConfig.WORLD_WIDTH);
            worldHeight = r.nextInt((int) GameConfig.WORLD_HEIGHT);
            point = r.nextInt((GameConfig.MAX_ASTEROID_POINTS - GameConfig.MIN_ASTEROID_POINTS) + 1) + GameConfig.MIN_ASTEROID_POINTS;

            Asteroid largeAsteroid = new LargeAsteroid(worldWidth, worldHeight, point);
            largeAsteroid.SetActive(true);
            _asteroidsToAdd.add(largeAsteroid);
            _asteroidsToAdd.addAll(largeAsteroid.get_child_asteroids());
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
    //trying a fixed time-step with accumulator, courtesy of
//   https://gafferongames.com/post/fix_your_timestep/

    @Override
    public void onDrawFrame(final GL10 unused) {
        _fpsCounter.logFrame();
        render();
        update();
    }

    private void update() {
        final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        if (_gameOver) {
            restart();
        }

        accumulator += frameTime;
        while (accumulator >= dt) {
            input(dt);
            for (final Asteroid a : _asteroids) {
                a.update(dt);
            }


            for (final Bullet b : _bullets) {
                if (b.isDead()) {
                    continue;
                } //skip
                b.update(dt);
            }
            for (final Star s : _stars) {
                s.update(dt);
            }
            _player.update(dt);

            collisionDetection();
            //removeDeadEntities();
            addAndRemoveAsteroids();
            accumulator -= dt;
        }
        checkGameOver();
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //clear buffer to background color
        //setup a projection matrix by passing in the range of the game world that will be mapped by OpenGL to the screen.
        //TODO: encapsulate this in a Camera-class, let it "position" itself relative to an entity
        final int offset = 0;
        final float left = 0;
        final float right = GameConfig.METERS_TO_SHOW_X;
        final float bottom = GameConfig.METERS_TO_SHOW_Y;
        final float top = 0;
        final float near = 0f;
        final float far = 1f;
        Matrix.orthoM(_viewportMatrix, offset, left, right, bottom, top, near, far);


        _border.render(_viewportMatrix);

        for (final Asteroid a : _asteroids) {
            a.render(_viewportMatrix);
        }

        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip
            b.render(_viewportMatrix);
        }


        for (final Star s : _stars) {
            if (s._showIt) {
                s.render(_viewportMatrix);
            }
        }
        _player.render(_viewportMatrix);
        _hud.renderHUD(_viewportMatrix, this);

    }


    private void checkGameOver() {
        if (_player._isDead) {
            _jukebox.play(GameConfig.GAME_OVER);
            _asteroids.clear();
            _gameOver = true;
        }

    }

    private void restart() {
        spawnPlayerAndAsteroids();
        _player.respawn();
        _hud = new HUD(this.getContext(), _player, _fpsCounter, levelNumber);
        _jukebox.play(GameConfig.START_GAME);
        _gameOver = false;
    }


    public void setControls(final InputManager input) {
        _inputs.onStart();
        _inputs = input;
        _inputs.onPause();
        _inputs.onStop();
    }

    private void input(final double dt) {
        _inputs.update((float) dt);

    }

    public boolean maybeFireBullet(final GLEntity source) {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                b.fireFrom(source);
                return true;
            }
        }
        return false;
    }

    private void collisionDetection() {
        for (final Bullet b : _bullets) {
            if (b.isDead()) {
                continue;
            } //skip dead bullets
            for (final Asteroid a : _asteroids) {
                if(!a.IsActive()) {
                    continue;
                }

                if (b.isColliding(a)) {
                    a.onHitLaser(a);
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                    b._ttl = GameConfig.BULLET_DEAD; //kill bullet if colliding with asteroid, timetolive = 0;
                }
            }

        }
        for (final Asteroid a : _asteroids) {
            if (a.isDead() || !a.IsActive()) {
                continue;
            }
            if (_player.isColliding(a)) {
                _player.onCollision(a);
                _player.onHitAsteroid();
                a.onCollision(_player);
            }
        }
    }

    public void removeDeadEntities() {
        Asteroid temp;
        final int count = _asteroidsToRemove.size();
        for (int i = count - 1; i >= 0; i--) {
            temp = _asteroidsToRemove.get(i);
            if (temp.isDead()) {
                temp.dead();
            }
        }
    }

    private void addAndRemoveAsteroids() {

        for (Asteroid asteroid : _asteroids) {
            if (asteroid.isDead()) {
                _asteroidsToRemove.add(asteroid);
            }
        }

        removeDeadEntities();

        _asteroids.addAll(_asteroidsToAdd);
        _asteroidsToRemove.clear();
        _asteroidsToAdd.clear();
    }

    //below is executing on UI THREAD

    public void onResume() {
        Log.d(TAG, "onResume");
        _inputs.onResume();
        _backgroundMusic.onResume();
        //todo: resume GLSurfaceView/onWindowFocusChanged?
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        _inputs.onPause();
        _backgroundMusic.onPause();
        //todo: pause GLSurfaceView

    }


    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _jukebox.destroy();
        _backgroundMusic.destroy();
        _inputs = null;
        GLEntity._game = null;
    }


}
