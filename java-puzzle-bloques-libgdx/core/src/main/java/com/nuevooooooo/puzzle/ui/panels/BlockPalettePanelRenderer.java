package com.nuevooooooo.puzzle.ui.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Marco visual del panel izquierdo de bloques (estilo holograma cartoon).
 */
public final class BlockPalettePanelRenderer {
    private static final float ICON_SIZE = 22f;
    private static final float ICON_X = 10f;
    private static final float TEXT_X = 46f;

    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;
    private final BitmapFont font;

    public BlockPalettePanelRenderer(SpaceThemeRenderer theme, BitmapFont font) {
        this.theme = theme;
        this.assets = theme.assets();
        this.font = font;
    }

    public void renderBackground(SpriteBatch batch, ShapeRenderer sr, float time) {
        float glow = 0.65f + 0.35f * MathUtils.sin(time * 1.6f);

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.panelHologramGame,
                SpaceTheme.LEFT_X - 4f, SpaceTheme.SIDE_TOP_Y - 6f,
                SpaceTheme.LEFT_W + 8f, SpaceTheme.SIDE_H + 10f);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.12f, 0.45f, 0.95f, 0.10f * glow);
        theme.drawNeonEdge(sr, SpaceTheme.LEFT_X, SpaceTheme.SIDE_TOP_Y,
                SpaceTheme.LEFT_W, SpaceTheme.SIDE_H, 16f, SpaceTheme.CYAN);
        sr.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "BLOQUES", SpaceTheme.LEFT_X + 72f, SpaceTheme.SIDE_TOP_Y + SpaceTheme.SIDE_H - 16f);
        batch.end();
    }

    public void renderPaletteBlock(SpriteBatch batch, Rectangle rect, String iconKey, String label, BitmapFont labelFont) {
        batch.setColor(Color.WHITE);
        batch.draw(blockBackground(iconKey), rect.x, rect.y, rect.width, rect.height);
        Texture icon = blockIcon(iconKey);
        if (icon != null) {
            float iconY = rect.y + (rect.height - ICON_SIZE) / 2f;
            batch.draw(icon, rect.x + ICON_X, iconY, ICON_SIZE, ICON_SIZE);
        }
        labelFont.setColor(Color.WHITE);
        labelFont.draw(batch, label, rect.x + TEXT_X, rect.y + rect.height * 0.68f);
    }

    private Texture blockBackground(String iconKey) {
        return switch (iconKey) {
            case "loop" -> assets.blockLoopOrange;
            case "wall", "path" -> assets.blockLogicPurple;
            case "star", "ship", "say" -> assets.blockActionGreen;
            case "turn_left", "turn_right" -> assets.blockTurnBlue;
            default -> assets.blockMoveBlue;
        };
    }

    private Texture blockIcon(String iconKey) {
        return switch (iconKey) {
            case "forward" -> assets.iconArrowForward;
            case "turn_left" -> assets.iconTurnLeft;
            case "turn_right" -> assets.iconTurnRight;
            case "loop" -> assets.iconRepeat;
            case "wall" -> assets.iconWall;
            case "path" -> assets.iconPathFree;
            case "star" -> assets.iconCollectStar;
            case "ship" -> assets.iconUseShip;
            default -> null;
        };
    }

    public void renderCategoryLabels(SpriteBatch batch, BitmapFont smallFont) {
        batch.begin();
        smallFont.setColor(new Color(0.72f, 0.94f, 1f, 1f));
        smallFont.draw(batch, "Movimiento", SpaceTheme.LEFT_X + 28f, 602f);
        smallFont.draw(batch, "Bucles", SpaceTheme.LEFT_X + 28f, 438f);
        smallFont.draw(batch, "Logica", SpaceTheme.LEFT_X + 28f, 370f);
        smallFont.draw(batch, "Accion", SpaceTheme.LEFT_X + 28f, 260f);
        batch.end();
    }
}
