package com.nuevooooooo.puzzle.ui.theme;

import com.badlogic.gdx.graphics.Color;

/**
 * Constantes de layout y paleta para la UI premium del juego RoboCode.
 */
public final class SpaceTheme {
    public static final float W = 1280f;
    public static final float H = 720f;

    public static final float HUD_Y = 656f;
    public static final float HUD_H = 64f;

    public static final float LOGO_X = 12f;
    public static final float LOGO_Y = 662f;
    public static final float LOGO_SIZE = 52f;

    public static final float MISSION_X = 248f;
    public static final float MISSION_Y = 666f;
    public static final float MISSION_W = 468f;
    public static final float MISSION_H = 44f;

    public static final float PILL_STAR_X = 738f;
    public static final float PILL_COIN_X = 862f;
    public static final float PILL_LIFE_X = 986f;
    public static final float PILL_W = 112f;
    public static final float PILL_H = 44f;
    public static final float PILL_Y = 666f;

    public static final float MENU_BTN_X = 1118f;
    public static final float MENU_BTN_Y = 664f;
    public static final float MENU_BTN_W = 148f;
    public static final float MENU_BTN_H = 48f;

    public static final float FOOTER_H = 88f;
    public static final float FOOTER_Y = 0f;
    public static final float MASCOT_X = 4f;
    public static final float MASCOT_Y = 2f;
    public static final float MASCOT_SIZE = 92f;
    public static final float OBJECTIVE_X = 420f;
    public static final float OBJECTIVE_Y = 18f;
    public static final float OBJECTIVE_W = 488f;
    public static final float OBJECTIVE_H = 52f;
    public static final float BUBBLE_X = 96f;
    public static final float BUBBLE_Y = 44f;
    public static final float BUBBLE_W = 318f;
    public static final float BUBBLE_H = 68f;

    public static final float BADGE_CHEST_X = 918f;
    public static final float BADGE_TROPHY_X = 1048f;
    public static final float BADGE_ROBOT_X = 1178f;
    public static final float BADGE_Y = 14f;
    public static final float BADGE_W = 112f;
    public static final float BADGE_H = 60f;

    public static final float LEFT_X = 14f;
    public static final float LEFT_W = 270f;
    public static final float SIDE_TOP_Y = 96f;
    public static final float SIDE_H = 554f;

    public static final float BOARD_FRAME_X = 296f;
    public static final float BOARD_FRAME_Y = 88f;
    public static final float BOARD_FRAME_W = 528f;
    public static final float BOARD_FRAME_H = 556f;

    /** Columna derecha ampliada: programa + controles. */
    public static final float RIGHT_X = 836f;
    public static final float RIGHT_W = 428f;

    public static final float PROGRAM_X = RIGHT_X;
    public static final float PROGRAM_Y = 362f;
    public static final float PROGRAM_W = RIGHT_W;
    public static final float PROGRAM_H = 280f;
    public static final float PROGRAM_HEADER_H = 46f;
    public static final float PROGRAM_PAD_X = 16f;

    /** Código solo en modal; botón en cabecera del panel. */
    public static final float CODE_BTN_W = 148f;
    public static final float CODE_BTN_H = 30f;
    public static final float CODE_BTN_X = PROGRAM_X + PROGRAM_W - CODE_BTN_W - 14f;
    public static final float CODE_BTN_Y = PROGRAM_Y + PROGRAM_H - PROGRAM_HEADER_H
            + (PROGRAM_HEADER_H - CODE_BTN_H) / 2f;

    public static final float ACTION_X = RIGHT_X;
    public static final float ACTION_W = RIGHT_W;
    public static final float ACTION_H = 48f;
    public static final float ACTION_GAP = 8f;
    public static final float ACTION_CLEAR_Y = 96f;
    public static final float ACTION_UNDO_Y = 152f;
    public static final float ACTION_STEP_Y = 208f;
    public static final float ACTION_RUN_Y = 264f;

    /** Fila auxiliar entre programa y botones principales. */
    public static final float AUX_ROW_Y = 320f;
    public static final float AUX_BTN_H = 34f;
    public static final float AUX_GAP = 10f;
    public static final float AUX_RESET_X = RIGHT_X;
    public static final float AUX_RESET_W = (RIGHT_W - AUX_GAP) / 2f;
    public static final float AUX_PAUSE_X = AUX_RESET_X + AUX_RESET_W + AUX_GAP;
    public static final float AUX_PAUSE_W = AUX_RESET_W;

    public static float programContentBottom() {
        return PROGRAM_Y + 18f;
    }

    public static float programContentTop() {
        return PROGRAM_Y + PROGRAM_H - PROGRAM_HEADER_H - 6f;
    }

    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    public static final Color CYAN = new Color(0.27f, 0.88f, 1f, 1f);
    public static final Color BLUE = new Color(0.02f, 0.56f, 0.95f, 1f);
    public static final Color PURPLE = new Color(0.49f, 0.24f, 0.90f, 1f);
    public static final Color GREEN = new Color(0.35f, 0.78f, 0.24f, 1f);
    public static final Color ORANGE = new Color(0.98f, 0.58f, 0.06f, 1f);
    public static final Color YELLOW = new Color(1f, 0.82f, 0.08f, 1f);
    public static final Color HUD_BAR = new Color(0.07f, 0.04f, 0.22f, 0.88f);
    public static final Color HUD_PILL = new Color(0.04f, 0.12f, 0.38f, 0.92f);
    public static final Color HUD_PILL_LIGHT = new Color(0.12f, 0.28f, 0.62f, 0.94f);
    public static final Color MISSION_PILL = new Color(0.08f, 0.32f, 0.78f, 0.94f);
    public static final Color FOOTER_BAR = new Color(0.10f, 0.05f, 0.28f, 0.90f);
    public static final Color OBJECTIVE_BAR = new Color(0.38f, 0.16f, 0.82f, 0.88f);
    public static final Color BUBBLE_FILL = new Color(1f, 1f, 1f, 0.96f);
    public static final Color BUBBLE_TEXT = new Color(0.12f, 0.18f, 0.42f, 1f);
    public static final Color STAR_GOLD = new Color(1f, 0.84f, 0.12f, 1f);
    public static final Color COIN_GOLD = new Color(1f, 0.78f, 0.18f, 1f);
    public static final Color HEART_RED = new Color(0.95f, 0.22f, 0.32f, 1f);
    public static final Color GRASS = new Color(0.34f, 0.74f, 0.18f, 1f);
    public static final Color GRASS_DARK = new Color(0.17f, 0.48f, 0.12f, 1f);
    public static final Color SAND = new Color(0.96f, 0.84f, 0.58f, 1f);
    public static final Color SAND_DARK = new Color(0.82f, 0.68f, 0.42f, 1f);
    public static final Color BOARD_GLOW = new Color(0.12f, 0.55f, 1f, 0.55f);
    public static final Color BTN_RUN = new Color(0.22f, 0.78f, 0.28f, 1f);
    public static final Color BTN_STEP = new Color(0.08f, 0.48f, 0.95f, 1f);
    public static final Color BTN_UNDO = new Color(0.52f, 0.28f, 0.92f, 1f);
    public static final Color BTN_CLEAR = new Color(0.92f, 0.28f, 0.22f, 1f);

    private SpaceTheme() {
    }
}
