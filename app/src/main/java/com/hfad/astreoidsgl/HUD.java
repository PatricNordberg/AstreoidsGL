package com.hfad.astreoidsgl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.SurfaceView;

import java.util.ArrayList;


@SuppressWarnings("UnusedAssignment")
@SuppressLint("ViewConstructor")
public class HUD extends SurfaceView {
    private static final String TAG = "";
    public static boolean _laserHitAsteroid = false;
    public static boolean _playerIsHit = false;
    Player _player = null;
    Asteroid _asteroid = null;
    protected ArrayList<Text> _texts = new ArrayList<>();

 

    String _textHealth = String.format(getResources().getString(R.string.playerHealth), GameConfig._health);
    String _textScore = String.format(getResources().getString(R.string.score), GameConfig._score);
    String _textLevel = "Level 1";




    //final float _halfScreenWidth = (float)GameConfig.STAGE_WIDTH/2; //to get the middle ish.

    public HUD(Context context, Player _player) {
        super(context);
        this._player = _player;

        _texts.add(0, new Text(_textHealth, 8, 8));
        _texts.add(1, new Text(_textScore, 8, 16));
        _texts.add(2, new Text(_textLevel, 8, 24));

    }



    public void renderHUD(Game game) {

        if(_playerIsHit){
            _textHealth = String.format(getResources().getString(R.string.playerHealth), GameConfig._health);
            _texts.set(0,new Text(_textHealth, 8, 8));
            _playerIsHit = false;
        }

        if (_laserHitAsteroid){
            _textScore = String.format(getResources().getString(R.string.score), GameConfig._score);
            _texts.set(1,new Text(_textScore, 8, 16));
            _laserHitAsteroid = false;
        }

        for(final Text t : _texts){
            t.render(game._viewportMatrix);
        }

    }

}
