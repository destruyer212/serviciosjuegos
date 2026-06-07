package com.nuevooooooo.puzzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Carga la fuente UI una sola vez desde {@code fonts/lsans-15.fnt} (recurso del modulo core).
 */
public class PuzzleBloquesLibGDXGame extends Game {

    BitmapFont uiFont;

    @Override
    public void create() {
        uiFont = new BitmapFont(Gdx.files.internal("fonts/lsans-15.fnt"));
        uiFont.getData().setScale(24f / 15f);
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        if (uiFont != null) {
            uiFont.dispose();
        }
        super.dispose();
    }
}
