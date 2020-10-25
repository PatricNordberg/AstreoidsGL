package com.hfad.astreoidsgl.input;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hfad.astreoidsgl.R;
import com.hfad.astreoidsgl.Utils;

public class VirtualJoystick extends InputManager {

    final String TAG = "";


    public VirtualJoystick(View view) {
        view.findViewById(R.id.joystick_region)
                .setOnTouchListener(new JoystickTouchListener());


        Log.d(TAG, "MaxDistance (pixels): " + _maxDistance);
    }
    


    private class JoystickTouchListener implements View.OnTouchListener{
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event){
            int action = event.getActionMasked();
            if(action == MotionEvent.ACTION_DOWN){
                _startingPositionX = event.getX(0);
                _startingPositionY = event.getY(0);
            }else if(action == MotionEvent.ACTION_UP){
                _horizontalFactor = 0.0f;
                _verticalFactor = 0.0f;
            }else if(action == MotionEvent.ACTION_MOVE){
                //get the proportion to the maxDistance
                _horizontalFactor = (event.getX(0) - _startingPositionX)/_maxDistance;
                _horizontalFactor = Utils.clamp(_horizontalFactor, -1.0f, 1.0f);

                _verticalFactor = (event.getY(0) - _startingPositionY)/_maxDistance;
                _verticalFactor = Utils.clamp(_verticalFactor, -1.0f, 1.0f);
            }
            return true;
        }
    }




}


