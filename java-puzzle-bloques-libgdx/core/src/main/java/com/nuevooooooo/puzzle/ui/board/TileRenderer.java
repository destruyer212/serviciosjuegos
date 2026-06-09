package com.nuevooooooo.puzzle.ui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Tiles del mapa con sprites PNG (IMG3) y fallback procedural.
 */
public final class TileRenderer {
    private static final char FLIGHT_TILE = '~';

    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;

    public TileRenderer(SpaceThemeRenderer theme) {
        this.theme = theme;
        this.assets = theme.assets();
    }

    public void drawCellBatch(SpriteBatch batch, char tile, int row, int col,
                              float x, float y, float cell) {
        float pad = 0.5f;
        float drawX = x + pad;
        float drawY = y + pad;
        float size = cell - pad * 2f;
        Texture tex = pickTileTexture(tile, row, col);
        batch.setColor(Color.WHITE);
        batch.draw(tex, drawX, drawY, size, size);
    }

    public void drawCell(ShapeRenderer sr, char tile, int row, int col,
                         float x, float y, float cell, float time) {
        float pad = 1.5f;
        float inner = cell - pad * 2f;
        if (tile == '#') {
            drawGrassTile(sr, x + pad, y + pad, inner, time, row, col);
            return;
        }
        if (tile == FLIGHT_TILE) {
            drawFlightTile(sr, x + pad, y + pad, inner, time, row, col);
            return;
        }
        if (tile == 'G') {
            drawPathTile(sr, x + pad, y + pad, inner, time, row, col);
            return;
        }
        drawPathTile(sr, x + pad, y + pad, inner, time, row, col);
    }

    private Texture pickTileTexture(char tile, int row, int col) {
        int seed = row * 17 + col * 31;
        if (tile == '#') {
            if (seed % 5 == 0) {
                return assets.tileGrassVariant2;
            }
            if (seed % 7 == 0) {
                return assets.tileWallGrass;
            }
            return assets.tileGrass;
        }
        if (tile == FLIGHT_TILE) {
            return assets.tileFlightSpace;
        }
        if (tile == 'G') {
            return assets.tileGoal;
        }
        if (seed % 6 == 0) {
            return assets.tilePathCorner;
        }
        return assets.tilePath;
    }

    private void drawGrassTile(ShapeRenderer sr, float x, float y, float size, float time, int row, int col) {
        int seed = row * 17 + col * 31;
        sr.setColor(0.10f, 0.32f, 0.08f, 1f);
        theme.drawRoundRect(sr, x - 0.5f, y - 1f, size + 1f, size + 1f, 8f,
                new Color(0.10f, 0.32f, 0.08f, 1f));
        theme.drawRoundRect(sr, x, y, size, size, 7f, new Color(0.14f, 0.42f, 0.10f, 1f));
        theme.drawRoundRect(sr, x + 2f, y + 2f, size - 4f, size - 4f, 6f, SpaceTheme.GRASS_DARK);
    }

    private void drawPathTile(ShapeRenderer sr, float x, float y, float size, float time, int row, int col) {
        theme.drawRoundRect(sr, x, y, size, size, 6f, new Color(0.68f, 0.52f, 0.32f, 1f));
        theme.drawRoundRect(sr, x + 2f, y + 2f, size - 4f, size - 4f, 5f, SpaceTheme.SAND);
    }

    private void drawFlightTile(ShapeRenderer sr, float x, float y, float size, float time, int row, int col) {
        theme.drawRoundRect(sr, x, y, size, size, 6f, new Color(0.04f, 0.08f, 0.28f, 0.96f));
        float pulse = 0.20f + MathUtils.sin(time * 2.4f + row + col) * 0.06f;
        sr.setColor(0.10f, 0.74f, 0.96f, pulse);
        sr.circle(x + size / 2f, y + size / 2f, size * 0.38f);
    }
}
