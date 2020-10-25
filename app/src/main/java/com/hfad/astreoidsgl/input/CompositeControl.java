package com.hfad.astreoidsgl.input;



import com.hfad.astreoidsgl.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class CompositeControl extends InputManager {
    private final ArrayList<InputManager> mInputs = new ArrayList<>();
    private int mCount;

    public CompositeControl(InputManager... inputs) {
        super();
        mInputs.addAll(Arrays.asList(inputs));
        mCount = mInputs.size();
    }

    public void addInput(InputManager im){
        mInputs.add(im);
        mCount = mInputs.size();
    }


    @Override
    public void onStart() {
        for(InputManager im : mInputs){
            im.onStart();
        }
    }

    @Override
    public void onStop() {
        for(InputManager im : mInputs){
            im.onStop();
        }
    }

    @Override
    public void onPause() {
        for(InputManager im : mInputs){
            im.onPause();
        }
    }

    @Override
    public void onResume() {
        for(InputManager im : mInputs){
            im.onResume();
        }
    }

  @Override
    public void update(float dt) {
        InputManager temp;
      _pressingHyperspace = false;
      _pressingBoost = false;
      _pressingLaser = false;
        _horizontalFactor = 0.0f;
        _verticalFactor = 0.0f;
        for(int i = 0; i < mCount; i++){
            temp = mInputs.get(i);
            temp.update(dt);
            _pressingHyperspace = _pressingHyperspace || temp._pressingHyperspace;
            _pressingBoost = _pressingBoost || temp._pressingBoost;
            _pressingLaser = _pressingLaser || temp._pressingLaser;
            _horizontalFactor += temp._horizontalFactor;
            _verticalFactor += temp._verticalFactor;
        }
        _horizontalFactor = Utils.clamp(_horizontalFactor, -1f, 1f);
        _verticalFactor = Utils.clamp(_verticalFactor, -1f, 1f);
    }
}