package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.GameState;

/**
 * Menu basado en el mockup AAA completo: una sola imagen de referencia +
 * datos vivos + zonas clicables + hover glow.
 */
public final class ReferenceMenuRenderer {
    private final MenuAssets assets;
    private final GlyphLayout layout = new GlyphLayout();
    private int hoveredButton = -1;
    private int hoveredWorld = -1;

    public ReferenceMenuRenderer(MenuAssets assets) {
        this.assets = assets;
    }

    public void update(float mx, float my) {
        hoveredButton = ReferenceMenuLayout.buttonAt(mx, my);
        hoveredWorld = ReferenceMenuLayout.worldAt(mx, my);
    }

    public int hoveredButton() {
        return hoveredButton;
    }

    public int worldAt(float x, float y) {
        return ReferenceMenuLayout.worldAt(x, y);
    }

    public void drawBase(SpriteBatch batch, float time) {
        batch.setColor(Color.WHITE);
        batch.draw(assets.menuFullReference, 0f, 0f, ReferenceMenuLayout.W, ReferenceMenuLayout.H);

        // Pulso sutil en portal (centro mockup)
        float pulse = 0.08f + MathUtils.sin(time * 2f) * 0.05f;
        batch.setColor(0.55f, 0.75f, 1f, pulse);
        batch.draw(assets.portalGlow, 538f, 248f, 204f, 204f);
        batch.setColor(Color.WHITE);
    }

    public void drawHoverGlow(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        if (hoveredButton >= 0) {
            drawGlowRect(sr, ReferenceMenuLayout.BUTTONS[hoveredButton], new Color(1f, 1f, 1f, 0.22f));
        }
        if (hoveredWorld >= 0 && ReferenceMenuLayout.WORLD_UNLOCKED[hoveredWorld]) {
            drawGlowCircle(sr, ReferenceMenuLayout.WORLDS[hoveredWorld], new Color(0.4f, 0.9f, 1f, 0.28f));
        } else if (hoveredWorld >= 0) {
            drawGlowCircle(sr, ReferenceMenuLayout.WORLDS[hoveredWorld], new Color(1f, 1f, 1f, 0.12f));
        }
        sr.end();
    }

    public void drawLiveStats(SpriteBatch batch, BitmapFont hudFont, BitmapFont smallFont) {
        // Tapar numeros estaticos del mockup con pills oscuras minimas
        drawStatPill(batch, ReferenceMenuLayout.HUD_STARS_X - 8f, ReferenceMenuLayout.HUD_STARS_Y - 18f, 72f);
        drawStatPill(batch, ReferenceMenuLayout.HUD_GEMS_X - 8f, ReferenceMenuLayout.HUD_GEMS_Y - 18f, 72f);
        drawStatPill(batch, ReferenceMenuLayout.HUD_COINS_X - 8f, ReferenceMenuLayout.HUD_COINS_Y - 18f, 82f);

        hudFont.setColor(Color.WHITE);
        hudFont.draw(batch, String.valueOf(GameState.starsEarned()),
                ReferenceMenuLayout.HUD_STARS_X, ReferenceMenuLayout.HUD_STARS_Y);
        hudFont.draw(batch, String.valueOf(GameState.gems()),
                ReferenceMenuLayout.HUD_GEMS_X, ReferenceMenuLayout.HUD_GEMS_Y);
        hudFont.draw(batch, String.valueOf(GameState.sessionCoins()),
                ReferenceMenuLayout.HUD_COINS_X, ReferenceMenuLayout.HUD_COINS_Y);

        smallFont.setColor(new Color(0.82f, 0.94f, 1f, 1f));
        smallFont.draw(batch, "Nv. " + GameState.playerLevel(),
                ReferenceMenuLayout.HUD_LEVEL_X, ReferenceMenuLayout.HUD_LEVEL_Y);

        // Barra XP encima del mockup
        batch.setColor(0.08f, 0.18f, 0.38f, 0.85f);
        batch.draw(assets.xpBar, ReferenceMenuLayout.HUD_XP_X, ReferenceMenuLayout.HUD_XP_Y - 8f,
                ReferenceMenuLayout.HUD_XP_W, 8f);
        batch.setColor(0.25f, 0.88f, 0.55f, 0.95f);
        batch.draw(assets.xpBar, ReferenceMenuLayout.HUD_XP_X, ReferenceMenuLayout.HUD_XP_Y - 8f,
                ReferenceMenuLayout.HUD_XP_W * GameState.xpProgress(), 8f);
        batch.setColor(Color.WHITE);

        // Subtexto Continuar (nivel actual)
        if (GameState.hasSaveProgress()) {
            smallFont.setColor(new Color(0.75f, 0.92f, 1f, 0.95f));
            smallFont.draw(batch, "Nivel " + (GameState.currentLevelIndex() + 1) + " - Planeta Lunar",
                    ReferenceMenuLayout.BTN_CONTINUE.x + 18f, ReferenceMenuLayout.BTN_CONTINUE.y + 14f);
        }
    }

    private void drawStatPill(SpriteBatch batch, float x, float y, float w) {
        batch.setColor(0.04f, 0.08f, 0.2f, 0.72f);
        batch.draw(assets.xpBar, x, y, w, 22f);
        batch.setColor(Color.WHITE);
    }

    private void drawGlowRect(ShapeRenderer sr, Rectangle r, Color c) {
        sr.setColor(c);
        sr.rect(r.x - 3f, r.y - 3f, r.width + 6f, r.height + 6f);
    }

    private void drawGlowCircle(ShapeRenderer sr, Rectangle r, Color c) {
        sr.setColor(c);
        float cx = r.x + r.width / 2f;
        float cy = r.y + r.height / 2f;
        sr.circle(cx, cy, r.width * 0.58f);
    }
}
