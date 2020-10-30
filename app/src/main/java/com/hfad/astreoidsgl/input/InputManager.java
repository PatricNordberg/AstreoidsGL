package com.hfad.astreoidsgl.input;

import com.hfad.astreoidsgl.Utils;

public class InputManager {
    protected final int _maxDistance = Utils.dpToPx(1); //48dp = minimum hit target. maxDistance is in pixels.
    public float _verticalFactor = 0.0f;
    public float _horizontalFactor = 0.0f;
    public boolean _pressingLaser = false;
    public boolean _pressingBoost = false;
    public boolean _pressingHyperspace = false;
    public float _startingPositionX = 0.0f;
    public float _startingPositionY = 0.0f;
    //"was just pressed" and "was just released" - bullets, one per press

    public void onStart() {
    }

    public void onStop() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public void update(float dt) {
    }


}