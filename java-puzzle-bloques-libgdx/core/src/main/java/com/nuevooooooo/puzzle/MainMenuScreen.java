package com.nuevooooooo.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen extends ScreenAdapter {
    private static final float W = 1280f;
    private static final float H = 720f;

    private static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    private static final Color CYAN = new Color(0.32f, 0.92f, 1f, 1f);
    private static final Color LIME = new Color(0.65f, 1f, 0.18f, 1f);
    private static final Color PANEL_EDGE = new Color(0.22f, 0.86f, 1f, 0.78f);

    private static final int TOTAL_MISSIONS = 3;
    private static final float LEVEL_CARD_SIZE = 184f;
    private static final float[][] LEVEL_CARD_XY = {
            {438f, 296f},
            {640f, 312f},
            {842f, 296f}
    };
    private static final Color[] LEVEL_COLORS = {
            LIME,
            new Color(0.30f, 0.74f, 1f, 1f),
            new Color(0.80f, 0.48f, 1f, 1f)
    };
    private static final String[] LEVEL_TITLES = {
            "NIVEL 1",
            "NIVEL 2",
            "NIVEL 3"
    };
    private static final String[] LEVEL_SUBTITLES = {
            "Primeros pasos",
            "Ruta con nave",
            "Laberinto estelar"
    };
    private static final String[] LEVEL_COVERS = {
            "images/covers/level_01_primeros_pasos.png",
            "images/covers/level_02_ruta_nave.png",
            "images/covers/level_03_laberinto_estelar.png"
    };

    private final PuzzleBloquesLibGDXGame game;
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer sr = new ShapeRenderer();
    private final Viewport viewport = new FitViewport(W, H);
    private final Vector3 touch = new Vector3();
    private final GlyphLayout layout = new GlyphLayout();

    private final BitmapFont titleFont = UiFonts.createUiFont(1.36f);
    private final BitmapFont font = UiFonts.createUiFont(1.08f);
    private final BitmapFont smallFont = UiFonts.createUiFont(0.86f);

    private final List<MenuButton> buttons = new ArrayList<>();
    private final List<MenuStar> stars = new ArrayList<>();
    private final Rectangle[] missionNodes = new Rectangle[TOTAL_MISSIONS];
    private final Rectangle roboCard = new Rectangle(168, 238, 280, 246);
    private final Rectangle monoCard = new Rectangle(468, 238, 280, 246);
    private final Rectangle pirataCard = new Rectangle(768, 238, 280, 246);

    private Texture background;
    private Texture logo;
    private Texture robo;
    private Texture monoEspacial;
    private Texture pirataEspacial;
    private Texture menuPanel;
    private Texture menuButtonBlue;
    private Texture menuButtonPurple;
    private Texture menuButtonGreen;
    private Texture hudProfileCard;
    private Texture hudResourcePill;
    private Texture characterPlatform;
    private final Texture[] levelCovers = new Texture[TOTAL_MISSIONS];
    private final Texture[] levelFrames = new Texture[TOTAL_MISSIONS];
    private Texture labelStart;
    private Texture labelMap;
    private Texture labelHow;
    private Texture labelCharacters;
    private Texture labelBack;
    private Texture labelPractice;
    private Texture iconStar;
    private Texture iconCoin;
    private Texture iconHeart;

    private static final float HUD_H = 54f;
    private static final float HUD_PILL_W = 150f;
    private static final float HUD_PILL_H = 54f;
    /** Centro del circulo izquierdo en hud_resource_pill.png (escala 150x54). */
    private static final float HUD_PILL_ICON_CX = 27f;
    private static final float HUD_PILL_ICON_CY = 27f;
    private static final float HUD_PILL_TEXT_LEFT = 56f;
    private static final float HUD_PILL_TEXT_RIGHT = 34f;

    private static final float LEFT_PANEL_X = 18f;
    private static final float LEFT_PANEL_Y = 72f;
    private static final float LEFT_PANEL_W = 330f;
    private static final float LEFT_PANEL_H = 520f;

    private static final float MENU_BTN_X = LEFT_PANEL_X + 44f;
    private static final float MENU_BTN_W = 270f;
    private static final float MENU_BTN_H = 58f;
    /** Centro del circulo en btn_menu_*.png y slots del panel (escala 270x58). */
    private static final float MENU_BTN_ICON_CX = 31f;
    /** Filas alineadas con panel_academia_hologram.png (coordenada Y inferior). */
    private static final float[] MENU_BTN_Y = {366f, 288f, 210f, 134f};
    /** Ajuste vertical del icono por fila (filas inferiores del panel). */
    private static final float[] MENU_BTN_ICON_CY_OFFSET = {0f, 0f, 5f, 6f};
    /** Ajuste vertical de la etiqueta por fila. */
    private static final float[] MENU_BTN_LABEL_Y_OFFSET = {2f, 2f, 5f, 7f};

    private MenuMode mode = MenuMode.HOME;
    private float time;
    private String helperText = "Selecciona una mision o una opcion del menu.";

    public MainMenuScreen(PuzzleBloquesLibGDXGame game) {
        this.game = game;
        for (int i = 0; i < TOTAL_MISSIONS; i++) {
            float cx = LEVEL_CARD_XY[i][0];
            float cy = LEVEL_CARD_XY[i][1];
            missionNodes[i] = new Rectangle(cx - LEVEL_CARD_SIZE * 0.52f, cy - LEVEL_CARD_SIZE * 0.42f,
                    LEVEL_CARD_SIZE * 1.04f, LEVEL_CARD_SIZE * 1.35f);
        }
        loadAssets();
        buildStars();
        setMode(MenuMode.HOME);
        setupInput();
    }

    private void loadAssets() {
        background = texturePrefer("images/menu_pro/background_menu_pro.png", "images/menu_background.png");
        logo = texture("images/logo_codequest.png");
        robo = texturePrefer("images/chars/robo_menu_pose.png", "images/robo_menu_pose.png");
        monoEspacial = texture("images/mono_espacial_wave.png");
        pirataEspacial = texture("images/pirata_espacial_menu_pose.png");
        menuPanel = texture("images/menu_pro/panel_academia_hologram.png");
        menuButtonBlue = texture("images/menu_pro/btn_menu_blue.png");
        menuButtonPurple = texture("images/menu_pro/btn_menu_purple.png");
        menuButtonGreen = texture("images/menu_pro/btn_menu_green.png");
        hudProfileCard = texture("images/menu_pro/hud_profile_card.png");
        hudResourcePill = texture("images/menu_pro/hud_resource_pill.png");
        characterPlatform = texture("images/menu_pro/character_platform.png");
        for (int i = 0; i < TOTAL_MISSIONS; i++) {
            levelCovers[i] = texture(LEVEL_COVERS[i]);
        }
        levelFrames[0] = texture("images/menu_pro/level_frame_green.png");
        levelFrames[1] = texture("images/menu_pro/level_frame_blue.png");
        levelFrames[2] = texture("images/menu_pro/level_frame_purple.png");
        labelStart = texture("images/label_start.png");
        labelMap = texture("images/label_map.png");
        labelHow = texture("images/label_how.png");
        labelCharacters = texture("images/label_characters.png");
        labelBack = texture("images/label_back.png");
        labelPractice = texture("images/label_practice.png");
        iconStar = texturePrefer("images/ui/icon_star_hud.png", "images/collectibles/star_gold_small.png");
        iconCoin = texturePrefer("images/ui/icon_coin_hud.png", "images/collectibles/coin_gold.png");
        iconHeart = texturePrefer("images/ui/icon_heart_hud.png", "images/ui/icon_heart_hud.png");
    }

    private Texture texturePrefer(String... paths) {
        for (String path : paths) {
            if (Gdx.files.internal(path).exists()) {
                return texture(path);
            }
        }
        return texture(paths[paths.length - 1]);
    }

    private Texture texture(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }

    private void buildStars() {
        stars.clear();
        for (int i = 0; i < 64; i++) {
            float x = 30f + ((i * 149f) % 1220f);
            float y = 36f + ((i * 83f) % 650f);
            float speed = 4f + (i % 9) * 1.35f;
            float radius = 1.1f + (i % 5) * 0.42f;
            stars.add(new MenuStar(x, y, speed, radius, i * 0.49f));
        }
    }

    private void setMode(MenuMode next) {
        mode = next;
        buttons.clear();
        if (mode == MenuMode.HOME) {
            buttons.add(new MenuButton(new Rectangle(MENU_BTN_X, MENU_BTN_Y[0], MENU_BTN_W, MENU_BTN_H),
                    MenuAction.START, labelStart, "INICIAR", new Color(1f, 0.82f, 0.45f, 1f)));
            buttons.add(new MenuButton(new Rectangle(MENU_BTN_X, MENU_BTN_Y[1], MENU_BTN_W, MENU_BTN_H),
                    MenuAction.MAP, labelMap, "MAPA DE MISIONES", new Color(0.78f, 0.62f, 1f, 1f)));
            buttons.add(new MenuButton(new Rectangle(MENU_BTN_X, MENU_BTN_Y[2], MENU_BTN_W, MENU_BTN_H),
                    MenuAction.HOW, labelHow, "COMO JUGAR", new Color(0.55f, 0.82f, 1f, 1f)));
            buttons.add(new MenuButton(new Rectangle(MENU_BTN_X, MENU_BTN_Y[3], MENU_BTN_W, MENU_BTN_H),
                    MenuAction.CHARACTERS, labelCharacters, "PERSONAJES", new Color(0.45f, 0.92f, 0.78f, 1f)));
            helperText = "Toca una mision o elige una opcion del menu.";
            return;
        }
        buttons.add(new MenuButton(new Rectangle(72f, 42f, 210f, 56f), MenuAction.BACK, labelBack, "VOLVER"));
        buttons.add(new MenuButton(new Rectangle(500f, 36f, 300f, 66f), MenuAction.PRACTICE, labelPractice, "PRACTICAR"));
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                handleTap(touch.x, touch.y);
                return true;
            }
        });
    }

    private void handleTap(float x, float y) {
        if (mode == MenuMode.HOME || mode == MenuMode.MAP) {
            for (int i = 0; i < TOTAL_MISSIONS; i++) {
                if (missionNodes[i].contains(x, y)) {
                    startMission(i);
                    return;
                }
            }
        }
        if (mode == MenuMode.CHARACTERS) {
            if (roboCard.contains(x, y)) {
                GameState.selectCharacter(PlayerCharacter.ROBO);
                helperText = "Robo seleccionado: personaje basico.";
                return;
            }
            if (monoCard.contains(x, y)) {
                trySelectMono();
                return;
            }
            if (pirataCard.contains(x, y)) {
                GameState.selectCharacter(PlayerCharacter.PIRATA_ESPACIAL);
                helperText = "Pirata espacial listo para la mision.";
                return;
            }
        }
        for (MenuButton menuButton : buttons) {
            if (!menuButton.rect.contains(x, y)) {
                continue;
            }
            switch (menuButton.action) {
                case START, PRACTICE -> startAdventure();
                case MAP -> setMode(MenuMode.MAP);
                case HOW -> setMode(MenuMode.HOW);
                case CHARACTERS -> setMode(MenuMode.CHARACTERS);
                case BACK -> setMode(MenuMode.HOME);
            }
            return;
        }
    }

    private void trySelectMono() {
        if (GameState.isMonoUnlocked()) {
            GameState.selectCharacter(PlayerCharacter.MONO_ESPACIAL);
            helperText = "Mono espacial listo para la mision.";
            return;
        }
        if (GameState.tryUnlockMono()) {
            GameState.selectCharacter(PlayerCharacter.MONO_ESPACIAL);
            helperText = "Desbloqueaste al mono por " + GameState.MONO_COST + " creditos!";
            return;
        }
        helperText = "El mono cuesta " + GameState.MONO_COST + " creditos. Tienes "
                + GameState.sessionCoins() + ". Recoge estrellas en las misiones.";
    }

    private void startAdventure() {
        startMission(GameState.currentLevelIndex());
    }

    private void startMission(int levelIndex) {
        if (GameState.selectedCharacter() == PlayerCharacter.MONO_ESPACIAL && !GameState.isMonoUnlocked()) {
            GameState.selectCharacter(PlayerCharacter.ROBO);
        }
        GameState.setCurrentLevelIndex(Math.min(levelIndex, TOTAL_MISSIONS - 1));
        Screen old = game.getScreen();
        game.setScreen(new RoboCodeGameScreen(game));
        if (old != null) {
            old.dispose();
        }
    }

    @Override
    public void render(float delta) {
        time += delta;
        ScreenUtils.clear(0.01f, 0.02f, 0.08f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        sr.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        drawMovingSpace();
        drawStars();
        if (mode == MenuMode.HOME) {
            drawHome();
        } else {
            drawSubScreen();
        }
        drawTopHudBar();
    }

    private void drawMovingSpace() {
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(background, 0f, 0f, W, H);
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawStars() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (MenuStar star : stars) {
            float x = star.x + MathUtils.sin(time * 0.48f + star.phase) * 7f;
            float y = star.y - ((time * star.speed) % 760f);
            if (y < -20f) {
                y += 760f;
            }
            float pulse = 0.55f + 0.45f * MathUtils.sin(time * 2.1f + star.phase);
            sr.setColor(0.68f, 0.92f, 1f, 0.34f + pulse * 0.30f);
            sr.circle(x, y, star.radius + pulse * 0.48f);
        }
        sr.end();
    }

    private void drawTopHudBar() {
        float profileX = 8f;
        float profileY = H - 82f;
        float pillY = H - 66f;
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(hudProfileCard, profileX, profileY, 330f, 72f);
        batch.draw(hudResourcePill, 806f, pillY, HUD_PILL_W, HUD_PILL_H);
        batch.draw(hudResourcePill, 966f, pillY, HUD_PILL_W, HUD_PILL_H);
        batch.draw(hudResourcePill, 1126f, pillY, HUD_PILL_W, HUD_PILL_H);

        Texture avatar = avatarForSelectedCharacter();
        float avatarSize = 46f;
        batch.draw(avatar, profileX + 13f, profileY + (72f - avatarSize) / 2f, avatarSize, avatarSize);
        font.setColor(Color.WHITE);
        font.draw(batch, "Explorador Estelar", profileX + 76f, profileY + 48f);
        smallFont.setColor(CYAN);
        smallFont.draw(batch, "Nivel " + GameState.playerLevel(), profileX + 76f, profileY + 24f);
        drawHudPillText(806f, pillY, iconStar, GameState.starsEarned() + "/" + GameState.maxStars(), false);
        drawHudPillText(966f, pillY, iconCoin, String.valueOf(GameState.sessionCoins()), false);
        drawHudPillText(1126f, pillY, iconHeart, String.valueOf(GameState.lives()), true);
        batch.end();
    }

    private Texture avatarForSelectedCharacter() {
        return switch (GameState.selectedCharacter()) {
            case MONO_ESPACIAL -> monoEspacial;
            case PIRATA_ESPACIAL -> pirataEspacial;
            default -> robo;
        };
    }

    private void drawProfileCardBg(float x, float y, float w, float h) {
        sr.setColor(0f, 0.02f, 0.10f, 0.46f);
        drawRoundedRect(x + 4f, y - 4f, w, h, 18f);
        sr.setColor(0.01f, 0.09f, 0.22f, 0.82f);
        drawRoundedRect(x, y, w, h, 18f);
        sr.setColor(0.16f, 0.82f, 1f, 0.68f);
        drawRoundedFrame(x, y, w, h, 18f, 2f);
        sr.setColor(0.20f, 0.90f, 1f, 0.34f);
        drawRoundedRect(x + 126f, y + 11f, 94f, 8f, 4f);
        sr.setColor(0.30f, 0.95f, 1f, 0.88f);
        drawRoundedRect(x + 126f, y + 11f, 40f + GameState.xpProgress() * 52f, 8f, 4f);
        sr.setColor(1f, 0.82f, 0.10f, 0.88f);
        drawStar(x + w - 36f, y + 28f, 19f);
    }

    private void drawHudPillBg(float x, float y, float w, Color color) {
        sr.setColor(0f, 0.02f, 0.10f, 0.42f);
        drawRoundedRect(x + 4f, y - 4f, w, 40f, 15f);
        sr.setColor(0.02f, 0.08f, 0.22f, 0.88f);
        drawRoundedRect(x, y, w, 40f, 15f);
        sr.setColor(color.r, color.g, color.b, 0.70f);
        drawRoundedFrame(x, y, w, 40f, 15f, 2f);
        sr.setColor(0.18f, 0.80f, 1f, 0.28f);
        drawRoundedRect(x + w - 32f, y + 7f, 24f, 26f, 9f);
        sr.setColor(CYAN);
        sr.rect(x + w - 21f, y + 12f, 3f, 16f);
        sr.rect(x + w - 27f, y + 18f, 15f, 3f);
    }

    private void drawHudPillText(float x, float y, Texture icon, String value, boolean compactIcon) {
        float iconSize = compactIcon ? 20f : 24f;
        float iconX = x + HUD_PILL_ICON_CX - iconSize / 2f;
        float iconY = y + HUD_PILL_ICON_CY - iconSize / 2f;
        batch.draw(icon, iconX, iconY, iconSize, iconSize);
        layout.setText(font, value);
        float textAreaW = HUD_PILL_W - HUD_PILL_TEXT_LEFT - HUD_PILL_TEXT_RIGHT;
        float textX = x + HUD_PILL_TEXT_LEFT + (textAreaW - layout.width) / 2f;
        float textY = y + (HUD_PILL_H + layout.height) / 2f - 2f;
        font.setColor(Color.WHITE);
        font.draw(batch, value, textX, textY);
    }

    private void drawHome() {
        float logoPulse = 1f + MathUtils.sin(time * 1.25f) * 0.01f;
        float logoW = 560f * logoPulse;
        float logoH = 187f * logoPulse;
        float logoX = (W - logoW) / 2f;
        float logoY = 462f + MathUtils.sin(time * 0.75f) * 4f;

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(menuPanel, LEFT_PANEL_X, LEFT_PANEL_Y, LEFT_PANEL_W, LEFT_PANEL_H);
        batch.draw(characterPlatform, 842f, 46f, 438f, 136f);
        batch.end();

        drawLevelCards(true);
        drawHomeMenuButtons();
        drawSubtitleLines(logoX + logoW * 0.5f, logoY - 18f);
        drawHeroSpotlights();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(logo, logoX, logoY, logoW, logoH);
        drawSubtitleText(logoX + logoW * 0.5f, logoY - 18f);
        drawCharacterShowcase();
        drawTextShadow(font, "ACADEMIA ESTELAR", LEFT_PANEL_X + 80f, LEFT_PANEL_Y + LEFT_PANEL_H - 76f, CYAN);
        drawTextShadow(smallFont, "Programa misiones y mejora habilidades", LEFT_PANEL_X + 56f,
                LEFT_PANEL_Y + LEFT_PANEL_H - 104f, new Color(0.82f, 0.96f, 1f, 1f));
        drawTextShadow(smallFont, "Explora el universo del codigo.", LEFT_PANEL_X + 56f,
                LEFT_PANEL_Y + LEFT_PANEL_H - 126f, new Color(0.82f, 0.96f, 1f, 1f));
        drawTextShadow(smallFont, "Tu equipo de exploradores", 930f, 52f, new Color(0.74f, 0.94f, 1f, 1f));
        batch.end();
    }

    /** Botones del panel izquierdo: iconos centrados en el circulo y etiquetas dentro de la barra. */
    private void drawHomeMenuButtons() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton menuButton = buttons.get(i);
            Rectangle r = menuButton.rect;
            if (isHovering(r)) {
                sr.setColor(menuButton.tint.r, menuButton.tint.g, menuButton.tint.b, 0.16f);
                drawRoundedRect(r.x + 56f, r.y + 7f, r.width - 76f, r.height - 14f, 16f);
            }
            float iconCx = r.x + MENU_BTN_ICON_CX;
            float iconCy = r.y + r.height / 2f + menuBtnIconCyOffset(i);
            drawMenuIcon(menuButton.action, iconCx, iconCy);
        }
        sr.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        drawHomeButtonLabels();
        batch.end();
    }

    private float menuBtnIconCyOffset(int index) {
        return index >= 0 && index < MENU_BTN_ICON_CY_OFFSET.length ? MENU_BTN_ICON_CY_OFFSET[index] : 0f;
    }

    private float menuBtnLabelYOffset(int index) {
        return index >= 0 && index < MENU_BTN_LABEL_Y_OFFSET.length ? MENU_BTN_LABEL_Y_OFFSET[index] : 2f;
    }

    private void drawHomeButtonLabels() {
        for (int i = 0; i < buttons.size(); i++) {
            MenuButton menuButton = buttons.get(i);
            float labelW = menuButton.rect.width * 0.58f;
            float labelH = menuButton.rect.height * 0.50f;
            float labelX = menuButton.rect.x + 74f;
            float labelY = menuButton.rect.y + (menuButton.rect.height - labelH) / 2f + menuBtnLabelYOffset(i);
            batch.draw(menuButton.labelTexture, labelX, labelY, labelW, labelH);
        }
    }

    private void drawHeroSpotlights() {
        float pulse = 0.55f + MathUtils.sin(time * 1.7f) * 0.10f;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.04f, 0.12f, 0.42f);
        sr.ellipse(898f, 92f, 356f, 56f);
        sr.setColor(0.24f, 0.88f, 1f, 0.14f + pulse * 0.05f);
        sr.ellipse(920f, 100f, 308f, 40f);
        sr.setColor(0.65f, 1f, 1f, 0.24f);
        sr.ellipse(956f, 118f, 74f, 15f);
        sr.ellipse(1054f, 118f, 86f, 16f);
        sr.ellipse(1142f, 116f, 80f, 15f);
        sr.end();
    }

    private void drawHomeStageGlow() {
        float pulse = 0.55f + MathUtils.sin(time * 1.25f) * 0.12f;
        sr.setColor(0.02f, 0.10f, 0.28f, 0.24f);
        sr.ellipse(340f, 38f, 610f, 92f);
        sr.setColor(0.08f, 0.52f, 1f, 0.16f + pulse * 0.05f);
        sr.ellipse(422f, 54f, 454f, 58f);
        sr.setColor(0.30f, 0.95f, 1f, 0.18f + pulse * 0.07f);
        drawRoundedFrame(480f, 62f, 322f, 38f, 18f, 3f);
    }

    private void drawSubtitleLines(float centerX, float y) {
        String subtitle = "LABERINTO ESTELAR";
        layout.setText(smallFont, subtitle);
        float textX = centerX - layout.width / 2f;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.22f, 0.86f, 1f, 0.55f);
        sr.rect(textX - 72f, y - 8f, 56f, 2f);
        sr.rect(textX + layout.width + 16f, y - 8f, 56f, 2f);
        sr.end();
    }

    private void drawSubtitleText(float centerX, float y) {
        String subtitle = "LABERINTO ESTELAR";
        layout.setText(smallFont, subtitle);
        float textX = centerX - layout.width / 2f;
        drawTextShadow(smallFont, subtitle, textX, y, new Color(0.88f, 0.96f, 1f, 1f));
    }

    private void drawHolographicPanel(float x, float y, float w, float h) {
        sr.setColor(0f, 0.02f, 0.10f, 0.32f);
        drawRoundedRect(x + 8f, y - 8f, w, h, 24f);
        sr.setColor(0.01f, 0.09f, 0.25f, 0.54f);
        drawRoundedRect(x, y, w, h, 24f);
        sr.setColor(0.08f, 0.38f, 0.78f, 0.14f);
        drawRoundedRect(x + 6f, y + 6f, w - 12f, h - 12f, 19f);
        sr.setColor(PANEL_EDGE);
        drawRoundedFrame(x, y, w, h, 24f, 3f);
        for (int i = 0; i < 6; i++) {
            float scanY = y + 18f + i * ((h - 36f) / 5f);
            float alpha = 0.04f + MathUtils.sin(time * 2.4f + i * 0.7f) * 0.02f;
            sr.setColor(0.35f, 0.88f, 1f, alpha);
            sr.rect(x + 10f, scanY, w - 20f, 1f);
        }
        float glow = 0.55f + MathUtils.sin(time * 1.8f) * 0.15f;
        sr.setColor(0.18f, 0.72f, 1f, 0.12f * glow);
        drawRoundedRect(x + 12f, y + 12f, w - 24f, 24f, 12f);
    }

    private void drawCharacterPlatform(float x, float y, float w) {
        float glow = 0.6f + MathUtils.sin(time * 1.4f) * 0.12f;
        sr.setColor(0.02f, 0.08f, 0.24f, 0.36f);
        sr.ellipse(x + 4f, y - 8f, w, 64f);
        sr.setColor(0.10f, 0.42f, 0.95f, 0.22f * glow);
        sr.ellipse(x, y, w, 62f);
        sr.setColor(0.24f, 0.78f, 1f, 0.36f * glow);
        sr.ellipse(x + 25f, y + 7f, w - 50f, 42f);
        sr.setColor(0.62f, 1f, 1f, 0.38f * glow);
        sr.rect(x + 80f, y + 30f, w - 160f, 3f);
    }

    private void drawCharacterShowcase() {
        float baseY = 112f + MathUtils.sin(time * 1.1f) * 4f;
        float roboFloat = MathUtils.sin(time * 1.18f) * 6f;
        float monoFloat = MathUtils.sin(time * 1.05f + 1.2f) * 6f;
        float pirataFloat = MathUtils.sin(time * 0.95f + 2.1f) * 6f;
        batch.draw(robo, 918f, baseY + roboFloat, 178f, 178f);
        batch.draw(monoEspacial, 1002f, baseY - 8f + monoFloat, 214f, 214f);
        batch.draw(pirataEspacial, 1098f, baseY - 2f + pirataFloat, 184f, 184f);
    }

    private void drawLevelCards(boolean homeLayout) {
        float[][] cardLayout = new float[TOTAL_MISSIONS][3];
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < TOTAL_MISSIONS; i++) {
            float cx = LEVEL_CARD_XY[i][0];
            float cy = LEVEL_CARD_XY[i][1];
            float bob = homeLayout ? MathUtils.sin(time * 1.2f + i * 0.8f) * 5f : 0f;
            cy += bob;
            float hover = isHovering(missionNodes[i]) ? 1.06f : 1f;
            float size = LEVEL_CARD_SIZE * hover;
            float pulse = 1f + MathUtils.sin(time * 1.5f + i * 0.9f) * 0.04f;
            cardLayout[i][0] = cx;
            cardLayout[i][1] = cy;
            cardLayout[i][2] = size;

            sr.setColor(0f, 0.02f, 0.10f, 0.42f);
            sr.ellipse(cx - size * 0.66f + 8f, cy - size * 0.78f - 8f, size * 1.32f, size * 1.56f);
            sr.setColor(LEVEL_COLORS[i].r, LEVEL_COLORS[i].g, LEVEL_COLORS[i].b, 0.16f);
            sr.circle(cx, cy, size * 0.54f * pulse);
            sr.setColor(LEVEL_COLORS[i].r, LEVEL_COLORS[i].g, LEVEL_COLORS[i].b, 0.26f);
            sr.circle(cx, cy, size * 0.44f * pulse);
        }
        sr.end();

        batch.begin();
        for (int i = 0; i < TOTAL_MISSIONS; i++) {
            float cx = cardLayout[i][0];
            float cy = cardLayout[i][1];
            float size = cardLayout[i][2];
            float imgSize = size * 0.90f;
            float frameW = size * 1.28f;
            float frameH = frameW * 320f / 260f;
            batch.setColor(Color.WHITE);
            batch.draw(levelCovers[i], cx - imgSize / 2f, cy - imgSize / 2f, imgSize, imgSize);
            batch.draw(levelFrames[i], cx - frameW / 2f, cy - frameH / 2f, frameW, frameH);
            drawLevelPillText(cx, cy + size * 0.54f, LEVEL_TITLES[i]);
            drawCenteredText(font, LEVEL_SUBTITLES[i].toUpperCase(), cx, cy - size * 0.58f + 20f, WHITE);
            drawLevelStars(cx, cy - size * 0.72f, i);
            if (GameState.currentLevelIndex() == i) {
                drawTextShadow(smallFont, "En progreso", cx - 42f, cy - size * 0.88f, CYAN);
            } else if (GameState.currentLevelIndex() > i) {
                drawTextShadow(smallFont, "Completada", cx - 38f, cy - size * 0.88f, LIME);
            }
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawLevelPillBg(float cx, float y, String text, Color color) {
        layout.setText(smallFont, text);
        float pillW = layout.width + 22f;
        float pillH = 22f;
        float pillX = cx - pillW / 2f;
        sr.setColor(0f, 0.02f, 0.10f, 0.46f);
        drawRoundedRect(pillX + 3f, y - 7f, pillW, pillH + 4f, 8f);
        sr.setColor(color.r * 0.18f, color.g * 0.24f, color.b * 0.42f, 0.92f);
        drawRoundedRect(pillX, y - 4f, pillW, pillH, 8f);
        sr.setColor(color.r, color.g, color.b, 0.92f);
        drawRoundedFrame(pillX, y - 4f, pillW, pillH, 8f, 2f);
    }

    private void drawLevelNamePlateBg(float cx, float cy, float w, Color color) {
        float h = 30f;
        float x = cx - w / 2f;
        float y = cy - h / 2f;
        sr.setColor(0f, 0.02f, 0.10f, 0.52f);
        drawRoundedRect(x + 3f, y - 4f, w, h, 10f);
        sr.setColor(0.02f, 0.08f, 0.22f, 0.94f);
        drawRoundedRect(x, y, w, h, 10f);
        sr.setColor(color.r, color.g, color.b, 0.82f);
        drawRoundedFrame(x, y, w, h, 10f, 2f);
    }

    private void drawLevelPillText(float cx, float y, String text) {
        layout.setText(smallFont, text);
        drawTextShadow(smallFont, text, cx - layout.width / 2f, y + 12f, WHITE);
    }

    private void drawLevelStars(float cx, float y, int levelIndex) {
        int earned = starsForLevel(levelIndex);
        for (int s = 0; s < 3; s++) {
            float sx = cx - 28f + s * 28f;
            if (s < earned) {
                batch.setColor(1f, 0.92f, 0.18f, 1f);
            } else {
                batch.setColor(0.35f, 0.45f, 0.62f, 0.7f);
            }
            batch.draw(iconStar, sx, y, 20f, 20f);
        }
        batch.setColor(Color.WHITE);
    }

    private int starsForLevel(int levelIndex) {
        if (GameState.currentLevelIndex() > levelIndex) {
            return 3;
        }
        if (GameState.currentLevelIndex() == levelIndex) {
            return 0;
        }
        return 0;
    }

    private Texture buttonTextureFor(MenuAction action) {
        return switch (action) {
            case MAP -> menuButtonPurple;
            case CHARACTERS -> menuButtonGreen;
            default -> menuButtonBlue;
        };
    }

    private void drawMenuIcon(MenuAction action, float cx, float cy) {
        sr.setColor(WHITE);
        switch (action) {
            case START, PRACTICE -> sr.triangle(cx - 4f, cy - 8f, cx - 4f, cy + 8f, cx + 9f, cy);
            case MAP -> {
                sr.circle(cx, cy + 2f, 5f);
                sr.triangle(cx - 6f, cy, cx + 6f, cy, cx, cy - 10f);
            }
            case CHARACTERS -> {
                drawRoundedRect(cx - 8f, cy - 7f, 16f, 12f, 4f);
                sr.setColor(0.02f, 0.12f, 0.32f, 0.94f);
                sr.circle(cx - 4f, cy - 2f, 2f);
                sr.circle(cx + 4f, cy - 2f, 2f);
                sr.setColor(WHITE);
                sr.rect(cx - 1f, cy + 5f, 2f, 4f);
                sr.circle(cx, cy + 10f, 2f);
            }
            case HOW -> {
                sr.circle(cx, cy, 6f);
                sr.setColor(0.02f, 0.12f, 0.32f, 0.94f);
                sr.circle(cx, cy, 2.5f);
                sr.setColor(WHITE);
                sr.rect(cx - 1.5f, cy + 5f, 3f, 4f);
                sr.rect(cx - 1.5f, cy - 9f, 3f, 4f);
                sr.rect(cx + 5f, cy - 1.5f, 4f, 3f);
                sr.rect(cx - 9f, cy - 1.5f, 4f, 3f);
            }
            case BACK -> sr.triangle(cx - 9f, cy, cx + 5f, cy + 8f, cx + 5f, cy - 8f);
        }
    }

    private void drawRoundedRect(float x, float y, float w, float h, float radius) {
        float r = Math.min(radius, Math.min(w, h) * 0.5f);
        sr.rect(x + r, y, w - 2f * r, h);
        sr.rect(x, y + r, w, h - 2f * r);
        sr.circle(x + r, y + r, r);
        sr.circle(x + w - r, y + r, r);
        sr.circle(x + r, y + h - r, r);
        sr.circle(x + w - r, y + h - r, r);
    }

    private void drawRoundedFrame(float x, float y, float w, float h, float radius, float thickness) {
        float r = Math.min(radius, Math.min(w, h) * 0.5f);
        sr.rect(x + r, y, w - 2f * r, thickness);
        sr.rect(x + r, y + h - thickness, w - 2f * r, thickness);
        sr.rect(x, y + r, thickness, h - 2f * r);
        sr.rect(x + w - thickness, y + r, thickness, h - 2f * r);
        sr.circle(x + r, y + r, thickness);
        sr.circle(x + w - r, y + r, thickness);
        sr.circle(x + r, y + h - r, thickness);
        sr.circle(x + w - r, y + h - r, thickness);
    }

    private void drawSubScreen() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawHolographicPanel(108f, 96f, 1064f, 488f);
        sr.end();
        drawSubScreenNavButtons();

        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.95f);
        batch.draw(logo, 384f, 548f, 512f, 170f);
        batch.end();

        if (mode == MenuMode.MAP) {
            drawMapContent();
        } else if (mode == MenuMode.HOW) {
            drawHowContent();
        } else if (mode == MenuMode.CHARACTERS) {
            drawCharacterContent();
        }

        batch.begin();
        drawSubScreenButtonLabels();
        batch.end();
    }

    /** Volver y Practicar: botones premium sin fondo de cuadritos. */
    private void drawSubScreenNavButtons() {
        batch.begin();
        for (MenuButton menuButton : buttons) {
            Rectangle r = menuButton.rect;
            boolean hover = isHovering(r);
            float grow = hover ? 3f : 0f;
            if (menuButton.action == MenuAction.PRACTICE) {
                batch.setColor(1f, 0.94f, 0.62f, 1f);
            } else {
                batch.setColor(Color.WHITE);
            }
            batch.draw(menuButtonBlue, r.x - grow, r.y - grow, r.width + grow * 2f, r.height + grow * 2f);
        }
        batch.setColor(Color.WHITE);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (MenuButton menuButton : buttons) {
            Rectangle r = menuButton.rect;
            if (isHovering(r)) {
                sr.setColor(0.30f, 0.88f, 1f, 0.14f);
                drawRoundedRect(r.x + 52f, r.y + 6f, r.width - 64f, r.height - 12f, 14f);
            }
            drawMenuIcon(menuButton.action, r.x + MENU_BTN_ICON_CX, r.y + r.height / 2f);
        }
        sr.end();
    }

    private void drawSubScreenButtonLabels() {
        for (MenuButton menuButton : buttons) {
            layout.setText(font, menuButton.label);
            float labelX = menuButton.rect.x + 74f;
            float labelW = menuButton.rect.width - 88f;
            float x = labelX + (labelW - layout.width) / 2f;
            float y = menuButton.rect.y + (menuButton.rect.height + layout.height) / 2f - 2f;
            drawTextShadow(font, menuButton.label, x, y, WHITE);
        }
    }

    private void drawMapContent() {
        drawLevelCards(false);

        batch.begin();
        drawTextShadow(titleFont, "Mapa de misiones", 190f, 518f, WHITE);
        drawTextShadow(smallFont, "Toca una mision para comenzar o pulsa Practicar ahora.", 190f, 148f,
                new Color(0.82f, 0.95f, 1f, 1f));
        batch.end();
    }

    private void drawHowContent() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawInfoCard(188, 366, 215, 120, new Color(0.04f, 0.35f, 0.78f, 0.82f));
        drawInfoCard(425, 366, 215, 120, new Color(0.09f, 0.47f, 0.78f, 0.82f));
        drawInfoCard(662, 366, 215, 120, new Color(0.38f, 0.22f, 0.82f, 0.82f));
        drawInfoCard(899, 366, 215, 120, new Color(0.08f, 0.62f, 0.40f, 0.82f));
        sr.setColor(LIME);
        sr.triangle(282, 442, 258, 402, 306, 402);
        sr.setColor(CYAN);
        sr.rect(506, 396, 56, 48);
        sr.setColor(WHITE);
        sr.triangle(758, 438, 758, 400, 802, 419);
        sr.setColor(1f, 0.82f, 0.12f, 1f);
        drawStar(1006, 421, 34);
        sr.end();

        batch.begin();
        drawTextShadow(titleFont, "Como jugar", 190f, 518f, WHITE);
        drawTextShadow(font, "1. Elige bloques", 210f, 392f, WHITE);
        drawTextShadow(font, "2. Ordena", 466f, 392f, WHITE);
        drawTextShadow(font, "3. Ejecuta", 706f, 392f, WHITE);
        drawTextShadow(font, "4. Corrige", 946f, 392f, WHITE);
        drawTextShadow(smallFont, "Construye una lista de instrucciones para guiar a tu explorador hasta la meta.",
                190f, 274f, new Color(0.84f, 0.96f, 1f, 1f));
        drawTextShadow(smallFont, "Si choca, cambia el orden y vuelve a intentar. Ese es el poder de programar.",
                190f, 246f, new Color(0.84f, 0.96f, 1f, 1f));
        batch.end();
    }

    private void drawCharacterContent() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawCharacterCardFrame(roboCard, PlayerCharacter.ROBO, new Color(0.05f, 0.38f, 0.78f, 0.82f));
        drawCharacterCardFrame(monoCard, PlayerCharacter.MONO_ESPACIAL, new Color(0.44f, 0.22f, 0.82f, 0.86f));
        drawCharacterCardFrame(pirataCard, PlayerCharacter.PIRATA_ESPACIAL, new Color(0.08f, 0.42f, 0.72f, 0.86f));
        sr.end();

        batch.begin();
        drawTextShadow(titleFont, "Personajes", 190f, 518f, WHITE);
        batch.draw(robo, roboCard.x + 58f, roboCard.y + 74f, 150f, 150f);
        batch.draw(monoEspacial, monoCard.x + 58f, monoCard.y + 75f, 150f, 150f);
        batch.draw(pirataEspacial, pirataCard.x + 58f, pirataCard.y + 75f, 150f, 150f);
        drawCharacterText(roboCard, "Robo", "Basico", PlayerCharacter.ROBO);
        drawCharacterText(monoCard, "Mono espacial", "Raro", PlayerCharacter.MONO_ESPACIAL);
        drawCharacterText(pirataCard, "Pirata espacial", "Epico", PlayerCharacter.PIRATA_ESPACIAL);
        drawTextShadow(smallFont, "Robo es gratis. Mono: " + GameState.MONO_COST
                + " creditos (2 estrellas). Pirata: gratis por ahora.", 190f, 172f, new Color(0.84f, 0.96f, 1f, 1f));
        batch.end();
    }

    private void drawCharacterCardFrame(Rectangle card, PlayerCharacter character, Color color) {
        drawInfoCard(card.x, card.y, card.width, card.height, color);
        if (GameState.selectedCharacter() == character) {
            sr.setColor(1f, 0.92f, 0.18f, 0.85f);
            sr.rect(card.x + 14f, card.y + card.height - 9f, card.width - 28f, 5f);
            sr.rect(card.x + 14f, card.y + 8f, card.width - 28f, 5f);
            sr.rect(card.x + 8f, card.y + 18f, 5f, card.height - 36f);
            sr.rect(card.x + card.width - 13f, card.y + 18f, 5f, card.height - 36f);
        }
    }

    private void drawCharacterText(Rectangle card, String name, String rarity, PlayerCharacter character) {
        drawTextShadow(font, name, card.x + 30f, card.y + 58f, WHITE);
        Color rarityColor = switch (character) {
            case MONO_ESPACIAL -> new Color(0.92f, 0.72f, 1f, 1f);
            case PIRATA_ESPACIAL -> new Color(0.72f, 0.88f, 1f, 1f);
            default -> new Color(0.78f, 1f, 0.70f, 1f);
        };
        drawTextShadow(smallFont, rarity, card.x + 30f, card.y + 34f, rarityColor);
        String state;
        if (character == PlayerCharacter.MONO_ESPACIAL) {
            if (GameState.isMonoUnlocked()) {
                state = GameState.selectedCharacter() == character ? "Seleccionado" : "Tocar para elegir";
            } else {
                state = GameState.MONO_COST + " cred. para desbloquear";
            }
        } else {
            state = GameState.selectedCharacter() == character ? "Seleccionado" : "Tocar para elegir";
        }
        drawTextShadow(smallFont, state, card.x + 30f, card.y + 16f, new Color(0.82f, 0.95f, 1f, 1f));
    }

    private void drawInfoCard(float x, float y, float w, float h, Color color) {
        sr.setColor(0f, 0.02f, 0.08f, 0.34f);
        sr.rect(x + 6f, y - 7f, w, h);
        sr.setColor(color);
        sr.rect(x, y, w, h);
        sr.setColor(PANEL_EDGE);
        sr.rect(x, y + h - 4f, w, 4f);
        sr.rect(x, y, w, 3f);
    }

    private void drawStar(float cx, float cy, float radius) {
        for (int i = 0; i < 5; i++) {
            float a = (float) (Math.PI * 2 * i / 5f - Math.PI / 2f);
            float a2 = (float) (Math.PI * 2 * (i + 2) / 5f - Math.PI / 2f);
            sr.triangle(cx, cy, cx + MathUtils.cos(a) * radius, cy + MathUtils.sin(a) * radius,
                    cx + MathUtils.cos(a2) * radius, cy + MathUtils.sin(a2) * radius);
        }
    }

    private void drawTextShadow(BitmapFont f, String text, float x, float y, Color color) {
        f.setColor(0f, 0.03f, 0.12f, 0.70f);
        f.draw(batch, text, x + 2f, y - 2f);
        f.setColor(color);
        f.draw(batch, text, x, y);
    }

    private void drawCenteredText(BitmapFont f, String text, float centerX, float y, Color color) {
        layout.setText(f, text);
        drawTextShadow(f, text, centerX - layout.width / 2f, y, color);
    }

    private boolean isHovering(Rectangle rect) {
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touch);
        return rect.contains(touch.x, touch.y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    @Override
    public void dispose() {
        batch.dispose();
        sr.dispose();
        titleFont.dispose();
        font.dispose();
        smallFont.dispose();
        background.dispose();
        logo.dispose();
        robo.dispose();
        monoEspacial.dispose();
        pirataEspacial.dispose();
        menuPanel.dispose();
        menuButtonBlue.dispose();
        menuButtonPurple.dispose();
        menuButtonGreen.dispose();
        hudProfileCard.dispose();
        hudResourcePill.dispose();
        characterPlatform.dispose();
        for (Texture cover : levelCovers) {
            cover.dispose();
        }
        for (Texture frame : levelFrames) {
            frame.dispose();
        }
        labelStart.dispose();
        labelMap.dispose();
        labelHow.dispose();
        labelCharacters.dispose();
        labelBack.dispose();
        labelPractice.dispose();
        iconStar.dispose();
        iconCoin.dispose();
        iconHeart.dispose();
    }

    private enum MenuMode {
        HOME,
        MAP,
        HOW,
        CHARACTERS
    }

    private enum MenuAction {
        START,
        MAP,
        HOW,
        CHARACTERS,
        BACK,
        PRACTICE
    }

    private static class MenuButton {
        final Rectangle rect;
        final MenuAction action;
        final Texture labelTexture;
        final String label;
        final Color tint;

        MenuButton(Rectangle rect, MenuAction action, Texture labelTexture, String label, Color tint) {
            this.rect = rect;
            this.action = action;
            this.labelTexture = labelTexture;
            this.label = label;
            this.tint = tint;
        }

        MenuButton(Rectangle rect, MenuAction action, Texture labelTexture, String label) {
            this(rect, action, labelTexture, label, Color.WHITE);
        }
    }

    private static class MenuStar {
        final float x;
        final float y;
        final float speed;
        final float radius;
        final float phase;

        MenuStar(float x, float y, float speed, float radius, float phase) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.radius = radius;
            this.phase = phase;
        }
    }
}
