package com.hfad.astreoidsgl;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hfad.astreoidsgl.input.CompositeControl;
import com.hfad.astreoidsgl.input.InputManager;
import com.hfad.astreoidsgl.input.TouchController;
import com.hfad.astreoidsgl.input.VirtualJoystick;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {
    private Game _game = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _game = findViewById(R.id.game);
        InputManager input = new CompositeControl(
                new VirtualJoystick(findViewById(R.id.virtual_joystick)),
                new TouchController(findViewById(R.id.gamepad))

        );
        _game.setControls(input);
    }


    @Override
    protected void onResume() {
        super.onResume();
        _game.onResume();
    }

    @Override
    protected void onPause() {
        _game.setVisibility(View.GONE);
        super.onPause();
        _game.onPause();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && _game.getVisibility() == View.GONE) {
            _game.setVisibility(View.VISIBLE);
        }
        if (!hasFocus) {
            return;
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}