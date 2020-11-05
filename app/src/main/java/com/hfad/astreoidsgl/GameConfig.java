package com.hfad.astreoidsgl;

public class GameConfig {

    //General
    public static final float WORLD_WIDTH = 250;
    public static final float WORLD_HEIGHT = 140;
    public static final float MAX_VEL = 14f;
    public static final float MIN_VEL = -14f;
    static final float[] BG_COLOR = {0 / 255f, 0 / 255f, 0 / 255f, 1f}; //RGBA
    static final float METERS_TO_SHOW_X = WORLD_WIDTH; //160m x 90m, the entire game world in view
    static final float METERS_TO_SHOW_Y = WORLD_HEIGHT;
    static final int ONE_SECOND = 1000;
    //Asteroid
    public static final int ASTEROID_COUNT = 10;
    final static int MIN_ASTEROID_POINTS = 3;
    final static int MAX_ASTEROID_POINTS = 11;
    final static int SMALLER_ASTEROIDS_COUNT = 4;
    final static int MEDIUM_ASTEROIDS_COUNT = 4;
    final static int LARGE_ASTEROIDS_COUNT = 3;
    //Bullet
    public static final float SPEED = 120f;
    public static final float TIME_TO_LIVE = 1.5f; //seconds
    public static final int MAX_STREAMS = 3;
    public static final int BG_VOLUME_LEFT = 100;
    public static final int BG_VOLUME_RIGHT = 100;
    public static final float ROTATION_VELOCITY = 360f;
    public static final float THRUST = 40f;
    public static final float DRAG = 0.99f;
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds.
    static final int BULLET_COUNT = (int) (TIME_TO_LIVE / TIME_BETWEEN_SHOTS) + 1;
    static final int BULLET_DEAD = 0;
    //storage
    public static final float TIME_BETWEEN_HYPERSPACE = 0.75f; //seconds.
    public static final float TIME_BETWEEN_BOOST = 0.75f; //seconds.
    //Star
    static final int STAR_COUNT = 100;
    //Jukebox
    public static int HURT = 0;
    public static int START_GAME = 0;
    public static int PICKUP_COIN = 0;
    public static int LASER = 0;
    public static int EXPLOSION = 0;
    public static int GAME_OVER = 0;
    public static int BOOST = 0;
    public static int HYPERSPACE = 0;
    //Player
    public static final int STARTING_HEALTH = 3;
    public static final int STARTING_SCORE = 0;
    public static final float _playerHeight = 8f;
    public static final float _playerWidth = 4f;
    //Particles
    public static final float PARTICLE_SIZE = 0.2f;
    public static final int PARTICLE_VEL_R_RANGE = 2;
}
