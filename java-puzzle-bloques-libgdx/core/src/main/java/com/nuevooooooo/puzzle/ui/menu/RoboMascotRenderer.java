package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Mascota Robo — sprite original con transparencia real. */
public final class RoboMascotRenderer {
    private static final float ROBO_X = 1002f;
    private static final float ROBO_Y = 195f;
    private static final float ROBO_SIZE = 228f;

    private final MenuAssets assets;
    private float blinkTimer = 3.5f;
    private float blinkDuration;
    private float waveTimer;
    private boolean waving;

    public RoboMascotRenderer(MenuAssets assets) {
        this.assets = assets;
    }

    public void update(float delta, boolean startHover) {
        blinkTimer -= delta;
        if (blinkTimer <= 0f) {
            blinkDuration = 0.1f;
            blinkTimer = 4f + MathUtils.random(2f);
        }
        if (blinkDuration > 0f) {
            blinkDuration -= delta;
        }
        if (startHover && !waving) {
            waving = true;
            waveTimer = 0f;
        }
        if (waving) {
            waveTimer += delta;
            if (waveTimer > 0.85f) {
                waving = false;
            }
        }
    }

    public void renderShadow(ShapeRenderer sr, float time) {
        float bob = MathUtils.sin(time * 1.1f) * 10f;
        sr.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.12f, 0.55f, 0.95f, 0.16f);
        sr.ellipse(ROBO_X + 28f, ROBO_Y + bob - 6f, 170f, 28f);
        sr.end();
    }

    public void render(SpriteBatch batch, float time) {
        float bob = MathUtils.sin(time * 1.1f) * 10f;
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(assets.roboIdle, ROBO_X, ROBO_Y + bob, ROBO_SIZE, ROBO_SIZE);
    }
}
