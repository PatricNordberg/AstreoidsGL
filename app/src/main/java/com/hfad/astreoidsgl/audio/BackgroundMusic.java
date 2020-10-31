package com.hfad.astreoidsgl.audio;

import android.content.Context;
import android.media.MediaPlayer;

import com.hfad.astreoidsgl.GameConfig;


public class BackgroundMusic {
    @SuppressWarnings("UnusedAssignment")
    Context _context = null;
    @SuppressWarnings("FieldCanBeLocal")
    private MediaPlayer _mediaPlayer = null;
    private int length = 0;

    public BackgroundMusic(Context context) {
        _context = context;
    }

    public void loadBackgroundMusic(int songName) {
        _mediaPlayer = MediaPlayer.create(_context, songName);
        _mediaPlayer.setLooping(true);
        _mediaPlayer.setVolume(GameConfig.BG_VOLUME_LEFT, GameConfig.BG_VOLUME_RIGHT);
        _mediaPlayer.start();

    }

    public void onPause() {
        _mediaPlayer.pause();
        length = _mediaPlayer.getCurrentPosition();
    }

    public void destroy() {
        _mediaPlayer = null;
    }

    public void onResume() {
    _mediaPlayer.seekTo(length);
    _mediaPlayer.start();
    }
}
