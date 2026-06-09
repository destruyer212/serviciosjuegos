package com.nuevooooooo.puzzle.ui.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * HUD superior con sprites PNG: misión, estrellas, monedas, vidas y menú.
 */
public final class TopHudRenderer {
    private static final float ICON_SIZE = 28f;
    private static final float MISSION_ICON = 32f;

    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;
    private final BitmapFont titleFont;
    private final BitmapFont font;
    private final BitmapFont smallFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle menuButton = new Rectangle(
            SpaceTheme.MENU_BTN_X, SpaceTheme.MENU_BTN_Y,
            SpaceTheme.MENU_BTN_W, SpaceTheme.MENU_BTN_H);
    private final Rectangle lifePill = new Rectangle(
            SpaceTheme.PILL_LIFE_X, SpaceTheme.PILL_Y,
            SpaceTheme.PILL_W, SpaceTheme.PILL_H);
    private final Rectangle coinPill = new Rectangle(
            SpaceTheme.PILL_COIN_X, SpaceTheme.PILL_Y,
            SpaceTheme.PILL_W, SpaceTheme.PILL_H);
    private final Rectangle starPill = new Rectangle(
            SpaceTheme.PILL_STAR_X, SpaceTheme.PILL_Y,
            SpaceTheme.PILL_W, SpaceTheme.PILL_H);

    public TopHudRenderer(SpaceThemeRenderer theme, BitmapFont titleFont,
                          BitmapFont font, BitmapFont smallFont) {
        this.theme = theme;
        this.assets = theme.assets();
        this.titleFont = titleFont;
        this.font = font;
        this.smallFont = smallFont;
    }

    public Rectangle menuButton() {
        return menuButton;
    }

    public Rectangle lifePill() {
        return lifePill;
    }

    public Rectangle coinPill() {
        return coinPill;
    }

    public Rectangle starPill() {
        return starPill;
    }

    public void render(ShapeRenderer sr, SpriteBatch batch, HudState state, float time) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        theme.drawRoundRect(sr, 0, SpaceTheme.HUD_Y, SpaceTheme.W, SpaceTheme.HUD_H, 0, SpaceTheme.HUD_BAR);
        theme.drawRoundRect(sr, 0, SpaceTheme.HUD_Y + SpaceTheme.HUD_H - 4f, SpaceTheme.W, 4f, 0,
                new Color(0.22f, 0.55f, 1f, 0.45f));
        sr.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.logo, SpaceTheme.LOGO_X, SpaceTheme.LOGO_Y, SpaceTheme.LOGO_SIZE, SpaceTheme.LOGO_SIZE);

        batch.draw(assets.panelDarkBlue, SpaceTheme.MISSION_X, SpaceTheme.MISSION_Y,
                SpaceTheme.MISSION_W, SpaceTheme.MISSION_H);
        batch.draw(assets.iconPlanetMission,
                SpaceTheme.MISSION_X + 10f,
                SpaceTheme.MISSION_Y + (SpaceTheme.MISSION_H - MISSION_ICON) / 2f,
                MISSION_ICON, MISSION_ICON);

        drawPill(batch, SpaceTheme.PILL_STAR_X, SpaceTheme.PILL_Y, assets.iconStarHud);
        drawPill(batch, SpaceTheme.PILL_COIN_X, SpaceTheme.PILL_Y, assets.iconCoinHud);
        drawPill(batch, SpaceTheme.PILL_LIFE_X, SpaceTheme.PILL_Y, assets.iconHeartHud);
        batch.draw(assets.btnMenu, SpaceTheme.MENU_BTN_X, SpaceTheme.MENU_BTN_Y,
                SpaceTheme.MENU_BTN_W, SpaceTheme.MENU_BTN_H);

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, "Codea y juega", SpaceTheme.LOGO_X + SpaceTheme.LOGO_SIZE + 8f, SpaceTheme.HUD_Y + 48f);
        smallFont.setColor(new Color(0.78f, 0.92f, 1f, 1f));
        smallFont.draw(batch, "con " + state.characterName, SpaceTheme.LOGO_X + SpaceTheme.LOGO_SIZE + 8f, SpaceTheme.HUD_Y + 24f);

        String mission = "MISION " + state.levelNumber + ": " + state.missionTitle;
        font.setColor(Color.WHITE);
        theme.drawCenteredText(font, batch, mission,
                SpaceTheme.MISSION_X + 46f, SpaceTheme.MISSION_Y,
                SpaceTheme.MISSION_W - 56f, SpaceTheme.MISSION_H);

        font.setColor(Color.WHITE);
        layout.setText(font, state.starsEarned + "/" + state.maxStars);
        font.draw(batch, state.starsEarned + "/" + state.maxStars,
                SpaceTheme.PILL_STAR_X + 52f + (SpaceTheme.PILL_W - 52f - 28f - layout.width) / 2f,
                SpaceTheme.PILL_Y + (SpaceTheme.PILL_H + layout.height) / 2f - 2f);
        layout.setText(font, String.valueOf(state.coins));
        font.draw(batch, String.valueOf(state.coins),
                SpaceTheme.PILL_COIN_X + 52f + (SpaceTheme.PILL_W - 52f - 28f - layout.width) / 2f,
                SpaceTheme.PILL_Y + (SpaceTheme.PILL_H + layout.height) / 2f - 2f);
        layout.setText(font, String.valueOf(state.lives));
        font.draw(batch, String.valueOf(state.lives),
                SpaceTheme.PILL_LIFE_X + 52f + (SpaceTheme.PILL_W - 52f - 28f - layout.width) / 2f,
                SpaceTheme.PILL_Y + (SpaceTheme.PILL_H + layout.height) / 2f - 2f);

        theme.drawCenteredText(font, batch, "Menu",
                SpaceTheme.MENU_BTN_X, SpaceTheme.MENU_BTN_Y,
                SpaceTheme.MENU_BTN_W, SpaceTheme.MENU_BTN_H);
        batch.end();
    }

    private void drawPill(SpriteBatch batch, float x, float y, com.badlogic.gdx.graphics.Texture icon) {
        batch.draw(assets.hudPill, x, y, SpaceTheme.PILL_W, SpaceTheme.PILL_H);
        boolean heart = icon == assets.iconHeartHud;
        float iconSize = heart ? 20f : ICON_SIZE;
        float slotCx = SpaceTheme.PILL_W * 0.24f;
        float iconX = x + slotCx - iconSize / 2f;
        float iconY = y + (SpaceTheme.PILL_H - iconSize) / 2f;
        batch.draw(icon, iconX, iconY, iconSize, iconSize);
    }
}
