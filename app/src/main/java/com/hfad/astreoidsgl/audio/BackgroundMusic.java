package com.hfad.astreoidsgl.audio;

import android.content.Context;
import android.media.MediaPlayer;

import com.hfad.astreoidsgl.GameConfig;


@SuppressWarnings("FieldCanBeLocal")
public class BackgroundMusic {
    @SuppressWarnings("FieldCanBeLocal")
    private MediaPlayer _mediaPlayer = null;
    @SuppressWarnings("UnusedAssignment")
    Context _context = null;

    public BackgroundMusic(Context context){
        _context = context;
    }

    public void loadBackgroundMusic(int songName) {
        _mediaPlayer = MediaPlayer.create(_context, songName);
        _mediaPlayer.setLooping(true);
        _mediaPlayer.setVolume(GameConfig.BG_VOLUME_LEFT, GameConfig.BG_VOLUME_RIGHT);
        _mediaPlayer.start();

    }
}
