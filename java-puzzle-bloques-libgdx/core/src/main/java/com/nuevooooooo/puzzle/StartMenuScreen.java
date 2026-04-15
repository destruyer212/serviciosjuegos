package com.nuevooooooo.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Menu inicial rehecho desde cero:
 * <ul>
 *   <li>Fondos y botones: {@link SpriteBatch} + textura 1x1 blanca teñida (sin ShapeRenderer para UI).</li>
 *   <li>Texto: {@link PuzzleBloquesLibGDXGame#uiFont} cargada en {@code create()}.</li>
 *   <li>Preview del heroe: solo al final, con {@link ShapeRenderer} (no mezcla con el batch de texto).</li>
 * </ul>
 */
public class StartMenuScreen extends com.badlogic.gdx.ScreenAdapter {

    private static final float W = 1280f;
    private static final float H = 720f;

    private final PuzzleBloquesLibGDXGame game;
    private final HeroConfig config = new HeroConfig();

    private final SpriteBatch batch = new SpriteBatch();
    /** Un pixel blanco; se teñine con {@link SpriteBatch#setColor(float, float, float, float)}. */
    private final Texture pixel;
    private final ShapeRenderer sr = new ShapeRenderer();
    private final Viewport viewport = new FitViewport(W, H);
    private final Vector3 touch = new Vector3();

    private final StringBuilder nameBuffer = new StringBuilder();
    private boolean nameFocused;

    private final Rectangle nameRect = new Rectangle(672, 560, 420, 40);
    private final Rectangle btnSuit = new Rectangle(672, 510, 420, 44);
    private final Rectangle btnHair = new Rectangle(672, 458, 420, 44);
    private final Rectangle btnHairColor = new Rectangle(672, 406, 420, 44);
    private final Rectangle btnFace = new Rectangle(672, 354, 420, 44);
    private final Rectangle btnDiff = new Rectangle(672, 302, 420, 44);
    private final Rectangle btnPlay = new Rectangle(672, 240, 260, 52);

    private static final float HERO_X = 952f;
    private static final float HERO_Y = 545f;

    public StartMenuScreen(PuzzleBloquesLibGDXGame game) {
        this.game = game;
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
        pixel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    @Override
    public void show() {
        nameBuffer.setLength(0);
        nameBuffer.append(config.name);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                float x = touch.x;
                float y = touch.y;

                if (nameRect.contains(x, y)) {
                    nameFocused = true;
                    return true;
                }
                nameFocused = false;

                if (btnSuit.contains(x, y)) {
                    cycleSuit();
                    return true;
                }
                if (btnHair.contains(x, y)) {
                    cycleHair();
                    return true;
                }
                if (btnHairColor.contains(x, y)) {
                    cycleHairColor();
                    return true;
                }
                if (btnFace.contains(x, y)) {
                    cycleFace();
                    return true;
                }
                if (btnDiff.contains(x, y)) {
                    cycleDiff();
                    return true;
                }
                if (btnPlay.contains(x, y)) {
                    startGame();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (!nameFocused) {
                    return false;
                }
                if (character == '\b') {
                    if (nameBuffer.length() > 0) {
                        nameBuffer.deleteCharAt(nameBuffer.length() - 1);
                    }
                    return true;
                }
                if (nameBuffer.length() < 18 && character >= 32 && character < 127) {
                    nameBuffer.append(character);
                    return true;
                }
                return false;
            }
        });
        resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    private void rect(SpriteBatch b, float r, float g, float bl, float a, float x, float y, float w, float h) {
        b.setColor(r, g, bl, a);
        b.draw(pixel, x, y, w, h);
    }

    private void cycleSuit() {
        HeroConfig.Suit[] arr = HeroConfig.Suit.values();
        config.suit = arr[(config.suit.ordinal() + 1) % arr.length];
    }

    private void cycleHair() {
        HeroConfig.HairStyle[] arr = HeroConfig.HairStyle.values();
        config.hairStyle = arr[(config.hairStyle.ordinal() + 1) % arr.length];
    }

    private void cycleHairColor() {
        HeroConfig.HairColor[] arr = HeroConfig.HairColor.values();
        config.hairColor = arr[(config.hairColor.ordinal() + 1) % arr.length];
    }

    private void cycleFace() {
        HeroConfig.Face[] arr = HeroConfig.Face.values();
        config.face = arr[(config.face.ordinal() + 1) % arr.length];
    }

    private void cycleDiff() {
        HeroConfig.Difficulty[] arr = HeroConfig.Difficulty.values();
        config.difficulty = arr[(config.difficulty.ordinal() + 1) % arr.length];
    }

    private void startGame() {
        String n = nameBuffer.toString().trim();
        if (!n.isEmpty()) {
            config.name = n;
        }
        Screen old = game.getScreen();
        game.setScreen(new PuzzleGameScreen3D(config));
        if (old != null) {
            old.dispose();
        }
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        ScreenUtils.clear(0.05f, 0.09f, 0.16f, 1f);

        batch.begin();

        rect(batch, 0.08f, 0.13f, 0.22f, 1f, 0, 0, W, H);
        rect(batch, 0.06f, 0.10f, 0.18f, 1f, 0, 656, W, 64);

        rect(batch, 0.10f, 0.18f, 0.30f, 1f, 8, 8, 620, 640);
        rect(batch, 0.12f, 0.18f, 0.30f, 1f, 652, 8, 620, 640);
        rect(batch, 0.09f, 0.16f, 0.28f, 1f, 672, 460, 560, 170);

        rect(batch, 0.15f, 0.22f, 0.36f, 1f, btnSuit.x, btnSuit.y, btnSuit.width, btnSuit.height);
        rect(batch, 0.15f, 0.22f, 0.36f, 1f, btnHair.x, btnHair.y, btnHair.width, btnHair.height);
        rect(batch, 0.15f, 0.22f, 0.36f, 1f, btnHairColor.x, btnHairColor.y, btnHairColor.width, btnHairColor.height);
        rect(batch, 0.15f, 0.22f, 0.36f, 1f, btnFace.x, btnFace.y, btnFace.width, btnFace.height);
        rect(batch, 0.15f, 0.22f, 0.36f, 1f, btnDiff.x, btnDiff.y, btnDiff.width, btnDiff.height);

        rect(batch, 0.18f, 0.65f, 0.32f, 1f, btnPlay.x, btnPlay.y, btnPlay.width, btnPlay.height);
        rect(batch, 0.12f, 0.16f, 0.26f, 1f, nameRect.x, nameRect.y, nameRect.width, nameRect.height);

        batch.setColor(1f, 1f, 1f, 1f);

        BitmapFont font = game.uiFont;
        float base = 24f / 15f;

        font.setColor(Color.WHITE);
        font.getData().setScale(base * 1.2f);
        font.draw(batch, "Bienvenido a Puzzle de Bloques", 32, 620);
        font.getData().setScale(base);
        font.setColor(0.9f, 0.93f, 1f, 1f);
        font.draw(batch, "El Escape de la Flor Dorada", 32, 588);
        font.setColor(Color.WHITE);
        font.draw(batch, "Juego para ninos: programa con bloques y llega a la flor dorada.", 32, 548);
        font.draw(batch, "Personaliza al heroe y pulsa el boton verde (A JUGAR).", 32, 520);

        font.getData().setScale(base * 1.15f);
        font.draw(batch, "Tu heroe", 672, 620);
        font.getData().setScale(base);

        font.draw(batch, "Traje (clic): " + config.suit.label, btnSuit.x + 14, btnSuit.y + 28);
        font.draw(batch, "Peinado (clic): " + config.hairStyle.label, btnHair.x + 14, btnHair.y + 28);
        font.draw(batch, "Color pelo (clic): " + config.hairColor.label, btnHairColor.x + 14, btnHairColor.y + 28);
        font.draw(batch, "Cara (clic): " + config.face.label, btnFace.x + 14, btnFace.y + 28);
        font.draw(batch, "Dificultad (clic): " + config.difficulty.label, btnDiff.x + 14, btnDiff.y + 28);

        font.getData().setScale(base * 1.1f);
        font.draw(batch, "A JUGAR", btnPlay.x + 72, btnPlay.y + 34);
        font.getData().setScale(base);

        String nameText = nameBuffer.toString();
        if (nameText.isEmpty() && !nameFocused) {
            font.setColor(0.55f, 0.62f, 0.72f, 1f);
            font.draw(batch, "Escribe tu nombre", nameRect.x + 12, nameRect.y + 26);
        } else {
            font.setColor(Color.WHITE);
            font.draw(batch, nameFocused ? nameText + "|" : nameText, nameRect.x + 12, nameRect.y + 26);
        }

        batch.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        sr.setProjectionMatrix(viewport.getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(config.suit.suitColor);
        sr.circle(HERO_X, HERO_Y - 2, 13);
        sr.setColor(config.suit.skinColor);
        sr.circle(HERO_X, HERO_Y + 12, 9);
        sr.setColor(config.hairColor.color);
        sr.rect(HERO_X - 8, HERO_Y + 16, 16, 4);
        sr.setColor(0.1f, 0.14f, 0.22f, 1f);
        sr.rect(HERO_X - 2, HERO_Y - 2, 4, 10);
        sr.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        pixel.dispose();
        batch.dispose();
        sr.dispose();
    }
}
