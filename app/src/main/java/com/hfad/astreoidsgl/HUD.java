package com.hfad.astreoidsgl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.SurfaceView;

import com.hfad.astreoidsgl.entities.Asteroid;
import com.hfad.astreoidsgl.entities.Player;
import com.hfad.astreoidsgl.entities.Text;

import java.util.ArrayList;


@SuppressWarnings("UnusedAssignment")
@SuppressLint("ViewConstructor")
public class HUD extends SurfaceView {
    private static final String TAG = "";
    public static boolean _laserHitAsteroid = false;
    public static boolean _playerIsHit = false;
    protected final ArrayList<Text> _texts = new ArrayList<>();
    Player _player = null;
    Asteroid _asteroid = null;
    String _textHealth = null;
    String _textScore = null;
    String _textLevel = "";
    FPSCounter _fps = null;


    //final float _halfScreenWidth = (float)GameConfig.STAGE_WIDTH/2; //to get the middle ish.

    public HUD(Context context, Player _player, FPSCounter _fps, int levelNumber) {
        super(context);
        this._player = _player;
        this._fps = _fps;
        _textLevel = "Level: " + levelNumber;
        _textHealth = String.format(getResources().getString(R.string.playerHealth), _player._health);
        _textScore = String.format(getResources().getString(R.string.score), _player._score);
        _texts.add(0, new Text(_textHealth, 8, 8));
        _texts.add(1, new Text(_textScore, 8, 16));
        _texts.add(2, new Text(_textLevel, 8, 130));
        _texts.add(3, new Text("", 8, 32));

    }


    public void renderHUD(float[] _viewportMatrix, Game game) {

        if (_playerIsHit) {
            _textHealth = String.format(getResources().getString(R.string.playerHealth), game._player._health);
            _texts.set(0, new Text(_textHealth, 8, 8));
            _playerIsHit = false;
        }


        if (_laserHitAsteroid) {
            _textScore = String.format(getResources().getString(R.string.score), _player._score);
            _texts.set(1, new Text(_textScore, 8, 16));
            _laserHitAsteroid = false;
        }

        _texts.set(3, new Text("FPS" + _fps.get_fps(), 205, 8));

        for (final Text t : _texts) {
            t.render(_viewportMatrix);
        }

    }

}
