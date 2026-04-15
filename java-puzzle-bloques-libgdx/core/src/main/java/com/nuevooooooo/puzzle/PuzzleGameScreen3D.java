package com.nuevooooooo.puzzle;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nuevooooooo.puzzle.ecs.BobMotionComponent;
import com.nuevooooooo.puzzle.ecs.EntityKind;
import com.nuevooooooo.puzzle.ecs.KindComponent;
import com.nuevooooooo.puzzle.ecs.Motion2DComponent;
import com.nuevooooooo.puzzle.ecs.SceneRefComponent;
import com.nuevooooooo.puzzle.ecs.Transform3DComponent;
import com.nuevooooooo.puzzle.ecs.systems.Motion2DSystem;
import com.nuevooooooo.puzzle.ecs.systems.MotionWorldMappingSystem;
import com.nuevooooooo.puzzle.ecs.systems.SceneTransformSystem;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PuzzleGameScreen3D extends ScreenAdapter {
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
    private final SpriteBatch postBatch = new SpriteBatch();
    private final BitmapFont font = UiFonts.createUiFont(24f / 15f);
    private final Viewport viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    private final Vector3 touch = new Vector3();

    private final char[][] map = new char[ROWS][COLS];
    private final List<BlockNode> workspaceBlocks = new ArrayList<>();
    private final List<Action> program = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<ModelInstance> floorInstances = new ArrayList<>();
    private final List<ModelInstance> wallInstances = new ArrayList<>();
    private final List<Vector3> floorBases = new ArrayList<>();
    private final List<Vector3> wallBases = new ArrayList<>();
    private final List<Scene> entityScenes = new ArrayList<>();
    private final List<SceneAsset> gltfAssets = new ArrayList<>();

    private final String[][] levels = new String[][]{
            {"##########", "#S...#...#", "###.#.#..#", "#...#.#..#", "#.###.#..#", "#.....#..#", "#.#####..#", "#.......F#", "#..#######", "##########"},
            {"##########", "#S.....###", "#####....#", "#...###..#", "#.#...#..#", "#.#.#.#..#", "#...#....#", "###.####.#", "#......F.#", "##########"},
            {"##########", "#S..#....#", "##.#.##..#", "#..#..#..#", "#.##..##.#", "#....#...#", "###.##.#.#", "#....#.#F#", "#.####...#", "##########"}
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

    private float shakeTime = 0f;
    private float shakePower = 0f;
    private float cameraDistance = 13.8f;
    private float cameraOrbitDeg = 42f;

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

    private final ModelBatch staticModelBatch = new ModelBatch();
    private final Environment environment = new Environment();
    private final PerspectiveCamera worldCamera = new PerspectiveCamera(62f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private final SceneManager sceneManager = new SceneManager();

    private final Engine ecs = new PooledEngine();
    private Entity heroEntity;
    private Entity goalEntity;

    private Model tileModel;
    private Model wallModel;
    private Model heroFallbackModel;
    private Model zombieFallbackModel;
    private Model goalFallbackModel;

    private FrameBuffer postFbo;
    private ShaderProgram postShader;
    private final Matrix4 postProjection = new Matrix4();

    public PuzzleGameScreen3D(HeroConfig hero) {
        this.hero = hero;
        setup3D();
        setupPostProcess();
        setupEcs();
        buildPalette();
        loadLevel(level);
        setupInput();
    }

    private void setup3D() {
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.72f, 0.77f, 0.88f, 1f));
        environment.add(new DirectionalLight().set(1f, 1f, 0.94f, -0.7f, -1f, -0.5f));
        worldCamera.near = 0.1f;
        worldCamera.far = 90f;
        sceneManager.setCamera(worldCamera);
        sceneManager.environment = environment;

        ModelBuilder mb = new ModelBuilder();
        long attrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        tileModel = mb.createBox(
                1f, 0.16f, 1f,
                new Material(
                        ColorAttribute.createDiffuse(new Color(0.10f, 0.45f, 0.75f, 1f)),
                        ColorAttribute.createSpecular(new Color(0.18f, 0.62f, 0.95f, 1f)),
                        FloatAttribute.createShininess(10f)
                ),
                attrs
        );
        wallModel = mb.createBox(
                1f, 0.95f, 1f,
                new Material(
                        ColorAttribute.createDiffuse(new Color(0.85f, 0.20f, 0.04f, 1f)),
                        ColorAttribute.createSpecular(new Color(0.98f, 0.55f, 0.12f, 1f)),
                        FloatAttribute.createShininess(26f)
                ),
                attrs
        );
        heroFallbackModel = mb.createSphere(
                0.56f, 0.56f, 0.56f, 20, 20,
                new Material(ColorAttribute.createDiffuse(hero.suit.suitColor)),
                attrs
        );
        zombieFallbackModel = mb.createSphere(
                0.52f, 0.52f, 0.52f, 18, 18,
                new Material(ColorAttribute.createDiffuse(new Color(0.20f, 0.80f, 0.30f, 1f))),
                attrs
        );
        goalFallbackModel = mb.createCylinder(
                0.48f, 0.38f, 0.48f, 20,
                new Material(
                        ColorAttribute.createDiffuse(new Color(1f, 0.90f, 0.18f, 1f)),
                        ColorAttribute.createSpecular(new Color(1f, 1f, 0.55f, 1f)),
                        FloatAttribute.createShininess(28f)
                ),
                attrs
        );
    }

    private void setupPostProcess() {
        ShaderProgram.pedantic = false;
        postShader = new ShaderProgram(
                "attribute vec4 a_position;\n" +
                        "attribute vec4 a_color;\n" +
                        "attribute vec2 a_texCoord0;\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 v_color;\n" +
                        "varying vec2 v_uv;\n" +
                        "void main(){\n" +
                        "  v_color = a_color;\n" +
                        "  v_uv = a_texCoord0;\n" +
                        "  gl_Position = u_projTrans * a_position;\n" +
                        "}\n",
                "#ifdef GL_ES\n" +
                        "precision mediump float;\n" +
                        "#endif\n" +
                        "varying vec4 v_color;\n" +
                        "varying vec2 v_uv;\n" +
                        "uniform sampler2D u_texture;\n" +
                        "uniform float u_time;\n" +
                        "uniform float u_strength;\n" +
                        "void main(){\n" +
                        "  vec2 uv = v_uv;\n" +
                        "  float wave = sin((uv.y + u_time * 0.13) * 38.0) * 0.0035 * u_strength;\n" +
                        "  uv.x += wave;\n" +
                        "  vec4 base = texture2D(u_texture, uv) * v_color;\n" +
                        "  float split = smoothstep(0.46, 0.54, uv.x);\n" +
                        "  vec3 waterTint = vec3(0.88, 1.03, 1.14);\n" +
                        "  vec3 fireTint = vec3(1.16, 0.95, 0.83);\n" +
                        "  vec3 graded = mix(base.rgb * waterTint, base.rgb * fireTint, split);\n" +
                        "  float vig = smoothstep(1.06, 0.24, distance(uv, vec2(0.5)));\n" +
                        "  float pulse = 0.05 * sin(u_time * 2.4);\n" +
                        "  gl_FragColor = vec4((graded + pulse) * vig, base.a);\n" +
                        "}\n"
        );
    }

    private void setupEcs() {
        ecs.addSystem(new Motion2DSystem());
        ecs.addSystem(new MotionWorldMappingSystem(new MotionWorldMappingSystem.UiToWorldMapper() {
            @Override
            public float uiToWorldX(float uiX) {
                return PuzzleGameScreen3D.this.uiToWorldX(uiX);
            }

            @Override
            public float uiToWorldZ(float uiY) {
                return PuzzleGameScreen3D.this.uiToWorldZ(uiY);
            }
        }));
        ecs.addSystem(new SceneTransformSystem(() -> stateTime));
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touch.set(screenX, screenY, 0);
                viewport.unproject(touch);
                float x = touch.x;
                float y = touch.y;

                if (btnRun.contains(x, y)) {
                    startRun();
                    return true;
                }
                if (btnStep.contains(x, y)) {
                    stepOne();
                    return true;
                }
                if (btnReset.contains(x, y)) {
                    loadLevel(level);
                    status = "Nivel reiniciado.";
                    return true;
                }
                if (btnClear.contains(x, y)) {
                    workspaceBlocks.clear();
                    program.clear();
                    status = "Programa limpiado.";
                    return true;
                }
                if (btnNext.contains(x, y)) {
                    level = (level + 1) % levels.length;
                    loadLevel(level);
                    status = "Nivel " + (level + 1);
                    return true;
                }

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
                if (draggingNode != null) {
                    draggingNode.rect.x = touch.x - dragDx;
                    draggingNode.rect.y = touch.y - dragDy;
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

        rebuildWorldGeometry();
        rebuildEcsEntities();
    }

    private void rebuildWorldGeometry() {
        floorInstances.clear();
        wallInstances.clear();
        floorBases.clear();
        wallBases.clear();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                float x = cellToWorldX(c);
                float z = cellToWorldZ(r);
                ModelInstance floor = new ModelInstance(tileModel);
                floor.transform.setToTranslation(x, -0.08f, z);
                floorInstances.add(floor);
                floorBases.add(new Vector3(x, -0.08f, z));
                if (map[r][c] == '#') {
                    ModelInstance wall = new ModelInstance(wallModel);
                    wall.transform.setToTranslation(x, 0.50f, z);
                    wallInstances.add(wall);
                    wallBases.add(new Vector3(x, 0.50f, z));
                }
            }
        }
    }

    private void rebuildEcsEntities() {
        for (Scene scene : entityScenes) {
            sceneManager.removeScene(scene);
        }
        entityScenes.clear();

        ImmutableArray<Entity> entities = ecs.getEntities();
        for (int i = entities.size() - 1; i >= 0; i--) {
            ecs.removeEntity(entities.get(i));
        }

        for (Zombie zombie : zombies) {
            zombie.entity = null;
        }

        heroEntity = createEcsEntity(EntityKind.HERO, "models/hero.glb", heroFallbackModel, 0.32f, 0.03f, 5f, 0f);
        applyGridPositionToEntity(heroEntity, playerRow, playerCol, false);

        int goalRow = 0;
        int goalCol = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (map[r][c] == 'F') {
                    goalRow = r;
                    goalCol = c;
                }
            }
        }
        goalEntity = createEcsEntity(EntityKind.GOAL, "models/goal.glb", goalFallbackModel, 0.20f, 0.06f, 3f, 0.3f);
        applyGridPositionToEntity(goalEntity, goalRow, goalCol, false);

        for (int i = 0; i < zombies.size(); i++) {
            Zombie zombie = zombies.get(i);
            zombie.entity = createEcsEntity(EntityKind.ZOMBIE, "models/zombie.glb", zombieFallbackModel, 0.30f, 0.02f, 4.5f, i);
            applyGridPositionToEntity(zombie.entity, zombie.row, zombie.col, false);
        }
    }

    private Entity createEcsEntity(EntityKind kind, String gltfPath, Model fallbackModel, float baseY, float bobAmp, float bobSpeed, float phase) {
        Model model = fallbackModel;
        if (Gdx.files.internal(gltfPath).exists()) {
            SceneAsset asset = new GLTFLoader().load(Gdx.files.internal(gltfPath));
            gltfAssets.add(asset);
            model = asset.scene.model;
        }
        ModelInstance instance = new ModelInstance(model);
        Scene scene = new Scene(instance);
        sceneManager.addScene(scene);
        entityScenes.add(scene);

        PooledEngine pooled = (PooledEngine) ecs;
        Entity entity = pooled.createEntity();

        Transform3DComponent transform = pooled.createComponent(Transform3DComponent.class);
        Motion2DComponent motion = pooled.createComponent(Motion2DComponent.class);
        motion.duration = MOVE_ANIM_DURATION;
        BobMotionComponent bob = pooled.createComponent(BobMotionComponent.class);
        bob.baseY = baseY;
        bob.amplitude = bobAmp;
        bob.speed = bobSpeed;
        bob.phase = phase;
        SceneRefComponent sceneRef = pooled.createComponent(SceneRefComponent.class);
        sceneRef.scene = scene;
        KindComponent kindComponent = pooled.createComponent(KindComponent.class);
        kindComponent.kind = kind;

        entity.add(transform);
        entity.add(motion);
        entity.add(bob);
        entity.add(sceneRef);
        entity.add(kindComponent);
        ecs.addEntity(entity);
        return entity;
    }

    private void applyGridPositionToEntity(Entity entity, int row, int col, boolean animated) {
        float uiX = cellCenterX(col);
        float uiY = cellCenterY(row);
        Motion2DComponent motion = entity.getComponent(Motion2DComponent.class);
        if (animated) {
            motion.fromX = motion.x;
            motion.fromY = motion.y;
            motion.toX = uiX;
            motion.toY = uiY;
            motion.timer = 0f;
            motion.moving = true;
        } else {
            motion.x = uiX;
            motion.y = uiY;
            motion.fromX = uiX;
            motion.fromY = uiY;
            motion.toX = uiX;
            motion.toY = uiY;
            motion.timer = motion.duration;
            motion.moving = false;
        }
    }

    @Override
    public void render(float delta) {
        stateTime += delta;
        updateWorldCamera(delta);

        if (running) {
            tick += delta;
            if (tick >= 0.42f) {
                tick = 0f;
                stepOne();
            }
        }

        ensurePostFramebuffer();
        postFbo.begin();
        ScreenUtils.clear(0.04f, 0.07f, 0.12f, 1f);
        drawWorld3D(delta);
        drawUiOverlay();
        postFbo.end();
        drawPostProcessedFrame();
    }

    private void drawWorld3D(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);

        for (int i = 0; i < floorInstances.size(); i++) {
            ModelInstance floor = floorInstances.get(i);
            Vector3 base = floorBases.get(i);
            float wave = MathUtils.sin(stateTime * 2.5f + i * 0.35f) * 0.05f;
            floor.transform.idt().setToTranslation(base.x, base.y + wave, base.z);
            ColorAttribute diffuse = (ColorAttribute) floor.materials.first().get(ColorAttribute.Diffuse);
            float glow = 0.5f + 0.5f * MathUtils.sin(stateTime * 1.8f + i * 0.20f);
            diffuse.color.set(0.07f + glow * 0.05f, 0.34f + glow * 0.20f, 0.55f + glow * 0.35f, 1f);
        }

        for (int i = 0; i < wallInstances.size(); i++) {
            ModelInstance wall = wallInstances.get(i);
            Vector3 base = wallBases.get(i);
            float heat = 0.5f + 0.5f * MathUtils.sin(stateTime * 6.8f + i * 0.77f);
            wall.transform.idt().setToTranslation(base.x, base.y + heat * 0.04f, base.z);
            ColorAttribute diffuse = (ColorAttribute) wall.materials.first().get(ColorAttribute.Diffuse);
            diffuse.color.set(0.55f + heat * 0.35f, 0.12f + heat * 0.22f, 0.02f, 1f);
        }

        ecs.update(delta);
        sceneManager.update(delta);

        staticModelBatch.begin(worldCamera);
        for (ModelInstance floor : floorInstances) {
            staticModelBatch.render(floor, environment);
        }
        for (ModelInstance wall : wallInstances) {
            staticModelBatch.render(wall, environment);
        }
        staticModelBatch.end();

        sceneManager.render();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    private void drawUiOverlay() {
        viewport.apply();
        sr.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        drawPanels();
        drawRightArea();
        drawTopInfo();
    }

    private void drawPostProcessedFrame() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        postProjection.setToOrtho2D(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        postBatch.setProjectionMatrix(postProjection);
        postBatch.setShader(postShader);
        postShader.bind();
        postShader.setUniformf("u_time", stateTime);
        postShader.setUniformf("u_strength", running ? 1f : 0.85f);
        postBatch.begin();
        postBatch.draw(postFbo.getColorBufferTexture(), 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        postBatch.end();
        postBatch.setShader(null);
    }

    private void ensurePostFramebuffer() {
        int w = Gdx.graphics.getBackBufferWidth();
        int h = Gdx.graphics.getBackBufferHeight();
        if (postFbo == null || postFbo.getWidth() != w || postFbo.getHeight() != h) {
            if (postFbo != null) {
                postFbo.dispose();
            }
            postFbo = new FrameBuffer(com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888, w, h, true);
        }
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
                if (isWalkable(playerRow + left.dr, playerCol + left.dc)) {
                    moveForward();
                }
            }
            case REPEAT2 -> {
                moveForward();
                if (!isOnGoal()) {
                    moveForward();
                }
            }
        }
        actionTicks++;
        if (actionTicks % hero.difficulty.zombieStepRate == 0) {
            moveZombies();
        }
        if (isOnZombie()) {
            running = false;
            status = "Oh no! Un zombie te atrapo.";
            kickCamera(0.24f, 4.6f);
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
        for (BlockNode b : workspaceBlocks) {
            program.add(b.action);
        }
        if (runIndex >= program.size()) {
            runIndex = 0;
        }
    }

    private void moveForward() {
        int nr = playerRow + direction.dr;
        int nc = playerCol + direction.dc;
        if (isWalkable(nr, nc)) {
            playerRow = nr;
            playerCol = nc;
            applyGridPositionToEntity(heroEntity, playerRow, playerCol, true);
        } else {
            kickCamera(0.08f, 2f);
        }
    }

    private void moveZombies() {
        for (Zombie zombie : zombies) {
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
                applyGridPositionToEntity(zombie.entity, zombie.row, zombie.col, true);
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
            if (zombie.row == playerRow && zombie.col == playerCol) {
                return true;
            }
        }
        return false;
    }

    private void drawPanels() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.09f, 0.14f, 0.22f, 0.82f);
        sr.rect(RIGHT_X, 8, 710, 698);
        sr.setColor(0.04f, 0.08f, 0.14f, 0.45f);
        sr.rect(8, 658, 534, 48);
        sr.end();
    }

    private void drawRightArea() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.09f, 0.16f, 0.28f, 1f);
        sr.rect(WORK_X, 110, 390, 550);
        sr.setColor(0.15f, 0.24f, 0.38f, 1f);
        sr.rect(PALETTE_X, 260, 252, 400);

        drawButtonRect(btnRun, new Color(0.2f, 0.72f, 0.34f, 1f));
        drawButtonRect(btnStep, new Color(0.91f, 0.60f, 0.18f, 1f));
        drawButtonRect(btnReset, new Color(0.13f, 0.65f, 0.92f, 1f));
        drawButtonRect(btnClear, new Color(0.3f, 0.36f, 0.46f, 1f));
        drawButtonRect(btnNext, new Color(0.86f, 0.32f, 0.25f, 1f));

        for (BlockPalette item : palette) {
            drawPuzzleBlock(item.rect, item.color);
        }
        for (BlockNode node : workspaceBlocks) {
            drawPuzzleBlock(node.rect, node.color);
        }

        if (draggingPalette != null) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);
            Rectangle ghost = new Rectangle(touch.x - 120, touch.y - 20, 240, 40);
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
        font.draw(batch, "Ashley ECS + gdx-gltf + PostFX | " + hero.name + " | Nivel " + (level + 1) + " | " + hero.difficulty.label, 18, 708);
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

    private float cellToWorldX(int col) {
        return col - COLS * 0.5f + 0.5f;
    }

    private float cellToWorldZ(int row) {
        return (ROWS - 1 - row) - ROWS * 0.5f + 0.5f;
    }

    private float uiToWorldX(float pixelX) {
        float col = (pixelX - BOARD_X - CELL * 0.5f) / CELL;
        return col - COLS * 0.5f + 0.5f;
    }

    private float uiToWorldZ(float pixelY) {
        float fromBottom = (pixelY - BOARD_Y - CELL * 0.5f) / CELL;
        return fromBottom - ROWS * 0.5f + 0.5f;
    }

    private void updateWorldCamera(float delta) {
        float targetDistance = running ? 13.1f : 13.8f;
        cameraDistance = MathUtils.lerp(cameraDistance, targetDistance, Math.min(1f, delta * 3f));
        cameraOrbitDeg += delta * (running ? 17f : 8f);

        float sx = 0f;
        float sz = 0f;
        if (shakeTime > 0f) {
            shakeTime = Math.max(0f, shakeTime - delta);
            sx = MathUtils.random(-shakePower, shakePower);
            sz = MathUtils.random(-shakePower, shakePower);
        } else {
            shakePower = Math.max(0f, shakePower - delta * 2f);
        }

        float rad = cameraOrbitDeg * MathUtils.degreesToRadians;
        float orbitX = MathUtils.cos(rad) * cameraDistance * 0.92f;
        float orbitZ = MathUtils.sin(rad) * cameraDistance * 0.72f;
        float orbitY = cameraDistance * 0.92f + 0.35f * MathUtils.sin(stateTime * 0.8f);
        worldCamera.position.set(orbitX + sx, orbitY, orbitZ + sz);
        worldCamera.lookAt(0f, 0f, 0f);
        worldCamera.up.set(0f, 1f, 0f);
        worldCamera.update();
    }

    private void kickCamera(float duration, float power) {
        shakeTime = Math.max(shakeTime, duration);
        shakePower = Math.max(shakePower, power * 0.05f);
    }

    private void snapNode(BlockNode moving) {
        moving.rect.x = Math.max(WORK_X + 8, Math.min(moving.rect.x, WORK_X + 390 - moving.rect.width - 8));
        moving.rect.y = Math.max(118, Math.min(moving.rect.y, 650 - moving.rect.height));
        BlockNode best = null;
        float bestScore = Float.MAX_VALUE;
        for (BlockNode other : workspaceBlocks) {
            if (other == moving) {
                continue;
            }
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
        worldCamera.viewportWidth = width;
        worldCamera.viewportHeight = height;
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
        postBatch.dispose();
        font.dispose();
        if (postFbo != null) {
            postFbo.dispose();
        }
        if (postShader != null) {
            postShader.dispose();
        }
        staticModelBatch.dispose();
        sceneManager.dispose();
        for (SceneAsset asset : gltfAssets) {
            asset.dispose();
        }
        tileModel.dispose();
        wallModel.dispose();
        heroFallbackModel.dispose();
        zombieFallbackModel.dispose();
        goalFallbackModel.dispose();
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
        Entity entity;

        Zombie(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    private static class BlockPalette {
        final Action action;
        final Color color;
        final Rectangle rect;

        BlockPalette(Action action, String ignoredLabel, Color color, Rectangle rect) {
            this.action = action;
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
