package com.nuevooooooo.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Fuente bitmap: primero intenta {@code fonts/lsans-15.fnt} en el classpath del módulo core;
 * si falla, usa la fuente por defecto del jar gdx.
 */
public final class UiFonts {
    private static final String FONT_INTERNAL = "fonts/lsans-15.fnt";

    private UiFonts() {}

    public static BitmapFont createUiFont(float scaleFactor) {
        BitmapFont font;
        try {
            if (Gdx.files.internal(FONT_INTERNAL).exists()) {
                font = new BitmapFont(Gdx.files.internal(FONT_INTERNAL));
            } else {
                font = new BitmapFont();
            }
        } catch (RuntimeException e) {
            font = new BitmapFont();
        }
        font.getData().setScale(Math.max(0.5f, scaleFactor));
        font.setUseIntegerPositions(false);
        for (int i = 0; i < font.getRegions().size; i++) {
            font.getRegion(i).getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return font;
    }
}
