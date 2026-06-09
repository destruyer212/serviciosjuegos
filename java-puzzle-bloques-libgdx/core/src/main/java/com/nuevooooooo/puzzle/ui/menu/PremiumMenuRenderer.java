package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.GameState;

/** Renderiza el menu premium segun mockup CodeQuest AAA. */
public final class PremiumMenuRenderer {
    private static final String[] WORLD_NAMES = {
            "Planeta Lunar", "Academia Estelar", "Saturnia", "Nebulosa Perdida", "Estacion Cosmica"
    };
    private static final boolean[] UNLOCKED = {true, false, false, false, false};
    private static final float[] WORLD_ANG = {200f, 92f, 338f, 232f, 312f};

    private final MenuAssets assets;
    private final GlyphLayout layout = new GlyphLayout();
    private final WorldNode[] worlds = new WorldNode[5];
    private int hoveredWorld = -1;
    private float roboBob;

    public PremiumMenuRenderer(MenuAssets assets) {
        this.assets = assets;
        rebuildWorlds();
    }

    private void rebuildWorlds() {
        Texture[] tex = {
                assets.worldMoon, assets.worldAcademy, assets.worldSaturn,
                assets.worldNebula, assets.worldStation
        };
        for (int i = 0; i < 5; i++) {
            float rad = (float) Math.toRadians(WORLD_ANG[i]);
            float x = MenuLayout.PORTAL_CX + MathUtils.cos(rad) * MenuLayout.WORLD_ORBIT;
            float y = MenuLayout.PORTAL_CY + MathUtils.sin(rad) * MenuLayout.WORLD_ORBIT * 0.78f;
            worlds[i] = new WorldNode(x, y, tex[i], i);
        }
    }

    public void update(float delta, float time, float mx, float my) {
        roboBob = MathUtils.sin(time * 1.12f) * 11f;
        hoveredWorld = -1;
        for (int i = 0; i < worlds.length; i++) {
            if (worlds[i].hit(mx, my)) {
                hoveredWorld = i;
                break;
            }
        }
    }

    public int worldAt(float x, float y) {
        for (int i = 0; i < worlds.length; i++) {
            if (worlds[i].hit(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public void drawBackground(SpriteBatch batch, float time) {
        float driftX = MathUtils.sin(time * 0.08f) * 6f;
        batch.setColor(Color.WHITE);
        batch.draw(assets.bgBase, driftX, 0f, MenuLayout.W, MenuLayout.H);
    }

    public void drawHubLines(ShapeRenderer sr, float time) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < worlds.length; i++) {
            drawDashedLine(sr, MenuLayout.PORTAL_CX, MenuLayout.PORTAL_CY,
                    worlds[i].x, worlds[i].y, UNLOCKED[i], time + i);
        }
        sr.end();
    }

    public void drawPortal(SpriteBatch batch, float time) {
        int frame = ((int) (time * 5f)) % 4;
        float pulse = 1f + MathUtils.sin(time * 1.7f) * 0.035f;
        float size = MenuLayout.PORTAL_SIZE * pulse;
        float cx = MenuLayout.PORTAL_CX;
        float cy = MenuLayout.PORTAL_CY;

        batch.setColor(1f, 1f, 1f, 0.42f + MathUtils.sin(time * 2f) * 0.1f);
        batch.draw(assets.portalGlow, cx - size * 0.62f, cy - size * 0.62f, size * 1.24f, size * 1.24f);
        batch.setColor(Color.WHITE);
        batch.draw(assets.portalSpiral[frame], cx - size / 2f, cy - size / 2f, size, size);
    }

    public void drawWorlds(SpriteBatch batch, BitmapFont smallFont, float time) {
        for (int i = 0; i < worlds.length; i++) {
            WorldNode w = worlds[i];
            float bob = MathUtils.sin(time * 1.15f + i) * 7f;
            float scale = (hoveredWorld == i ? 1.1f : 1f) * 88f;
            float dx = w.x - scale / 2f;
            float dy = w.y + bob - scale / 2f;

            batch.setColor(Color.WHITE);
            batch.draw(w.texture, dx, dy, scale, scale);
            if (!UNLOCKED[i]) {
                batch.setColor(1f, 1f, 1f, 0.72f);
                batch.draw(assets.worldLocked, dx, dy, scale, scale);
            }

            layout.setText(smallFont, WORLD_NAMES[i]);
            float lx = w.x - layout.width / 2f;
            float ly = dy - 8f;
            smallFont.setColor(0f, 0.02f, 0.08f, 0.55f);
            smallFont.draw(batch, WORLD_NAMES[i], lx + 1f, ly - 1f);
            smallFont.setColor(UNLOCKED[i] ? Color.WHITE : new Color(0.78f, 0.86f, 1f, 0.92f));
            smallFont.draw(batch, WORLD_NAMES[i], lx, ly);

            String sub = UNLOCKED[i]
                    ? GameState.starsEarned() + "/" + GameState.maxStars()
                    : "Proximamente";
            layout.setText(smallFont, sub);
            smallFont.setColor(UNLOCKED[i] ? new Color(1f, 0.9f, 0.35f, 1f) : new Color(0.62f, 0.72f, 0.9f, 0.85f));
            smallFont.draw(batch, sub, w.x - layout.width / 2f, ly - 16f);
        }
        batch.setColor(Color.WHITE);
    }

    public void drawLogo(SpriteBatch batch, float time) {
        float bob = MathUtils.sin(time * 0.9f) * 2f;
        batch.setColor(Color.WHITE);
        batch.draw(assets.logo, MenuLayout.LOGO_X, MenuLayout.LOGO_Y + bob, MenuLayout.LOGO_W, MenuLayout.LOGO_H);
    }

    public void drawPanel(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        batch.draw(assets.panelHologram, MenuLayout.PANEL_X, MenuLayout.PANEL_Y,
                MenuLayout.PANEL_W, MenuLayout.PANEL_H);
    }

    public void drawMenuButton(SpriteBatch batch, BitmapFont font, Rectangle rect,
                               Texture tintBtn, Color tint, Texture icon, String label,
                               String sublabel, boolean hover, boolean disabled) {
        float scale = hover && !disabled ? 1.04f : 1f;
        float bw = rect.width * scale;
        float bh = rect.height * scale;
        float bx = rect.x - (bw - rect.width) / 2f;
        float by = rect.y - (bh - rect.height) / 2f;

        if (disabled) {
            batch.setColor(0.45f, 0.48f, 0.55f, 0.8f);
        } else if (hover) {
            batch.setColor(Math.min(1.2f, tint.r * 1.15f), Math.min(1.2f, tint.g * 1.15f),
                    Math.min(1.2f, tint.b * 1.15f), 1f);
        } else {
            batch.setColor(tint);
        }
        batch.draw(tintBtn, bx, by, bw, bh);
        batch.setColor(Color.WHITE);

        if (icon != null) {
            batch.draw(icon, bx + 12f, by + bh / 2f - 15f, 30f, 30f);
        }
        font.setColor(0f, 0.02f, 0.08f, disabled ? 0.35f : 0.55f);
        font.draw(batch, label, bx + 48f, by + bh / 2f + 5f);
        font.setColor(disabled ? new Color(0.78f, 0.8f, 0.88f, 1f) : Color.WHITE);
        font.draw(batch, label, bx + 47f, by + bh / 2f + 6f);
        if (sublabel != null && !sublabel.isEmpty()) {
            font.setColor(new Color(0.72f, 0.92f, 1f, 1f));
            font.draw(batch, sublabel, bx + 47f, by + 12f);
        }
    }

    public void drawRoboAndShip(SpriteBatch batch, float time) {
        float sy = MenuLayout.SHIP_Y + MathUtils.sin(time * 1.05f) * 8f;
        batch.setColor(1f, 1f, 1f, 0.88f);
        batch.draw(assets.shipDecor, MenuLayout.SHIP_X, sy, MenuLayout.SHIP_W, MenuLayout.SHIP_H);
        batch.setColor(Color.WHITE);
        batch.draw(assets.roboIdle, MenuLayout.ROBO_X, MenuLayout.ROBO_Y + roboBob,
                MenuLayout.ROBO_SIZE, MenuLayout.ROBO_SIZE);
    }

    public void drawTopHudBg(ShapeRenderer sr) {
        float y = MenuLayout.HUD_TOP;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.02f, 0.05f, 0.14f, 0.86f);
        sr.rect(0f, y, MenuLayout.W, MenuLayout.HUD_H);
        sr.setColor(0.22f, 0.62f, 1f, 0.42f);
        sr.rect(0f, y, MenuLayout.W, 2f);
        sr.end();
    }

    public void drawTopHud(SpriteBatch batch, BitmapFont font, BitmapFont smallFont) {
        float y = MenuLayout.HUD_TOP;
        batch.draw(assets.avatarFrame, 12f, y + 6f, 42f, 42f);
        batch.draw(assets.roboIdle, 16f, y + 10f, 34f, 34f);
        batch.draw(assets.rankBadges[GameState.rankIndex()], 46f, y + 6f, 20f, 20f);

        font.setColor(Color.WHITE);
        font.draw(batch, "Explorador Estelar", 68f, y + 40f);
        smallFont.setColor(new Color(0.72f, 0.9f, 1f, 1f));
        smallFont.draw(batch, "Nivel " + GameState.playerLevel(), 68f, y + 22f);

        batch.draw(assets.xpBar, 68f, y + 8f, 160f, 10f);
        batch.setColor(0.25f, 0.85f, 0.55f, 0.9f);
        batch.draw(assets.xpBar, 68f, y + 8f, 160f * GameState.xpProgress(), 10f);
        batch.setColor(Color.WHITE);

        drawResource(batch, font, 820f, y + 10f, assets.iconStarHud, String.valueOf(GameState.starsEarned()),
                new Color(0.16f, 0.34f, 0.72f, 0.92f));
        drawResource(batch, font, 940f, y + 10f, assets.iconGem, String.valueOf(GameState.gems()),
                new Color(0.42f, 0.16f, 0.72f, 0.92f));
        drawResource(batch, font, 1060f, y + 10f, assets.iconCoinHud, String.valueOf(GameState.sessionCoins()),
                new Color(0.62f, 0.42f, 0.1f, 0.92f));

        batch.draw(assets.iconGift, 1188f, y + 12f, 30f, 30f);
        batch.draw(assets.iconMail, 1224f, y + 12f, 30f, 30f);
        batch.draw(assets.badgeNotification, 1204f, y + 32f, 12f, 12f);
        batch.setColor(Color.WHITE);
    }

    public void drawFooter(SpriteBatch batch, BitmapFont smallFont) {
        float fy = 8f;
        batch.setColor(Color.WHITE);
        batch.draw(assets.footerStarPass, 14f, fy, 290f, 44f);
        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, "Pase Estelar  Nv.15", 26f, 42f);
        smallFont.setColor(new Color(0.78f, 0.9f, 1f, 1f));
        smallFont.draw(batch, "350 / 500", 26f, 22f);

        drawFooterBtn(batch, smallFont, 500f, "DIARIO", assets.iconDaily);
        drawFooterBtn(batch, smallFont, 590f, "RULETA", assets.iconWheel);
        drawFooterBtn(batch, smallFont, 680f, "TIENDA", assets.iconShop);
        batch.draw(assets.badgeNotification, 708f, 36f, 10f, 10f);

        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, "Robots " + GameState.robotsUnlockedCount() + "/20", 1010f, 42f);
        batch.draw(assets.roboIdle, 1010f, fy + 2f, 36f, 36f);
        if (GameState.isMonoUnlocked()) {
            batch.draw(assets.monoPreview, 1052f, fy + 2f, 36f, 36f);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawResource(SpriteBatch batch, BitmapFont font, float x, float y,
                              Texture icon, String value, Color pill) {
        batch.setColor(pill);
        batch.draw(assets.hudPillStar, x, y, 108f, 32f);
        batch.setColor(Color.WHITE);
        batch.draw(icon, x + 6f, y + 2f, 28f, 28f);
        font.setColor(Color.WHITE);
        font.draw(batch, value, x + 38f, y + 24f);
    }

    private void drawFooterBtn(SpriteBatch batch, BitmapFont font, float x, String label, Texture icon) {
        batch.draw(icon, x, 14f, 34f, 34f);
        font.setColor(new Color(0.82f, 0.92f, 1f, 1f));
        font.draw(batch, label, x - 2f, 12f);
    }

    private void drawDashedLine(ShapeRenderer sr, float x1, float y1, float x2, float y2,
                                boolean bright, float phase) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 1f) return;
        int segments = (int) (len / 10f);
        float ux = dx / len;
        float uy = dy / len;
        for (int i = 0; i < segments; i += 2) {
            float t0 = i / (float) segments;
            float t1 = Math.min(1f, (i + 1) / (float) segments);
            float sx = x1 + ux * len * t0;
            float sy = y1 + uy * len * t0;
            float ex = x1 + ux * len * t1;
            float ey = y1 + uy * len * t1;
            float alpha = bright ? 0.55f : 0.28f;
            sr.setColor(0.35f, 0.88f, 1f, alpha);
            sr.rectLine(sx, sy, ex, ey, 3f);
        }
    }

    public static Rectangle buttonRect(int index) {
        float y = MenuLayout.BTN_FIRST_Y - index * (MenuLayout.BTN_H + MenuLayout.BTN_GAP);
        return new Rectangle(MenuLayout.BTN_X, y, MenuLayout.BTN_W, MenuLayout.BTN_H);
    }

    public static final class WorldNode {
        public final float x;
        public final float y;
        public final Texture texture;
        private final Rectangle hit = new Rectangle();

        WorldNode(float x, float y, Texture texture, int index) {
            this.x = x;
            this.y = y;
            this.texture = texture;
        }

        boolean hit(float px, float py) {
            hit.set(x - 46f, y - 46f, 92f, 92f);
            return hit.contains(px, py);
        }
    }
}
