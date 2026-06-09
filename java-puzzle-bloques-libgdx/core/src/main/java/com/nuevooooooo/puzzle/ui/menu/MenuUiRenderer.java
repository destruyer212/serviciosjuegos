package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Botones y panel del menu dibujados en codigo (sin PNGs rotos). */
public final class MenuUiRenderer {
    public static final float PANEL_X = 24f;
    public static final float PANEL_Y = 92f;
    public static final float PANEL_W = 268f;
    public static final float PANEL_H = 500f;

    private static final Color GLASS = new Color(0.04f, 0.14f, 0.32f, 0.72f);
    private static final Color GLASS_EDGE = new Color(0.28f, 0.78f, 1f, 0.55f);

    public void drawPanel(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.02f, 0.08f, 0.35f);
        sr.rect(PANEL_X + 5f, PANEL_Y - 5f, PANEL_W, PANEL_H);
        sr.setColor(GLASS);
        sr.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);
        sr.setColor(GLASS_EDGE);
        sr.rect(PANEL_X, PANEL_Y + PANEL_H - 3f, PANEL_W, 3f);
        sr.rect(PANEL_X, PANEL_Y, PANEL_W, 2f);
        sr.rect(PANEL_X, PANEL_Y, 2f, PANEL_H);
        sr.rect(PANEL_X + PANEL_W - 2f, PANEL_Y, 2f, PANEL_H);
        sr.end();
    }

    public void drawButton(ShapeRenderer sr, float x, float y, float w, float h,
                           Color base, boolean hover, boolean disabled) {
        float scale = hover && !disabled ? 1.04f : 1f;
        float bw = w * scale;
        float bh = h * scale;
        float bx = x - (bw - w) / 2f;
        float by = y - (bh - h) / 2f;
        float r = bh / 2f;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (disabled) {
            sr.setColor(0.22f, 0.26f, 0.34f, 0.75f);
        } else {
            sr.setColor(base.r * 0.45f, base.g * 0.45f, base.b * 0.45f, 0.55f);
        }
        sr.rect(bx + 2f, by - 3f, bw, bh);
        if (disabled) {
            sr.setColor(0.32f, 0.36f, 0.44f, 0.88f);
        } else if (hover) {
            sr.setColor(
                    Math.min(1f, base.r * 1.15f),
                    Math.min(1f, base.g * 1.15f),
                    Math.min(1f, base.b * 1.15f),
                    0.96f);
        } else {
            sr.setColor(base);
        }
        sr.rect(bx, by, bw, bh);
        sr.setColor(1f, 1f, 1f, disabled ? 0.15f : 0.28f);
        sr.rect(bx, by + bh - r * 0.55f, bw, r * 0.55f);
        sr.end();
    }

    public static Color colorForAction(String action) {
        return switch (action) {
            case "START" -> new Color(0.95f, 0.48f, 0.12f, 0.94f);
            case "CONTINUE" -> new Color(0.18f, 0.52f, 0.95f, 0.94f);
            case "MAP" -> new Color(0.52f, 0.28f, 0.92f, 0.94f);
            case "ACHIEVEMENTS" -> new Color(0.18f, 0.72f, 0.38f, 0.94f);
            case "ROBOTS" -> new Color(0.12f, 0.72f, 0.88f, 0.94f);
            case "SETTINGS" -> new Color(0.14f, 0.22f, 0.48f, 0.94f);
            default -> new Color(0.2f, 0.3f, 0.5f, 0.94f);
        };
    }
}
