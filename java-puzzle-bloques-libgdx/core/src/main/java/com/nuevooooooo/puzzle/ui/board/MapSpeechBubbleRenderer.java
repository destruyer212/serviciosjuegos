package com.nuevooooooo.puzzle.ui.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Globo de dialogo sobre el robot en el mapa.
 */
public final class MapSpeechBubbleRenderer {
    private final SpaceThemeAssets assets;
    private final BitmapFont smallFont;
    private final GlyphLayout layout = new GlyphLayout();

    public MapSpeechBubbleRenderer(SpaceThemeRenderer theme, BitmapFont smallFont) {
        this.assets = theme.assets();
        this.smallFont = smallFont;
    }

    public void render(SpriteBatch batch, float robotX, float robotY,
                       String message, float time, boolean visible) {
        if (!visible || message == null || message.isEmpty()) {
            return;
        }

        float bob = MathUtils.sin(time * 2.4f) * 3f;
        float bw = 228f;
        float bh = 50f;
        float bx = robotX - bw / 2f + 32f;
        float by = robotY + 54f + bob;

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.speechBubble, bx, by, bw, bh);
        smallFont.setColor(0.14f, 0.20f, 0.42f, 1f);
        layout.setText(smallFont, message);
        float ty = by + (bh + layout.height) / 2f - 2f;
        if (layout.width > bw - 16f) {
            smallFont.draw(batch, truncate(message, 28), bx + 10f, ty);
        } else {
            float tx = bx + (bw - layout.width) / 2f;
            smallFont.draw(batch, message, tx, ty);
        }
        batch.end();
    }

    private static String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }
}
