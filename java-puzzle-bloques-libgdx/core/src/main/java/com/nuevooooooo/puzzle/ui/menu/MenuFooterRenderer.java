package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nuevooooooo.puzzle.GameState;

/** Footer con iconos vectoriales. */
public final class MenuFooterRenderer {
    private static final float FOOTER_H = 52f;

    public void renderBackground(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.02f, 0.05f, 0.14f, 0.92f);
        sr.rect(0f, 0f, 1280f, FOOTER_H);
        sr.setColor(0.25f, 0.62f, 1f, 0.38f);
        sr.rect(0f, FOOTER_H - 2f, 1280f, 2f);
        sr.end();
    }

    public void render(SpriteBatch batch, ShapeRenderer sr, BitmapFont smallFont, MenuAssets assets) {
        smallFont.setColor(new Color(0.84f, 0.92f, 1f, 1f));
        smallFont.draw(batch, "Pase Estelar  Nv.15", 22f, 34f);
        smallFont.setColor(new Color(0.65f, 0.82f, 1f, 1f));
        smallFont.draw(batch, "350 / 500", 22f, 16f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawQuickIcon(sr, 520f, new Color(0.95f, 0.55f, 0.18f, 1f));
        drawQuickIcon(sr, 610f, new Color(0.55f, 0.28f, 0.92f, 1f));
        drawQuickIcon(sr, 700f, new Color(0.22f, 0.72f, 0.42f, 1f));
        sr.end();

        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, "DIARIO", 508f, 12f);
        smallFont.draw(batch, "RULETA", 598f, 12f);
        smallFont.draw(batch, "TIENDA", 688f, 12f);

        int unlocked = GameState.robotsUnlockedCount();
        smallFont.draw(batch, "Robots " + unlocked + "/20", 1040f, 34f);
        batch.setColor(Color.WHITE);
        batch.draw(assets.roboIdle, 1040f, 8f, 34f, 34f);
        if (GameState.isMonoUnlocked()) {
            batch.draw(assets.monoPreview, 1080f, 8f, 34f, 34f);
        }
    }

    private void drawQuickIcon(ShapeRenderer sr, float x, Color c) {
        sr.setColor(c.r * 0.5f, c.g * 0.5f, c.b * 0.5f, 0.5f);
        sr.circle(x + 16f, 26f, 18f);
        sr.setColor(c);
        sr.circle(x + 16f, 28f, 16f);
    }
}
