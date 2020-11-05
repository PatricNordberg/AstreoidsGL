package com.hfad.astreoidsgl.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.hfad.astreoidsgl.GameConfig;

import java.io.IOException;

@SuppressWarnings("UnusedAssignment")
public class Jukebox {
    SoundPool _soundPool = null;

    public Jukebox(final Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        //create soundpool
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(GameConfig.MAX_STREAMS)
                .build();
        loadSounds(context);

    }


    private void loadSounds(final Context context) {

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("hurt.wav");
            GameConfig.HURT = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("startgame.wav");
            GameConfig.START_GAME = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("pickup_coin.wav");
            GameConfig.PICKUP_COIN = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("gameover.wav");
            GameConfig.GAME_OVER = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("laser.wav");
            GameConfig.LASER = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("explosion.wav");
            GameConfig.EXPLOSION = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("boost.wav");
            GameConfig.BOOST = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("hyperspace.wav");
            GameConfig.HYPERSPACE = _soundPool.load(descriptor, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void play(final int soundID) {
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final int priority = 1;
        final int loop = 0;


        final float rate = 1.0f;
        if (soundID > 0) {
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }


    public void destroy() {
        _soundPool.release();
        _soundPool = null;
    }

    public void onPause() {
    }


}