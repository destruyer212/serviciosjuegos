package com.nuevooooooo.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PuzzleGameScreen extends ScreenAdapter {
    private static final float VIRTUAL_WIDTH = 1280f;
    private static final float VIRTUAL_HEIGHT = 720f;

    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int CELL = 50;
    private static final int BOARD_X = 16;
    private static final int BOARD_Y = 90;
    private static final int RIGHT_X = 560;
    private static final int PALETTE_X = RIGHT_X + 16;
    private static final int WORK_X = RIGHT_X + 290;
    private static final float MOVE_ANIM_DURATION = 0.17f;

    private final HeroConfig hero;
    private final ShapeRenderer sr = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = UiFonts.createUiFont(24f / 15f);
    private final Viewport viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    private final Vector3 touch = new Vector3();

    private final char[][] map = new char[ROWS][COLS];
    private final List<BlockNode> workspaceBlocks = new ArrayList<>();
    private final List<Action> program = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final String[][] levels = new String[][]{
            {"##########","#S...#...#","###.#.#..#","#...#.#..#","#.###.#..#","#.....#..#","#.#####..#","#.......F#","#..#######","##########"},
            {"##########","#S.....###","#####....#","#...###..#","#.#...#..#","#.#.#.#..#","#...#....#","###.####.#","#......F.#","##########"},
            {"##########","#S..#....#","##.#.##..#","#..#..#..#","#.##..##.#","#....#...#","###.##.#.#","#....#.#F#","#.####...#","##########"}
    };

    private int level = 0;
    private int playerRow;
    private int playerCol;
    private Direction direction = Direction.RIGHT;
    private int runIndex = 0;
    private boolean running = false;
    private float tick = 0f;
    private int actionTicks = 0;
    private String status = "Arrastra bloques al area y ejecuta.";
    private float stateTime = 0f;

    private float heroVisualX;
    private float heroVisualY;
    private float heroFromX;
    private float heroFromY;
    private float heroToX;
    private float heroToY;
    private float heroAnimTimer = MOVE_ANIM_DURATION;

    private float cameraZoom = 1f;
    private float shakeTime = 0f;
    private float shakePower = 0f;

    private BlockPalette draggingPalette = null;
    private BlockNode draggingNode = null;
    private float dragDx;
    private float dragDy;

    private final Rectangle btnRun = new Rectangle(PALETTE_X, 96, 120, 40);
    private final Rectangle btnStep = new Rectangle(PALETTE_X + 132, 96, 120, 40);
    private final Rectangle btnReset = new Rectangle(PALETTE_X, 146, 120, 40);
    private final Rectangle btnClear = new Rectangle(PALETTE_X + 132, 146, 120, 40);
    private final Rectangle btnNext = new Rectangle(PALETTE_X, 196, 252, 40);

    private final List<BlockPalette> palette = new ArrayList<>();

    public PuzzleGameScreen(HeroConfig hero) {
        this.hero = hero;
        buildPalette();
        loadLevel(level);
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                float x = touch.x;
                float y = touch.y;

                if (btnRun.contains(x, y)) { startRun(); return true; }
                if (btnStep.contains(x, y)) { stepOne(); return true; }
                if (btnReset.contains(x, y)) { loadLevel(level); status = "Nivel reiniciado."; return true; }
                if (btnClear.contains(x, y)) { workspaceBlocks.clear(); program.clear(); status = "Programa limpiado."; return true; }
                if (btnNext.contains(x, y)) { level = (level + 1) % levels.length; loadLevel(level); status = "Nivel " + (level + 1); return true; }

                for (BlockPalette item : palette) {
                    if (item.rect.contains(x, y)) {
                        draggingPalette = item;
                        return true;
                    }
                }
                for (int i = workspaceBlocks.size() - 1; i >= 0; i--) {
                    BlockNode node = workspaceBlocks.get(i);
                    if (node.rect.contains(x, y)) {
                        draggingNode = node;
                        dragDx = x - node.rect.x;
                        dragDy = y - node.rect.y;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                float x = touch.x;
                float y = touch.y;
                if (draggingNode != null) {
                    draggingNode.rect.x = x - dragDx;
                    draggingNode.rect.y = y - dragDy;
                    return true;
                }
                return draggingPalette != null;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                float x = touch.x;
                float y = touch.y;
                if (draggingPalette != null) {
                    if (x >= WORK_X && x <= 1250 && y >= 110 && y <= 660) {
                        BlockNode n = new BlockNode(draggingPalette.action, x - 120, y - 20, draggingPalette.color);
                        workspaceBlocks.add(n);
                        snapNode(n);
                    }
                    draggingPalette = null;
                    return true;
                }
                if (draggingNode != null) {
                    snapNode(draggingNode);
                    draggingNode = null;
                    return true;
                }
                return false;
            }
        });
    }

    private void buildPalette() {
        palette.clear();
        palette.add(new BlockPalette(Action.MOVE, "Mover adelante", new Color(0.12f, 0.63f, 0.92f, 1f), new Rectangle(PALETTE_X, 600, 252, 48)));
        palette.add(new BlockPalette(Action.LEFT, "Girar izquierda", new Color(0.56f, 0.31f, 0.82f, 1f), new Rectangle(PALETTE_X, 545, 252, 48)));
        palette.add(new BlockPalette(Action.RIGHT, "Girar derecha", new Color(0.63f, 0.22f, 0.77f, 1f), new Rectangle(PALETTE_X, 490, 252, 48)));
        palette.add(new BlockPalette(Action.IF_LEFT, "Si izquierda -> mover", new Color(0.95f, 0.65f, 0.16f, 1f), new Rectangle(PALETTE_X, 435, 252, 48)));
        palette.add(new BlockPalette(Action.REPEAT2, "Repetir x2 mover", new Color(0.90f, 0.28f, 0.25f, 1f), new Rectangle(PALETTE_X, 380, 252, 48)));
    }

    private void loadLevel(int levelIndex) {
        level = levelIndex;
        program.clear();
        workspaceBlocks.clear();
        running = false;
        runIndex = 0;
        actionTicks = 0;
        zombies.clear();

        String[] layout = levels[levelIndex];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                char ch = layout[r].charAt(c);
                if (ch == 'S') {
                    playerRow = r;
                    playerCol = c;
                    map[r][c] = '.';
                } else {
                    map[r][c] = ch;
                }
            }
        }
        direction = Direction.RIGHT;
        zombies.add(new Zombie(5, 2, Direction.RIGHT));
        zombies.add(new Zombie(7, 6, Direction.LEFT));
        if (hero.difficulty == HeroConfig.Difficulty.PRO) {
            zombies.add(new Zombie(2, 7, Direction.LEFT));
        }
        heroVisualX = cellCenterX(playerCol);
        heroVisualY = cellCenterY(playerRow);
        heroFromX = heroVisualX;
        heroFromY = heroVisualY;
        heroToX = heroVisualX;
        heroToY = heroVisualY;
        heroAnimTimer = MOVE_ANIM_DURATION;
        for (Zombie zombie : zombies) {
            float zx = cellCenterX(zombie.col);
            float zy = cellCenterY(zombie.row);
            zombie.snapTo(zx, zy);
        }
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        updateAnimations(delta);
        updateCameraEffects(delta);
        viewport.apply();
        sr.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        if (running) {
            tick += delta;
            if (tick >= 0.42f) {
                tick = 0f;
                stepOne();
            }
        }

        ScreenUtils.clear(0.05f, 0.09f, 0.16f, 1f);
        drawPanels();
        drawBoard();
        drawRightArea();
        drawTopInfo();
    }

    private void startRun() {
        buildProgramFromWorkspace();
        if (program.isEmpty()) {
            status = "Arrastra bloques primero.";
            return;
        }
        running = true;
        runIndex = 0;
        tick = 0;
        status = "Ejecutando...";
    }

    private void stepOne() {
        buildProgramFromWorkspace();
        if (program.isEmpty()) {
            status = "Arrastra bloques primero.";
            running = false;
            return;
        }
        if (runIndex >= program.size()) {
            running = false;
            status = isOnGoal() ? "Excelente! Llegaste a la flor." : "Fin del programa.";
            if (isOnGoal()) {
                kickCamera(0.16f, 2.8f);
            }
            return;
        }

        Action action = program.get(runIndex);
        switch (action) {
            case MOVE -> moveForward();
            case LEFT -> direction = direction.left();
            case RIGHT -> direction = direction.right();
            case IF_LEFT -> {
                Direction left = direction.left();
                if (isWalkable(playerRow + left.dr, playerCol + left.dc)) moveForward();
            }
            case REPEAT2 -> {
                moveForward();
                if (!isOnGoal()) moveForward();
            }
        }
        actionTicks++;
        if (actionTicks % hero.difficulty.zombieStepRate == 0) moveZombies();
        if (isOnZombie()) {
            running = false;
            status = "Oh no! Un zombie te atrapo.";
            kickCamera(0.25f, 4.8f);
            return;
        }
        if (isOnGoal()) {
            running = false;
            status = "Ganaste el nivel!";
            kickCamera(0.16f, 2.8f);
            return;
        }
        runIndex++;
    }

    private void buildProgramFromWorkspace() {
        workspaceBlocks.sort(Comparator.comparingDouble((BlockNode b) -> -b.rect.y).thenComparingDouble(b -> b.rect.x));
        program.clear();
        for (BlockNode b : workspaceBlocks) program.add(b.action);
        if (runIndex >= program.size()) runIndex = 0;
    }

    private void moveForward() {
        int nr = playerRow + direction.dr;
        int nc = playerCol + direction.dc;
        if (isWalkable(nr, nc)) {
            float fromX = cellCenterX(playerCol);
            float fromY = cellCenterY(playerRow);
            playerRow = nr;
            playerCol = nc;
            startHeroMove(fromX, fromY, cellCenterX(playerCol), cellCenterY(playerRow));
        } else {
            kickCamera(0.08f, 2f);
        }
    }

    private void moveZombies() {
        for (Zombie zombie : zombies) {
            int oldRow = zombie.row;
            int oldCol = zombie.col;
            int nr = zombie.row + zombie.direction.dr;
            int nc = zombie.col + zombie.direction.dc;
            if (!isWalkable(nr, nc)) {
                zombie.direction = zombie.direction.back();
                nr = zombie.row + zombie.direction.dr;
                nc = zombie.col + zombie.direction.dc;
            }
            if (isWalkable(nr, nc)) {
                zombie.row = nr;
                zombie.col = nc;
                zombie.startMove(
                        cellCenterX(oldCol),
                        cellCenterY(oldRow),
                        cellCenterX(zombie.col),
                        cellCenterY(zombie.row)
                );
            }
        }
    }

    private boolean isWalkable(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS && map[r][c] != '#';
    }

    private boolean isOnGoal() {
        return map[playerRow][playerCol] == 'F';
    }

    private boolean isOnZombie() {
        for (Zombie zombie : zombies) {
            if (zombie.row == playerRow && zombie.col == playerCol) return true;
        }
        return false;
    }

    private void drawPanels() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.08f, 0.13f, 0.22f, 1f);
        sr.rect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        sr.setColor(0.10f, 0.18f, 0.30f, 1f);
        sr.rect(8, 8, 534, 698);
        sr.setColor(0.12f, 0.18f, 0.30f, 1f);
        sr.rect(RIGHT_X, 8, 710, 698);
        sr.end();
    }

    private void drawBoard() {
        int boardX = BOARD_X;
        int boardY = BOARD_Y;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = boardX + c * CELL;
                int y = boardY + (ROWS - 1 - r) * CELL;

                if (map[r][c] == '#') {
                    sr.setColor(0.45f, 0.32f, 0.2f, 1f);
                    sr.rect(x + 4, y + 4, CELL - 8, CELL - 8);
                } else {
                    sr.setColor(0.20f, 0.72f, 0.24f, 1f);
                    sr.rect(x, y, CELL, CELL);
                }

                if (map[r][c] == 'F') {
                    sr.setColor(1f, 0.92f, 0.1f, 1f);
                    sr.circle(x + CELL / 2f, y + CELL / 2f, 8);
                }
            }
        }

        drawEntityShadow(heroVisualX, heroVisualY, 13f);
        drawHero(heroVisualX, heroVisualY);

        for (Zombie zombie : zombies) {
            drawEntityShadow(zombie.visualX, zombie.visualY, 11f);
            sr.setColor(0.25f, 0.9f, 0.35f, 1f);
            sr.circle(zombie.visualX, zombie.visualY, 11f);
            sr.setColor(0.14f, 0.55f, 0.21f, 1f);
            sr.circle(zombie.visualX + 2f, zombie.visualY - 2f, 7f);
        }
        sr.end();
    }

    private void drawHero(float px, float py) {
        sr.setColor(hero.suit.suitColor);
        sr.circle(px, py - 2, 13);
        sr.setColor(hero.suit.skinColor);
        sr.circle(px, py + 12, 9);
        sr.setColor(hero.hairColor.color);
        sr.rect(px - 8, py + 16, 16, 4);
        sr.setColor(0.1f, 0.14f, 0.22f, 1f);
        sr.rect(px - 2, py - 2, 4, 10);
    }

    private void drawRightArea() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        // workspace
        sr.setColor(0.09f, 0.16f, 0.28f, 1f);
        sr.rect(WORK_X, 110, 390, 550);
        sr.setColor(0.15f, 0.24f, 0.38f, 1f);
        sr.rect(PALETTE_X, 260, 252, 400);

        drawButtonRect(btnRun, new Color(0.2f, 0.72f, 0.34f, 1f));
        drawButtonRect(btnStep, new Color(0.91f, 0.60f, 0.18f, 1f));
        drawButtonRect(btnReset, new Color(0.13f, 0.65f, 0.92f, 1f));
        drawButtonRect(btnClear, new Color(0.3f, 0.36f, 0.46f, 1f));
        drawButtonRect(btnNext, new Color(0.86f, 0.32f, 0.25f, 1f));

        for (BlockPalette item : palette) drawPuzzleBlock(item.rect, item.color);
        for (BlockNode node : workspaceBlocks) drawPuzzleBlock(node.rect, node.color);

        // dragging ghost from palette
        if (draggingPalette != null) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);
            float mx = touch.x;
            float my = touch.y;
            Rectangle ghost = new Rectangle(mx - 120, my - 20, 240, 40);
            drawPuzzleBlock(ghost, new Color(draggingPalette.color.r, draggingPalette.color.g, draggingPalette.color.b, 0.65f));
        }
        sr.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.begin();
        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        font.draw(batch, "Paleta (arrastra bloques)", PALETTE_X, 680);
        font.draw(batch, "Area de armado (encaje)", WORK_X, 680);
        drawButtonText("Ejecutar", btnRun);
        drawButtonText("Paso", btnStep);
        drawButtonText("Reiniciar", btnReset);
        drawButtonText("Limpiar", btnClear);
        drawButtonText("Siguiente nivel", btnNext);

        for (BlockPalette item : palette) {
            font.draw(batch, item.action.label, item.rect.x + 16, item.rect.y + 25);
        }
        for (BlockNode node : workspaceBlocks) {
            font.draw(batch, node.action.label, node.rect.x + 16, node.rect.y + 25);
        }
        batch.end();
    }

    private void drawTopInfo() {
        batch.begin();
        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);
        font.draw(batch, hero.name + " | Nivel " + (level + 1) + " | " + hero.difficulty.label + " | Dir " + direction.name(), 18, 708);
        font.draw(batch, status, 18, 682);
        batch.end();
    }

    private void drawButtonRect(Rectangle r, Color color) {
        sr.setColor(color);
        sr.rect(r.x, r.y, r.width, r.height);
    }

    private void drawButtonText(String text, Rectangle r) {
        font.draw(batch, text, r.x + 16, r.y + 26);
    }

    private void drawPuzzleBlock(Rectangle r, Color color) {
        float frontH = r.height - 8f;
        sr.setColor(color.r * 0.72f, color.g * 0.72f, color.b * 0.72f, color.a);
        sr.rect(r.x, r.y, r.width, frontH);
        sr.setColor(color);
        sr.rect(r.x + 3, r.y + 8, r.width - 6, frontH - 8);
        sr.setColor(Math.min(1f, color.r + 0.2f), Math.min(1f, color.g + 0.2f), Math.min(1f, color.b + 0.2f), color.a);
        sr.rect(r.x + 3, r.y + frontH, r.width - 6, 8f);
        sr.setColor(1f, 1f, 1f, 0.22f);
        sr.rect(r.x + 8, r.y + frontH - 3f, r.width - 16, 3f);
        sr.setColor(0.05f, 0.08f, 0.14f, 1f);
        sr.rect(r.x + r.width / 2f - 14, r.y - 4, 28, 4);
        sr.rect(r.x + r.width / 2f - 14, r.y + r.height, 28, 4);
    }

    private float cellCenterX(int col) {
        return BOARD_X + col * CELL + CELL / 2f;
    }

    private float cellCenterY(int row) {
        return BOARD_Y + (ROWS - 1 - row) * CELL + CELL / 2f;
    }

    private void startHeroMove(float fromX, float fromY, float toX, float toY) {
        heroFromX = fromX;
        heroFromY = fromY;
        heroToX = toX;
        heroToY = toY;
        heroAnimTimer = 0f;
    }

    private void updateAnimations(float delta) {
        if (heroAnimTimer < MOVE_ANIM_DURATION) {
            heroAnimTimer = Math.min(MOVE_ANIM_DURATION, heroAnimTimer + delta);
            float p = Interpolation.sineOut.apply(heroAnimTimer / MOVE_ANIM_DURATION);
            heroVisualX = MathUtils.lerp(heroFromX, heroToX, p);
            heroVisualY = MathUtils.lerp(heroFromY, heroToY, p);
        } else {
            heroVisualX = cellCenterX(playerCol);
            heroVisualY = cellCenterY(playerRow);
        }

        for (Zombie zombie : zombies) {
            zombie.update(delta);
        }
    }

    private void updateCameraEffects(float delta) {
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();
        float targetZoom = running ? 0.985f : 1f;
        cameraZoom = MathUtils.lerp(cameraZoom, targetZoom, Math.min(1f, delta * 4f));
        camera.zoom = cameraZoom;

        float cx = VIRTUAL_WIDTH * 0.5f;
        float cy = VIRTUAL_HEIGHT * 0.5f;
        if (shakeTime > 0f) {
            shakeTime = Math.max(0f, shakeTime - delta);
            float dx = MathUtils.random(-shakePower, shakePower);
            float dy = MathUtils.random(-shakePower, shakePower);
            camera.position.set(cx + dx, cy + dy, 0f);
        } else {
            camera.position.set(cx, cy, 0f);
        }
        camera.update();
    }

    private void kickCamera(float duration, float power) {
        shakeTime = Math.max(shakeTime, duration);
        shakePower = Math.max(shakePower, power);
    }

    private void drawEntityShadow(float x, float y, float radius) {
        float pulse = 0.9f + 0.1f * MathUtils.sin(stateTime * 4f);
        sr.setColor(0f, 0f, 0f, 0.20f);
        sr.circle(x + 1.5f, y - radius * 0.95f, radius * 0.90f * pulse);
    }

    private void snapNode(BlockNode moving) {
        moving.rect.x = Math.max(WORK_X + 8, Math.min(moving.rect.x, WORK_X + 390 - moving.rect.width - 8));
        moving.rect.y = Math.max(118, Math.min(moving.rect.y, 650 - moving.rect.height));
        BlockNode best = null;
        float bestScore = Float.MAX_VALUE;
        for (BlockNode other : workspaceBlocks) {
            if (other == moving) continue;
            float targetY = other.rect.y - 36;
            float dy = Math.abs(targetY - moving.rect.y);
            float dx = Math.abs(other.rect.x - moving.rect.x);
            if (dy < 18 && dx < 24 && (dx + dy) < bestScore) {
                bestScore = dx + dy;
                best = other;
            }
        }
        if (best != null) {
            moving.rect.x = best.rect.x;
            moving.rect.y = best.rect.y - 36;
        }
    }

    @Override
    public void show() {
        resize(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
        font.dispose();
    }

    private enum Action {
        MOVE("Mover adelante"),
        LEFT("Girar izquierda"),
        RIGHT("Girar derecha"),
        IF_LEFT("Si izquierda -> mover"),
        REPEAT2("Repetir x2 mover");

        final String label;

        Action(String label) {
            this.label = label;
        }
    }

    private enum Direction {
        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        final int dr;
        final int dc;

        Direction(int dr, int dc) {
            this.dr = dr;
            this.dc = dc;
        }

        Direction left() {
            return values()[(ordinal() + 3) % 4];
        }

        Direction right() {
            return values()[(ordinal() + 1) % 4];
        }

        Direction back() {
            return values()[(ordinal() + 2) % 4];
        }
    }

    private static class Zombie {
        int row;
        int col;
        Direction direction;
        float visualX;
        float visualY;
        float fromX;
        float fromY;
        float toX;
        float toY;
        float animTimer = MOVE_ANIM_DURATION;

        Zombie(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }

        void snapTo(float x, float y) {
            visualX = x;
            visualY = y;
            fromX = x;
            fromY = y;
            toX = x;
            toY = y;
            animTimer = MOVE_ANIM_DURATION;
        }

        void startMove(float fromX, float fromY, float toX, float toY) {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.animTimer = 0f;
        }

        void update(float delta) {
            if (animTimer < MOVE_ANIM_DURATION) {
                animTimer = Math.min(MOVE_ANIM_DURATION, animTimer + delta);
                float p = Interpolation.sineOut.apply(animTimer / MOVE_ANIM_DURATION);
                visualX = MathUtils.lerp(fromX, toX, p);
                visualY = MathUtils.lerp(fromY, toY, p);
            } else {
                visualX = toX;
                visualY = toY;
            }
        }
    }

    private static class BlockPalette {
        final Action action;
        final String label;
        final Color color;
        final Rectangle rect;
        BlockPalette(Action action, String label, Color color, Rectangle rect) {
            this.action = action;
            this.label = label;
            this.color = color;
            this.rect = rect;
        }
    }

    private static class BlockNode {
        final Action action;
        final Rectangle rect;
        final Color color;
        BlockNode(Action action, float x, float y, Color color) {
            this.action = action;
            this.rect = new Rectangle(x, y, 240, 40);
            this.color = new Color(color);
        }
    }
}
