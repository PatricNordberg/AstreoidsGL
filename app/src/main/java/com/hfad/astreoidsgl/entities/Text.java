package com.hfad.astreoidsgl.entities;

import android.opengl.Matrix;

import com.hfad.astreoidsgl.GLManager;
import com.hfad.astreoidsgl.GLPixelFont;
import com.hfad.astreoidsgl.Mesh;

@SuppressWarnings("FieldCanBeLocal")
public class Text extends GLEntity {
    public static final GLPixelFont FONT = new GLPixelFont();
    public static final float GLYPH_WIDTH = GLPixelFont.WIDTH;
    public static final float GLYPH_HEIGHT = GLPixelFont.HEIGHT;
    public static final float GLYPH_SPACING = 0f;
    Mesh[] _meshes = null;
    private float setTextSize = 2f;
    private float _spacing = GLYPH_SPACING; //spacing between characters
    private float _glyphWidth = GLYPH_WIDTH;
    private float _glyphHeight = GLYPH_HEIGHT;

    public Text(final String s, final float x, final float y) {
        setString(s);
        _x = x;
        _y = y;
        setScale(setTextSize);
    }

    @Override
    public void render(final float[] viewportMatrix) {
        final int OFFSET = 0;
        for (int i = 0; i < _meshes.length; i++) {
            if (_meshes[i] == null) {
                continue;
            }
            Matrix.setIdentityM(modelMatrix, OFFSET); //reset model matrix
            Matrix.translateM(modelMatrix, OFFSET, _x + (_glyphWidth + _spacing) * i, _y, _depth);
            Matrix.scaleM(modelMatrix, OFFSET, _scale, _scale, 1f);
            Matrix.multiplyMM(viewportModelMatrix, OFFSET, viewportMatrix, OFFSET, modelMatrix, OFFSET);
            GLManager.draw(_meshes[i], viewportModelMatrix, _color);
        }
    }

    public void setScale(float factor) {
        _scale = factor;
        _spacing = GLYPH_SPACING * _scale;
        _glyphWidth = GLYPH_WIDTH * _scale;
        _glyphHeight = GLYPH_HEIGHT * _scale;
        _height = _glyphHeight;
        _width = (_glyphWidth + _spacing) * _meshes.length;
    }

    public void setString(final String s) {
        _meshes = FONT.getString(s);
    }
}
