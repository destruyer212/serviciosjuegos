package com.nuevooooooo.puzzle.ui.theme;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Utilidades de dibujo reutilizables para la UI premium espacial.
 */
public final class SpaceThemeRenderer {
    private final SpaceThemeAssets assets;
    private final GlyphLayout layout = new GlyphLayout();

    public SpaceThemeRenderer(SpaceThemeAssets assets) {
        this.assets = assets;
    }

    public void drawBackground(SpriteBatch batch, ShapeRenderer sr, float time) {
        float driftFarX = MathUtils.sin(time * 0.06f) * 10f;
        float driftFarY = MathUtils.cos(time * 0.05f) * 8f;
        float driftStarsX = MathUtils.sin(time * 0.11f) * 22f;
        float driftStarsY = MathUtils.cos(time * 0.09f) * 16f;

        batch.begin();
        batch.setColor(0.04f, 0.02f, 0.14f, 1f);
        batch.draw(assets.bgGameParallaxFar, -36f + driftFarX, -28f + driftFarY,
                SpaceTheme.W + 72f, SpaceTheme.H + 56f);
        batch.setColor(1f, 1f, 1f, 0.92f);
        batch.draw(assets.bgGameParallaxStars, -24f + driftStarsX, -18f + driftStarsY,
                SpaceTheme.W + 48f, SpaceTheme.H + 36f);
        batch.setColor(1f, 1f, 1f, 0.22f);
        batch.draw(assets.alienPlanetBackground, -16f + driftFarX * 0.5f, -12f + driftFarY * 0.5f,
                SpaceTheme.W + 32f, SpaceTheme.H + 24f);
        batch.setColor(1f, 1f, 1f, 0.18f);
        batch.draw(assets.spaceBackground, -10f + driftStarsX * 0.35f, -8f + driftStarsY * 0.35f,
                SpaceTheme.W + 20f, SpaceTheme.H + 16f);
        batch.setColor(SpaceTheme.WHITE);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.02f, 0.01f, 0.08f, 0.12f);
        sr.rect(0, 0, SpaceTheme.W, SpaceTheme.H);
        drawParallaxStars(sr, time, 18);
        sr.end();
    }

    public void drawParallaxStars(ShapeRenderer sr, float time, int count) {
        for (int i = 0; i < count; i++) {
            float x = 20f + ((i * 149f) % 1240f);
            float y = 24f + ((i * 83f) % 680f);
            y -= (time * (4.5f + i % 7)) % 760f;
            if (y < -16f) {
                y += 760f;
            }
            float pulse = 0.50f + 0.50f * MathUtils.sin(time * 2.0f + i * 0.68f);
            sr.setColor(0.78f, 0.92f, 1f, 0.12f + pulse * 0.28f);
            sr.circle(x, y, 1.2f + (i % 5) * 0.55f);
        }
    }

    public void drawRoundRect(ShapeRenderer sr, float x, float y, float w, float h, float radius, Color color) {
        sr.setColor(color);
        if (radius <= 0f) {
            sr.rect(x, y, w, h);
            return;
        }
        float r = Math.min(radius, Math.min(w, h) / 2f);
        sr.rect(x + r, y, w - 2 * r, h);
        sr.rect(x, y + r, w, h - 2 * r);
        sr.circle(x + r, y + r, r);
        sr.circle(x + w - r, y + r, r);
        sr.circle(x + r, y + h - r, r);
        sr.circle(x + w - r, y + h - r, r);
    }

    public void drawPanelGlow(ShapeRenderer sr, float x, float y, float w, float h, float radius, Color edge) {
        sr.setColor(edge.r, edge.g, edge.b, edge.a * 0.35f);
        drawRoundRect(sr, x - 3f, y - 3f, w + 6f, h + 6f, radius + 3f, edge.cpy().mul(1f, 1f, 1f, 0.35f));
        sr.setColor(edge.r, edge.g, edge.b, 0.55f);
        drawRoundRect(sr, x, y, w, h, radius, edge);
    }

    public void drawNeonEdge(ShapeRenderer sr, float x, float y, float w, float h, float radius, Color color) {
        sr.setColor(color.r, color.g, color.b, 0.42f);
        sr.rect(x + radius, y + h - 3f, w - 2f * radius, 3f);
        sr.rect(x + radius, y, w - 2f * radius, 3f);
        sr.rect(x, y + radius, 3f, h - 2f * radius);
        sr.rect(x + w - 3f, y + radius, 3f, h - 2f * radius);
    }

    public void drawPill(ShapeRenderer sr, float x, float y, float w, float h, Color fill, Color highlight) {
        drawRoundRect(sr, x + 2f, y - 2f, w, h, h / 2f, fill.cpy().mul(0.6f, 0.6f, 0.6f, fill.a));
        drawRoundRect(sr, x, y, w, h, h / 2f, fill);
        sr.setColor(highlight.r, highlight.g, highlight.b, 0.55f);
        sr.rect(x + h * 0.35f, y + h - 5f, w - h * 0.7f, 3f);
    }

    public void drawStarIcon(ShapeRenderer sr, float cx, float cy, float radius, Color color) {
        sr.setColor(color);
        for (int i = 0; i < 5; i++) {
            float a = (float) (Math.PI * 2 * i / 5f - Math.PI / 2f);
            float a2 = (float) (Math.PI * 2 * (i + 2) / 5f - Math.PI / 2f);
            sr.triangle(cx, cy,
                    cx + MathUtils.cos(a) * radius, cy + MathUtils.sin(a) * radius,
                    cx + MathUtils.cos(a2) * radius, cy + MathUtils.sin(a2) * radius);
        }
    }

    public void drawCoinIcon(ShapeRenderer sr, float cx, float cy, float radius, Color color) {
        sr.setColor(color);
        sr.circle(cx, cy, radius);
        sr.setColor(color.r * 0.85f, color.g * 0.85f, color.b * 0.5f, 1f);
        sr.circle(cx, cy, radius * 0.72f);
        sr.setColor(1f, 1f, 1f, 0.35f);
        sr.circle(cx - radius * 0.22f, cy + radius * 0.18f, radius * 0.22f);
    }

    public void drawHeartIcon(ShapeRenderer sr, float cx, float cy, float size, Color color) {
        sr.setColor(color);
        float r = size * 0.42f;
        sr.circle(cx - r * 0.55f, cy + r * 0.15f, r);
        sr.circle(cx + r * 0.55f, cy + r * 0.15f, r);
        sr.triangle(cx - r * 1.15f, cy + r * 0.05f, cx + r * 1.15f, cy + r * 0.05f, cx, cy - r * 1.05f);
    }

    public void drawCenteredText(BitmapFont font, SpriteBatch batch, String text, float x, float y, float w, float h) {
        layout.setText(font, text);
        font.draw(batch, text, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f - 2f);
    }

    public void drawScaledTexture(SpriteBatch batch, com.badlogic.gdx.graphics.Texture texture,
                                  float x, float y, float w, float h) {
        batch.draw(texture, x, y, w, h);
    }

    public SpaceThemeAssets assets() {
        return assets;
    }
}
