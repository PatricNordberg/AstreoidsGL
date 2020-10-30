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
import com.hfad.astreoidsgl.entities.Particles;
import com.hfad.astreoidsgl.entities.Player;
import com.hfad.astreoidsgl.entities.Star;
import com.hfad.astreoidsgl.input.InputManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


@SuppressWarnings({"FieldCanBeLocal", "unchecked", "SameParameterValue", "rawtypes", "SpellCheckingInspection"})
public class Game extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final long SECOND_IN_NANOSECONDS = 1000000000;
    public static final long MILLISECOND_IN_NANOSECONDS = 1000000;
    public static final float NANOSECONDS_TO_SECONDS = 1.0f / SECOND_IN_NANOSECONDS;
    private static final String TAG = "";
    //storage
    private static final int BULLET_COUNT = (int) (GameConfig.TIME_TO_LIVE / GameConfig.TIME_BETWEEN_SHOTS) + 1;
    public static float NANOSECONDS_TO_MILLISECONDS = 1.0f / MILLISECOND_IN_NANOSECONDS;
    private static DecimalFormat df = new DecimalFormat("0");
    public final int levelNumber = 1;
    public final ArrayList<Asteroid> _asteroids = new ArrayList();
    public final ArrayList<Asteroid> _shatteredAsteroids = new ArrayList();
    public final ArrayList<Asteroid> _asteroidsToRemove = new ArrayList();
    public final ArrayList<Asteroid> _shatteredAsteroidsToRemove = new ArrayList();
    public final ArrayList<Asteroid> _asteroidsToAdd = new ArrayList();
    public final ArrayList<Asteroid> _shatteredAsteroidsToAdd = new ArrayList();
    public final ArrayList<Particles> _smallParticles = new ArrayList();
    public final ArrayList<Particles> _mediumParticles = new ArrayList();
    public final ArrayList<Particles> _largeParticles = new ArrayList();
    // Create the projection Matrix. This is used to project the scene onto a 2D viewport.
    protected final float[] _viewportMatrix = new float[4 * 4]; //In essence, it is our our Camera
    final Bullet[] _bullets = new Bullet[BULLET_COUNT];
    final double dt = 0.01;
    final int minAsteroid = 3;
    final int maxAsteroid = 11;
    final Random r = new Random();
    private final ArrayList<Star> _stars = new ArrayList();
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    public Jukebox _jukebox = null;
    public InputManager _inputs = new InputManager(); //empty but valid default
    public volatile float mAngle;
    public boolean initSmallAsteroid;
    public boolean initMediumAsteroid;
    protected Player _player = null;
    double accumulator = 0.0;
    double currentTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
    FPSCounter _fpsCounter = null;
    private Border _border;
    private HUD _hud = null;
    private BackgroundMusic _backgroundMusic = null;
    private float previousX;
    private float previousY;
    private boolean _explosion;
    private boolean showParticles;


    //private MyGLRenderer _renderer = null;
    private long timeLarge;
    private long timeMedium;
    private long timeSmall;


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

        _hud = new HUD(this.getContext(), _player, this);

        for (int i = 0; i < BULLET_COUNT; i++) {
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
        _player = new Player(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2); //y == 10
        // spawn Border at the center of the world now!
        _border = new Border(GameConfig.WORLD_WIDTH / 2, GameConfig.WORLD_HEIGHT / 2, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        _jukebox.play(GameConfig.START_GAME);


        for (int i = 0; i < GameConfig.STAR_COUNT; i++) {
            _stars.add(new Star(r.nextInt((int) GameConfig.WORLD_WIDTH), r.nextInt((int) GameConfig.WORLD_HEIGHT)));
        }

        for (int i = 0; i < GameConfig.ASTEROID_COUNT; i++) {
            try {
                _asteroidsToAdd.add(new Asteroid(r.nextInt((int) GameConfig.WORLD_WIDTH), r.nextInt((int) GameConfig.WORLD_HEIGHT), r.nextInt((maxAsteroid - minAsteroid) + 1) + minAsteroid));

            } catch (Exception e) {
                e.printStackTrace();
            }
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
        update(); //TODO: move updates away from the render thread...
        render();
    }

    private void update() {
        final double newTime = System.nanoTime() * NANOSECONDS_TO_SECONDS;
        final double frameTime = newTime - currentTime;
        currentTime = newTime;
        updateParticles();

        accumulator += frameTime;
        while (accumulator >= dt) {
            input(dt);
            for (final Asteroid a : _asteroids) {
                a.update(dt);
            }

            for (final Asteroid smallAsteroid : _shatteredAsteroids) {
                smallAsteroid.update(dt);
            }
            if (_largeParticles.size() > 0) {
                for (final Particles particles : _largeParticles) {
                    particles.update(dt);
                }
            }
            if (_mediumParticles.size() > 0) {
                for (final Particles particles : _mediumParticles) {
                    particles.update(dt);
                }
            }
            if (_smallParticles.size() > 0) {
                for (final Particles particles : _smallParticles) {
                    particles.update(dt);
                }
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

        for (final Asteroid smallAsteroid : _shatteredAsteroids) {
            smallAsteroid.render(_viewportMatrix);
        }
        renderParticles();

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
        _hud.renderHUD(this);

    }

    private void updateParticles() {
        double getTime = System.currentTimeMillis();

        //update particles
        if (getTime - timeLarge > 1000) {
            _largeParticles.clear();
        }
        if (getTime - timeMedium > 1000) {
            _mediumParticles.clear();
        }
        if (getTime - timeSmall > 1000) {
            _smallParticles.clear();
        }
    }

    private void renderParticles() {
        //render particles
        double getTime = System.currentTimeMillis();
        if (getTime - timeLarge < 1000) {
            for (final Particles particles : _largeParticles) {
                particles.render(_viewportMatrix);
            }

        }
        if (getTime - timeMedium < 1000) {
            for (final Particles particles : _mediumParticles) {
                particles.render(_viewportMatrix);
            }

        }
        if (getTime - timeSmall < 1000) {
            for (final Particles particles : _smallParticles) {
                particles.render(_viewportMatrix);
            }

        }
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
                if (b.isColliding(a)) {
                    a.onHitLaser(a);
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                    b._ttl = 0; //kill bullet if colliding with asteroid, timetolive = 0;
                }
            }
            for (final Asteroid a : _shatteredAsteroids) {
                if (b.isColliding(a)) {
                    a.onHitLaser(a);
                    if (a.isDead()) {
                        continue;
                    }
                    b.onCollision(a); //notify each entity so they can decide what to do
                    a.onCollision(b);
                    b._ttl = 0; //kill bullet if colliding with asteroid, timetolive = 0;
                }
            }
        }
        for (final Asteroid a : _asteroids) {
            if (a.isDead()) {
                continue;
            }
            if (_player.isColliding(a)) {
                _player.onCollision(a);
                _player.onHitAsteroid();
                a.onCollision(_player);
            }
        }
        for (final Asteroid a : _shatteredAsteroids) {
            if (a.isDead()) {
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
                _asteroids.remove(temp);
            }
        }
        Asteroid currSmallAsteroid;
        final int count2 = _shatteredAsteroidsToRemove.size();
        for (int i = count2 - 1; i >= 0; i--) {
            currSmallAsteroid = _shatteredAsteroidsToRemove.get(i);
            if (currSmallAsteroid.isDead()) {
                _shatteredAsteroids.remove(currSmallAsteroid);
            }
        }
    }

    private void addAndRemoveAsteroids() {

        for (Asteroid asteroid : _asteroids) {
            if (asteroid.isDead()) {
                _asteroidsToRemove.add(asteroid);
            }
        }


        if (_shatteredAsteroids.size() > 0) {
            for (Asteroid shatteredAsteroid : _shatteredAsteroids) {
                if (shatteredAsteroid.isDead()) {
                    _shatteredAsteroidsToRemove.add(shatteredAsteroid);
                }
            }
        }
        removeDeadEntities();

        _asteroids.addAll(_asteroidsToAdd);
        _shatteredAsteroids.addAll(_shatteredAsteroidsToAdd);
        _asteroidsToRemove.clear();
        _shatteredAsteroidsToRemove.clear();
        _asteroidsToAdd.clear();
        _shatteredAsteroidsToAdd.clear();
    }

    public void smallAsteroidExploding(Asteroid a) {
        timeSmall = System.currentTimeMillis();
        for (int i = 0; i < GameConfig.PARTICLE_COUNT; i++) {
            _smallParticles.add(new Particles(a._x, a._y, 0));
        }
    }

    public void mediumAsteroidExploding(Asteroid a) {
        initSmallAsteroid = true;
        timeMedium = System.currentTimeMillis();
        for (int i = 0; i < GameConfig.PARTICLE_COUNT; i++) {
            _mediumParticles.add(new Particles(a._x, a._y, 0));
        }
        int i = 0;
        while (i < 3) {
            try {
                _shatteredAsteroidsToAdd.add(new Asteroid(a._x, a._y, r.nextInt((maxAsteroid - minAsteroid) + 1) + minAsteroid));

            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }
        initSmallAsteroid = false;

    }

    public void largeAsteroidExploding(Asteroid a) {
        initMediumAsteroid = true;
        timeLarge = System.currentTimeMillis();
        for (int i = 0; i < GameConfig.PARTICLE_COUNT; i++) {
            _largeParticles.add(new Particles(a._x, a._y, 0));
        }
        int i = 0;
        while (i < 3) {
            try {
                _shatteredAsteroidsToAdd.add(new Asteroid(a._x, a._y, r.nextInt((maxAsteroid - minAsteroid) + 1) + minAsteroid));

            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }
        initMediumAsteroid = false;


    }
}
