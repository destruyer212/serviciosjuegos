package com.nuevooooooo.puzzle.ui.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Iconos cartoon dibujados proceduralmente (cofre, trofeo, globo, moneda).
 */
public final class CartoonIcons {
    private CartoonIcons() {
    }

    public static void drawChest(ShapeRenderer sr, float cx, float cy, float scale, float time) {
        float w = 34f * scale;
        float h = 26f * scale;
        float x = cx - w / 2f;
        float y = cy - h / 2f;
        float shine = 0.65f + 0.35f * MathUtils.sin(time * 2.8f);

        sr.setColor(0.35f, 0.18f, 0.05f, 0.45f);
        sr.ellipse(x - 2f, y - 5f, w + 4f, 10f * scale);

        sr.setColor(0.72f, 0.42f, 0.08f, 1f);
        sr.rect(x, y + h * 0.38f, w, h * 0.62f);
        sr.setColor(0.95f, 0.72f, 0.14f, 1f);
        sr.rect(x + 2f, y + h * 0.55f, w - 4f, h * 0.42f);

        sr.setColor(0.88f, 0.58f, 0.10f, 1f);
        sr.rect(x, y + h * 0.22f, w, h * 0.22f);
        sr.setColor(1f, 0.86f, 0.35f, 0.85f * shine);
        sr.rect(x + 4f, y + h * 0.62f, w * 0.35f, 3f * scale);

        sr.setColor(0.55f, 0.32f, 0.06f, 1f);
        sr.rect(x + w * 0.5f - 3f * scale, y + h * 0.08f, 6f * scale, h * 0.92f);
        sr.setColor(1f, 0.92f, 0.45f, 1f);
        sr.circle(cx, cy - h * 0.02f, 5f * scale);

        sr.setColor(0.42f, 0.24f, 0.05f, 1f);
        sr.rect(x, y + h * 0.48f, w, h * 0.18f);
        sr.setColor(0.98f, 0.78f, 0.18f, 1f);
        sr.rect(x + 2f, y + h * 0.50f, w - 4f, h * 0.14f);
    }

    public static void drawTrophy(ShapeRenderer sr, float cx, float cy, float scale, float time) {
        float shine = 0.7f + 0.3f * MathUtils.sin(time * 3.2f);
        float cupW = 22f * scale;
        float cupH = 18f * scale;

        sr.setColor(0.55f, 0.38f, 0.05f, 0.35f);
        sr.ellipse(cx - cupW * 0.6f, cy - cupH * 0.85f, cupW * 1.2f, 8f * scale);

        sr.setColor(0.92f, 0.68f, 0.08f, 1f);
        sr.rect(cx - cupW / 2f, cy - cupH * 0.15f, cupW, cupH);
        sr.setColor(1f, 0.88f, 0.22f, shine);
        sr.triangle(cx, cy + cupH * 0.95f, cx - cupW * 0.55f, cy + cupH * 0.05f, cx + cupW * 0.55f, cy + cupH * 0.05f);

        sr.setColor(0.82f, 0.58f, 0.06f, 1f);
        sr.rect(cx - cupW * 0.65f, cy + cupH * 0.05f, 5f * scale, cupH * 0.55f);
        sr.rect(cx + cupW * 0.65f - 5f * scale, cy + cupH * 0.05f, 5f * scale, cupH * 0.55f);

        sr.setColor(0.78f, 0.52f, 0.05f, 1f);
        sr.rect(cx - 5f * scale, cy - cupH * 0.55f, 10f * scale, cupH * 0.42f);
        sr.setColor(0.95f, 0.74f, 0.12f, 1f);
        sr.rect(cx - 11f * scale, cy - cupH * 0.72f, 22f * scale, 5f * scale);

        sr.setColor(1f, 1f, 1f, 0.55f * shine);
        sr.circle(cx - cupW * 0.15f, cy + cupH * 0.35f, 3f * scale);
    }

    public static void drawGlobe(ShapeRenderer sr, float cx, float cy, float radius) {
        sr.setColor(0.12f, 0.48f, 0.92f, 1f);
        sr.circle(cx, cy, radius);
        sr.setColor(0.28f, 0.82f, 0.38f, 0.85f);
        sr.circle(cx - radius * 0.22f, cy + radius * 0.12f, radius * 0.42f);
        sr.circle(cx + radius * 0.28f, cy - radius * 0.18f, radius * 0.32f);
        sr.setColor(1f, 1f, 1f, 0.35f);
        sr.circle(cx - radius * 0.28f, cy + radius * 0.28f, radius * 0.18f);
        sr.setColor(0.05f, 0.22f, 0.55f, 0.45f);
        sr.rect(cx - radius, cy - 1.5f, radius * 2f, 3f);
        sr.rect(cx - 1.5f, cy - radius, 3f, radius * 2f);
    }

    public static void drawCoin(ShapeRenderer sr, float cx, float cy, float radius) {
        sr.setColor(0.85f, 0.55f, 0.05f, 1f);
        sr.circle(cx, cy, radius);
        sr.setColor(1f, 0.82f, 0.18f, 1f);
        sr.circle(cx, cy, radius * 0.82f);
        sr.setColor(1f, 1f, 1f, 0.45f);
        sr.circle(cx - radius * 0.25f, cy + radius * 0.2f, radius * 0.22f);
    }

    public static void drawNotificationBadge(ShapeRenderer sr, float cx, float cy, float radius, int count, float pulse) {
        sr.setColor(0.92f, 0.12f, 0.18f, pulse);
        sr.circle(cx, cy, radius);
        sr.setColor(1f, 1f, 1f, 0.35f);
        sr.circle(cx - radius * 0.22f, cy + radius * 0.18f, radius * 0.28f);
    }

    public static void drawRobotPortrait(SpriteBatch batch, TextureRegion robo, float x, float y, float size) {
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(robo, x, y, size, size);
    }
}
