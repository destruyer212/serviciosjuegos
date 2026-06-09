package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Solo el fondo principal — sin capas que ensucian la UI. */
public final class MenuParallaxRenderer {
    private static final float W = 1280f;
    private static final float H = 720f;

    private final MenuAssets assets;

    public MenuParallaxRenderer(MenuAssets assets) {
        this.assets = assets;
    }

    public void render(SpriteBatch batch, float delta, float time, float mouseX, float mouseY) {
        batch.setColor(Color.WHITE);
        batch.draw(assets.bgBase, 0f, 0f, W, H);
        batch.setColor(Color.WHITE);
    }
}
