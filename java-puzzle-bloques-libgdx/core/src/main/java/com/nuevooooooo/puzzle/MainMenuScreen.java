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
    private static final Color BLUE_DARK = new Color(0.01f, 0.09f, 0.25f, 0.74f);
    private static final Color PANEL_EDGE = new Color(0.22f, 0.86f, 1f, 0.78f);
    private static final Color LOCKED = new Color(0.20f, 0.25f, 0.42f, 0.82f);

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
    private final Rectangle firstMission = new Rectangle(252, 358, 112, 112);
    private final Rectangle roboCard = new Rectangle(190, 238, 220, 246);
    private final Rectangle monoCard = new Rectangle(432, 238, 220, 246);

    private Texture background;
    private Texture logo;
    private Texture robo;
    private Texture spaceship;
    private Texture monoEspacial;
    private Texture button;
    private Texture panel;
    private Texture labelStart;
    private Texture labelMap;
    private Texture labelHow;
    private Texture labelCharacters;
    private Texture labelBack;
    private Texture labelPractice;

    private MenuMode mode = MenuMode.HOME;
    private float time;
    private String helperText = "Selecciona una opcion para comenzar tu mision.";

    public MainMenuScreen(PuzzleBloquesLibGDXGame game) {
        this.game = game;
        loadAssets();
        buildStars();
        setMode(MenuMode.HOME);
        setupInput();
    }

    private void loadAssets() {
        background = texture("images/menu_background.png");
        logo = texture("images/logo_codequest.png");
        robo = texture("images/robo_menu_pose.png");
        spaceship = texture("images/spaceship_menu.png");
        monoEspacial = texture("images/mono_espacial_wave.png");
        button = texture("images/btn_menu_primary.png");
        panel = texture("images/panel_hologram_menu.png");
        labelStart = texture("images/label_start.png");
        labelMap = texture("images/label_map.png");
        labelHow = texture("images/label_how.png");
        labelCharacters = texture("images/label_characters.png");
        labelBack = texture("images/label_back.png");
        labelPractice = texture("images/label_practice.png");
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
            float x = 138f;
            float y = 292f;
            float w = 314f;
            float h = 52f;
            buttons.add(new MenuButton(new Rectangle(x, y, w, h), MenuAction.START, labelStart));
            buttons.add(new MenuButton(new Rectangle(x, y - 60f, w, h), MenuAction.MAP, labelMap));
            buttons.add(new MenuButton(new Rectangle(x, y - 120f, w, h), MenuAction.HOW, labelHow));
            buttons.add(new MenuButton(new Rectangle(x, y - 180f, w, h), MenuAction.CHARACTERS, labelCharacters));
            helperText = "Programa rutas y guia a Robo por el espacio.";
            return;
        }
        buttons.add(new MenuButton(new Rectangle(72f, 42f, 210f, 56f), MenuAction.BACK, labelBack));
        buttons.add(new MenuButton(new Rectangle(500f, 36f, 300f, 66f), MenuAction.PRACTICE, labelPractice));
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
        if (mode == MenuMode.MAP && firstMission.contains(x, y)) {
            startAdventure();
            return;
        }
        if (mode == MenuMode.CHARACTERS) {
            if (roboCard.contains(x, y)) {
                GameState.selectCharacter(PlayerCharacter.ROBO);
                helperText = "Robo seleccionado: personaje basico.";
                return;
            }
            if (monoCard.contains(x, y)) {
                GameState.selectCharacter(PlayerCharacter.MONO_ESPACIAL);
                helperText = "Mono espacial seleccionado: rareza rara.";
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

    private void startAdventure() {
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
    }

    private void drawMovingSpace() {
        float driftX = MathUtils.sin(time * 0.10f) * 14f;
        float driftY = MathUtils.cos(time * 0.08f) * 8f;
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(background, -12f + driftX, -8f + driftY, W + 24f, H + 16f);
        batch.setColor(0.68f, 0.86f, 1f, 0.08f);
        batch.draw(background, -22f - driftX * 0.35f, -12f - driftY * 0.35f, W + 44f, H + 28f);
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

    private void drawHome() {
        float logoPulse = 1f + MathUtils.sin(time * 1.25f) * 0.012f;
        float logoW = 720f * logoPulse;
        float logoH = 240f * logoPulse;
        float logoX = (W - logoW) / 2f;
        float logoY = 456f + MathUtils.sin(time * 0.75f) * 4f;

        float roboFloat = MathUtils.sin(time * 1.18f) * 14f;
        float roboW = 350f;
        float roboH = 350f;
        float roboX = 790f;
        float roboY = 116f + roboFloat;
        float shipFloat = MathUtils.sin(time * 1.05f + 1.4f) * 10f;
        float shipW = 190f;
        float shipH = 134f;
        float shipX = 940f;
        float shipY = 346f + shipFloat;

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(logo, logoX, logoY, logoW, logoH);
        batch.draw(panel, 70f, 62f, 500f, 392f);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawShipSmoke(shipX, shipY, shipW, shipH);
        sr.setColor(0.17f, 0.88f, 1f, 0.18f + MathUtils.sin(time * 2f) * 0.05f);
        sr.ellipse(roboX + 58f, roboY - 5f, 238f, 34f);
        sr.end();

        batch.begin();
        batch.draw(spaceship, shipX, shipY, shipW, shipH);
        batch.draw(robo, roboX, roboY, roboW, roboH);
        drawButtons();
        batch.end();

        batch.begin();
        drawTextShadow(titleFont, "Academia Estelar", 132f, 414f, WHITE);
        drawTextShadow(smallFont, "Programacion para misiones espaciales", 132f, 388f, new Color(0.82f, 0.96f, 1f, 1f));
        drawTextShadow(smallFont, helperText, 132f, 366f, new Color(0.72f, 0.88f, 1f, 1f));
        drawButtonLabels();
        drawTextShadow(smallFont, "Pulsa un modo para continuar", 880f, 111f, new Color(0.74f, 0.94f, 1f, 1f));
        batch.end();
    }

    private void drawShipSmoke(float shipX, float shipY, float shipW, float shipH) {
        float engineX = shipX + shipW * 0.10f;
        float engineY = shipY + shipH * 0.50f;
        for (int i = 0; i < 7; i++) {
            float phase = time * 1.9f + i * 0.72f;
            float drift = (time * 26f + i * 24f) % 118f;
            float x = engineX - drift;
            float y = engineY + MathUtils.sin(phase) * 18f;
            float radius = 18f + i * 3.2f + MathUtils.sin(phase * 1.3f) * 3f;
            float alpha = Math.max(0f, 0.28f - drift / 420f);
            sr.setColor(0.54f, 0.86f, 1f, alpha);
            sr.circle(x, y, radius);
            sr.setColor(1f, 1f, 1f, alpha * 0.38f);
            sr.circle(x + 6f, y + 4f, radius * 0.58f);
        }

        float glow = 0.62f + MathUtils.sin(time * 7.5f) * 0.18f;
        sr.setColor(0.15f, 0.82f, 1f, 0.22f * glow);
        sr.ellipse(engineX - 26f, engineY - 18f, 80f, 36f);
        sr.setColor(0.75f, 1f, 1f, 0.62f * glow);
        sr.ellipse(engineX - 14f, engineY - 10f, 42f, 20f);
    }

    private void drawSubScreen() {
        batch.begin();
        batch.setColor(1f, 1f, 1f, 0.92f);
        batch.draw(logo, 384f, 548f, 512f, 170f);
        batch.draw(panel, 130f, 104f, 1020f, 464f);
        drawButtons();
        batch.end();

        if (mode == MenuMode.MAP) {
            drawMapContent();
        } else if (mode == MenuMode.HOW) {
            drawHowContent();
        } else if (mode == MenuMode.CHARACTERS) {
            drawCharacterContent();
        }

        batch.begin();
        drawButtonLabels();
        batch.end();
    }

    private void drawButtons() {
        for (MenuButton menuButton : buttons) {
            float hover = isHovering(menuButton.rect) ? 1.045f : 1f;
            float bw = menuButton.rect.width * hover;
            float bh = menuButton.rect.height * hover;
            float bx = menuButton.rect.x - (bw - menuButton.rect.width) / 2f;
            float by = menuButton.rect.y - (bh - menuButton.rect.height) / 2f;
            batch.draw(button, bx, by, bw, bh);
        }
    }

    private void drawButtonLabels() {
        for (MenuButton menuButton : buttons) {
            float labelW = menuButton.rect.width * 0.82f;
            float labelH = menuButton.rect.height * 0.58f;
            batch.draw(menuButton.labelTexture,
                    menuButton.rect.x + (menuButton.rect.width - labelW) / 2f,
                    menuButton.rect.y + (menuButton.rect.height - labelH) / 2f + 2f,
                    labelW,
                    labelH);
        }
    }

    private void drawMapContent() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawLine(306, 414, 492, 472, CYAN);
        drawLine(492, 472, 660, 382, CYAN);
        drawLine(660, 382, 840, 444, CYAN);
        drawLine(840, 444, 1010, 342, CYAN);
        drawPlanet(306, 414, 48, LIME, true);
        drawPlanet(492, 472, 42, new Color(0.30f, 0.74f, 1f, 1f), false);
        drawPlanet(660, 382, 42, new Color(0.80f, 0.48f, 1f, 1f), false);
        drawPlanet(840, 444, 42, new Color(1f, 0.68f, 0.20f, 1f), false);
        drawPlanet(1010, 342, 42, new Color(0.34f, 0.46f, 0.80f, 1f), false);
        sr.end();

        batch.begin();
        drawTextShadow(titleFont, "Mapa de misiones", 190f, 518f, WHITE);
        drawTextShadow(font, "Mision 1", 262f, 340f, WHITE);
        drawTextShadow(smallFont, "Primeros pasos", 246f, 318f, new Color(0.78f, 0.95f, 1f, 1f));
        drawTextShadow(smallFont, "Proximamente", 446f, 404f, new Color(0.68f, 0.78f, 0.94f, 1f));
        drawTextShadow(smallFont, "Completa misiones para desbloquear nuevos planetas.", 190f, 172f, new Color(0.82f, 0.95f, 1f, 1f));
        drawTextShadow(smallFont, "Toca Mision 1 o Practicar ahora para entrar al laberinto.", 190f, 148f, new Color(0.82f, 0.95f, 1f, 1f));
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
        drawTextShadow(smallFont, "Construye una lista de instrucciones para que Robo llegue a la meta.", 190f, 274f, new Color(0.84f, 0.96f, 1f, 1f));
        drawTextShadow(smallFont, "Si choca, cambia el orden y vuelve a intentar. Ese es el poder de programar.", 190f, 246f, new Color(0.84f, 0.96f, 1f, 1f));
        batch.end();
    }

    private void drawCharacterContent() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawCharacterCardFrame(roboCard, PlayerCharacter.ROBO, new Color(0.05f, 0.38f, 0.78f, 0.82f));
        drawCharacterCardFrame(monoCard, PlayerCharacter.MONO_ESPACIAL, new Color(0.44f, 0.22f, 0.82f, 0.86f));
        drawInfoCard(674, 238, 180, 246, LOCKED);
        drawInfoCard(876, 238, 180, 246, LOCKED);
        sr.end();

        batch.begin();
        drawTextShadow(titleFont, "Personajes", 190f, 518f, WHITE);
        batch.draw(robo, roboCard.x + 34f, roboCard.y + 74f, 150f, 150f);
        batch.draw(monoEspacial, monoCard.x + 35f, monoCard.y + 75f, 150f, 150f);
        drawCharacterText(roboCard, "Robo", "Basico", PlayerCharacter.ROBO);
        drawCharacterText(monoCard, "Mono espacial", "Raro", PlayerCharacter.MONO_ESPACIAL);
        drawLockedCharacter("Proximo", 716f, 384f, "Epico");
        drawLockedCharacter("Proximo", 918f, 384f, "Legendario");
        drawTextShadow(smallFont, "Selecciona un personaje y pulsa Practicar ahora para entrar con el.", 190f, 172f, new Color(0.84f, 0.96f, 1f, 1f));
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
        Color rarityColor = character == PlayerCharacter.MONO_ESPACIAL
                ? new Color(0.92f, 0.72f, 1f, 1f)
                : new Color(0.78f, 1f, 0.70f, 1f);
        drawTextShadow(smallFont, rarity, card.x + 30f, card.y + 34f, rarityColor);
        String state = GameState.selectedCharacter() == character ? "Seleccionado" : "Tocar para elegir";
        drawTextShadow(smallFont, state, card.x + 30f, card.y + 16f, new Color(0.82f, 0.95f, 1f, 1f));
    }

    private void drawLockedCharacter(String name, float x, float y, String rarity) {
        drawTextShadow(font, "?", x + 34f, y + 20f, new Color(0.64f, 0.82f, 1f, 1f));
        drawTextShadow(font, name, x - 12f, y - 50f, WHITE);
        drawTextShadow(smallFont, rarity, x - 20f, y - 74f, new Color(0.88f, 0.78f, 1f, 1f));
        drawTextShadow(smallFont, "Bloqueado", x - 20f, y - 96f, new Color(0.74f, 0.82f, 0.98f, 1f));
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

    private void drawPlanet(float x, float y, float radius, Color color, boolean unlocked) {
        sr.setColor(0f, 0.02f, 0.08f, 0.38f);
        sr.circle(x + 7f, y - 7f, radius + 7f);
        sr.setColor(color);
        sr.circle(x, y, radius);
        sr.setColor(1f, 1f, 1f, 0.42f);
        sr.circle(x - radius * 0.28f, y + radius * 0.28f, radius * 0.28f);
        if (!unlocked) {
            sr.setColor(0f, 0f, 0f, 0.36f);
            sr.circle(x, y, radius + 2f);
        } else {
            sr.setColor(1f, 0.92f, 0.18f, 1f);
            drawStar(x, y, radius * 0.42f);
        }
    }

    private void drawLine(float x1, float y1, float x2, float y2, Color color) {
        sr.setColor(0f, 0f, 0f, 0.20f);
        sr.rectLine(x1, y1 - 4f, x2, y2 - 4f, 8f);
        sr.setColor(color);
        sr.rectLine(x1, y1, x2, y2, 5f);
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
        spaceship.dispose();
        monoEspacial.dispose();
        button.dispose();
        panel.dispose();
        labelStart.dispose();
        labelMap.dispose();
        labelHow.dispose();
        labelCharacters.dispose();
        labelBack.dispose();
        labelPractice.dispose();
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

        MenuButton(Rectangle rect, MenuAction action, Texture labelTexture) {
            this.rect = rect;
            this.action = action;
            this.labelTexture = labelTexture;
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
