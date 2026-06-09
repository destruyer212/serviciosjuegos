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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nuevooooooo.puzzle.ui.animation.StarCollectAnimator;
import com.nuevooooooo.puzzle.ui.board.GameBoardRenderer;
import com.nuevooooooo.puzzle.ui.board.MapSpeechBubbleRenderer;
import com.nuevooooooo.puzzle.ui.hud.FooterHudRenderer;
import com.nuevooooooo.puzzle.ui.hud.HudState;
import com.nuevooooooo.puzzle.ui.hud.TopHudRenderer;
import com.nuevooooooo.puzzle.ui.panels.BlockPalettePanelRenderer;
import com.nuevooooooo.puzzle.ui.panels.ProgramPanelRenderer;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;
import com.nuevooooooo.puzzle.ui.widgets.ActionButtonsRenderer;

import java.util.ArrayList;
import java.util.List;

public class RoboCodeGameScreen extends ScreenAdapter {
    private static final float W = 1280f;
    private static final float H = 720f;

    private static final int MAX_MAP_ROWS = 14;
    private static final int MAX_MAP_COLS = 14;

    private int mapRows = 10;
    private int mapCols = 10;
    private float cellSize = 46f;
    private float boardX = SpaceTheme.BOARD_FRAME_X + (SpaceTheme.BOARD_FRAME_W - 10 * 46f) / 2f;
    private float boardY = SpaceTheme.BOARD_FRAME_Y + (SpaceTheme.BOARD_FRAME_H - 10 * 46f) / 2f;

    private static final float LEFT_X = 14f;
    private static final float LEFT_W = 270f;
    private static final float SIDE_TOP_Y = 96f;
    private static final float SIDE_H = 554f;
    private static final float PROGRAM_X = SpaceTheme.PROGRAM_X;
    private static final float PROGRAM_Y = SpaceTheme.PROGRAM_Y;
    private static final float PROGRAM_W = SpaceTheme.PROGRAM_W;
    private static final float PROGRAM_H = SpaceTheme.PROGRAM_H;
    private static final float MODAL_X = 226f;
    private static final float MODAL_Y = 82f;
    private static final float MODAL_W = 828f;
    private static final float MODAL_H = 562f;
    private static final float FAIL_MODAL_X = 390f;
    private static final float FAIL_MODAL_Y = 162f;
    private static final float FAIL_MODAL_W = 500f;
    private static final float FAIL_MODAL_H = 360f;
    private static final int MAX_PROGRAM_BLOCKS = 100;

    private static final float MOVE_TIME = 0.24f;
    private static final float RUN_STEP_TIME = 0.42f;

    private static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    private static final Color PANEL = new Color(0.06f, 0.20f, 0.48f, 0.86f);
    private static final Color PANEL_DARK = new Color(0.02f, 0.09f, 0.25f, 0.84f);
    private static final Color PANEL_LIGHT = new Color(0.74f, 0.92f, 1f, 0.88f);
    private static final Color CYAN = new Color(0.27f, 0.88f, 1f, 1f);
    private static final Color BLUE = new Color(0.02f, 0.56f, 0.95f, 1f);
    private static final Color PURPLE = new Color(0.49f, 0.24f, 0.90f, 1f);
    private static final Color GREEN = new Color(0.35f, 0.78f, 0.24f, 1f);
    private static final Color ORANGE = new Color(0.98f, 0.58f, 0.06f, 1f);
    private static final Color YELLOW = new Color(1f, 0.82f, 0.08f, 1f);
    private static final Color SAND = new Color(0.96f, 0.84f, 0.58f, 1f);
    private static final Color GRASS = new Color(0.34f, 0.74f, 0.18f, 1f);
    private static final Color GRASS_DARK = new Color(0.17f, 0.48f, 0.12f, 1f);
    private static final Color ROUTE = new Color(0.02f, 0.38f, 0.90f, 1f);
    private static final char FLIGHT_TILE = '~';

    private static final LevelData[] LEVELS = {
            new LevelData(
                    "Nivel 1: Secuencia",
                    "Ayuda a Robo a llegar a la Meta",
                    new String[]{
                            "##########",
                            "#S....#..#",
                            "###.#.#..#",
                            "#...#....#",
                            "#.###.##.#",
                            "#.....#..#",
                            "#.#####..#",
                            "#.......G#",
                            "#..#######",
                            "##########"
                    },
                    new CellPoint[]{
                            new CellPoint(1, 4),
                            new CellPoint(3, 7),
                            new CellPoint(5, 2),
                            new CellPoint(7, 5)
                    }),
            new LevelData(
                    "Nivel 2: Ruta con nave",
                    "Sube a la nave, teleportate al otro lado y llega a la Meta",
                    new String[]{
                            "##########",
                            "#S......G#",
                            "#.###.#..#",
                            "#........#",
                            "#.#...#..#",
                            "#..N~~...#",
                            "#..#.~~~T#",
                            "#...##...#",
                            "#..#.....#",
                            "##########"
                    },
                    new CellPoint[]{
                            new CellPoint(1, 3),
                            new CellPoint(3, 2),
                            new CellPoint(7, 2),
                            new CellPoint(8, 5)
                    }),
            new LevelData(
                    "Nivel 3: Laberinto estelar",
                    "Recorre el laberinto, usa las dos naves y llega a la Meta",
                    new String[]{
                            "##############",
                            "#S..###.....G#",
                            "#.###.#.##...#",
                            "#...#.#..#...#",
                            "###.#.#..#...#",
                            "#...#....#...#",
                            "#.#######....#",
                            "#.......#....#",
                            "#.#####.#....#",
                            "#.....#.#....#",
                            "#.###.#.##...#",
                            "#.....#......#",
                            "#..N~~~~~~~T.#",
                            "##############"
                    },
                    new CellPoint[]{
                            new CellPoint(3, 3),
                            new CellPoint(5, 4),
                            new CellPoint(7, 1),
                            new CellPoint(9, 3),
                            new CellPoint(11, 2),
                            new CellPoint(11, 7)
                    })
    };

    private static int totalStarsInCampaign() {
        int total = 0;
        for (LevelData level : LEVELS) {
            total += level.coins.length;
        }
        return total;
    }

    private final PuzzleBloquesLibGDXGame game;
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer sr = new ShapeRenderer();
    private final Viewport viewport = new FitViewport(W, H);
    private final Vector3 touch = new Vector3();
    private final GlyphLayout layout = new GlyphLayout();

    private final BitmapFont titleFont = UiFonts.createUiFont(1.82f);
    private final BitmapFont font = UiFonts.createUiFont(1.28f);
    private final BitmapFont smallFont = UiFonts.createUiFont(1.02f);
    private final BitmapFont codeFont = UiFonts.createUiFont(0.92f);
    private final BitmapFont modalCodeFont = UiFonts.createUiFont(1.06f);

    private Texture spaceBackground;
    private Texture roboIdle;
    private Texture roboDown;
    private Texture roboBack;
    private Texture roboRight;
    private Texture roboLeft;
    private Texture roboUp;
    private Texture roboError;
    private Texture roboWin;
    private Texture guidePortrait;
    private Texture shipTexture;

    private final char[][] map = new char[MAX_MAP_ROWS][MAX_MAP_COLS];
    private final List<CommandButton> palette = new ArrayList<>();
    private final List<Command> program = new ArrayList<>();
    private final List<Command> executionQueue = new ArrayList<>();
    private final List<Integer> executionSourceIndexes = new ArrayList<>();
    private final List<CellPoint> previewPath = new ArrayList<>();
    private final List<PathStep> previewSteps = new ArrayList<>();
    private final List<CellPoint> coins = new ArrayList<>();
    private boolean[] collectedCoins = new boolean[8];

    private final Rectangle expandCodeButton = new Rectangle();
    private final Rectangle closeCodeButton = new Rectangle(MODAL_X + MODAL_W - 102f, MODAL_Y + MODAL_H - 56f, 72f, 34f);
    private final Rectangle failRetryButton = new Rectangle(FAIL_MODAL_X + 52f, FAIL_MODAL_Y + 42f, 128f, 46f);
    private final Rectangle failEditButton = new Rectangle(FAIL_MODAL_X + 186f, FAIL_MODAL_Y + 42f, 128f, 46f);
    private final Rectangle failMenuButton = new Rectangle(FAIL_MODAL_X + 320f, FAIL_MODAL_Y + 42f, 128f, 46f);
    private final Rectangle winNewButton = new Rectangle(FAIL_MODAL_X + 52f, FAIL_MODAL_Y + 42f, 128f, 46f);
    private final Rectangle winPracticeButton = new Rectangle(FAIL_MODAL_X + 186f, FAIL_MODAL_Y + 42f, 128f, 46f);
    private final Rectangle winMenuButton = new Rectangle(FAIL_MODAL_X + 320f, FAIL_MODAL_Y + 42f, 128f, 46f);

    private int startRow;
    private int startCol;
    private int goalRow;
    private int goalCol;
    private int shipRow = -1;
    private int shipCol = -1;
    private int shipDestRow = -1;
    private int shipDestCol = -1;
    private int currentLevelIndex;
    private int robotRow;
    private int robotCol;
    private Direction facing = Direction.RIGHT;
    private RobotMood mood = RobotMood.IDLE;

    private float robotX;
    private float robotY;
    private float robotFromX;
    private float robotFromY;
    private float robotToX;
    private float robotToY;
    private float moveTimer = MOVE_TIME;

    private boolean running;
    private boolean showCodeModal;
    private boolean showFailModal;
    private boolean failGameOver;
    private boolean showWinModal;
    private int executionIndex;
    private int activeProgramIndex = -1;
    private float runTimer;
    private float time;
    private String failReason = "Robo necesita otro algoritmo.";
    private String failAdvice = "Cambia el orden de tus bloques e intenta otra vez.";
    private String failLivesMessage = "";
    private String winLesson = "Creaste una secuencia correcta.";
    private int coinsCollected;
    private int levelRating = 1;
    private int lastCreditsEarned;
    private String status = "Toca bloques para armar tu programa.";
    private boolean usingShip;
    private boolean shipBoarded;

    private int previewRow;
    private int previewCol;
    private Direction previewFacing = Direction.RIGHT;

    private SpaceThemeAssets themeAssets;
    private SpaceThemeRenderer themeRenderer;
    private TopHudRenderer topHudRenderer;
    private FooterHudRenderer footerHudRenderer;
    private BlockPalettePanelRenderer blockPalettePanelRenderer;
    private GameBoardRenderer gameBoardRenderer;
    private ProgramPanelRenderer programPanelRenderer;
    private ActionButtonsRenderer actionButtonsRenderer;
    private StarCollectAnimator starCollectAnimator;
    private MapSpeechBubbleRenderer mapSpeechBubbleRenderer;
    private final List<ProgramPanelRenderer.ProgramCommandView> programViews = new ArrayList<>();

    public RoboCodeGameScreen() {
        this(null);
    }

    public RoboCodeGameScreen(PuzzleBloquesLibGDXGame game) {
        this.game = game;
        loadAssets();
        initThemeUi();
        buildPalette();
        currentLevelIndex = GameState.currentLevelIndex();
        GameState.setMaxStars(totalStarsInCampaign());
        loadLevel();
        setupInput();
    }

    private void initThemeUi() {
        themeAssets = new SpaceThemeAssets();
        themeRenderer = new SpaceThemeRenderer(themeAssets);
        topHudRenderer = new TopHudRenderer(themeRenderer, titleFont, font, smallFont);
        footerHudRenderer = new FooterHudRenderer(themeRenderer, font, smallFont);
        blockPalettePanelRenderer = new BlockPalettePanelRenderer(themeRenderer, font);
        gameBoardRenderer = new GameBoardRenderer(themeRenderer);
        programPanelRenderer = new ProgramPanelRenderer(themeRenderer, font, smallFont);
        actionButtonsRenderer = new ActionButtonsRenderer(themeRenderer, font, smallFont);
        starCollectAnimator = new StarCollectAnimator();
        mapSpeechBubbleRenderer = new MapSpeechBubbleRenderer(themeRenderer, smallFont);
        layoutUiRects();
    }

    private void layoutUiRects() {
        expandCodeButton.set(
                SpaceTheme.CODE_BTN_X,
                SpaceTheme.CODE_BTN_Y,
                SpaceTheme.CODE_BTN_W,
                SpaceTheme.CODE_BTN_H);
    }

    private void loadAssets() {
        spaceBackground = texture("images/space_background_main.png");
        shipTexture = texture("images/props/spaceship_goal.png");
        if (GameState.selectedCharacter() == PlayerCharacter.MONO_ESPACIAL && !GameState.isMonoUnlocked()) {
            GameState.selectCharacter(PlayerCharacter.ROBO);
        }
        if (GameState.selectedCharacter() == PlayerCharacter.MONO_ESPACIAL) {
            roboIdle = texture("images/mono_espacial_idle_front.png");
            roboDown = texture("images/mono_espacial_look_down.png");
            roboBack = texture("images/mono_espacial_back.png");
            roboRight = texture("images/mono_espacial_right.png");
            roboLeft = texture("images/mono_espacial_left.png");
            roboUp = texture("images/mono_espacial_look_up.png");
            roboError = texture("images/mono_espacial_thinking.png");
            roboWin = texture("images/mono_espacial_wave.png");
            guidePortrait = texture("images/mono_espacial_wave.png");
        } else if (GameState.selectedCharacter() == PlayerCharacter.PIRATA_ESPACIAL) {
            roboIdle = texture("images/pirata_espacial_idle_front.png");
            roboDown = texture("images/pirata_espacial_look_down.png");
            roboBack = texture("images/pirata_espacial_back.png");
            roboRight = texture("images/pirata_espacial_right.png");
            roboLeft = texture("images/pirata_espacial_left.png");
            roboUp = texture("images/pirata_espacial_look_up.png");
            roboError = texture("images/pirata_espacial_thinking.png");
            roboWin = texture("images/pirata_espacial_wave.png");
            guidePortrait = texture("images/pirata_espacial_menu_pose.png");
        } else {
            roboIdle = texture("images/chars/robo_idle.png");
            roboDown = texture("images/chars/robo_down.png");
            roboBack = texture("images/robo_back.png");
            roboRight = texture("images/chars/robo_right.png");
            roboLeft = texture("images/chars/robo_left.png");
            roboUp = texture("images/chars/robo_up.png");
            roboError = texture("images/chars/robo_error.png");
            roboWin = texture("images/chars/robo_win.png");
            guidePortrait = texture("images/chars/robo_idle.png");
        }
    }

    private Texture texture(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }

    private void buildPalette() {
        palette.clear();
        float x = LEFT_X + 18f;
        float w = LEFT_W - 36f;
        float h = 36f;
        palette.add(new CommandButton(Command.FORWARD, "avanzar", "forward", new Rectangle(x, 554f, w, h), BLUE));
        palette.add(new CommandButton(Command.TURN_LEFT, "girar izquierda", "turn_left", new Rectangle(x, 512f, w, h), BLUE));
        palette.add(new CommandButton(Command.TURN_RIGHT, "girar derecha", "turn_right", new Rectangle(x, 470f, w, h), BLUE));
        palette.add(new CommandButton(Command.REPEAT_PREVIOUS, "repetir x3", "loop", new Rectangle(x, 402f, w, h), ORANGE));
        palette.add(new CommandButton(Command.IF_WALL, "si hay pared", "wall", new Rectangle(x, 334f, w, h), PURPLE));
        palette.add(new CommandButton(Command.IF_PATH_FREE, "si camino libre", "path", new Rectangle(x, 292f, w, h), PURPLE));
        palette.add(new CommandButton(Command.SAY, "decir: Vamos!", "say", new Rectangle(x, 224f, w, h), new Color(0.26f, 0.62f, 0.96f, 1f)));
        palette.add(new CommandButton(Command.COLLECT_STAR, "recoger estrella", "star", new Rectangle(x, 182f, w, h), GREEN));
        palette.add(new CommandButton(Command.USE_SHIP, "usar nave", "ship", new Rectangle(x, 140f, w, h), new Color(0.10f, 0.72f, 0.88f, 1f)));
    }

    private void loadLevel() {
        GameState.setCurrentLevelIndex(currentLevelIndex);
        LevelData level = currentLevel();
        mapRows = level.rows.length;
        mapCols = level.rows[0].length();
        cellSize = Math.min(SpaceTheme.BOARD_FRAME_W / mapCols, SpaceTheme.BOARD_FRAME_H / mapRows);
        boardX = SpaceTheme.BOARD_FRAME_X + (SpaceTheme.BOARD_FRAME_W - mapCols * cellSize) / 2f;
        boardY = SpaceTheme.BOARD_FRAME_Y + (SpaceTheme.BOARD_FRAME_H - mapRows * cellSize) / 2f;
        shipRow = -1;
        shipCol = -1;
        shipDestRow = -1;
        shipDestCol = -1;
        for (int r = 0; r < MAX_MAP_ROWS; r++) {
            for (int c = 0; c < MAX_MAP_COLS; c++) {
                map[r][c] = '#';
            }
        }
        for (int r = 0; r < mapRows; r++) {
            for (int c = 0; c < mapCols; c++) {
                char ch = level.rows[r].charAt(c);
                if (ch == 'S') {
                    startRow = r;
                    startCol = c;
                    map[r][c] = '.';
                } else if (ch == 'G') {
                    goalRow = r;
                    goalCol = c;
                    map[r][c] = 'G';
                } else if (ch == 'N') {
                    shipRow = r;
                    shipCol = c;
                    map[r][c] = '.';
                } else if (ch == 'T') {
                    shipDestRow = r;
                    shipDestCol = c;
                    map[r][c] = '.';
                } else {
                    map[r][c] = ch;
                }
            }
        }
        coins.clear();
        for (CellPoint coin : level.coins) {
            coins.add(coin);
        }
        resetRobotAndRewards();
    }

    private LevelData currentLevel() {
        return LEVELS[currentLevelIndex];
    }

    private boolean hasNextLevel() {
        return currentLevelIndex < LEVELS.length - 1;
    }

    private void resetRobotAndRewards() {
        robotRow = startRow;
        robotCol = startCol;
        facing = Direction.RIGHT;
        mood = RobotMood.IDLE;
        usingShip = false;
        shipBoarded = false;
        running = false;
        showFailModal = false;
        failGameOver = false;
        showWinModal = false;
        executionIndex = 0;
        activeProgramIndex = -1;
        runTimer = 0f;
        executionQueue.clear();
        executionSourceIndexes.clear();
        coinsCollected = 0;
        if (collectedCoins.length < coins.size()) {
            collectedCoins = new boolean[coins.size()];
        } else {
            for (int i = 0; i < coins.size(); i++) {
                collectedCoins[i] = false;
            }
        }
        robotX = cellCenterX(robotCol);
        robotY = cellCenterY(robotRow);
        robotFromX = robotX;
        robotFromY = robotY;
        robotToX = robotX;
        robotToY = robotY;
        moveTimer = MOVE_TIME;
        rebuildPreview();
        applyLevelIntroStatus();
    }

    private void applyLevelIntroStatus() {
        if (currentLevelIndex == 2 && levelRequiresShip()) {
            status = "Laberinto grande: baja a NAVE, teleporta y sube a META.";
        } else if (isShipTutorialLevel()) {
            status = "Tutorial nave: cruza el vacio con usar nave y llega a META.";
        }
    }

    private boolean isShipTutorialLevel() {
        return currentLevelIndex >= 1 && levelRequiresShip();
    }

    private String shipLevelTutorialMessage() {
        if (program.isEmpty()) {
            if (currentLevelIndex >= 2) {
                return "Laberinto estelar: baja a NAVE, usa nave, sube a META.";
            }
            return "Cruza el vacio azul: llega a NAVE y usa el bloque usar nave.";
        }
        if (!program.contains(Command.USE_SHIP)) {
            return "Pon usar nave cuando estes encima de la plataforma NAVE.";
        }
        if (!program.contains(Command.COLLECT_STAR) && !allCoinsCollected()) {
            return "Recoge estrellas (+15 cred.) con recoger estrella.";
        }
        return "Listo! Pulsa Ejecutar.";
    }

    private String shipLevelMapHint() {
        if (program.isEmpty()) {
            return "Camino marron → NAVE → usar nave";
        }
        if (!program.contains(Command.USE_SHIP)) {
            return "Entra a la casilla NAVE por el camino";
        }
        if (!program.contains(Command.COLLECT_STAR) && !allCoinsCollected()) {
            return "Recoge las estrellas del camino";
        }
        return "¡Vamos! Sigue las flechas";
    }

    private boolean shouldShowShipLevelTutorial() {
        if (!isShipTutorialLevel() || running || showWinModal || showFailModal) {
            return false;
        }
        return program.isEmpty()
                || !program.contains(Command.USE_SHIP)
                || (!allCoinsCollected() && !program.contains(Command.COLLECT_STAR));
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
        if (showWinModal) {
            if (winNewButton.contains(x, y)) {
                goToNextLevelOrRestart();
            } else if (winPracticeButton.contains(x, y)) {
                showWinModal = false;
                resetRobotAndRewards();
                status = "Sigue practicando con tu programa.";
            } else if (winMenuButton.contains(x, y)) {
                goToMenu();
            }
            return;
        }
        if (showFailModal) {
            if (failRetryButton.contains(x, y)) {
                if (failGameOver) {
                    restartCampaignAfterGameOver();
                } else {
                    showFailModal = false;
                    failGameOver = false;
                    clearProgram();
                    resetRobotAndRewards();
                    status = "Sigue en el nivel " + (currentLevelIndex + 1)
                            + ". Te quedan " + GameState.lives() + " vidas.";
                }
            } else if (failEditButton.contains(x, y)) {
                if (failGameOver) {
                    goToMenu();
                } else {
                    showFailModal = false;
                    failGameOver = false;
                    resetRobotAndRewards();
                    status = "Edita tu algoritmo. Te quedan " + GameState.lives() + " vidas.";
                }
            } else if (failMenuButton.contains(x, y)) {
                goToMenu();
            }
            return;
        }
        if (showCodeModal) {
            if (closeCodeButton.contains(x, y)) {
                showCodeModal = false;
                status = "Volviste al tablero.";
            }
            return;
        }
        if (expandCodeButton.contains(x, y)) {
            showCodeModal = true;
            status = "Codigo expandido con explicacion.";
            return;
        }
        if (topHudRenderer.menuButton().contains(x, y)) {
            goToMenu();
            return;
        }
        if (topHudRenderer.lifePill().contains(x, y) || footerHudRenderer.buyLifeBadge().contains(x, y)) {
            tryBuyLife();
            return;
        }
        if (topHudRenderer.coinPill().contains(x, y)) {
            showCreditsInfo();
            return;
        }
        if (topHudRenderer.starPill().contains(x, y)) {
            showStarsInfo();
            return;
        }
        if (footerHudRenderer.achievementsBadge().contains(x, y)) {
            showAchievementsInfo();
            return;
        }
        if (footerHudRenderer.robotsBadge().contains(x, y)) {
            showRobotsInfo();
            return;
        }
        if (actionButtonsRenderer.runButton().contains(x, y)) {
            startRun();
            return;
        }
        if (actionButtonsRenderer.stepButton().contains(x, y)) {
            stepOnce();
            return;
        }
        if (actionButtonsRenderer.pauseButton().contains(x, y)) {
            pauseRun();
            return;
        }
        if (actionButtonsRenderer.undoButton().contains(x, y)) {
            undoProgram();
            return;
        }
        if (actionButtonsRenderer.clearButton().contains(x, y)) {
            clearProgram();
            return;
        }
        if (actionButtonsRenderer.resetButton().contains(x, y)) {
            resetRobotAndRewards();
            status = characterName() + " volvio al inicio. Flechas del mapa actualizadas.";
            return;
        }
        for (CommandButton item : palette) {
            if (item.rect.contains(x, y)) {
                addCommand(item.command);
                return;
            }
        }
        int clickedProgram = programIndexAt(x, y);
        if (clickedProgram >= 0) {
            program.remove(clickedProgram);
            activeProgramIndex = -1;
            status = "Bloque retirado del programa.";
            rebuildPreview();
        }
    }

    private void goToNextLevelOrRestart() {
        showWinModal = false;
        program.clear();
        executionQueue.clear();
        executionSourceIndexes.clear();
        running = false;
        executionIndex = 0;
        activeProgramIndex = -1;
        if (hasNextLevel()) {
            currentLevelIndex++;
            GameState.setCurrentLevelIndex(currentLevelIndex);
            loadLevel();
            if (isShipTutorialLevel()) {
                status = "Nivel 2: aprende a usar la nave y el teleport.";
            } else {
                status = "Nivel " + (currentLevelIndex + 1) + ": " + currentLevel().title
                        + ". Te quedan " + GameState.lives() + " vidas.";
            }
            return;
        }
        resetRobotAndRewards();
        rebuildPreview();
        status = "Nuevo reto: arma otra solucion.";
    }

    private void restartCampaignAfterGameOver() {
        GameState.resetAfterGameOver();
        currentLevelIndex = 0;
        showFailModal = false;
        failGameOver = false;
        program.clear();
        executionQueue.clear();
        executionSourceIndexes.clear();
        loadLevel();
        status = "Nueva aventura desde el nivel 1. Tienes 3 vidas.";
    }

    private void tryBuyLife() {
        if (GameState.buyLife()) {
            status = "Compraste 1 vida por " + GameState.LIFE_COST + " creditos. Te quedan "
                    + GameState.lives() + " vidas.";
            mood = RobotMood.IDLE;
            return;
        }
        if (GameState.lives() >= GameState.MAX_LIVES) {
            status = "Ya tienes el maximo de vidas (" + GameState.MAX_LIVES + ").";
            return;
        }
        status = "Necesitas " + GameState.LIFE_COST + " creditos (tienes " + GameState.sessionCoins()
                + "). Recoge estrellas: +" + GameState.CREDITS_PER_STAR + " cada una.";
        mood = RobotMood.IDLE;
    }

    private void showCreditsInfo() {
        status = "Creditos: " + GameState.sessionCoins()
                + ". Estrella = +" + GameState.CREDITS_PER_STAR
                + ". Vida = " + GameState.LIFE_COST + " (toca corazon o cofre).";
        mood = RobotMood.IDLE;
    }

    private void showStarsInfo() {
        status = "Estrellas recogidas: " + GameState.starsEarned() + "/" + GameState.maxStars()
                + ". Cada una suma +" + GameState.CREDITS_PER_STAR + " creditos.";
        mood = RobotMood.IDLE;
    }

    private void showAchievementsInfo() {
        status = "Logros desbloqueados: " + GameState.achievementCount()
                + ". Completa niveles y recoge todas las estrellas.";
        mood = RobotMood.IDLE;
    }

    private void showRobotsInfo() {
        String monoStatus = GameState.isMonoUnlocked()
                ? "Mono desbloqueado."
                : "Mono: " + GameState.MONO_COST + " cred. para desbloquear.";
        status = "Robo (gratis). " + monoStatus + " Pirata (gratis). Activo: " + characterName() + ".";
        mood = RobotMood.IDLE;
    }

    private int sessionCreditsPreview() {
        return GameState.sessionCoins() + GameState.creditsForStars(coinsCollected);
    }

    private void goToMenu() {
        if (game == null) {
            status = "Vuelve al menu desde la pantalla inicial.";
            return;
        }
        Screen old = game.getScreen();
        game.setScreen(new MainMenuScreen(game));
        if (old != null) {
            old.dispose();
        }
    }

    private void addCommand(Command command) {
        if (program.size() >= MAX_PROGRAM_BLOCKS) {
            status = "Tu programa ya tiene " + MAX_PROGRAM_BLOCKS + " bloques.";
            mood = RobotMood.ERROR;
            return;
        }
        program.add(command);
        mood = RobotMood.IDLE;
        activeProgramIndex = program.size() - 1;
        rebuildPreview();
        if (command == Command.TURN_LEFT) {
            status = "Flecha en el mapa: hacia " + directionLabel(previewFacing) + ".";
        } else if (command == Command.TURN_RIGHT) {
            status = "Flecha en el mapa: hacia " + directionLabel(previewFacing) + ".";
        } else if (command == Command.FORWARD) {
            status = "Sigue las flechas blancas del camino azul.";
        } else if (command == Command.USE_SHIP && isShipTutorialLevel()) {
            status = "Perfecto: usar nave solo funciona cuando estoy encima de la NAVE.";
        } else if (command == Command.COLLECT_STAR && isShipTutorialLevel()) {
            status = "Recuerda: recoger estrella solo funciona parado sobre una estrella.";
        } else {
            status = "Bloque agregado: " + command.label + ".";
        }
    }

    private void undoProgram() {
        if (program.isEmpty()) {
            status = "No hay bloques para deshacer.";
            return;
        }
        program.remove(program.size() - 1);
        activeProgramIndex = -1;
        status = "Ultimo bloque retirado.";
        rebuildPreview();
    }

    private void clearProgram() {
        program.clear();
        executionQueue.clear();
        executionSourceIndexes.clear();
        running = false;
        executionIndex = 0;
        activeProgramIndex = -1;
        status = "Programa limpiado.";
        rebuildPreview();
    }

    private int programIndexAt(float x, float y) {
        return programPanelRenderer.programIndexAt(x, y);
    }

    private void rebuildProgramViews() {
        programViews.clear();
        for (Command command : program) {
            programViews.add(new ProgramPanelRenderer.ProgramCommandView(
                    command.label,
                    command.paletteIcon(),
                    programColor(command, 0.92f),
                    command.isRepeatable(),
                    command == Command.REPEAT_PREVIOUS));
        }
        programPanelRenderer.rebuildLayout(programViews, activeProgramIndex);
    }

    private void startRun() {
        if (program.isEmpty()) {
            status = "Primero agrega bloques.";
            mood = RobotMood.ERROR;
            return;
        }
        resetRobotAndRewards();
        buildExecutionQueue();
        running = true;
        activeProgramIndex = -1;
        status = "Ejecutando programa...";
        mood = RobotMood.IDLE;
    }

    private void pauseRun() {
        if (running) {
            running = false;
            status = "Programa pausado. Puedes revisar tus bloques.";
        } else {
            status = "Pulsa Ejecutar para iniciar la secuencia.";
        }
    }

    private void stepOnce() {
        if (program.isEmpty()) {
            status = "Primero agrega bloques.";
            mood = RobotMood.ERROR;
            return;
        }
        if (executionQueue.isEmpty() || executionIndex >= executionQueue.size() || mood == RobotMood.WIN || mood == RobotMood.ERROR) {
            resetRobotAndRewards();
            buildExecutionQueue();
        }
        running = false;
        executeNextCommand();
    }

    private void buildExecutionQueue() {
        executionQueue.clear();
        executionSourceIndexes.clear();
        Command lastRepeatable = null;
        for (int i = 0; i < program.size(); i++) {
            Command command = program.get(i);
            if (command == Command.REPEAT_PREVIOUS) {
                if (lastRepeatable == null) {
                    continue;
                }
                executionQueue.add(lastRepeatable);
                executionSourceIndexes.add(i);
                executionQueue.add(lastRepeatable);
                executionSourceIndexes.add(i);
            } else {
                executionQueue.add(command);
                executionSourceIndexes.add(i);
                if (command.isRepeatable()) {
                    lastRepeatable = command;
                }
            }
        }
        executionIndex = 0;
    }

    private void executeNextCommand() {
        if (executionIndex >= executionQueue.size()) {
            running = false;
            activeProgramIndex = -1;
            tryCompleteLevel();
            return;
        }

        activeProgramIndex = executionSourceIndexes.get(executionIndex);
        Command command = executionQueue.get(executionIndex++);
        if (command == Command.SAY) {
            status = characterName() + " dice: Vamos!";
            mood = RobotMood.IDLE;
            return;
        }
        if (command == Command.COLLECT_STAR) {
            int before = coinsCollected;
            collectCoinIfNeeded();
            status = coinsCollected > before
                    ? "Bien! " + characterName() + " recogio una estrella."
                    : "No hay estrella en esta casilla. Pasa por una estrella y usa recoger estrella.";
            mood = RobotMood.IDLE;
            return;
        }
        if (command == Command.USE_SHIP) {
            if (usingShip) {
                status = "La nave ya esta activa. Sigue el algoritmo.";
                mood = RobotMood.IDLE;
                return;
            }
            if (robotRow == shipRow && robotCol == shipCol) {
                usingShip = true;
                shipBoarded = true;
                if (hasTeleportShip()) {
                    teleportRobotTo(shipDestRow, shipDestCol);
                    status = characterName() + " uso la nave y se teleporto al otro lado del vacio!";
                } else {
                    status = characterName() + " activo la nave espacial.";
                }
                mood = RobotMood.IDLE;
            } else {
                triggerFail("La nave no esta en esta casilla.",
                        hasTeleportShip()
                                ? "Llega a la plataforma NAVE y usa el bloque usar nave para teleportarte."
                                : "Llega a la plataforma de nave y luego usa el bloque usar nave.");
            }
            return;
        }
        if (command == Command.IF_WALL) {
            if (!isWallAhead()) {
                skipNextQueuedCommand("Condicion falsa: no hay pared. Se salta el siguiente bloque.");
                return;
            }
            status = "Condicion verdadera: hay pared adelante. Ejecutando el siguiente bloque.";
            mood = RobotMood.IDLE;
            return;
        }
        if (command == Command.IF_PATH_FREE) {
            if (!isPathFreeAhead()) {
                skipNextQueuedCommand("Condicion falsa: hay pared adelante. Se salta el siguiente bloque.");
                return;
            }
            status = "Condicion verdadera: " + characterName() + " puede avanzar. Ejecutando el siguiente bloque.";
            mood = RobotMood.IDLE;
            return;
        }
        if (command == Command.TURN_LEFT) {
            facing = facing.turnLeft();
            status = characterName() + " giro a la izquierda. Ahora mira hacia " + directionLabel(facing) + ".";
            mood = RobotMood.IDLE;
            return;
        }
        if (command == Command.TURN_RIGHT) {
            facing = facing.turnRight();
            status = characterName() + " giro a la derecha. Ahora mira hacia " + directionLabel(facing) + ".";
            mood = RobotMood.IDLE;
            return;
        }

        Direction nextFacing = facing;
        int nr = robotRow + nextFacing.dr;
        int nc = robotCol + nextFacing.dc;
        if (!isInside(nr, nc) || map[nr][nc] == '#') {
            triggerFail("Ups, " + characterName() + " choco con una pared.", wallCrashAdvice());
            return;
        }
        if (isFlightTile(nr, nc) && !usingShip) {
            triggerFail("Ese tramo necesita la nave.", "Agrega usar nave cuando llegues a la plataforma.");
            return;
        }

        robotFromX = robotX;
        robotFromY = robotY;
        robotRow = nr;
        robotCol = nc;
        robotToX = cellCenterX(robotCol);
        robotToY = cellCenterY(robotRow);
        moveTimer = 0f;
        mood = RobotMood.MOVING;

        if (isOnGoal()) {
            running = false;
            tryCompleteLevel();
        } else if (hasUncollectedStarAt(robotRow, robotCol)) {
            status = characterName() + " avanzo un paso. Hay una estrella aqui: usa recoger estrella.";
        } else {
            status = characterName() + " avanzo un paso.";
        }
    }

    private String successMessage() {
        if (program.contains(Command.USE_SHIP)) {
            if (coinsCollected > 0) {
                return "Excelente! Usaste la nave y recogiste " + coinsCollected + " estrella"
                        + (coinsCollected == 1 ? "" : "s") + ".";
            }
            return "Excelente! Usaste la nave para cruzar el vacio.";
        }
        if (program.contains(Command.REPEAT_PREVIOUS)) {
            return "Excelente! Usaste un bucle para llegar a la meta.";
        }
        if (program.contains(Command.IF_WALL) || program.contains(Command.IF_PATH_FREE)) {
            return "Muy bien! Tus condiciones guiaron bien a " + characterName() + ".";
        }
        if (coinsCollected > 0 && program.contains(Command.COLLECT_STAR)) {
            return "Muy bien! Recogiste " + coinsCollected + " estrella"
                    + (coinsCollected == 1 ? "" : "s") + " del mapa.";
        }
        if (!coins.isEmpty() && coinsCollected == 0) {
            return "Llegaste a la meta. Intenta recoger estrellas para ganar creditos.";
        }
        return "Muy bien! Creaste una secuencia correcta.";
    }

    private void triggerWin() {
        running = false;
        mood = RobotMood.WIN;
        activeProgramIndex = -1;
        showCodeModal = false;
        showFailModal = false;
        showWinModal = true;
        levelRating = computeStarRating();
        lastCreditsEarned = GameState.creditsForStars(coinsCollected);
        winLesson = program.contains(Command.USE_SHIP)
                ? "Combinaste caminar, activar nave y seguir la ruta."
                : program.contains(Command.REPEAT_PREVIOUS)
                ? "Usaste un bucle para ahorrar instrucciones."
                : program.contains(Command.IF_WALL) || program.contains(Command.IF_PATH_FREE)
                ? "Usaste condiciones para decidir cuando avanzar o girar."
                : coinsCollected == 0 && !coins.isEmpty()
                ? "Cada estrella vale " + GameState.CREDITS_PER_STAR + " creditos. Una vida cuesta "
                + GameState.LIFE_COST + "."
                : "Ordenaste una secuencia que llega a la meta.";
        status = successMessage();
        GameState.addCoins(lastCreditsEarned);
        GameState.addStars(coinsCollected);
    }

    private void tryCompleteLevel() {
        if (!isOnGoal()) {
            triggerFail("El programa termino antes de llegar a la meta.", "Agrega mas pasos o cambia la secuencia.");
            return;
        }
        if (levelRequiresShip() && !shipBoarded) {
            triggerFail(
                    "Llegaste cerca, pero este nivel necesita la nave.",
                    hasTeleportShip()
                            ? "Sube a la NAVE, usa usar nave para teleportarte y luego llega a la META."
                            : "Usa el bloque usar nave en la plataforma antes de cruzar el vacio.");
            return;
        }
        triggerWin();
    }

    private void skipNextQueuedCommand(String message) {
        if (executionIndex < executionQueue.size()) {
            executionIndex++;
        }
        activeProgramIndex = -1;
        status = message;
        mood = RobotMood.IDLE;
    }

    private boolean allCoinsCollected() {
        if (coins.isEmpty()) {
            return true;
        }
        for (int i = 0; i < coins.size(); i++) {
            if (!collectedCoins[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean hasUncollectedStarAt(int row, int col) {
        for (int i = 0; i < coins.size(); i++) {
            if (!collectedCoins[i] && coins.get(i).row == row && coins.get(i).col == col) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTeleportShip() {
        return shipDestRow >= 0 && shipDestCol >= 0;
    }

    private void teleportRobotTo(int row, int col) {
        robotFromX = robotX;
        robotFromY = robotY;
        robotRow = row;
        robotCol = col;
        robotToX = cellCenterX(col);
        robotToY = cellCenterY(row);
        robotX = robotToX;
        robotY = robotToY;
        moveTimer = MOVE_TIME;
    }

    private boolean levelRequiresShip() {
        return shipRow >= 0;
    }

    private int computeStarRating() {
        if (coins.isEmpty()) {
            return 3;
        }
        if (allCoinsCollected()) {
            return 3;
        }
        if (coinsCollected > 0) {
            return 2;
        }
        return 1;
    }

    private String wallCrashAdvice() {
        if (!program.contains(Command.IF_WALL) && !program.contains(Command.IF_PATH_FREE)) {
            return "Usa si hay pared o si camino libre antes de avanzar o girar.";
        }
        if (!program.contains(Command.IF_WALL)) {
            return "Prueba si camino libre antes de avanzar, o gira antes de chocar.";
        }
        return "Coloca si hay pared justo antes del giro que evita la pared.";
    }

    private void triggerFail(String reason, String advice) {
        running = false;
        mood = RobotMood.ERROR;
        activeProgramIndex = -1;
        showCodeModal = false;
        showFailModal = true;
        failReason = reason;
        failAdvice = advice;
        int remaining = GameState.loseLife();
        failGameOver = remaining <= 0;
        if (failGameOver) {
            failLivesMessage = "Se acabaron tus vidas. Vuelves al nivel 1 con 3 vidas nuevas.";
            status = "Sin vidas. Reinicia la aventura desde el principio.";
        } else {
            failLivesMessage = "Te quedan " + remaining + " "
                    + (remaining == 1 ? "vida" : "vidas")
                    + ". Sigue en el nivel " + (currentLevelIndex + 1) + ".";
            status = failLivesMessage;
        }
    }

    private String characterName() {
        return GameState.selectedCharacter().displayName;
    }

    private void collectCoinIfNeeded() {
        for (int i = 0; i < coins.size(); i++) {
            if (!collectedCoins[i] && coins.get(i).row == robotRow && coins.get(i).col == robotCol) {
                collectedCoins[i] = true;
                coinsCollected++;
                starCollectAnimator.spawn(cellCenterX(coins.get(i).col), cellCenterY(coins.get(i).row));
            }
        }
    }

    private void rebuildPreview() {
        previewPath.clear();
        previewSteps.clear();
        previewPath.add(new CellPoint(startRow, startCol));
        PreviewCursor cursor = new PreviewCursor(startRow, startCol, Direction.RIGHT, false);
        previewSteps.add(new PathStep(startRow, startCol, cursor.facing, null));
        Command lastRepeatable = null;
        for (int i = 0; i < program.size(); ) {
            Command command = program.get(i);
            if (command == Command.REPEAT_PREVIOUS) {
                if (lastRepeatable != null) {
                    for (int rep = 0; rep < 2; rep++) {
                        applyPreviewCommand(cursor, lastRepeatable);
                    }
                }
                i++;
                continue;
            }
            if (command == Command.IF_WALL || command == Command.IF_PATH_FREE) {
                boolean conditionMet = evaluatePreviewCondition(cursor, command);
                if (conditionMet && i + 1 < program.size()) {
                    Command guarded = program.get(i + 1);
                    if (guarded == Command.REPEAT_PREVIOUS) {
                        if (lastRepeatable != null) {
                            for (int rep = 0; rep < 2; rep++) {
                                applyPreviewCommand(cursor, lastRepeatable);
                            }
                        }
                    } else {
                        applyPreviewCommand(cursor, guarded);
                        if (guarded.isRepeatable()) {
                            lastRepeatable = guarded;
                        }
                    }
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }
            applyPreviewCommand(cursor, command);
            if (command.isRepeatable()) {
                lastRepeatable = command;
            }
            i++;
        }
        if (previewSteps.isEmpty()) {
            previewRow = startRow;
            previewCol = startCol;
            previewFacing = Direction.RIGHT;
        } else {
            PathStep last = previewSteps.get(previewSteps.size() - 1);
            previewRow = last.row;
            previewCol = last.col;
            previewFacing = last.facing;
        }
    }

    private boolean evaluatePreviewCondition(PreviewCursor cursor, Command conditional) {
        int nr = cursor.row + cursor.facing.dr;
        int nc = cursor.col + cursor.facing.dc;
        boolean walkable = isWalkableForPreview(nr, nc, cursor.usingShip);
        if (conditional == Command.IF_WALL) {
            return !walkable;
        }
        return walkable;
    }

    private void applyPreviewCommand(PreviewCursor cursor, Command command) {
        if (command == Command.TURN_LEFT) {
            cursor.facing = cursor.facing.turnLeft();
            previewSteps.add(new PathStep(cursor.row, cursor.col, cursor.facing, Command.TURN_LEFT));
            return;
        }
        if (command == Command.TURN_RIGHT) {
            cursor.facing = cursor.facing.turnRight();
            previewSteps.add(new PathStep(cursor.row, cursor.col, cursor.facing, Command.TURN_RIGHT));
            return;
        }
        if (command == Command.USE_SHIP) {
            if (cursor.row == shipRow && cursor.col == shipCol) {
                cursor.usingShip = true;
                if (hasTeleportShip()) {
                    cursor.row = shipDestRow;
                    cursor.col = shipDestCol;
                    previewPath.add(new CellPoint(cursor.row, cursor.col));
                    previewSteps.add(new PathStep(cursor.row, cursor.col, cursor.facing, Command.USE_SHIP));
                }
            }
            return;
        }
        if (command != Command.FORWARD) {
            return;
        }
        int nr = cursor.row + cursor.facing.dr;
        int nc = cursor.col + cursor.facing.dc;
        if (isWalkableForPreview(nr, nc, cursor.usingShip)) {
            cursor.row = nr;
            cursor.col = nc;
            previewPath.add(new CellPoint(cursor.row, cursor.col));
            previewSteps.add(new PathStep(cursor.row, cursor.col, cursor.facing, null));
        }
    }

    private String directionLabel(Direction direction) {
        return switch (direction) {
            case UP -> "arriba";
            case DOWN -> "abajo";
            case LEFT -> "izquierda";
            case RIGHT -> "derecha";
        };
    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(0.02f, 0.04f, 0.12f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        sr.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        themeRenderer.drawBackground(batch, sr, time);
        blockPalettePanelRenderer.renderBackground(batch, sr, time);
        rebuildProgramViews();
        drawPaletteBlocks();
        drawBoardPanel();
        drawProgramPanel();
        actionButtonsRenderer.render(batch, sr, time, running);
        drawTextLayer();
        HudState hud = buildHudState();
        topHudRenderer.render(sr, batch, hud, time);
        footerHudRenderer.render(sr, batch, hud, guidePortrait, themeAssets.roboMini, time);
        if (showCodeModal) {
            drawCodeModal();
        }
        if (showFailModal) {
            drawFailModal();
        }
        if (showWinModal) {
            drawWinModal();
        }
    }

    private void update(float delta) {
        time += delta;
        starCollectAnimator.update(delta);
        if (moveTimer < MOVE_TIME) {
            moveTimer = Math.min(MOVE_TIME, moveTimer + delta);
            float alpha = Interpolation.sineOut.apply(moveTimer / MOVE_TIME);
            robotX = MathUtils.lerp(robotFromX, robotToX, alpha);
            robotY = MathUtils.lerp(robotFromY, robotToY, alpha);
            if (moveTimer >= MOVE_TIME && mood == RobotMood.MOVING) {
                mood = RobotMood.IDLE;
            }
        }

        if (running) {
            runTimer += delta;
            if (runTimer >= RUN_STEP_TIME) {
                runTimer = 0f;
                executeNextCommand();
            }
        }
    }

    private void drawBackground() {
        float driftX = MathUtils.sin(time * 0.10f) * 12f;
        float driftY = MathUtils.cos(time * 0.08f) * 8f;
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(spaceBackground, -12f + driftX, -8f + driftY, W + 24f, H + 16f);
        batch.setColor(0.65f, 0.82f, 1f, 0.10f);
        batch.draw(spaceBackground, -26f - driftX * 0.3f, -16f - driftY * 0.3f, W + 52f, H + 32f);
        batch.setColor(WHITE);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.03f, 0.13f, 0.18f);
        sr.rect(0, 0, W, H);
        drawSpaceDots();
        sr.end();
    }

    private void drawSpaceDots() {
        for (int i = 0; i < 42; i++) {
            float x = 26f + ((i * 157f) % 1220f);
            float y = 22f + ((i * 91f) % 660f);
            y = y - ((time * (5f + i % 6)) % 760f);
            if (y < -18f) {
                y += 760f;
            }
            float pulse = 0.50f + 0.50f * MathUtils.sin(time * 1.8f + i * 0.72f);
            sr.setColor(0.62f, 0.90f, 1f, 0.22f + pulse * 0.34f);
            sr.circle(x, y, 1.5f + (i % 4) * 0.55f);
        }
    }

    private void drawTopBar() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawRoundRect(0, 656, W, 64, 0, new Color(0.00f, 0.24f, 0.68f, 0.92f));
        drawRoundRect(318, 664, 444, 48, 22, new Color(0.83f, 0.94f, 1f, 0.94f));
        drawRoundRect(910, 664, 250, 48, 22, new Color(0.01f, 0.16f, 0.43f, 0.92f));
        drawRoundRect(1184, 664, 62, 48, 18, PURPLE);
        sr.end();
    }

    private HudState buildHudState() {
        LevelData level = currentLevel();
        String title = level.title;
        int colon = title.indexOf(':');
        String missionTitle = colon >= 0 ? title.substring(colon + 1).trim() : title;
        return new HudState(
                currentLevelIndex + 1,
                missionTitle,
                level.goal,
                GameState.starsEarned(),
                GameState.maxStars(),
                sessionCreditsPreview(),
                GameState.lives(),
                roboDialogueMessage(),
                characterName(),
                GameState.achievementCount(),
                GameState.robotsUnlocked(),
                GameState.creditsForStars(coinsCollected),
                GameState.LIFE_COST,
                GameState.canBuyLife());
    }

    private String mapSpeechMessage() {
        if (running || showWinModal || showFailModal) {
            return "";
        }
        if (isShipTutorialLevel()) {
            return shipLevelMapHint();
        }
        if (program.isEmpty()) {
            return "¡Vamos! Recolectemos estrellas";
        }
        return "";
    }

    private String roboDialogueMessage() {
        if (showWinModal) {
            return successMessage();
        }
        if (showFailModal) {
            return failGameOver
                    ? "Sin vidas... pero aprendimos algo. Reiniciemos la aventura."
                    : failLivesMessage;
        }
        if (running) {
            return "Ejecutando tu programa paso a paso...";
        }
        if (shouldShowShipLevelTutorial()) {
            return shipLevelTutorialMessage();
        }
        if (program.isEmpty()) {
            return isShipTutorialLevel()
                    ? "Camino marron → NAVE → usar nave."
                    : "Arma tu programa con bloques y recoge estrellas.";
        }
        return status;
    }

    private void drawPaletteBlocks() {
        batch.begin();
        for (CommandButton item : palette) {
            blockPalettePanelRenderer.renderPaletteBlock(batch, item.rect, item.icon, item.label, smallFont);
        }
        batch.end();
        blockPalettePanelRenderer.renderCategoryLabels(batch, smallFont);
    }

    private void drawProgramPanel() {
        programPanelRenderer.renderBackground(batch, sr);
        programPanelRenderer.renderChips(batch, viewport.getCamera(), programViews);
        programPanelRenderer.renderChipLabels(batch, viewport.getCamera());
        programPanelRenderer.renderCodeButton(batch, expandCodeButton);
    }

    private void drawBoardPanel() {
        gameBoardRenderer.renderFrame(batch, sr, time);
        gameBoardRenderer.renderGridTiles(batch, map, mapRows, mapCols, boardX, boardY, cellSize);
        gameBoardRenderer.renderGridProps(batch, map, mapRows, mapCols, boardX, boardY, cellSize, time);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawPreviewRoute();
        sr.end();

        drawStartAndGoal();
        drawCoins();
        for (StarCollectAnimator.Burst burst : starCollectAnimator.bursts()) {
            gameBoardRenderer.renderStarCollectBurst(batch, sr, burst.x, burst.y, burst.progress(), YELLOW);
        }

        drawShipOnBoard();
        if (!running && !program.isEmpty()) {
            drawMapGuideOverlay();
        }
        drawRobotOnBoard();
        String mapBubble = mapSpeechMessage();
        mapSpeechBubbleRenderer.render(batch, robotX, robotY, mapBubble, time, !mapBubble.isEmpty());
        drawRobotDirectionBadge();
        drawGoalMarker();
    }

    private void drawGoalMarker() {
        float gx = cellCenterX(goalCol);
        float gy = cellCenterY(goalRow);
        float pulse = 0.92f + MathUtils.sin(time * 2.4f) * 0.08f;
        float bob = MathUtils.sin(time * 2.2f) * 3f;
        batch.begin();
        batch.setColor(1f, 0.95f, 0.45f, 0.35f * pulse);
        float glow = 52f * pulse;
        batch.draw(themeAssets.starGlow, gx - glow / 2f, gy - glow / 2f + bob, glow, glow);
        batch.setColor(Color.WHITE);
        float trophy = 40f * pulse;
        batch.draw(themeAssets.iconTrophyGold, gx - trophy / 2f, gy - trophy / 2f + bob, trophy, trophy);
        batch.draw(themeAssets.labelMeta, gx - 34f, gy + 22f + bob, 68f, 24f);
        batch.end();
    }

    private void drawMapGuideOverlay() {
        batch.begin();
        batch.setColor(Color.WHITE);
        for (int i = 1; i < previewSteps.size(); i++) {
            PathStep step = previewSteps.get(i);
            if (step.row == robotRow && step.col == robotCol) {
                continue;
            }
            drawCellDirectionArrow(step.row, step.col, step.facing);
        }
        batch.end();
    }

    private void drawCellDirectionArrow(int row, int col, Direction direction) {
        float cx = cellCenterX(col);
        float cy = cellCenterY(row);
        float rotation = switch (direction) {
            case RIGHT -> 0f;
            case UP -> 90f;
            case LEFT -> 180f;
            case DOWN -> 270f;
        };
        batch.draw(new TextureRegion(themeAssets.pathArrowPreview),
                cx - 17f, cy - 17f, 17f, 17f, 34f, 34f, 1f, 1f, rotation);
    }

    private void drawRobotDirectionBadge() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float badgeX = robotX;
        float badgeY = robotY + 46f;
        sr.setColor(0.06f, 0.28f, 0.62f, 0.85f);
        sr.circle(badgeX, badgeY, 14f);
        sr.setColor(1f, 0.96f, 0.35f, 1f);
        drawDirectionArrow(badgeX, badgeY, facing, new Color(1f, 0.96f, 0.35f, 1f), 12f, false);
        sr.end();
    }

    private void drawPanelEdge(float x, float y, float w, float h, float radius) {
        sr.setColor(CYAN.r, CYAN.g, CYAN.b, 0.42f);
        sr.rect(x + radius, y + h - 3f, w - 2f * radius, 3f);
        sr.rect(x + radius, y, w - 2f * radius, 3f);
        sr.rect(x, y + radius, 3f, h - 2f * radius);
        sr.rect(x + w - 3f, y + radius, 3f, h - 2f * radius);
    }

    private void drawGrid() {
        for (int r = 0; r < mapRows; r++) {
            for (int c = 0; c < mapCols; c++) {
                float x = boardX + c * cellSize;
                float y = boardY + (mapRows - 1 - r) * cellSize;
                if (map[r][c] == '#') {
                    sr.setColor(0f, 0.02f, 0.08f, 0.28f);
                    drawRoundRect(x + 3, y + 1, cellSize - 6, cellSize - 6, 5, new Color(0f, 0.02f, 0.08f, 0.28f));
                    drawRoundRect(x + 4, y + 4, cellSize - 8, cellSize - 8, 6, GRASS_DARK);
                    drawRoundRect(x + 7, y + 9, cellSize - 14, cellSize - 13, 5, GRASS);
                    sr.setColor(0.56f, 0.96f, 0.25f, 0.88f);
                    sr.rect(x + 10, y + cellSize - 12, cellSize - 20, 4);
                } else if (isFlightTile(r, c)) {
                    drawRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 5, new Color(0.04f, 0.08f, 0.28f, 0.96f));
                    sr.setColor(0.10f, 0.74f, 0.96f, 0.20f + MathUtils.sin(time * 2.4f + r + c) * 0.05f);
                    sr.circle(x + cellSize / 2f, y + cellSize / 2f, cellSize * 0.38f);
                    sr.setColor(0.64f, 0.96f, 1f, 0.56f);
                    sr.rect(x + 10f, y + cellSize - 13f, cellSize - 20f, 3f);
                    sr.setColor(0.42f, 0.22f, 0.92f, 0.46f);
                    sr.rect(x + 9f, y + 9f, cellSize - 18f, 2f);
                } else {
                    drawRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 5, new Color(0.94f, 0.82f, 0.55f, 0.94f));
                    sr.setColor(SAND);
                    sr.rect(x + 7, y + 7, cellSize - 14, cellSize - 14);
                    sr.setColor(0.04f, 0.32f, 0.45f, 0.18f);
                    sr.rect(x + 8, y + 8, cellSize - 16, 2);
                    sr.setColor(1f, 1f, 1f, 0.20f);
                    sr.rect(x + 8, y + cellSize - 10, cellSize - 16, 2);
                }
            }
        }
    }

    private void drawPreviewRoute() {
        if (previewPath.size() < 2) {
            return;
        }
        sr.setColor(0.22f, 0.90f, 1f, 0.26f);
        for (int i = 1; i < previewPath.size(); i++) {
            CellPoint a = previewPath.get(i - 1);
            CellPoint b = previewPath.get(i);
            sr.rectLine(cellCenterX(a.col), cellCenterY(a.row), cellCenterX(b.col), cellCenterY(b.row), 8f);
        }
        for (CellPoint p : previewPath) {
            float x = cellCenterX(p.col);
            float y = cellCenterY(p.row);
            sr.setColor(0.04f, 0.28f, 0.92f, 0.90f);
            sr.circle(x, y, 5f);
            sr.setColor(0.68f, 0.96f, 1f, 0.92f);
            sr.circle(x, y, 2.4f);
        }
    }

    private void drawDirectionArrow(float cx, float cy, Direction direction, Color color, float length, boolean emphasized) {
        if (emphasized) {
            sr.setColor(color.r, color.g, color.b, 0.40f);
            sr.circle(cx, cy, length + 10f);
        }
        sr.setColor(color);
        float shaft = length * 0.58f;
        float head = length * 0.42f;
        float thickness = emphasized ? 9f : 7f;
        switch (direction) {
            case RIGHT -> {
                sr.rect(cx - shaft * 0.25f, cy - thickness / 2f, shaft + head * 0.35f, thickness);
                sr.triangle(cx + shaft + head, cy, cx + shaft * 0.15f, cy + 11f, cx + shaft * 0.15f, cy - 11f);
            }
            case LEFT -> {
                sr.rect(cx - shaft - head * 0.35f, cy - thickness / 2f, shaft + head * 0.35f, thickness);
                sr.triangle(cx - shaft - head, cy, cx - shaft * 0.15f, cy + 11f, cx - shaft * 0.15f, cy - 11f);
            }
            case UP -> {
                sr.rect(cx - thickness / 2f, cy - shaft * 0.25f, thickness, shaft + head * 0.35f);
                sr.triangle(cx, cy + shaft + head, cx - 11f, cy + shaft * 0.15f, cx + 11f, cy + shaft * 0.15f);
            }
            case DOWN -> {
                sr.rect(cx - thickness / 2f, cy - shaft - head * 0.35f, thickness, shaft + head * 0.35f);
                sr.triangle(cx, cy - shaft - head, cx - 11f, cy - shaft * 0.15f, cx + 11f, cy - shaft * 0.15f);
            }
        }
    }

    private void drawCoins() {
        for (int i = 0; i < coins.size(); i++) {
            if (collectedCoins[i]) {
                continue;
            }
            CellPoint p = coins.get(i);
            float x = cellCenterX(p.col);
            float y = cellCenterY(p.row);
            float pulse = 1f + MathUtils.sin(time * 2.6f + i) * 0.14f;
            gameBoardRenderer.renderStar(batch, sr, x, y, new Color(1f, 0.96f, 0.62f, 1f), pulse, time, i);
        }
    }

    private void drawStartAndGoal() {
        float sx = cellCenterX(startCol);
        float sy = cellCenterY(startRow);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(themeAssets.labelInicio, sx - 36f, sy + 16f, 72f, 24f);
        batch.end();
    }

    private void drawTextLayer() {
        batch.begin();
        drawBoardLabels();
        batch.end();
    }

    private void drawPaletteCategories() {
        smallFont.setColor(0.64f, 0.93f, 1f, 1f);
        smallFont.draw(batch, "Movimiento", LEFT_X + 28, 602);
        smallFont.draw(batch, "Control", LEFT_X + 28, 450);
        smallFont.draw(batch, "Logica", LEFT_X + 28, 382);
        smallFont.draw(batch, "Accion", LEFT_X + 28, 272);
    }

    private void drawCodeModal() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.01f, 0.05f, 0.68f);
        sr.rect(0, 0, W, H);
        drawRoundRect(MODAL_X + 8f, MODAL_Y - 8f, MODAL_W, MODAL_H, 26, new Color(0f, 0f, 0f, 0.42f));
        drawRoundRect(MODAL_X, MODAL_Y, MODAL_W, MODAL_H, 26, new Color(0.02f, 0.08f, 0.24f, 0.96f));
        drawRoundRect(MODAL_X + 22f, MODAL_Y + 78f, 470f, 404f, 18, new Color(0.01f, 0.04f, 0.14f, 0.92f));
        drawRoundRect(MODAL_X + 516f, MODAL_Y + 78f, 286f, 404f, 18, new Color(0.03f, 0.15f, 0.32f, 0.86f));
        drawRoundRect(closeCodeButton.x, closeCodeButton.y, closeCodeButton.width, closeCodeButton.height, 12, new Color(0.50f, 0.24f, 0.88f, 1f));
        drawPanelEdge(MODAL_X, MODAL_Y, MODAL_W, MODAL_H, 26);
        sr.setColor(CYAN.r, CYAN.g, CYAN.b, 0.78f);
        sr.rect(MODAL_X + 34f, MODAL_Y + MODAL_H - 88f, MODAL_W - 68f, 3f);
        sr.end();

        batch.begin();
        titleFont.setColor(WHITE);
        titleFont.draw(batch, "Codigo de Robo expandido", MODAL_X + 34f, MODAL_Y + MODAL_H - 34f);
        smallFont.setColor(0.70f, 0.92f, 1f, 1f);
        smallFont.draw(batch, "Aqui ves como tus bloques se convierten en un algoritmo paso a paso.", MODAL_X + 36f, MODAL_Y + MODAL_H - 64f);
        smallFont.setColor(WHITE);
        drawCentered(smallFont, "Cerrar", closeCodeButton.x, closeCodeButton.y + 1f, closeCodeButton.width, 18f);

        font.setColor(WHITE);
        font.draw(batch, "Vista tipo programacion", MODAL_X + 42f, MODAL_Y + 456f);
        font.draw(batch, "Explicacion", MODAL_X + 536f, MODAL_Y + 456f);
        drawExpandedCodeLines();
        drawCodeExplanationLines();
        batch.end();
    }

    private void drawFailModal() {
        float floatY = MathUtils.sin(time * 2.4f) * 7f;
        float x = FAIL_MODAL_X;
        float y = FAIL_MODAL_Y + floatY;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.01f, 0.05f, 0.62f);
        sr.rect(0, 0, W, H);
        drawFailButton(failRetryButton, new Color(0.08f, 0.58f, 0.95f, 1f), floatY);
        drawFailButton(failEditButton, new Color(0.48f, 0.25f, 0.90f, 1f), floatY);
        drawFailButton(failMenuButton, new Color(0.16f, 0.28f, 0.55f, 1f), floatY);
        sr.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(themeAssets.modalFailPanel, x, y, FAIL_MODAL_W, FAIL_MODAL_H);
        batch.draw(currentRobotTexture(), x + 198f, y + 176f, 104f, 104f);
        titleFont.setColor(WHITE);
        drawCentered(titleFont, failGameOver ? "Sin vidas" : "Intento fallido",
                x + 60f, y + 292f, FAIL_MODAL_W - 120f, 34f);
        font.setColor(new Color(1f, 0.86f, 0.62f, 1f));
        drawCentered(font, failReason, x + 54f, y + 158f, FAIL_MODAL_W - 108f, 30f);
        smallFont.setColor(new Color(0.82f, 0.95f, 1f, 1f));
        drawCentered(smallFont, failAdvice, x + 58f, y + 132f, FAIL_MODAL_W - 116f, 24f);
        smallFont.setColor(new Color(1f, 0.92f, 0.55f, 1f));
        drawCentered(smallFont, failLivesMessage, x + 58f, y + 108f, FAIL_MODAL_W - 116f, 24f);
        smallFont.setColor(new Color(0.78f, 0.94f, 1f, 1f));
        drawCentered(smallFont, failGameOver
                        ? "No te rindas: repasa los bloques y vuelve al nivel 1."
                        : "Corrige tu algoritmo y sigue jugando.",
                x + 64f, y + 84f, FAIL_MODAL_W - 128f, 22f);
        smallFont.setColor(WHITE);
        drawCentered(smallFont, failGameOver ? "Reiniciar" : "Seguir",
                failRetryButton.x, failRetryButton.y + floatY + 4f, failRetryButton.width, 18f);
        drawCentered(smallFont, failGameOver ? "Menu" : "Editar",
                failEditButton.x, failEditButton.y + floatY + 4f, failEditButton.width, 18f);
        drawCentered(smallFont, "Menu", failMenuButton.x, failMenuButton.y + floatY + 4f, failMenuButton.width, 18f);
        batch.end();
    }

    private void drawFailButton(Rectangle r, Color color, float offsetY) {
        drawRoundRect(r.x, r.y + offsetY - 5f, r.width, r.height, 14, new Color(0f, 0.03f, 0.12f, 0.42f));
        drawRoundRect(r.x, r.y + offsetY, r.width, r.height, 14, color);
        sr.setColor(Math.min(1f, color.r + 0.16f), Math.min(1f, color.g + 0.16f), Math.min(1f, color.b + 0.16f), 0.92f);
        sr.rect(r.x + 10f, r.y + offsetY + r.height - 8f, r.width - 20f, 4f);
    }

    private void drawWinModal() {
        float floatY = MathUtils.sin(time * 2.2f) * 7f;
        float x = FAIL_MODAL_X;
        float y = FAIL_MODAL_Y + floatY;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0.01f, 0.05f, 0.58f);
        sr.rect(0, 0, W, H);
        drawCelebrationStars(x, y);
        drawFailButton(winNewButton, new Color(0.08f, 0.66f, 0.96f, 1f), floatY);
        drawFailButton(winPracticeButton, new Color(0.17f, 0.74f, 0.34f, 1f), floatY);
        drawFailButton(winMenuButton, new Color(0.48f, 0.25f, 0.90f, 1f), floatY);
        sr.end();

        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(themeAssets.modalWinPanel, x, y, FAIL_MODAL_W, FAIL_MODAL_H);
        batch.draw(themeAssets.bannerVictory, x + 72f, y + FAIL_MODAL_H - 72f, FAIL_MODAL_W - 144f, 42f);
        batch.draw(roboWin, x + 190f, y + 166f, 120f, 120f);
        titleFont.setColor(WHITE);
        drawCentered(titleFont, "Mision completada!", x + 42f, y + 294f, FAIL_MODAL_W - 84f, 34f);
        font.setColor(new Color(1f, 0.92f, 0.48f, 1f));
        drawCentered(font, successMessage(), x + 50f, y + 144f, FAIL_MODAL_W - 100f, 30f);
        smallFont.setColor(new Color(0.82f, 1f, 0.88f, 1f));
        drawCentered(smallFont, winLesson, x + 58f, y + 118f, FAIL_MODAL_W - 116f, 24f);
        smallFont.setColor(new Color(0.82f, 0.95f, 1f, 1f));
        drawCentered(smallFont,
                "Creditos: +" + lastCreditsEarned + " | Estrellas: " + coinsCollected + "/" + coins.size()
                        + " | Nota: " + levelRating + "/3",
                x + 64f, y + 96f, FAIL_MODAL_W - 128f, 22f);
        smallFont.setColor(WHITE);
        drawCentered(smallFont, hasNextLevel() ? "Siguiente" : "Nuevo", winNewButton.x, winNewButton.y + floatY + 4f, winNewButton.width, 18f);
        drawCentered(smallFont, "Practicar", winPracticeButton.x, winPracticeButton.y + floatY + 4f, winPracticeButton.width, 18f);
        drawCentered(smallFont, "Menu", winMenuButton.x, winMenuButton.y + floatY + 4f, winMenuButton.width, 18f);
        batch.end();
    }

    private void drawCelebrationStars(float x, float y) {
        for (int i = 0; i < 12; i++) {
            float angle = i * MathUtils.PI2 / 12f + time * 0.35f;
            float radius = 118f + MathUtils.sin(time * 2f + i) * 12f;
            float sx = x + 250f + MathUtils.cos(angle) * radius;
            float sy = y + 222f + MathUtils.sin(angle) * radius * 0.72f;
            float size = 8f + (i % 3) * 3f;
            drawStar(sx, sy, size, i % 2 == 0 ? YELLOW : CYAN);
        }
    }

    private void drawExpandedCodeLines() {
        List<String> lines = buildCodeLines();
        float y = MODAL_Y + 426f;
        for (String line : lines) {
            if (y < MODAL_Y + 104f) {
                modalCodeFont.setColor(0.70f, 0.92f, 1f, 1f);
                modalCodeFont.draw(batch, "...", MODAL_X + 50f, y);
                break;
            }
            if (line.contains("//")) {
                modalCodeFont.setColor(0.48f, 0.78f, 1f, 1f);
            } else if (line.contains("if")) {
                modalCodeFont.setColor(0.95f, 0.72f, 1f, 1f);
            } else if (line.contains("repetir")) {
                modalCodeFont.setColor(1f, 0.82f, 0.35f, 1f);
            } else if (line.startsWith("}")) {
                modalCodeFont.setColor(0.62f, 0.92f, 1f, 1f);
            } else {
                modalCodeFont.setColor(0.92f, 0.98f, 1f, 1f);
            }
            modalCodeFont.draw(batch, line, MODAL_X + 48f, y);
            y -= 24f;
        }
    }

    private void drawCodeExplanationLines() {
        List<String> lines = buildExplanationLines();
        float y = MODAL_Y + 420f;
        for (String line : lines) {
            if (y < MODAL_Y + 104f) {
                break;
            }
            if (line.startsWith("*")) {
                smallFont.setColor(1f, 0.88f, 0.42f, 1f);
            } else {
                smallFont.setColor(0.82f, 0.95f, 1f, 1f);
            }
            smallFont.draw(batch, line, MODAL_X + 536f, y);
            y -= 25f;
        }
    }

    private List<String> buildExplanationLines() {
        List<String> lines = new ArrayList<>();
        lines.add("* Secuencia");
        lines.add("Robo ejecuta cada linea");
        lines.add("en orden, de arriba abajo.");
        lines.add("");
        lines.add("* Direccion");
        lines.add("girar cambia hacia donde");
        lines.add("mira Robo; avanzar usa");
        lines.add("esa direccion.");
        lines.add("");
        lines.add("* Bucles");
        lines.add("repetir(3) ahorra bloques");
        lines.add("repitiendo el paso anterior.");
        lines.add("");
        lines.add("* Nave");
        lines.add("usarNave() activa rutas");
        lines.add("que no se cruzan caminando.");
        lines.add("");
        lines.add("* Flechas del mapa");
        lines.add("cada casilla muestra");
        lines.add("arriba, abajo, izq o der.");
        lines.add("");
        lines.add("* Condiciones");
        lines.add("si hay pared / si camino libre");
        lines.add("controlan el bloque siguiente.");
        lines.add("");
        lines.add("* Estrellas");
        lines.add("recoger estrella solo funciona");
        lines.add("cuando estas encima de una.");
        return lines;
    }

    private List<String> buildCodeLines() {
        List<String> lines = new ArrayList<>();
        lines.add("programa Robo {");
        if (program.isEmpty()) {
            lines.add("  // toca bloques");
            lines.add("  // para crear pasos");
            lines.add("}");
            return lines;
        }

        int lineNumber = 1;
        Command lastRepeatable = null;
        for (Command command : program) {
            if (command == Command.REPEAT_PREVIOUS) {
                if (lastRepeatable == null) {
                    lines.add(twoDigits(lineNumber++) + "  repetir(3) { }");
                } else {
                    lines.add(twoDigits(lineNumber++) + "  repetir(3) {");
                    lines.add("      " + codeFor(lastRepeatable));
                    lines.add("    }");
                }
                continue;
            }

            lines.add(twoDigits(lineNumber++) + "  " + codeFor(command));
            if (command.isRepeatable()) {
                lastRepeatable = command;
            }
        }
        lines.add("}");
        return lines;
    }

    private String twoDigits(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    private String codeFor(Command command) {
        return switch (command) {
            case FORWARD -> "Robo.avanzar();";
            case TURN_LEFT -> "Robo.girarIzquierda();";
            case TURN_RIGHT -> "Robo.girarDerecha();";
            case IF_WALL -> "if (Robo.hayPared()) { /* siguiente bloque */ }";
            case IF_PATH_FREE -> "if (Robo.caminoLibre()) { /* siguiente bloque */ }";
            case SAY -> "Robo.decir(\"Vamos!\");";
            case COLLECT_STAR -> "Robo.recogerEstrella();";
            case USE_SHIP -> "Robo.usarNave();";
            case REPEAT_PREVIOUS -> "repetir(3) { }";
        };
    }

    private void drawBoardLabels() {
        if (shipRow >= 0 && !shipBoarded) {
            smallFont.setColor(new Color(0.78f, 1f, 1f, 1f));
            drawCentered(smallFont, "NAVE", cellCenterX(shipCol) - 34f, cellCenterY(shipRow) - 40f, 68f, 22f);
        }
        if (hasTeleportShip()) {
            smallFont.setColor(new Color(0.55f, 1f, 0.85f, 1f));
            drawCentered(smallFont, "LLEGADA", cellCenterX(shipDestCol) - 38f, cellCenterY(shipDestRow) - 40f, 76f, 22f);
        }
        smallFont.setColor(new Color(1f, 0.92f, 0.35f, 1f));
        drawCentered(smallFont, "META", cellCenterX(goalCol) - 28f, cellCenterY(goalRow) - 52f, 56f, 20f);
    }

    private void drawGuideText() {
        float x = 908f;
        float y = 20f;
        batch.draw(guidePortrait, x, y - 10f, 100, 100);

        font.setColor(WHITE);
        drawCentered(font, status, 320, 48, 492, 34);
        smallFont.setColor(0.62f, 0.86f, 1f, 0.92f);
        drawCentered(smallFont, "Objetivo: " + currentLevel().goal, 320, 24, 492, 22);
    }

    private void drawShipOnBoard() {
        batch.begin();
        batch.setColor(Color.WHITE);
        if (shipRow >= 0 && !shipBoarded) {
            drawShipSpriteAt(shipRow, shipCol, 1f);
        }
        if (hasTeleportShip()) {
            batch.setColor(0.75f, 1f, 0.92f, 0.95f);
            drawShipSpriteAt(shipDestRow, shipDestCol, 0.88f);
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawShipSpriteAt(int row, int col, float scale) {
        float x = cellCenterX(col);
        float y = cellCenterY(row);
        float pulse = MathUtils.sin(time * 2.2f + row + col) * 4f;
        float w = 88f * scale;
        float h = 62f * scale;
        batch.draw(shipTexture, x - w / 2f, y - h / 2f + pulse, w, h);
    }

    private void drawRobotOnBoard() {
        Texture robot = currentRobotTexture();
        float size = mood == RobotMood.WIN ? 92f : usingShip ? 58f : 76f;
        float bounce = mood == RobotMood.IDLE ? MathUtils.sin(time * 2.4f) * 3f : 0f;
        float winScale = mood == RobotMood.WIN ? 1f + MathUtils.sin(time * 4f) * 0.06f : 1f;
        float blinkCycle = time % 4.2f;
        float alpha = mood == RobotMood.IDLE && blinkCycle > 3.75f && blinkCycle < 3.92f ? 0.78f : 1f;

        batch.begin();
        if (usingShip) {
            batch.setColor(WHITE);
            batch.draw(shipTexture, robotX - 48f, robotY - 28f + bounce, 96f, 66f);
        }
        batch.setColor(1f, 1f, 1f, alpha);
        float drawSize = size * winScale;
        batch.draw(robot, robotX - drawSize / 2f, robotY - drawSize / 2f + 10f + bounce, drawSize, drawSize);
        batch.setColor(WHITE);
        batch.end();
    }

    private Texture currentRobotTexture() {
        if (mood == RobotMood.ERROR) {
            return roboError;
        }
        if (mood == RobotMood.WIN) {
            return roboWin;
        }
        if (mood == RobotMood.MOVING) {
            if (facing == Direction.LEFT) {
                return roboLeft;
            }
            if (facing == Direction.RIGHT) {
                return roboRight;
            }
            if (facing == Direction.UP) {
                return roboUp;
            }
            return roboDown;
        }
        if (facing == Direction.UP) {
            return roboBack;
        }
        if (facing == Direction.DOWN) {
            return roboDown;
        }
        if (facing == Direction.LEFT) {
            return roboLeft;
        }
        if (facing == Direction.RIGHT) {
            return roboRight;
        }
        return roboIdle;
    }

    private void drawCommandBlock(Rectangle r, Color color) {
        drawRoundRect(r.x, r.y - 4, r.width, r.height, 12, new Color(0f, 0.09f, 0.23f, 0.35f));
        drawRoundRect(r.x, r.y, r.width, r.height, 12, color);
        sr.setColor(Math.min(1f, color.r + 0.15f), Math.min(1f, color.g + 0.15f), Math.min(1f, color.b + 0.15f), 1f);
        sr.rect(r.x + 8, r.y + r.height - 8, r.width - 16, 4);
    }

    private void drawCommandIcon(CommandButton item) {
        Rectangle r = item.rect;
        sr.setColor(WHITE);
        float cx = r.x + 38f;
        float iconX = r.x + 42f;
        float cy = r.y + r.height / 2f;
        switch (item.icon) {
            case "forward" -> drawDirectionArrow(iconX, cy, Direction.RIGHT, WHITE, 17f, false);
            case "turn_left" -> drawDirectionArrow(iconX, cy, Direction.UP, WHITE, 17f, false);
            case "turn_right" -> drawDirectionArrow(iconX, cy, Direction.DOWN, WHITE, 17f, false);
            case "loop" -> {
                sr.circle(cx, cy, 15);
                sr.setColor(item.color);
                sr.circle(cx + 4, cy, 9);
                sr.setColor(WHITE);
                sr.triangle(cx - 8, cy + 12, cx - 21, cy + 7, cx - 11, cy - 1);
            }
            case "wall" -> {
                sr.rect(cx - 14, cy - 12, 8, 24);
                sr.rect(cx - 3, cy - 12, 8, 24);
                sr.rect(cx + 8, cy - 12, 8, 24);
            }
            case "path" -> {
                sr.rect(cx - 16, cy - 3, 30, 6);
                sr.triangle(cx + 20, cy, cx + 8, cy + 10, cx + 8, cy - 10);
            }
            case "star" -> drawStar(cx, cy, 13, WHITE);
            case "ship" -> {
                sr.triangle(cx + 18, cy, cx - 14, cy + 12, cx - 8, cy);
                sr.triangle(cx + 18, cy, cx - 14, cy - 12, cx - 8, cy);
                sr.rect(cx - 10, cy - 5, 18, 10);
                sr.setColor(item.color);
                sr.circle(cx + 1, cy, 4);
            }
            default -> {
                drawRoundRect(cx - 16, cy - 12, 32, 24, 8, WHITE);
                sr.setColor(item.color);
                sr.triangle(cx - 4, cy - 12, cx + 4, cy - 12, cx, cy - 18);
            }
        }
    }

    private Color programColor(Command command, float alpha) {
        if (command == Command.REPEAT_PREVIOUS) {
            return new Color(ORANGE.r, ORANGE.g, ORANGE.b, alpha);
        }
        if (command == Command.IF_WALL || command == Command.IF_PATH_FREE) {
            return new Color(PURPLE.r, PURPLE.g, PURPLE.b, alpha);
        }
        if (command == Command.SAY || command == Command.COLLECT_STAR || command == Command.USE_SHIP) {
            return new Color(GREEN.r, GREEN.g, GREEN.b, alpha);
        }
        return new Color(BLUE.r, BLUE.g, BLUE.b, alpha);
    }

    private void drawBigButton(Rectangle r, Color color) {
        drawRoundRect(r.x, r.y - 6, r.width, r.height, 18, new Color(0f, 0.06f, 0.22f, 0.42f));
        drawRoundRect(r.x, r.y, r.width, r.height, 18, color);
        sr.setColor(Math.min(1f, color.r + 0.12f), Math.min(1f, color.g + 0.12f), Math.min(1f, color.b + 0.12f), 1f);
        sr.rect(r.x + 10, r.y + r.height - 10, r.width - 20, 5);
    }

    private void drawPlayIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.triangle(r.x + 62, r.y + 46, r.x + 62, r.y + 22, r.x + 92, r.y + 34);
    }

    private void drawStepIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.rect(r.x + 54, r.y + 24, 10, 24);
        sr.triangle(r.x + 72, r.y + 48, r.x + 72, r.y + 24, r.x + 96, r.y + 36);
    }

    private void drawPauseIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.rect(r.x + 50, r.y + 18, 10, 24);
        sr.rect(r.x + 68, r.y + 18, 10, 24);
    }

    private void drawUndoIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.circle(r.x + 80, r.y + 38, 20);
        sr.setColor(0.57f, 0.30f, 0.94f, 1f);
        sr.circle(r.x + 86, r.y + 38, 13);
        sr.setColor(WHITE);
        sr.triangle(r.x + 63, r.y + 54, r.x + 48, r.y + 42, r.x + 66, r.y + 37);
    }

    private void drawTrashIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.rect(r.x + 64, r.y + 24, 34, 30);
        sr.rect(r.x + 59, r.y + 54, 44, 5);
        sr.rect(r.x + 73, r.y + 60, 16, 4);
        sr.setColor(ORANGE);
        sr.rect(r.x + 72, r.y + 30, 4, 18);
        sr.rect(r.x + 84, r.y + 30, 4, 18);
    }

    private void drawResetIcon(Rectangle r) {
        sr.setColor(WHITE);
        sr.circle(r.x + 80, r.y + 38, 20);
        sr.setColor(0.17f, 0.65f, 0.98f, 1f);
        sr.circle(r.x + 80, r.y + 38, 13);
        sr.setColor(WHITE);
        sr.triangle(r.x + 69, r.y + 58, r.x + 52, r.y + 54, r.x + 64, r.y + 42);
    }

    private void drawRoundRect(float x, float y, float w, float h, float radius, Color color) {
        sr.setColor(color);
        if (radius <= 0f) {
            sr.rect(x, y, w, h);
            return;
        }
        float r = Math.min(radius, Math.min(w, h) / 2f);
        sr.rect(x + r, y, w - 2 * r, h);
        sr.rect(x, y + r, w, h - 2 * r);
        sr.circle(x + r, y + r, r);
        sr.circle(x + w - r, y + r, r);
        sr.circle(x + r, y + h - r, r);
        sr.circle(x + w - r, y + h - r, r);
    }

    private void drawStar(float cx, float cy, float radius, Color color) {
        sr.setColor(color);
        for (int i = 0; i < 5; i++) {
            float a = (float) (Math.PI * 2 * i / 5f - Math.PI / 2f);
            float a2 = (float) (Math.PI * 2 * (i + 2) / 5f - Math.PI / 2f);
            sr.triangle(cx, cy, cx + MathUtils.cos(a) * radius, cy + MathUtils.sin(a) * radius,
                    cx + MathUtils.cos(a2) * radius, cy + MathUtils.sin(a2) * radius);
        }
    }

    private void drawCentered(BitmapFont f, String text, float x, float y, float w, float h) {
        layout.setText(f, text);
        f.draw(batch, text, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f - 2f);
    }

    private float cellCenterX(int col) {
        return boardX + col * cellSize + cellSize / 2f;
    }

    private float cellCenterY(int row) {
        return boardY + (mapRows - 1 - row) * cellSize + cellSize / 2f;
    }

    private boolean isWalkable(int row, int col) {
        return isWalkableForPreview(row, col, usingShip);
    }

    private boolean isWalkableForPreview(int row, int col, boolean previewUsingShip) {
        if (!isInside(row, col) || map[row][col] == '#') {
            return false;
        }
        return !isFlightTile(row, col) || previewUsingShip;
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < mapRows && col >= 0 && col < mapCols;
    }

    private boolean isFlightTile(int row, int col) {
        return isInside(row, col) && map[row][col] == FLIGHT_TILE;
    }

    private boolean isWallAhead() {
        return !isWalkable(robotRow + facing.dr, robotCol + facing.dc);
    }

    private boolean isPathFreeAhead() {
        return isWalkable(robotRow + facing.dr, robotCol + facing.dc);
    }

    private boolean isOnGoal() {
        return robotRow == goalRow && robotCol == goalCol;
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
        codeFont.dispose();
        modalCodeFont.dispose();
        spaceBackground.dispose();
        shipTexture.dispose();
        guidePortrait.dispose();
        roboIdle.dispose();
        roboDown.dispose();
        roboBack.dispose();
        roboRight.dispose();
        roboLeft.dispose();
        roboUp.dispose();
        roboError.dispose();
        roboWin.dispose();
        if (themeAssets != null) {
            themeAssets.dispose();
        }
    }

    private enum RobotMood {
        IDLE,
        MOVING,
        ERROR,
        WIN
    }

    private enum Command {
        FORWARD("avanzar", "avanza"),
        TURN_LEFT("girar izquierda", "giro izquierda"),
        TURN_RIGHT("girar derecha", "giro derecha"),
        REPEAT_PREVIOUS("repetir x3", "bucle"),
        IF_WALL("si hay pared", "condicion"),
        IF_PATH_FREE("si camino libre", "condicion"),
        SAY("decir Vamos!", "mensaje"),
        COLLECT_STAR("recoger estrella", "estrella"),
        USE_SHIP("usar nave", "nave");

        final String label;
        final String shortLabel;

        Command(String label, String shortLabel) {
            this.label = label;
            this.shortLabel = shortLabel;
        }

        boolean isRepeatable() {
            return this == FORWARD || this == TURN_LEFT || this == TURN_RIGHT;
        }

        String paletteIcon() {
            return switch (this) {
                case FORWARD -> "forward";
                case TURN_LEFT -> "turn_left";
                case TURN_RIGHT -> "turn_right";
                case REPEAT_PREVIOUS -> "loop";
                case IF_WALL -> "wall";
                case IF_PATH_FREE -> "path";
                case COLLECT_STAR -> "star";
                case USE_SHIP -> "ship";
                default -> "say";
            };
        }
    }

    private enum Direction {
        UP(-1, 0),
        DOWN(1, 0),
        RIGHT(0, 1),
        LEFT(0, -1);

        final int dr;
        final int dc;

        Direction(int dr, int dc) {
            this.dr = dr;
            this.dc = dc;
        }

        Direction turnLeft() {
            return switch (this) {
                case UP -> LEFT;
                case LEFT -> DOWN;
                case DOWN -> RIGHT;
                case RIGHT -> UP;
            };
        }

        Direction turnRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }

    private static class CommandButton {
        final Command command;
        final String label;
        final String icon;
        final Rectangle rect;
        final Color color;

        CommandButton(Command command, String label, String icon, Rectangle rect, Color color) {
            this.command = command;
            this.label = label;
            this.icon = icon;
            this.rect = rect;
            this.color = color;
        }
    }

    private static class PreviewCursor {
        int row;
        int col;
        Direction facing;
        boolean usingShip;

        PreviewCursor(int row, int col, Direction facing, boolean usingShip) {
            this.row = row;
            this.col = col;
            this.facing = facing;
            this.usingShip = usingShip;
        }
    }

    private static class PathStep {
        final int row;
        final int col;
        final Direction facing;
        final Command turnType;

        PathStep(int row, int col, Direction facing, Command turnType) {
            this.row = row;
            this.col = col;
            this.facing = facing;
            this.turnType = turnType;
        }
    }

    private static class CellPoint {
        final int row;
        final int col;

        CellPoint(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static class LevelData {
        final String title;
        final String goal;
        final String[] rows;
        final CellPoint[] coins;

        LevelData(String title, String goal, String[] rows, CellPoint[] coins) {
            this.title = title;
            this.goal = goal;
            this.rows = rows;
            this.coins = coins;
        }
    }
}
