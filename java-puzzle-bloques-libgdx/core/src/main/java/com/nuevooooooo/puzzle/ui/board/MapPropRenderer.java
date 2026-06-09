package com.nuevooooooo.puzzle.ui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Rocas, arbustos y flores sobre el mapa con sprites PNG.
 */
public final class MapPropRenderer {
    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;

    public MapPropRenderer(SpaceThemeRenderer theme) {
        this.theme = theme;
        this.assets = theme.assets();
    }

    public void drawPropsBatch(SpriteBatch batch, char[][] map, int rows, int cols,
                               float boardX, float boardY, float cell, float time) {
        batch.setColor(Color.WHITE);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = boardX + c * cell;
                float y = boardY + (rows - 1 - r) * cell;
                int hash = r * 31 + c * 17;
                char tile = map[r][c];

                if (tile == '#') {
                    int kind = Math.floorMod(hash, 9);
                    if (kind == 0 || kind == 4) {
                        drawProp(batch, pickRock(hash), x, y, cell, 0.78f, hash, time);
                    } else if (kind == 2) {
                        drawProp(batch, assets.bushGreen, x, y, cell, 0.82f, hash, time);
                    } else if (kind == 6) {
                        drawProp(batch, assets.plantSprout, x, y, cell, 0.72f, hash, time);
                    }
                } else if (tile == '.' || tile == 'G') {
                    if (Math.floorMod(hash, 11) == 3) {
                        drawProp(batch, assets.flowerPink, x, y, cell, 0.55f, hash, time);
                    }
                }
            }
        }
    }

    public void drawProps(ShapeRenderer sr, char[][] map, int rows, int cols,
                          float boardX, float boardY, float cell, float time) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = boardX + c * cell;
                float y = boardY + (rows - 1 - r) * cell;
                int hash = r * 31 + c * 17;
                char tile = map[r][c];

                if (tile == '#') {
                    int kind = Math.floorMod(hash, 9);
                    if (kind == 0 || kind == 4) {
                        drawRock(sr, x + cell / 2f, y + cell * 0.38f, 0.85f + (hash % 3) * 0.08f, hash, time);
                    } else if (kind == 2) {
                        drawBush(sr, x + cell / 2f, y + cell * 0.42f, 0.9f, time, hash);
                    }
                } else if (tile == '.' || tile == 'G') {
                    if (Math.floorMod(hash, 11) == 3) {
                        drawTinyFlower(sr, x + cell * 0.62f, y + cell * 0.55f, time, hash);
                    }
                }
            }
        }
    }

    private Texture pickRock(int hash) {
        return hash % 2 == 0 ? assets.rockLarge : assets.rockSmall;
    }

    private void drawProp(SpriteBatch batch, Texture tex, float x, float y, float cell,
                          float scale, int seed, float time) {
        float bob = MathUtils.sin(time * 0.9f + seed) * 0.6f;
        float size = cell * scale;
        float cx = x + (cell - size) / 2f;
        float cy = y + cell * 0.08f + bob;
        batch.draw(tex, cx, cy, size, size);
    }

    public void drawRock(ShapeRenderer sr, float cx, float cy, float scale, int seed, float time) {
        float bob = MathUtils.sin(time * 0.9f + seed) * 0.4f;
        cy += bob;
        float s = 14f * scale;
        sr.setColor(0.48f, 0.50f, 0.58f, 1f);
        sr.triangle(cx - s * 0.75f, cy - s * 0.05f, cx + s * 0.15f, cy + s * 0.72f, cx - s * 0.35f, cy - s * 0.55f);
    }

    public void drawBush(ShapeRenderer sr, float cx, float cy, float scale, float time, int seed) {
        float sway = MathUtils.sin(time * 1.5f + seed) * 1.2f;
        float s = 11f * scale;
        sr.setColor(0.14f, 0.48f, 0.12f, 1f);
        sr.circle(cx - s * 0.45f + sway, cy, s * 0.55f);
        sr.circle(cx + s * 0.35f - sway * 0.5f, cy + s * 0.05f, s * 0.62f);
    }

    public void drawTinyFlower(ShapeRenderer sr, float cx, float cy, float time, int seed) {
        float sway = MathUtils.sin(time * 2.2f + seed) * 0.8f;
        sr.setColor(0.95f, 0.45f, 0.82f, 1f);
        sr.circle(cx + sway, cy + 5f, 4f);
    }
}
