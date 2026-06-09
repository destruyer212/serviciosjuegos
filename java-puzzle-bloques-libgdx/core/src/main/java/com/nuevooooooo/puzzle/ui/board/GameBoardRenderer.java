package com.nuevooooooo.puzzle.ui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Marco neón, grid con sprites, props y estrellas con glow.
 */
public final class GameBoardRenderer {
    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;
    private final TileRenderer tileRenderer;
    private final MapPropRenderer propRenderer;

    public GameBoardRenderer(SpaceThemeRenderer theme) {
        this.theme = theme;
        this.assets = theme.assets();
        this.tileRenderer = new TileRenderer(theme);
        this.propRenderer = new MapPropRenderer(theme);
    }

    public TileRenderer tiles() {
        return tileRenderer;
    }

    public void renderFrame(SpriteBatch batch, ShapeRenderer sr, float time) {
        float x = SpaceTheme.BOARD_FRAME_X;
        float y = SpaceTheme.BOARD_FRAME_Y;
        float w = SpaceTheme.BOARD_FRAME_W;
        float h = SpaceTheme.BOARD_FRAME_H;
        float pulse = 0.55f + 0.45f * MathUtils.sin(time * 1.7f);

        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.35f * pulse);
        batch.draw(assets.boardInnerGlow, x - 8f, y - 8f, w + 16f, h + 16f);
        batch.setColor(Color.WHITE);
        batch.draw(assets.boardFrame, x, y, w, h);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(SpaceTheme.BOARD_GLOW.r, SpaceTheme.BOARD_GLOW.g, SpaceTheme.BOARD_GLOW.b, 0.12f * pulse);
        theme.drawNeonEdge(sr, x, y, w, h, 28f, SpaceTheme.CYAN);
        sr.end();
    }

    public void renderGridTiles(SpriteBatch batch, char[][] map, int rows, int cols,
                                float boardX, float boardY, float cell) {
        batch.begin();
        batch.setColor(Color.WHITE);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = boardX + c * cell;
                float y = boardY + (rows - 1 - r) * cell;
                tileRenderer.drawCellBatch(batch, map[r][c], r, c, x, y, cell);
            }
        }
        batch.end();
    }

    public void renderGridProps(SpriteBatch batch, char[][] map, int rows, int cols,
                                float boardX, float boardY, float cell, float time) {
        batch.begin();
        propRenderer.drawPropsBatch(batch, map, rows, cols, boardX, boardY, cell, time);
        batch.end();
    }

    public void renderGrid(ShapeRenderer sr, char[][] map, int rows, int cols,
                           float boardX, float boardY, float cell, float time) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = boardX + c * cell;
                float y = boardY + (rows - 1 - r) * cell;
                tileRenderer.drawCell(sr, map[r][c], r, c, x, y, cell, time);
            }
        }
        propRenderer.drawProps(sr, map, rows, cols, boardX, boardY, cell, time);
    }

    public void renderStarCollectBurst(SpriteBatch batch, ShapeRenderer sr,
                                       float cx, float cy, float progress, Color color) {
        if (progress <= 0f || progress >= 1f) {
            return;
        }
        float alpha = 1f - progress;
        float size = 48f + progress * 40f;
        batch.begin();
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(assets.starCollectBurst, cx - size / 2f, cy - size / 2f, size, size);
        batch.setColor(Color.WHITE);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        float radius = 10f + progress * 32f;
        for (int ring = 0; ring < 2; ring++) {
            sr.setColor(color.r, color.g, color.b, alpha * (0.25f - ring * 0.08f));
            sr.circle(cx, cy, radius + ring * 8f);
        }
        sr.end();
    }

    public void renderStar(SpriteBatch batch, ShapeRenderer sr, float cx, float cy,
                           Color color, float pulse, float time, int index) {
        float glowSize = 56f * pulse;
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.55f * pulse);
        batch.draw(assets.starGlow, cx - glowSize / 2f, cy - glowSize / 2f, glowSize, glowSize);
        float starSize = 28f * pulse;
        batch.setColor(Color.WHITE);
        batch.draw(assets.starGold, cx - starSize / 2f, cy - starSize / 2f, starSize, starSize);
        float sparkle = 14f * (0.7f + 0.3f * MathUtils.sin(time * 3f + index));
        batch.setColor(1f, 1f, 1f, 0.7f * pulse);
        batch.draw(assets.starSparkle, cx - sparkle / 2f, cy + starSize * 0.35f, sparkle, sparkle);
        batch.setColor(Color.WHITE);
        batch.end();
    }
}
