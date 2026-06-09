package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.math.Rectangle;

/**
 * Hitboxes calibradas sobre menu_full_reference.png (1280x720).
 * Coordenadas alineadas al mockup AAA CodeQuest.
 */
public final class ReferenceMenuLayout {
    public static final float W = 1280f;
    public static final float H = 720f;

    /** Botones sidebar izquierdo */
    public static final Rectangle BTN_START = new Rectangle(72f, 398f, 268f, 48f);
    public static final Rectangle BTN_CONTINUE = new Rectangle(72f, 452f, 268f, 44f);
    public static final Rectangle BTN_MAP = new Rectangle(72f, 502f, 268f, 44f);
    public static final Rectangle BTN_ACHIEVEMENTS = new Rectangle(72f, 552f, 268f, 44f);
    public static final Rectangle BTN_ROBOTS = new Rectangle(72f, 602f, 268f, 44f);
    public static final Rectangle BTN_SETTINGS = new Rectangle(72f, 652f, 268f, 44f);

    /** Mundos orbitando el portal */
    public static final Rectangle WORLD_MOON = new Rectangle(318f, 318f, 100f, 100f);
    public static final Rectangle WORLD_ACADEMY = new Rectangle(578f, 188f, 100f, 100f);
    public static final Rectangle WORLD_SATURN = new Rectangle(838f, 268f, 100f, 100f);
    public static final Rectangle WORLD_NEBULA = new Rectangle(348f, 468f, 100f, 100f);
    public static final Rectangle WORLD_STATION = new Rectangle(798f, 448f, 100f, 100f);

    /** HUD dinamico (solo numeros encima del arte) */
    public static final float HUD_STARS_X = 798f;
    public static final float HUD_STARS_Y = 694f;
    public static final float HUD_GEMS_X = 918f;
    public static final float HUD_GEMS_Y = 694f;
    public static final float HUD_COINS_X = 1048f;
    public static final float HUD_COINS_Y = 694f;
    public static final float HUD_LEVEL_X = 168f;
    public static final float HUD_LEVEL_Y = 694f;
    public static final float HUD_XP_X = 168f;
    public static final float HUD_XP_Y = 672f;
    public static final float HUD_XP_W = 148f;

    public static final Rectangle[] BUTTONS = {
            BTN_START, BTN_CONTINUE, BTN_MAP, BTN_ACHIEVEMENTS, BTN_ROBOTS, BTN_SETTINGS
    };

    public static final Rectangle[] WORLDS = {
            WORLD_MOON, WORLD_ACADEMY, WORLD_SATURN, WORLD_NEBULA, WORLD_STATION
    };

    public static final boolean[] WORLD_UNLOCKED = {true, false, false, false, false};

    private ReferenceMenuLayout() {}

    public static int buttonAt(float x, float y) {
        for (int i = 0; i < BUTTONS.length; i++) {
            if (BUTTONS[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public static int worldAt(float x, float y) {
        for (int i = 0; i < WORLDS.length; i++) {
            if (WORLDS[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }
}
