package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.nuevooooooo.puzzle.GameState;

/** Hub galactico 100% vectorial — sin PNGs de mundos/portal. */
public final class GalacticHubRenderer {
    public static final float PORTAL_CX = 640f;
    public static final float PORTAL_CY = 318f;
    private static final float ORBIT = 118f;

    private static final String[] WORLD_NAMES = {
            "Planeta Lunar", "Academia Estelar", "Saturnia", "Nebulosa Perdida", "Estacion Cosmica"
    };
    private static final boolean[] UNLOCKED = {true, false, false, false, false};
    private static final Color[] WORLD_COLORS = {
            new Color(0.62f, 0.66f, 0.72f, 1f),
            new Color(0.95f, 0.78f, 0.28f, 1f),
            new Color(0.95f, 0.55f, 0.22f, 1f),
            new Color(0.72f, 0.28f, 0.88f, 1f),
            new Color(0.28f, 0.78f, 0.95f, 1f)
    };
    private static final float[] WORLD_ANGLES = {-38f, 68f, 32f, -112f, 108f};

    private final GlyphLayout layout = new GlyphLayout();
    private final WorldNode[] nodes = new WorldNode[5];
    private int hovered = -1;

    public GalacticHubRenderer() {
        for (int i = 0; i < 5; i++) {
            float rad = (float) Math.toRadians(WORLD_ANGLES[i]);
            float x = PORTAL_CX + MathUtils.cos(rad) * ORBIT;
            float y = PORTAL_CY + MathUtils.sin(rad) * ORBIT * 0.82f;
            nodes[i] = new WorldNode(x, y, i);
        }
    }

    public void updateHover(float x, float y) {
        hovered = -1;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].contains(x, y)) {
                hovered = i;
                break;
            }
        }
    }

    public int hoveredWorld() {
        return hovered;
    }

    public WorldNode node(int i) {
        return nodes[i];
    }

    public boolean isUnlocked(int i) {
        return i >= 0 && i < UNLOCKED.length && UNLOCKED[i];
    }

    public void renderPortal(ShapeRenderer sr, float time) {
        float pulse = 1f + MathUtils.sin(time * 1.6f) * 0.04f;
        float core = 52f * pulse;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.35f, 0.08f, 0.55f, 0.18f);
        sr.circle(PORTAL_CX, PORTAL_CY, core + 42f);
        sr.setColor(0.2f, 0.45f, 0.95f, 0.28f);
        sr.circle(PORTAL_CX, PORTAL_CY, core + 22f);

        for (int arm = 0; arm < 6; arm++) {
            float base = time * 55f + arm * 60f;
            for (int seg = 0; seg < 12; seg++) {
                float t0 = seg / 12f;
                float t1 = (seg + 1) / 12f;
                float r0 = core * (0.25f + t0 * 0.85f);
                float r1 = core * (0.25f + t1 * 0.85f);
                float a0 = (float) Math.toRadians(base + t0 * 210f);
                float a1 = (float) Math.toRadians(base + t1 * 210f);
                float hue = 0.55f + seg * 0.03f;
                sr.setColor(0.4f + hue * 0.3f, 0.35f, 0.95f, 0.55f - seg * 0.03f);
                sr.rectLine(
                        PORTAL_CX + MathUtils.cos(a0) * r0,
                        PORTAL_CY + MathUtils.sin(a0) * r0,
                        PORTAL_CX + MathUtils.cos(a1) * r1,
                        PORTAL_CY + MathUtils.sin(a1) * r1,
                        5f - seg * 0.2f);
            }
        }

        sr.setColor(1f, 1f, 1f, 0.88f);
        sr.circle(PORTAL_CX, PORTAL_CY, core * 0.2f);
        sr.setColor(0.65f, 0.35f, 1f, 0.75f);
        sr.circle(PORTAL_CX, PORTAL_CY, core * 0.12f);
        sr.end();
    }

    public void renderWorlds(ShapeRenderer sr, float time) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < nodes.length; i++) {
            WorldNode n = nodes[i];
            float bob = MathUtils.sin(time * 1.15f + i * 0.9f) * 6f;
            float cx = n.x;
            float cy = n.y + bob;
            float r = (hovered == i ? 38f : 34f);

            sr.setColor(0f, 0.04f, 0.12f, 0.35f);
            sr.circle(cx + 3f, cy - 4f, r + 4f);

            if (!isUnlocked(i)) {
                sr.setColor(0.28f, 0.32f, 0.45f, 0.92f);
            } else {
                sr.setColor(WORLD_COLORS[i]);
            }
            sr.circle(cx, cy, r);

            drawWorldIcon(sr, i, cx, cy, r, time);

            if (!isUnlocked(i)) {
                sr.setColor(0f, 0f, 0f, 0.38f);
                sr.circle(cx, cy, r + 1f);
                sr.setColor(1f, 0.88f, 0.25f, 1f);
                sr.rect(cx - 7f, cy + 2f, 14f, 12f);
                sr.circle(cx, cy - 6f, 6f);
            }

            if (isUnlocked(i)) {
                sr.setColor(1f, 0.92f, 0.35f, 1f);
                sr.circle(cx + r * 0.55f, cy + r * 0.5f, 6f);
            }
        }
        sr.end();
    }

    private void drawWorldIcon(ShapeRenderer sr, int index, float cx, float cy, float r, float time) {
        switch (index) {
            case 0 -> {
                sr.setColor(0.38f, 0.4f, 0.45f, 1f);
                sr.circle(cx - 10f, cy + 6f, 7f);
                sr.circle(cx + 8f, cy - 4f, 5f);
                sr.setColor(0.95f, 0.35f, 0.55f, 1f);
                sr.triangle(cx - 16f, cy + 14f, cx - 20f, cy + 2f, cx - 12f, cy + 2f);
                sr.setColor(0.25f, 0.55f, 0.95f, 1f);
                sr.triangle(cx + 14f, cy + 12f, cx + 10f, cy, cx + 18f, cy);
            }
            case 1 -> {
                sr.setColor(0.85f, 0.72f, 0.28f, 1f);
                sr.rect(cx - 16f, cy - 8f, 32f, 18f);
                sr.setColor(0.55f, 0.75f, 0.95f, 1f);
                sr.circle(cx, cy + 12f, 14f);
            }
            case 2 -> {
                sr.setColor(0.92f, 0.58f, 0.2f, 1f);
                sr.circle(cx, cy, r * 0.55f);
                sr.setColor(0.95f, 0.78f, 0.45f, 0.85f);
                sr.ellipse(cx - r * 0.85f, cy - 4f, r * 1.7f, 12f);
            }
            case 3 -> {
                sr.setColor(0.78f, 0.32f, 0.92f, 1f);
                for (int k = 0; k < 4; k++) {
                    float a = time * 30f + k * 90f;
                    float px = cx + MathUtils.cosDeg(a) * 12f;
                    float py = cy + MathUtils.sinDeg(a) * 12f;
                    sr.circle(px, py, 6f);
                }
            }
            case 4 -> {
                sr.setColor(0.72f, 0.82f, 0.92f, 1f);
                sr.circle(cx, cy, 14f);
                sr.setColor(0.35f, 0.65f, 0.88f, 1f);
                sr.rect(cx - 18f, cy - 3f, 36f, 6f);
            }
            default -> { }
        }
    }

    public void renderLabels(SpriteBatch batch, BitmapFont font, float time) {
        for (int i = 0; i < nodes.length; i++) {
            WorldNode n = nodes[i];
            float bob = MathUtils.sin(time * 1.15f + i * 0.9f) * 6f;
            float cy = n.y + bob;
            String name = WORLD_NAMES[i];
            layout.setText(font, name);
            float lx = n.x - layout.width / 2f;
            float ly = cy - 50f;

            font.setColor(0f, 0.03f, 0.1f, 0.55f);
            font.draw(batch, name, lx + 1f, ly - 1f);
            font.setColor(isUnlocked(i) ? Color.WHITE : new Color(0.78f, 0.86f, 1f, 0.9f));
            font.draw(batch, name, lx, ly);

            if (i == 0 && isUnlocked(i)) {
                String prog = GameState.starsEarned() + "/" + GameState.maxStars();
                layout.setText(font, prog);
                font.setColor(1f, 0.9f, 0.35f, 1f);
                font.draw(batch, prog, n.x - layout.width / 2f, ly - 16f);
            } else if (!isUnlocked(i)) {
                layout.setText(font, "Proximamente");
                font.setColor(0.65f, 0.74f, 0.9f, 0.85f);
                font.draw(batch, "Proximamente", n.x - layout.width / 2f, ly - 16f);
            }
        }
        font.setColor(Color.WHITE);
    }

    public int worldAt(float x, float y) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public static final class WorldNode {
        public final float x;
        public final float y;
        public final int index;
        private final Rectangle hit = new Rectangle();

        WorldNode(float x, float y, int index) {
            this.x = x;
            this.y = y;
            this.index = index;
        }

        boolean contains(float px, float py) {
            hit.set(x - 40f, y - 40f, 80f, 80f);
            return hit.contains(px, py);
        }
    }
}
