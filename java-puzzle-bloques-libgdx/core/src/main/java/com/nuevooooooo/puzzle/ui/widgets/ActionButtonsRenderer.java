package com.nuevooooooo.puzzle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Botones de acción: icono a la izquierda, texto a la derecha (sin solaparse).
 */
public final class ActionButtonsRenderer {
    private static final float ICON_CX = 30f;
    private static final float TEXT_START = 62f;

    private final SpaceThemeRenderer theme;
    private final BitmapFont font;
    private final BitmapFont smallFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final Rectangle runButton = new Rectangle();
    private final Rectangle stepButton = new Rectangle();
    private final Rectangle undoButton = new Rectangle();
    private final Rectangle clearButton = new Rectangle();
    private final Rectangle pauseButton = new Rectangle();
    private final Rectangle resetButton = new Rectangle();

    public ActionButtonsRenderer(SpaceThemeRenderer theme, BitmapFont font, BitmapFont smallFont) {
        this.theme = theme;
        this.font = font;
        this.smallFont = smallFont;
        layoutButtons();
    }

    public void layoutButtons() {
        runButton.set(SpaceTheme.ACTION_X, SpaceTheme.ACTION_RUN_Y, SpaceTheme.ACTION_W, SpaceTheme.ACTION_H);
        stepButton.set(SpaceTheme.ACTION_X, SpaceTheme.ACTION_STEP_Y, SpaceTheme.ACTION_W, SpaceTheme.ACTION_H);
        undoButton.set(SpaceTheme.ACTION_X, SpaceTheme.ACTION_UNDO_Y, SpaceTheme.ACTION_W, SpaceTheme.ACTION_H);
        clearButton.set(SpaceTheme.ACTION_X, SpaceTheme.ACTION_CLEAR_Y, SpaceTheme.ACTION_W, SpaceTheme.ACTION_H);
        pauseButton.set(SpaceTheme.AUX_PAUSE_X, SpaceTheme.AUX_ROW_Y,
                SpaceTheme.AUX_PAUSE_W, SpaceTheme.AUX_BTN_H);
        resetButton.set(SpaceTheme.AUX_RESET_X, SpaceTheme.AUX_ROW_Y,
                SpaceTheme.AUX_RESET_W, SpaceTheme.AUX_BTN_H);
    }

    public Rectangle runButton() {
        return runButton;
    }

    public Rectangle stepButton() {
        return stepButton;
    }

    public Rectangle undoButton() {
        return undoButton;
    }

    public Rectangle clearButton() {
        return clearButton;
    }

    public Rectangle pauseButton() {
        return pauseButton;
    }

    public Rectangle resetButton() {
        return resetButton;
    }

    public void render(SpriteBatch batch, ShapeRenderer sr, float time, boolean running) {
        float runPulse = running ? 1f : 0.96f + 0.04f * MathUtils.sin(time * 3.2f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawCartoonButton(sr, runButton, SpaceTheme.BTN_RUN, runPulse);
        drawCartoonButton(sr, stepButton, SpaceTheme.BTN_STEP, 1f);
        drawCartoonButton(sr, undoButton, SpaceTheme.BTN_UNDO, 1f);
        drawCartoonButton(sr, clearButton, SpaceTheme.BTN_CLEAR, 1f);
        drawCartoonButton(sr, pauseButton, new Color(0.12f, 0.36f, 0.76f, 1f), 1f);
        drawCartoonButton(sr, resetButton, new Color(0.17f, 0.65f, 0.98f, 1f), 1f);

        drawPlayIcon(sr, runButton);
        drawStepIcon(sr, stepButton);
        drawUndoIcon(sr, undoButton);
        drawTrashIcon(sr, clearButton);
        drawPauseIcon(sr, pauseButton);
        drawResetIcon(sr, resetButton);
        sr.end();

        batch.begin();
        font.setColor(Color.WHITE);
        drawMainLabel(font, batch, "Ejecutar", runButton);
        drawMainLabel(font, batch, "Paso a paso", stepButton);
        drawMainLabel(font, batch, "Deshacer", undoButton);
        drawMainLabel(font, batch, "Borrar todo", clearButton);
        smallFont.setColor(Color.WHITE);
        drawAuxLabel(smallFont, batch, "Pausa", pauseButton);
        drawAuxLabel(smallFont, batch, "Reiniciar", resetButton);
        batch.end();
    }

    private void drawMainLabel(BitmapFont f, SpriteBatch batch, String text, Rectangle r) {
        layout.setText(f, text);
        f.draw(batch, text, r.x + TEXT_START, r.y + (r.height + layout.height) / 2f - 2f);
    }

    private void drawAuxLabel(BitmapFont f, SpriteBatch batch, String text, Rectangle r) {
        layout.setText(f, text);
        f.draw(batch, text, r.x + 50f, r.y + (r.height + layout.height) / 2f - 2f);
    }

    private void drawCartoonButton(ShapeRenderer sr, Rectangle r, Color color, float pulse) {
        Color shadow = new Color(0f, 0.06f, 0.22f, 0.42f);
        theme.drawRoundRect(sr, r.x, r.y - 4f * pulse, r.width, r.height, 16f, shadow);
        theme.drawRoundRect(sr, r.x, r.y, r.width, r.height * pulse, 16f, color);
        sr.setColor(Math.min(1f, color.r + 0.14f), Math.min(1f, color.g + 0.14f), Math.min(1f, color.b + 0.14f), 1f);
        sr.rect(r.x + 12f, r.y + r.height * pulse - 6f, r.width - 24f, 4f);
        sr.setColor(0f, 0f, 0f, 0.12f);
        sr.rect(r.x + ICON_CX + 18f, r.y + 6f, 2f, r.height - 12f);
    }

    private void drawPlayIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + ICON_CX;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.triangle(cx + 6f, cy + 10f, cx + 6f, cy - 10f, cx + 22f, cy);
    }

    private void drawStepIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + ICON_CX;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.rect(cx - 4f, cy - 10f, 7f, 20f);
        sr.triangle(cx + 10f, cy + 10f, cx + 10f, cy - 10f, cx + 24f, cy);
    }

    private void drawUndoIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + ICON_CX;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.circle(cx, cy, 12f);
        sr.setColor(SpaceTheme.BTN_UNDO);
        sr.circle(cx + 3f, cy, 8f);
        sr.setColor(Color.WHITE);
        sr.triangle(cx - 12f, cy + 10f, cx - 20f, cy + 2f, cx - 8f, cy - 1f);
    }

    private void drawTrashIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + ICON_CX;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.rect(cx - 8f, cy - 8f, 22f, 18f);
        sr.rect(cx - 12f, cy + 10f, 30f, 4f);
        sr.rect(cx - 4f, cy + 14f, 14f, 5f);
    }

    private void drawPauseIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + 22f;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.rect(cx, cy - 9f, 7f, 18f);
        sr.rect(cx + 14f, cy - 9f, 7f, 18f);
    }

    private void drawResetIcon(ShapeRenderer sr, Rectangle r) {
        float cx = r.x + 22f;
        float cy = r.y + r.height / 2f;
        sr.setColor(Color.WHITE);
        sr.circle(cx + 8f, cy, 10f);
        sr.triangle(cx - 2f, cy + 8f, cx - 8f, cy + 1f, cx + 2f, cy - 2f);
    }
}
