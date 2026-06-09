package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nuevooooooo.puzzle.GameState;

/** Barra superior limpia. */
public final class MenuHudRenderer {
    private static final float BAR_H = 52f;

    public void renderBackground(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.02f, 0.06f, 0.16f, 0.9f);
        sr.rect(0f, 720f - BAR_H, 1280f, BAR_H);
        sr.setColor(0.25f, 0.62f, 1f, 0.45f);
        sr.rect(0f, 720f - BAR_H, 1280f, 2f);
        sr.end();
    }

    public void render(SpriteBatch batch, ShapeRenderer sr, BitmapFont font, BitmapFont smallFont,
                       MenuAssets assets) {
        float barY = 720f - BAR_H;
        batch.setColor(Color.WHITE);
        batch.draw(assets.roboIdle, 14f, barY + 7f, 36f, 36f);

        font.setColor(Color.WHITE);
        font.draw(batch, "Explorador Estelar", 58f, barY + 38f);
        smallFont.setColor(new Color(0.72f, 0.88f, 1f, 1f));
        smallFont.draw(batch, GameState.rankTitle() + "  ·  Nv. " + GameState.playerLevel(),
                58f, barY + 18f);

        drawPill(sr, batch, font, 880f, barY + 10f, new Color(0.14f, 0.32f, 0.68f, 0.92f),
                String.valueOf(GameState.starsEarned()));
        drawPill(sr, batch, font, 990f, barY + 10f, new Color(0.62f, 0.42f, 0.1f, 0.92f),
                String.valueOf(GameState.sessionCoins()));
        drawPill(sr, batch, font, 1100f, barY + 10f, new Color(0.48f, 0.18f, 0.72f, 0.92f),
                String.valueOf(GameState.gems()));
    }

    private void drawPill(ShapeRenderer sr, SpriteBatch batch, BitmapFont font,
                          float x, float y, Color c, String value) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(c);
        sr.rect(x, y, 96f, 30f);
        sr.setColor(1f, 1f, 1f, 0.18f);
        sr.rect(x, y + 18f, 96f, 12f);
        sr.end();
        font.setColor(Color.WHITE);
        font.draw(batch, value, x + 14f, y + 22f);
    }
}
