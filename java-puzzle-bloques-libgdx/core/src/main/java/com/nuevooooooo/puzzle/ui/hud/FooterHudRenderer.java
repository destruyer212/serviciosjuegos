package com.nuevooooooo.puzzle.ui.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

/**
 * Barra inferior: mascota, globo, objetivo, cofre, logros y robots con PNG.
 */
public final class FooterHudRenderer {
    private static final float BADGE_ICON = 34f;

    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;
    private final BitmapFont font;
    private final BitmapFont smallFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle buyLifeBadge = new Rectangle(
            SpaceTheme.BADGE_CHEST_X, SpaceTheme.BADGE_Y,
            SpaceTheme.BADGE_W, SpaceTheme.BADGE_H);
    private final Rectangle achievementsBadge = new Rectangle(
            SpaceTheme.BADGE_TROPHY_X, SpaceTheme.BADGE_Y,
            SpaceTheme.BADGE_W, SpaceTheme.BADGE_H);
    private final Rectangle robotsBadge = new Rectangle(
            SpaceTheme.BADGE_ROBOT_X, SpaceTheme.BADGE_Y,
            SpaceTheme.BADGE_W, SpaceTheme.BADGE_H);

    public FooterHudRenderer(SpaceThemeRenderer theme, BitmapFont font, BitmapFont smallFont) {
        this.theme = theme;
        this.assets = theme.assets();
        this.font = font;
        this.smallFont = smallFont;
    }

    public Rectangle buyLifeBadge() {
        return buyLifeBadge;
    }

    public Rectangle achievementsBadge() {
        return achievementsBadge;
    }

    public Rectangle robotsBadge() {
        return robotsBadge;
    }

    public void render(ShapeRenderer sr, SpriteBatch batch, HudState state,
                       Texture guidePortrait, Texture roboIcon, float time) {
        float bounce = MathUtils.sin(time * 2.2f) * 3f;
        float glow = 0.55f + 0.45f * MathUtils.sin(time * 1.8f);
        float badgePulse = 0.75f + 0.25f * MathUtils.sin(time * 3.5f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        theme.drawRoundRect(sr, 0, SpaceTheme.FOOTER_Y, SpaceTheme.W, SpaceTheme.FOOTER_H, 0, SpaceTheme.FOOTER_BAR);
        sr.setColor(0.22f, 0.55f, 1f, 0.18f);
        sr.rect(0, SpaceTheme.FOOTER_H - 3f, SpaceTheme.W, 3f);

        sr.setColor(SpaceTheme.OBJECTIVE_BAR.r, SpaceTheme.OBJECTIVE_BAR.g, SpaceTheme.OBJECTIVE_BAR.b, 0.22f * glow);
        theme.drawRoundRect(sr, SpaceTheme.OBJECTIVE_X - 6f, SpaceTheme.OBJECTIVE_Y - 6f,
                SpaceTheme.OBJECTIVE_W + 12f, SpaceTheme.OBJECTIVE_H + 12f, 24f,
                new Color(SpaceTheme.OBJECTIVE_BAR.r, SpaceTheme.OBJECTIVE_BAR.g,
                        SpaceTheme.OBJECTIVE_BAR.b, 0.22f * glow));
        theme.drawNeonEdge(sr, SpaceTheme.OBJECTIVE_X, SpaceTheme.OBJECTIVE_Y,
                SpaceTheme.OBJECTIVE_W, SpaceTheme.OBJECTIVE_H, 20f, SpaceTheme.CYAN);
        sr.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        if (guidePortrait != null) {
            batch.draw(guidePortrait, SpaceTheme.MASCOT_X, SpaceTheme.MASCOT_Y + bounce,
                    SpaceTheme.MASCOT_SIZE, SpaceTheme.MASCOT_SIZE);
        }

        batch.draw(assets.speechBubble, SpaceTheme.BUBBLE_X, SpaceTheme.BUBBLE_Y + bounce,
                SpaceTheme.BUBBLE_W, SpaceTheme.BUBBLE_H);

        batch.setColor(1f, 1f, 1f, 0.96f);
        batch.draw(assets.panelDarkBlue, SpaceTheme.OBJECTIVE_X, SpaceTheme.OBJECTIVE_Y,
                SpaceTheme.OBJECTIVE_W, SpaceTheme.OBJECTIVE_H);
        batch.setColor(Color.WHITE);

        drawBadge(batch, SpaceTheme.BADGE_CHEST_X, assets.iconChestGold);
        drawBadge(batch, SpaceTheme.BADGE_TROPHY_X, assets.iconTrophyGold);
        drawBadge(batch, SpaceTheme.BADGE_ROBOT_X,
                roboIcon != null ? roboIcon : assets.iconRobotCollection);

        float globeSize = 30f;
        batch.draw(assets.iconGlobeObjective,
                SpaceTheme.OBJECTIVE_X + 12f,
                SpaceTheme.OBJECTIVE_Y + (SpaceTheme.OBJECTIVE_H - globeSize) / 2f,
                globeSize, globeSize);

        drawNotificationBadge(batch, SpaceTheme.BADGE_TROPHY_X, state.achievementBadge, badgePulse);
        drawNotificationBadge(batch, SpaceTheme.BADGE_ROBOT_X, state.robotsBadge, badgePulse);

        smallFont.setColor(SpaceTheme.BUBBLE_TEXT);
        float bubbleTextTop = SpaceTheme.BUBBLE_Y + bounce + SpaceTheme.BUBBLE_H - 12f;
        drawWrapped(smallFont, batch, state.roboMessage,
                SpaceTheme.BUBBLE_X + 12f, bubbleTextTop,
                SpaceTheme.BUBBLE_W - 20f, 3);

        float objectiveTextX = SpaceTheme.OBJECTIVE_X + 48f;
        float objectiveTextW = SpaceTheme.OBJECTIVE_W - 56f;
        smallFont.setColor(new Color(0.62f, 0.88f, 1f, 1f));
        smallFont.draw(batch, "Objetivo", objectiveTextX, SpaceTheme.OBJECTIVE_Y + SpaceTheme.OBJECTIVE_H - 8f);
        smallFont.setColor(Color.WHITE);
        drawWrapped(smallFont, batch, state.missionGoal,
                objectiveTextX, SpaceTheme.OBJECTIVE_Y + SpaceTheme.OBJECTIVE_H - 24f,
                objectiveTextW, 2);

        float badgeTextX = SpaceTheme.BADGE_CHEST_X + 46f;
        float badgeTextW = SpaceTheme.BADGE_W - 50f;
        if (state.canBuyLife) {
            smallFont.setColor(new Color(1f, 0.92f, 0.45f, 1f));
            theme.drawCenteredText(smallFont, batch, "Comprar vida",
                    badgeTextX, SpaceTheme.BADGE_Y + 30f, badgeTextW, 18f);
            font.setColor(Color.WHITE);
            theme.drawCenteredText(font, batch, state.lifeCost + " cred.",
                    badgeTextX, SpaceTheme.BADGE_Y + 12f, badgeTextW, 18f);
        } else {
            smallFont.setColor(new Color(1f, 0.92f, 0.45f, 1f));
            theme.drawCenteredText(smallFont, batch, "Creditos",
                    badgeTextX, SpaceTheme.BADGE_Y + 30f, badgeTextW, 18f);
            font.setColor(Color.WHITE);
            theme.drawCenteredText(font, batch, "+" + state.chestReward,
                    badgeTextX, SpaceTheme.BADGE_Y + 12f, badgeTextW, 18f);
        }

        badgeTextX = SpaceTheme.BADGE_TROPHY_X + 46f;
        theme.drawCenteredText(smallFont, batch, "Logros",
                badgeTextX, SpaceTheme.BADGE_Y + 22f, badgeTextW, SpaceTheme.BADGE_H - 8f);

        badgeTextX = SpaceTheme.BADGE_ROBOT_X + 46f;
        theme.drawCenteredText(smallFont, batch, "Robots",
                badgeTextX, SpaceTheme.BADGE_Y + 22f, badgeTextW, SpaceTheme.BADGE_H - 8f);
        batch.end();
    }

    private void drawBadge(SpriteBatch batch, float x, Texture icon) {
        batch.draw(assets.panelDarkBlue, x, SpaceTheme.BADGE_Y, SpaceTheme.BADGE_W, SpaceTheme.BADGE_H);
        batch.draw(icon, x + 8f, SpaceTheme.BADGE_Y + 11f, BADGE_ICON, BADGE_ICON);
    }

    private void drawNotificationBadge(SpriteBatch batch, float badgeX, int count, float pulse) {
        float size = 22f * pulse;
        float bx = badgeX + SpaceTheme.BADGE_W - size - 4f;
        float by = SpaceTheme.BADGE_Y + SpaceTheme.BADGE_H - size - 2f;
        batch.draw(assets.badgeNotificationRed, bx, by, size, size);
        font.setColor(Color.WHITE);
        layout.setText(font, String.valueOf(count));
        font.draw(batch, String.valueOf(count), bx + (size - layout.width) / 2f, by + (size + layout.height) / 2f - 2f);
    }

    private void drawWrapped(BitmapFont f, SpriteBatch batch, String text, float x, float y, float maxWidth, int maxLines) {
        if (text == null || text.isEmpty()) {
            return;
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        float lineHeight = f.getLineHeight() * 0.92f;
        float lineY = y;
        int lines = 0;
        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            layout.setText(f, candidate);
            if (layout.width > maxWidth && line.length() > 0) {
                f.draw(batch, line.toString(), x, lineY);
                line = new StringBuilder(word);
                lineY -= lineHeight;
                lines++;
                if (lines >= maxLines - 1) {
                    break;
                }
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (line.length() > 0 && lines < maxLines) {
            String last = line.toString();
            layout.setText(f, last);
            if (layout.width > maxWidth) {
                last = truncateWithEllipsis(f, last, maxWidth);
            }
            f.draw(batch, last, x, lineY);
        }
    }

    private String truncateWithEllipsis(BitmapFont f, String text, float maxWidth) {
        String ellipsis = "...";
        layout.setText(f, ellipsis);
        float ellipsisWidth = layout.width;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            layout.setText(f, sb.toString() + ellipsis);
            if (layout.width > maxWidth) {
                sb.deleteCharAt(sb.length() - 1);
                break;
            }
        }
        return sb + ellipsis;
    }
}
