import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PuzzleBloquesGame extends JFrame {
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int CELL_SIZE = 52;
    private static final int MAX_BLOCKS = 28;
    private static final int PIECE_WIDTH = 300;
    private static final int PIECE_HEIGHT = 54;
    private static final int CONNECT_OVERLAP = 12;
    private static final Color BG_APP = new Color(15, 23, 42);
    private static final Color BG_PANEL = new Color(30, 41, 59);
    private static final Color BG_PANEL_SOFT = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color ACCENT = new Color(14, 165, 233);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(245, 158, 11);

    private final char[][] map = new char[ROWS][COLS];
    private final List<BlockPiece> program = new ArrayList<>();
    private final List<BlockPiece> pieces = new ArrayList<>();
    private final List<BlockPiece> runOrderPieces = new ArrayList<>();
    private final List<String[]> levels = new ArrayList<>();
    private final List<List<ZombieSpawn>> zombieLevels = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();

    private int playerRow;
    private int playerCol;
    private Direction direction = Direction.UP;


    private final BoardPanel boardPanel = new BoardPanel();
    private final PuzzleWorkspace workspace = new PuzzleWorkspace();
    private final JLabel statusLabel = new JLabel("Listo para programar.");
    private final JLabel programInfoLabel = new JLabel("Bloques: 0/" + MAX_BLOCKS);
    private final JComboBox<String> levelSelector = new JComboBox<>(new String[]{"Nivel 1", "Nivel 2", "Nivel 3"});
    private BlockPiece selectedPiece;
    private BlockType draggingPaletteType;
    private int paletteGhostX;
    private int paletteGhostY;
    private boolean paletteGhostVisible;
    private String heroName = "Valiente";
    private HeroStyle heroStyle = HeroStyle.ASTRONAUTA;
    private HairStyle heroHairStyle = HairStyle.CORTO;
    private HairColorPreset heroHairColor = HairColorPreset.CASTANO;
    private FaceTrait heroFaceTrait = FaceTrait.SONRISA;
    private Difficulty difficulty = Difficulty.NORMAL;
    private int currentLevelIndex = 0;
    private int actionTick = 0;
    private boolean suppressLevelEvents = false;

    private final Timer runTimer;
    private int runIndex = 0;

    public PuzzleBloquesGame() {
        super("Puzzle de Bloques - Java");
        applyGlobalTheme();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_APP);

        initLevels();
        loadLevel(currentLevelIndex);

        JPanel gamePanel = createGamePanel();
        JPanel blocksPanel = createBlocksPanel();
        gamePanel.setMinimumSize(new Dimension(520, 560));
        blocksPanel.setMinimumSize(new Dimension(520, 560));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gamePanel, blocksPanel);
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(8);
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);

        statusLabel.setBorder(new EmptyBorder(8, 10, 8, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(BG_PANEL);
        statusLabel.setForeground(TEXT_PRIMARY);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(statusLabel, BorderLayout.SOUTH);

        runTimer = new Timer(420, e -> executeNextStep());

        setSize(1180, 700);
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.46));
    }

    private void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        UIManager.put("Panel.background", BG_APP);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", BG_PANEL_SOFT);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ScrollPane.background", BG_PANEL);
        UIManager.put("ScrollBar.thumb", BG_PANEL_SOFT);
    }

    public boolean showStoryAndCharacterDialog() {
        JDialog dialog = new JDialog(this, "Comenzar Aventura", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("El Escape de la Flor Dorada");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);

        JTextArea story = new JTextArea(
                "Historia:\n" +
                "Los zombies tomaron el jardin encantado.\n" +
                "Tu mision es guiar a tu heroe con bloques para\n" +
                "llegar a la Flor Dorada antes de que sea tarde.\n\n" +
                "Solo una mente valiente puede programar la ruta\n" +
                "perfecta y salvar el bosque.");
        story.setEditable(false);
        story.setWrapStyleWord(true);
        story.setLineWrap(true);
        story.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        story.setForeground(TEXT_PRIMARY);
        story.setBackground(BG_PANEL);
        story.setBorder(new EmptyBorder(12, 12, 12, 12));
        center.add(story);

        JPanel custom = new JPanel(new BorderLayout(8, 8));
        custom.setBackground(BG_PANEL);
        custom.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setOpaque(false);
        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setForeground(TEXT_PRIMARY);
        JTextField nameField = new JTextField(heroName, 14);
        JLabel styleLabel = new JLabel("Traje:");
        styleLabel.setForeground(TEXT_PRIMARY);
        JComboBox<HeroStyle> styleCombo = new JComboBox<>(HeroStyle.values());
        styleCombo.setSelectedItem(heroStyle);
        JLabel diffLabel = new JLabel("Dificultad:");
        diffLabel.setForeground(TEXT_PRIMARY);
        JComboBox<Difficulty> diffCombo = new JComboBox<>(Difficulty.values());
        diffCombo.setSelectedItem(difficulty);
        JLabel hairLabel = new JLabel("Peinado:");
        hairLabel.setForeground(TEXT_PRIMARY);
        JComboBox<HairStyle> hairCombo = new JComboBox<>(HairStyle.values());
        hairCombo.setSelectedItem(heroHairStyle);
        JLabel hairColorLabel = new JLabel("Color de pelo:");
        hairColorLabel.setForeground(TEXT_PRIMARY);
        JComboBox<HairColorPreset> hairColorCombo = new JComboBox<>(HairColorPreset.values());
        hairColorCombo.setSelectedItem(heroHairColor);
        JLabel traitLabel = new JLabel("Rasgo:");
        traitLabel.setForeground(TEXT_PRIMARY);
        JComboBox<FaceTrait> traitCombo = new JComboBox<>(FaceTrait.values());
        traitCombo.setSelectedItem(heroFaceTrait);

        form.add(nameLabel);
        form.add(nameField);
        form.add(diffLabel);
        form.add(diffCombo);
        form.add(styleLabel);
        form.add(styleCombo);
        form.add(hairLabel);
        form.add(hairCombo);
        form.add(hairColorLabel);
        form.add(hairColorCombo);
        form.add(traitLabel);
        form.add(traitCombo);

        JPanel preview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(2, 6, 23));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                drawHeroAvatar(
                        g2,
                        getWidth() / 2 - 28,
                        getHeight() / 2 - 30,
                        56,
                        (HeroStyle) styleCombo.getSelectedItem(),
                        (HairStyle) hairCombo.getSelectedItem(),
                        ((HairColorPreset) hairColorCombo.getSelectedItem()).color,
                        (FaceTrait) traitCombo.getSelectedItem()
                );
            }
        };
        preview.setPreferredSize(new Dimension(180, 170));
        preview.setOpaque(false);

        styleCombo.addActionListener(e -> preview.repaint());
        hairCombo.addActionListener(e -> preview.repaint());
        hairColorCombo.addActionListener(e -> preview.repaint());
        traitCombo.addActionListener(e -> preview.repaint());

        custom.add(form, BorderLayout.NORTH);
        custom.add(preview, BorderLayout.CENTER);
        center.add(custom);

        root.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = new ModernButton("Salir", BG_PANEL_SOFT);
        JButton start = new ModernButton("Iniciar Aventura", SUCCESS);
        actions.add(cancel);
        actions.add(start);
        root.add(actions, BorderLayout.SOUTH);

        final boolean[] started = {false};
        cancel.addActionListener(e -> dialog.dispose());
        start.addActionListener(e -> {
            String typedName = nameField.getText().trim();
            if (!typedName.isEmpty()) {
                heroName = typedName;
            }
            heroStyle = (HeroStyle) styleCombo.getSelectedItem();
            difficulty = (Difficulty) diffCombo.getSelectedItem();
            heroHairStyle = (HairStyle) hairCombo.getSelectedItem();
            heroHairColor = (HairColorPreset) hairColorCombo.getSelectedItem();
            heroFaceTrait = (FaceTrait) traitCombo.getSelectedItem();
            started[0] = true;
            dialog.dispose();
        });

        dialog.setContentPane(root);
        dialog.setSize(860, 520);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        if (started[0]) {
            statusLabel.setText("Aventura lista, " + heroName + "! Dificultad: " + difficulty.label + ".");
            boardPanel.repaint();
        }
        return started[0];
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(6, 6, 6, 4));
        panel.setBackground(BG_APP);
        panel.add(boardPanel, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        JLabel hint = new JLabel("Objetivo: llega a la flor programando bloques.");
        hint.setFont(new Font("Segoe UI", Font.BOLD, 22));
        hint.setForeground(TEXT_PRIMARY);
        left.add(hint);
        top.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JLabel mapLabel = new JLabel("Mapa:");
        mapLabel.setForeground(TEXT_PRIMARY);
        mapLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        right.add(mapLabel);
        levelSelector.addActionListener(e -> {
            if (!suppressLevelEvents) {
                changeLevel(levelSelector.getSelectedIndex());
            }
        });
        levelSelector.setBackground(BG_PANEL_SOFT);
        levelSelector.setForeground(TEXT_PRIMARY);
        levelSelector.setFocusable(false);
        right.add(levelSelector);
        top.add(right, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createBlocksPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 4, 6, 6));
        panel.setBackground(BG_APP);

        JLabel title = new JLabel("Editor de Bloques");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel actionButtons = new JPanel();
        actionButtons.setLayout(new BoxLayout(actionButtons, BoxLayout.Y_AXIS));
        actionButtons.add(createPaletteBlock(BlockType.MOVE));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.LEFT));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.RIGHT));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.IF_AHEAD));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.IF_LEFT));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.IF_RIGHT));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.IF_LEFT_CONTAINER));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.REPEAT_3_CONTAINER));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.REPEAT_MOVE_2));
        actionButtons.add(Box.createVerticalStrut(6));
        actionButtons.add(createPaletteBlock(BlockType.REPEAT_MOVE_3));
        actionButtons.setBorder(new EmptyBorder(4, 4, 4, 4));
        actionButtons.setBackground(BG_PANEL);

        JScrollPane paletteScroll = new JScrollPane(actionButtons);
        paletteScroll.setBorder(createModernTitledBorder("Paleta de bloques"));
        paletteScroll.setPreferredSize(new Dimension(238, 420));
        paletteScroll.getViewport().setBackground(BG_PANEL);

        workspace.setPreferredSize(new Dimension(560, 900));
        JScrollPane blockScroll = new JScrollPane(workspace);
        blockScroll.setBorder(createModernTitledBorder("Area de armado (arrastra y encaja)"));
        blockScroll.getViewport().setBackground(BG_PANEL);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(paletteScroll, BorderLayout.WEST);
        center.add(blockScroll, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(0, 2, 6, 6));
        controls.setOpaque(false);

        JButton removeBtn = createControlButton("Eliminar", new Color(239, 68, 68));
        removeBtn.addActionListener(e -> removeSelectedPiece());
        controls.add(removeBtn);

        JButton clearBtn = createControlButton("Limpiar", BG_PANEL_SOFT);
        clearBtn.addActionListener(e -> clearProgram());
        controls.add(clearBtn);

        JButton alignBtn = createControlButton("Alinear", BG_PANEL_SOFT);
        alignBtn.addActionListener(e -> workspace.alignPieces());
        controls.add(alignBtn);

        JButton stepBtn = createControlButton("Paso", WARNING);
        stepBtn.addActionListener(e -> executeSingleStep());
        controls.add(stepBtn);

        JButton runBtn = createControlButton("Ejecutar", SUCCESS);
        runBtn.addActionListener(e -> runProgram());
        controls.add(runBtn);

        JButton resetBtn = createControlButton("Reiniciar", ACCENT);
        resetBtn.addActionListener(e -> resetLevel("Nivel reiniciado."));
        controls.add(resetBtn);

        JButton hintBtn = createControlButton("Tip", BG_PANEL_SOFT);
        hintBtn.addActionListener(e -> statusLabel.setText("Une piezas acercando una debajo de otra para que encajen."));
        controls.add(hintBtn);
        
        JButton blankBtn = createControlButton("", BG_PANEL_SOFT);
        blankBtn.setEnabled(false);
        controls.add(blankBtn);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        programInfoLabel.setForeground(TEXT_MUTED);
        programInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bottom.add(programInfoLabel, BorderLayout.NORTH);
        bottom.add(controls, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createControlButton(String text, Color color) {
        return new ModernButton(text, color);
    }

    private javax.swing.border.TitledBorder createModernTitledBorder(String title) {
        javax.swing.border.TitledBorder border =
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BG_PANEL_SOFT, 1), title);
        border.setTitleColor(TEXT_PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 13));
        return border;
    }

    private JComponent createPaletteBlock(BlockType type) {
        PaletteBlockItem item = new PaletteBlockItem(type);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(210, 52));
        item.setPreferredSize(new Dimension(210, 52));
        return item;
    }

    private void initLevels() {
        levels.clear();
        zombieLevels.clear();
        levels.add(new String[]{
                "##########",
                "#S...#...#",
                "###.#.#..#",
                "#...#.#..#",
                "#.###.#..#",
                "#.....#..#",
                "#.#####..#",
                "#.......F#",
                "#..#######",
                "##########"
        });
        levels.add(new String[]{
                "##########",
                "#S.....###",
                "#####....#",
                "#...###..#",
                "#.#...#..#",
                "#.#.#.#..#",
                "#...#....#",
                "###.####.#",
                "#......F.#",
                "##########"
        });
        levels.add(new String[]{
                "##########",
                "#S..#....#",
                "##.#.##..#",
                "#..#..#..#",
                "#.##..##.#",
                "#....#...#",
                "###.##.#.#",
                "#....#.#F#",
                "#.####...#",
                "##########"
        });

        zombieLevels.add(List.of(
                new ZombieSpawn(5, 2, Direction.RIGHT),
                new ZombieSpawn(7, 6, Direction.LEFT)
        ));
        zombieLevels.add(List.of(
                new ZombieSpawn(1, 5, Direction.DOWN),
                new ZombieSpawn(6, 8, Direction.UP)
        ));
        zombieLevels.add(List.of(
                new ZombieSpawn(3, 7, Direction.LEFT),
                new ZombieSpawn(7, 2, Direction.RIGHT),
                new ZombieSpawn(5, 5, Direction.UP)
        ));
    }

    private void loadLevel(int levelIndex) {
        currentLevelIndex = levelIndex;
        actionTick = 0;
        String[] layout = levels.get(levelIndex);
        zombies.clear();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                char cell = layout[r].charAt(c);
                if (cell == 'S') {
                    playerRow = r;
                    playerCol = c;
                    map[r][c] = '.';
                } else {
                    map[r][c] = cell;
                }
            }
        }
        for (ZombieSpawn spawn : zombieLevels.get(levelIndex)) {
            zombies.add(new Zombie(spawn.row, spawn.col, spawn.direction));
        }
        if (difficulty == Difficulty.PRO) {
            for (ZombieSpawn spawn : zombieLevels.get(levelIndex)) {
                int rr = Math.max(1, Math.min(ROWS - 2, spawn.row + 1));
                int cc = Math.max(1, Math.min(COLS - 2, spawn.col - 1));
                if (map[rr][cc] != '#' && map[rr][cc] != 'F' && (rr != playerRow || cc != playerCol)) {
                    zombies.add(new Zombie(rr, cc, spawn.direction.turnRight()));
                    break;
                }
            }
        }
        direction = Direction.RIGHT;
        suppressLevelEvents = true;
        levelSelector.setSelectedIndex(levelIndex);
        suppressLevelEvents = false;
        boardPanel.repaint();
    }

    private void changeLevel(int levelIndex) {
        stopRun();
        clearProgram();
        loadLevel(levelIndex);
        statusLabel.setText("Cargado Nivel " + (levelIndex + 1) + " | " + difficulty.label + ". Crea tu programa.");
    }

    private void updateProgramInfo() {
        programInfoLabel.setText("Bloques: " + pieces.size() + "/" + MAX_BLOCKS);
    }

    private void removeSelectedPiece() {
        if (selectedPiece == null) {
            statusLabel.setText("Selecciona una pieza para eliminarla.");
            return;
        }
        removePieceTree(selectedPiece);
        selectedPiece = null;
        workspace.repaint();
        updateProgramInfo();
    }

    private void clearProgram() {
        stopRun();
        program.clear();
        runOrderPieces.clear();
        pieces.clear();
        workspace.removeAll();
        selectedPiece = null;
        workspace.revalidate();
        workspace.repaint();
        updateProgramInfo();
        statusLabel.setText("Programa limpiado.");
    }

    private void removePieceTree(BlockPiece piece) {
        if (piece.parentPiece != null) {
            piece.parentPiece.children.remove(piece);
            piece.parentPiece.relayoutChildren();
            piece.parentPiece = null;
        }
        List<BlockPiece> childCopy = new ArrayList<>(piece.children);
        for (BlockPiece child : childCopy) {
            removePieceTree(child);
        }
        piece.children.clear();
        pieces.remove(piece);
        workspace.remove(piece);
    }

    private void executeSingleStep() {
        stopRun();
        rebuildProgramFromWorkspace();
        if (program.isEmpty()) {
            statusLabel.setText("Agrega bloques para ejecutar.");
            return;
        }
        int index = 0;
        if (selectedPiece != null) {
            index = runOrderPieces.indexOf(selectedPiece);
            if (index < 0) {
                index = 0;
            }
        }
        runIndex = index;
        executeAction(program.get(runIndex));
    }

    private void rebuildProgramFromWorkspace() {
        program.clear();
        runOrderPieces.clear();
        List<BlockPiece> topLevel = new ArrayList<>();
        for (BlockPiece piece : pieces) {
            if (piece.parentPiece == null) {
                topLevel.add(piece);
            }
        }
        topLevel.sort(Comparator
                .comparingInt((BlockPiece p) -> p.getY())
                .thenComparingInt(BlockPiece::getX));
        runOrderPieces.addAll(topLevel);
        program.addAll(topLevel);
    }

    private void runProgram() {
        rebuildProgramFromWorkspace();
        if (program.isEmpty()) {
            statusLabel.setText("Agrega bloques para ejecutar.");
            return;
        }
        stopRun();
        runIndex = 0;
        runTimer.start();
        statusLabel.setText("Ejecutando programa...");
    }

    private void executeNextStep() {
        if (runIndex >= program.size()) {
            stopRun();
            if (isOnGoal()) {
                handleLevelComplete();
            } else {
                statusLabel.setText("Programa finalizado. No llegaste a la meta.");
            }
            return;
        }
        selectedPiece = runOrderPieces.get(runIndex);
        workspace.repaint();
        executeAction(program.get(runIndex));
        runIndex++;
    }

    private void stopRun() {
        if (runTimer.isRunning()) {
            runTimer.stop();
        }
    }

    private void executeAction(BlockPiece piece) {
        switch (piece.blockType) {
            case MOVE -> moveForward();
            case LEFT -> direction = direction.turnLeft();
            case RIGHT -> direction = direction.turnRight();
            case IF_AHEAD -> {
                int nr = playerRow + direction.dr;
                int nc = playerCol + direction.dc;
                if (isFree(nr, nc)) {
                    moveForward();
                }
            }
            case IF_LEFT -> {
                Direction leftDir = direction.turnLeft();
                if (isFree(playerRow + leftDir.dr, playerCol + leftDir.dc)) {
                    moveForward();
                }
            }
            case IF_RIGHT -> {
                Direction rightDir = direction.turnRight();
                if (isFree(playerRow + rightDir.dr, playerCol + rightDir.dc)) {
                    moveForward();
                }
            }
            case REPEAT_MOVE_2 -> {
                moveForward();
                if (!isOnGoal()) {
                    moveForward();
                }
            }
            case REPEAT_MOVE_3 -> {
                moveForward();
                if (!isOnGoal()) {
                    moveForward();
                }
                if (!isOnGoal()) {
                    moveForward();
                }
            }
            case IF_LEFT_CONTAINER -> {
                Direction leftDir = direction.turnLeft();
                if (isFree(playerRow + leftDir.dr, playerCol + leftDir.dc)) {
                    executeChildren(piece);
                }
            }
            case REPEAT_3_CONTAINER -> {
                for (int i = 0; i < 3; i++) {
                    if (isOnGoal()) {
                        break;
                    }
                    executeChildren(piece);
                }
            }
        }
        if (isOnZombie()) {
            stopRun();
            statusLabel.setText("Oh no! Un zombie te atrapo. Reintenta con otro programa.");
            return;
        }
        actionTick++;
        if (actionTick % difficulty.zombieMoveEverySteps == 0) {
            moveZombies();
        }
        boardPanel.repaint();
        if (isOnZombie()) {
            stopRun();
            statusLabel.setText("Oh no! Un zombie te atrapo. Reintenta con otro programa.");
            return;
        }
        if (isOnGoal()) {
            stopRun();
            handleLevelComplete();
        }
    }

    private void executeChildren(BlockPiece parent) {
        List<BlockPiece> ordered = new ArrayList<>(parent.children);
        ordered.sort(Comparator.comparingInt(BlockPiece::getY));
        for (BlockPiece child : ordered) {
            executeAction(child);
            if (isOnGoal()) {
                break;
            }
        }
    }

    private void moveForward() {
        int nr = playerRow + direction.dr;
        int nc = playerCol + direction.dc;
        if (isFree(nr, nc)) {
            playerRow = nr;
            playerCol = nc;
        } else {
            statusLabel.setText("Choque con muro. Ajusta tus bloques.");
        }
    }

    private void moveZombies() {
        for (Zombie zombie : zombies) {
            int nr = zombie.row + zombie.direction.dr;
            int nc = zombie.col + zombie.direction.dc;
            if (!canZombieMoveTo(nr, nc)) {
                zombie.direction = zombie.direction.turnBack();
                nr = zombie.row + zombie.direction.dr;
                nc = zombie.col + zombie.direction.dc;
            }
            if (canZombieMoveTo(nr, nc)) {
                zombie.row = nr;
                zombie.col = nc;
            }
        }
    }

    private boolean canZombieMoveTo(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS && map[r][c] != '#';
    }

    private boolean isOnZombie() {
        for (Zombie zombie : zombies) {
            if (zombie.row == playerRow && zombie.col == playerCol) {
                return true;
            }
        }
        return false;
    }

    private boolean isFree(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS && map[r][c] != '#';
    }

    private boolean isOnGoal() {
        return map[playerRow][playerCol] == 'F';
    }

    private void resetLevel(String message) {
        stopRun();
        loadLevel(currentLevelIndex);
        boardPanel.repaint();
        statusLabel.setText(message);
    }

    private void handleLevelComplete() {
        if (currentLevelIndex < levels.size() - 1) {
            int next = currentLevelIndex + 1;
            loadLevel(next);
            clearProgram();
            statusLabel.setText("Nivel superado! Bien hecho, " + heroName + ". Ahora Nivel " + (next + 1) + ".");
        } else {
            statusLabel.setText("Increible! Completaste todos los niveles, " + heroName + "!");
        }
    }

    private Shape buildPuzzleShape(int x, int y, int w, int h) {
        int topSocketW = 44;
        int topSocketDepth = 8;
        int bottomTabW = 42;
        int bottomTabHeight = 10;
        int bodyBottom = y + h - bottomTabHeight;

        GeneralPath shape = new GeneralPath();
        shape.moveTo(x + 8, y);
        shape.lineTo(x + w / 2 - topSocketW / 2, y);
        shape.curveTo(x + w / 2 - 16, y, x + w / 2 - 13, y + topSocketDepth, x + w / 2, y + topSocketDepth);
        shape.curveTo(x + w / 2 + 13, y + topSocketDepth, x + w / 2 + 16, y, x + w / 2 + topSocketW / 2, y);
        shape.lineTo(x + w - 8, y);
        shape.quadTo(x + w, y, x + w, y + 8);
        shape.lineTo(x + w, bodyBottom - 8);
        shape.quadTo(x + w, bodyBottom, x + w - 8, bodyBottom);
        shape.lineTo(x + w / 2 + bottomTabW / 2, bodyBottom);
        shape.curveTo(x + w / 2 + 13, bodyBottom, x + w / 2 + 11, y + h, x + w / 2, y + h);
        shape.curveTo(x + w / 2 - 11, y + h, x + w / 2 - 13, bodyBottom, x + w / 2 - bottomTabW / 2, bodyBottom);
        shape.lineTo(x + 8, bodyBottom);
        shape.quadTo(x, bodyBottom, x, bodyBottom - 8);
        shape.lineTo(x, y + 8);
        shape.quadTo(x, y, x + 8, y);
        shape.closePath();
        return shape;
    }

    private Color getReadableTextColor(Color bg) {
        int luminance = (int) (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
        return luminance > 150 ? TEXT_DARK : TEXT_PRIMARY;
    }

    private void drawHeroAvatar(Graphics2D g2, int x, int y, int size, HeroStyle style, HairStyle hairStyle, Color hairColor, FaceTrait trait) {
        int unit = Math.max(1, size / 14);
        int headW = unit * 8;
        int headH = unit * 5;
        int bodyW = unit * 9;
        int bodyH = unit * 6;
        int cx = x + size / 2;
        int headX = cx - headW / 2;
        int headY = y + unit;
        int bodyX = cx - bodyW / 2;
        int bodyY = headY + headH - unit;

        g2.setColor(style.suitColor);
        g2.fillRoundRect(bodyX, bodyY, bodyW, bodyH, unit * 2, unit * 2);
        g2.setColor(new Color(15, 23, 42));
        g2.drawRoundRect(bodyX, bodyY, bodyW, bodyH, unit * 2, unit * 2);

        g2.setColor(style.skinColor);
        g2.fillOval(headX, headY, headW, headH);
        g2.setColor(new Color(71, 85, 105));
        g2.drawOval(headX, headY, headW, headH);

        g2.setColor(hairColor);
        if (hairStyle == HairStyle.CORTO) {
            g2.fillRoundRect(headX + unit, headY - unit / 2, headW - unit * 2, unit + 2, unit, unit);
        } else if (hairStyle == HairStyle.PINCHOS) {
            Polygon p = new Polygon(
                    new int[]{headX + unit / 2, headX + unit * 2, headX + unit * 3, headX + unit * 4, headX + unit * 5, headX + unit * 6, headX + headW - unit / 2},
                    new int[]{headY + unit, headY - unit, headY + unit, headY - unit, headY + unit, headY - unit, headY + unit},
                    7
            );
            g2.fillPolygon(p);
        } else if (hairStyle == HairStyle.RIZADO) {
            for (int i = 0; i < 4; i++) {
                g2.fillOval(headX + unit + i * unit * 2, headY - unit, unit * 2, unit * 2);
            }
        } else if (hairStyle == HairStyle.COLA) {
            g2.fillRoundRect(headX + unit, headY - unit / 2, headW - unit * 2, unit + 2, unit, unit);
            g2.fillOval(headX + headW - unit / 2, headY + unit, unit * 2, unit * 3);
        }

        g2.setColor(Color.WHITE);
        g2.fillOval(headX + unit * 2, headY + unit * 2, unit + 2, unit + 1);
        g2.fillOval(headX + unit * 5, headY + unit * 2, unit + 2, unit + 1);
        g2.setColor(Color.BLACK);
        g2.fillOval(headX + unit * 2 + 1, headY + unit * 2 + 1, unit / 2 + 1, unit / 2 + 1);
        g2.fillOval(headX + unit * 5 + 1, headY + unit * 2 + 1, unit / 2 + 1, unit / 2 + 1);

        g2.setColor(new Color(127, 29, 29));
        if (trait == FaceTrait.SONRISA) {
            g2.drawArc(headX + unit * 3 - 1, headY + unit * 3, unit * 2, unit, 180, 180);
        } else if (trait == FaceTrait.DETERMINADO) {
            g2.drawLine(headX + unit * 3 - 1, headY + unit * 4, headX + unit * 5 + 1, headY + unit * 4 - 1);
        } else if (trait == FaceTrait.ALEGRE) {
            g2.fillOval(headX + unit * 4 - 1, headY + unit * 3 + 1, unit, unit / 2 + 1);
        }

        if (style == HeroStyle.ASTRONAUTA) {
            g2.setColor(new Color(203, 213, 225));
            g2.drawOval(headX - unit / 2, headY - unit / 2, headW + unit, headH + unit);
        } else if (style == HeroStyle.CABALLERO) {
            g2.setColor(new Color(148, 163, 184));
            g2.fillRoundRect(headX + unit, headY - unit / 2, headW - unit * 2, unit, unit, unit);
        } else if (style == HeroStyle.EXPLORADORA) {
            g2.setColor(new Color(245, 158, 11));
            g2.fillRoundRect(headX + unit, headY + unit / 3, headW - unit * 2, unit, unit, unit);
        }
    }

    private class BoardPanel extends JPanel {
        BoardPanel() {
            setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
            setBackground(new Color(33, 120, 36));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint bg = new GradientPaint(0, 0, new Color(21, 128, 61), 0, getHeight(), new Color(22, 101, 52));
            g2.setPaint(bg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int boardWidth = COLS * CELL_SIZE;
            int boardHeight = ROWS * CELL_SIZE;
            int ox = Math.max(0, (getWidth() - boardWidth) / 2);
            int oy = Math.max(0, (getHeight() - boardHeight) / 2);

            g2.setColor(new Color(15, 23, 42, 90));
            g2.fillRoundRect(ox - 8, oy - 8, boardWidth + 16, boardHeight + 16, 16, 16);

            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    int x = ox + c * CELL_SIZE;
                    int y = oy + r * CELL_SIZE;
                    char cell = map[r][c];

                    if (cell == '#') {
                        g2.setColor(new Color(110, 82, 56));
                        g2.fillRoundRect(x + 4, y + 4, CELL_SIZE - 8, CELL_SIZE - 8, 10, 10);
                    } else {
                        g2.setColor(new Color(56, 173, 63));
                        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }

                    if (cell == 'F') {
                        g2.setColor(new Color(255, 242, 0));
                        g2.fillOval(x + 15, y + 12, 20, 20);
                        g2.setColor(new Color(46, 204, 113));
                        g2.fillOval(x + 11, y + 28, 12, 10);
                        g2.fillOval(x + 27, y + 28, 12, 10);
                    }

                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }

            for (Zombie zombie : zombies) {
                int cellX = ox + zombie.col * CELL_SIZE;
                int cellY = oy + zombie.row * CELL_SIZE;
                drawPvzStyleZombie(g2, cellX, cellY);
            }

            int px = ox + playerCol * CELL_SIZE + 10;
            int py = oy + playerRow * CELL_SIZE + 10;
            drawHero(g2, px, py);

            drawDirectionHud(g2, ox, oy);
        }

        private Polygon getDirectionArrow(int px, int py) {
            int cx = px + (CELL_SIZE - 20) / 2;
            int cy = py + (CELL_SIZE - 20) / 2;
            int size = 10;

            return switch (direction) {
                case UP -> new Polygon(
                        new int[]{cx, cx - size, cx + size},
                        new int[]{cy - size, cy + size, cy + size}, 3
                );
                case DOWN -> new Polygon(
                        new int[]{cx, cx - size, cx + size},
                        new int[]{cy + size, cy - size, cy - size}, 3
                );
                case LEFT -> new Polygon(
                        new int[]{cx - size, cx + size, cx + size},
                        new int[]{cy, cy - size, cy + size}, 3
                );
                case RIGHT -> new Polygon(
                        new int[]{cx + size, cx - size, cx - size},
                        new int[]{cy, cy - size, cy + size}, 3
                );
            };
        }

        private void drawPvzStyleZombie(Graphics2D g2, int cellX, int cellY) {
            int headX = cellX + 11;
            int headY = cellY + 8;
            int headW = 30;
            int headH = 28;

            // Head
            g2.setColor(new Color(147, 197, 114));
            g2.fillOval(headX, headY, headW, headH);
            g2.setColor(new Color(71, 85, 49));
            g2.drawOval(headX, headY, headW, headH);

            // Hair / brow
            g2.setColor(new Color(90, 72, 59));
            g2.fillRoundRect(headX + 5, headY + 1, 20, 5, 4, 4);

            // Eyes
            g2.setColor(Color.WHITE);
            g2.fillOval(headX + 7, headY + 9, 9, 8);
            g2.fillOval(headX + 17, headY + 9, 9, 8);
            g2.setColor(new Color(15, 23, 42));
            g2.fillOval(headX + 10, headY + 11, 3, 3);
            g2.fillOval(headX + 20, headY + 11, 3, 3);

            // Mouth + teeth
            g2.setColor(new Color(120, 53, 15));
            g2.fillRoundRect(headX + 9, headY + 19, 13, 6, 4, 4);
            g2.setColor(new Color(250, 250, 250));
            g2.fillRect(headX + 12, headY + 20, 2, 4);
            g2.fillRect(headX + 16, headY + 20, 2, 4);

            // Body (shirt + jacket)
            int bodyX = cellX + 13;
            int bodyY = cellY + 33;
            g2.setColor(new Color(59, 130, 246));
            g2.fillRoundRect(bodyX, bodyY, 26, 12, 6, 6);
            g2.setColor(new Color(37, 99, 235));
            g2.drawRoundRect(bodyX, bodyY, 26, 12, 6, 6);
            g2.setColor(new Color(55, 65, 81));
            g2.fillRect(bodyX + 11, bodyY + 1, 4, 10);

            // Arms
            g2.setColor(new Color(147, 197, 114));
            g2.fillRoundRect(bodyX - 4, bodyY + 2, 6, 4, 3, 3);
            g2.fillRoundRect(bodyX + 24, bodyY + 4, 6, 4, 3, 3);

            // Legs + shoes
            g2.setColor(new Color(71, 85, 105));
            g2.fillRect(bodyX + 6, bodyY + 10, 5, 7);
            g2.fillRect(bodyX + 15, bodyY + 10, 5, 7);
            g2.setColor(new Color(28, 25, 23));
            g2.fillRoundRect(bodyX + 4, bodyY + 16, 8, 3, 2, 2);
            g2.fillRoundRect(bodyX + 14, bodyY + 16, 8, 3, 2, 2);
        }

        private void drawDirectionHud(Graphics2D g2, int ox, int oy) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(ox + 8, oy + 8, 430, 35, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
            g2.drawString(heroName + " | Nivel " + (currentLevelIndex + 1) + " | " + difficulty.label + " | Direccion: " + direction.label, ox + 16, oy + 30);
        }

        private void drawHero(Graphics2D g2, int px, int py) {
            drawHeroAvatar(g2, px, py - 2, CELL_SIZE - 12, heroStyle, heroHairStyle, heroHairColor.color, heroFaceTrait);

            Polygon arrow = getDirectionArrow(px, py);
            g2.setColor(new Color(30, 41, 59));
            g2.fillPolygon(arrow);
        }
    }

    private static class ModernButton extends JButton {
        private final Color base;

        ModernButton(String text, Color base) {
            super(text);
            this.base = base;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            Color preferred = getStaticReadableTextColor(base);
            setForeground(preferred);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 12, 10, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color paint = isEnabled() ? base : base.darker().darker();
            if (getModel().isRollover()) {
                paint = paint.brighter();
            }
            if (getModel().isPressed()) {
                paint = paint.darker();
            }
            g2.setColor(new Color(2, 6, 23, 120));
            g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, 12, 12);
            g2.setColor(paint);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color getStaticReadableTextColor(Color bg) {
            int luminance = (int) (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
            return luminance > 150 ? TEXT_DARK : TEXT_PRIMARY;
        }
    }

    private class PuzzleWorkspace extends JPanel {
        PuzzleWorkspace() {
            setLayout(null);
            setBackground(new Color(245, 247, 252));
        }

        BlockPiece addPiece(BlockType type) {
            BlockPiece piece = new BlockPiece(type);
            int y = 14 + (pieces.size() * (PIECE_HEIGHT - CONNECT_OVERLAP + 4));
            int h = type.container ? 120 : PIECE_HEIGHT;
            piece.setBounds(24, y, PIECE_WIDTH, h);
            pieces.add(piece);
            add(piece);
            revalidate();
            repaint();
            return piece;
        }

        void snapPiece(BlockPiece moving) {
            if (moving.parentPiece != null) {
                moving.parentPiece.children.remove(moving);
                moving.parentPiece.relayoutChildren();
                moving.parentPiece = null;
            }

            int x = Math.max(8, Math.min(moving.getX(), getWidth() - moving.getWidth() - 8));
            int y = Math.max(8, Math.min(moving.getY(), getHeight() - moving.getHeight() - 8));
            moving.setLocation(x, y);

            for (BlockPiece other : pieces) {
                if (other == moving || !other.blockType.container || moving.isAncestorOf(other)) {
                    continue;
                }
                Rectangle slot = other.getInnerDropZone();
                int cx = moving.getX() + moving.getWidth() / 2;
                int cy = moving.getY() + 8;
                if (slot.contains(cx, cy)) {
                    other.addChild(moving);
                    other.relayoutChildren();
                    repaint();
                    return;
                }
            }

            BlockPiece bestTarget = null;
            int bestScore = Integer.MAX_VALUE;
            for (BlockPiece other : pieces) {
                if (other == moving || other.parentPiece != null || moving.parentPiece != null) {
                    continue;
                }
                int desiredY = other.getY() + other.getHeight() - CONNECT_OVERLAP;
                int dy = Math.abs(desiredY - moving.getY());
                int dx = Math.abs(other.getX() - moving.getX());
                if (dy <= 22 && dx <= 34) {
                    int score = dy + dx;
                    if (score < bestScore) {
                        bestScore = score;
                        bestTarget = other;
                    }
                }
            }
            if (bestTarget != null) {
                int snapY = bestTarget.getY() + bestTarget.getHeight() - CONNECT_OVERLAP;
                moving.setLocation(bestTarget.getX(), snapY);
            } else {
                int laneX = 24;
                int laneX2 = 56;
                int targetLane = Math.abs(moving.getX() - laneX) < Math.abs(moving.getX() - laneX2) ? laneX : laneX2;
                moving.setLocation(targetLane, moving.getY());
            }
            repaint();
        }

        void alignPieces() {
            List<BlockPiece> topLevel = new ArrayList<>();
            for (BlockPiece piece : pieces) {
                if (piece.parentPiece == null) {
                    topLevel.add(piece);
                }
            }
            topLevel.sort(Comparator.comparingInt(BlockPiece::getY));
            int y = 18;
            for (BlockPiece piece : topLevel) {
                piece.setLocation(24, y);
                piece.relayoutChildren();
                y += piece.getHeight() - CONNECT_OVERLAP;
            }
            repaint();
        }

        BlockPiece addPieceAt(BlockType type, int x, int y) {
            BlockPiece piece = addPiece(type);
            piece.setLocation(x, y);
            snapPiece(piece);
            return piece;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (paletteGhostVisible && draggingPaletteType != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = draggingPaletteType.container ? 120 : PIECE_HEIGHT;
                Shape ghost = buildPuzzleShape(paletteGhostX, paletteGhostY, PIECE_WIDTH - 1, h - 1);
                Color c = draggingPaletteType.color;
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 110));
                g2.fill(ghost);
                g2.setColor(new Color(40, 40, 40, 120));
                g2.draw(ghost);
            }
        }
    }

    private class BlockPiece extends JComponent {
        private final BlockType blockType;
        private BlockPiece parentPiece;
        private final List<BlockPiece> children = new ArrayList<>();
        private Point dragOffset = new Point(0, 0);

        BlockPiece(BlockType blockType) {
            this.blockType = blockType;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedPiece = BlockPiece.this;
                    dragOffset = e.getPoint();
                    if (parentPiece != null) {
                        parentPiece.children.remove(BlockPiece.this);
                        parentPiece.relayoutChildren();
                        parentPiece = null;
                    }
                    workspace.setComponentZOrder(BlockPiece.this, 0);
                    workspace.repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int dx = e.getX() - dragOffset.x;
                    int dy = e.getY() - dragOffset.y;
                    moveWithChildren(dx, dy);
                    workspace.repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    workspace.snapPiece(BlockPiece.this);
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 1;
            int h = getHeight() - 1;
            GeneralPath shape = buildPuzzleShape(w, h);

            Color baseColor = blockType.color;
            if (this == selectedPiece) {
                baseColor = baseColor.brighter();
            } else if (runIndex < runOrderPieces.size() && runOrderPieces.get(runIndex) == this && runTimer.isRunning()) {
                baseColor = new Color(46, 204, 113);
            }

            g2.setColor(baseColor);
            g2.fill(shape);
            g2.setColor(new Color(0, 0, 0, 80));
            g2.draw(shape);

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillRoundRect(14, 5, w - 28, 10, 10, 10);

            g2.setColor(getReadableTextColor(baseColor));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.drawString(blockType.label, 16, 32);

            if (blockType.container) {
                Rectangle slot = new Rectangle(20, 46, w - 40, Math.max(44, h - 58));
                g2.setColor(new Color(255, 255, 255, 45));
                g2.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 10, 10);
                g2.setColor(new Color(255, 255, 255, 130));
                g2.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 10, 10);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
                g2.setColor(TEXT_PRIMARY);
                g2.drawString("Suelta bloques aqui", slot.x + 8, slot.y + 18);
            }
        }

        private GeneralPath buildPuzzleShape(int w, int h) {
            return (GeneralPath) PuzzleBloquesGame.this.buildPuzzleShape(0, 0, w, h);
        }

        private Rectangle getInnerDropZone() {
            return new Rectangle(getX() + 20, getY() + 46, getWidth() - 40, Math.max(44, getHeight() - 58));
        }

        private void addChild(BlockPiece child) {
            if (!children.contains(child)) {
                children.add(child);
                child.parentPiece = this;
            }
        }

        private boolean isAncestorOf(BlockPiece candidate) {
            BlockPiece cursor = candidate;
            while (cursor != null) {
                if (cursor == this) {
                    return true;
                }
                cursor = cursor.parentPiece;
            }
            return false;
        }

        private void relayoutChildren() {
            if (!blockType.container) {
                return;
            }
            children.sort(Comparator.comparingInt(BlockPiece::getY));
            int y = getY() + 56;
            for (BlockPiece child : children) {
                int childWidth = Math.max(210, getWidth() - 48);
                child.setSize(childWidth, child.getHeight());
                child.setLocation(getX() + 24, y);
                child.relayoutChildren();
                y += child.getHeight() - CONNECT_OVERLAP;
            }
            int newHeight = Math.max(110, y - getY() + 10);
            setSize(getWidth(), newHeight);
            if (parentPiece != null) {
                parentPiece.relayoutChildren();
            }
        }

        private void moveWithChildren(int dx, int dy) {
            setLocation(getX() + dx, getY() + dy);
            for (BlockPiece child : children) {
                child.moveWithChildren(dx, dy);
            }
        }
    }

    private class PaletteBlockItem extends JComponent {
        private final BlockType type;
        private Point dragOffset = new Point(0, 0);

        PaletteBlockItem(BlockType type) {
            this.type = type;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            MouseAdapter adapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    draggingPaletteType = type;
                    dragOffset = e.getPoint();
                    updateGhost(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    updateGhost(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    updateGhost(e);
                    if (paletteGhostVisible && draggingPaletteType != null && pieces.size() < MAX_BLOCKS) {
                        BlockPiece piece = workspace.addPieceAt(draggingPaletteType, paletteGhostX, paletteGhostY);
                        selectedPiece = piece;
                        updateProgramInfo();
                    } else if (pieces.size() >= MAX_BLOCKS) {
                        statusLabel.setText("Limite alcanzado: " + MAX_BLOCKS + " bloques.");
                    }
                    paletteGhostVisible = false;
                    draggingPaletteType = null;
                    workspace.repaint();
                }

                private void updateGhost(MouseEvent e) {
                    Point global = SwingUtilities.convertPoint(PaletteBlockItem.this, e.getPoint(), workspace);
                    paletteGhostX = global.x - dragOffset.x;
                    paletteGhostY = global.y - dragOffset.y;
                    paletteGhostVisible = workspace.contains(global);
                    workspace.repaint();
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape shape = buildPuzzleShape(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(type.color);
            g2.fill(shape);
            g2.setColor(new Color(0, 0, 0, 80));
            g2.draw(shape);
            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillRoundRect(10, 5, getWidth() - 20, 9, 8, 8);
            g2.setColor(getReadableTextColor(type.color));
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 13f));
            g2.drawString(type.label, 12, 32);
        }
    }

    private enum Direction {
        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        final int dr;
        final int dc;
        final String label;

        Direction(int dr, int dc) {
            this.dr = dr;
            this.dc = dc;
            this.label = switch (this.ordinal()) {
                case 0 -> "Arriba";
                case 1 -> "Derecha";
                case 2 -> "Abajo";
                default -> "Izquierda";
            };
        }

        Direction turnLeft() {
            return values()[(ordinal() + 3) % 4];
        }

        Direction turnRight() {
            return values()[(ordinal() + 1) % 4];
        }

        Direction turnBack() {
            return values()[(ordinal() + 2) % 4];
        }
    }

    private static class ZombieSpawn {
        final int row;
        final int col;
        final Direction direction;

        ZombieSpawn(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    private static class Zombie {
        int row;
        int col;
        Direction direction;

        Zombie(int row, int col, Direction direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    private enum BlockType {
        MOVE("Mover adelante", new Color(52, 152, 219), false),
        LEFT("Girar izquierda", new Color(155, 89, 182), false),
        RIGHT("Girar derecha", new Color(142, 68, 173), false),
        IF_AHEAD("Si camino al frente -> mover", new Color(241, 196, 15), false),
        IF_LEFT("Si camino izquierda -> mover", new Color(243, 156, 18), false),
        IF_RIGHT("Si camino derecha -> mover", new Color(230, 126, 34), false),
        REPEAT_MOVE_2("Repetir x2 -> mover", new Color(231, 76, 60), false),
        REPEAT_MOVE_3("Repetir x3 -> mover", new Color(192, 57, 43), false),
        IF_LEFT_CONTAINER("Si izquierda { }", new Color(211, 84, 0), true),
        REPEAT_3_CONTAINER("Repetir x3 { }", new Color(192, 57, 43), true);

        final String label;
        final Color color;
        final boolean container;

        BlockType(String label, Color color, boolean container) {
            this.label = label;
            this.color = color;
            this.container = container;
        }
    }

    private enum HeroStyle {
        ASTRONAUTA("Astronauta", new Color(59, 130, 246), new Color(254, 240, 138)),
        CABALLERO("Caballero", new Color(99, 102, 241), new Color(224, 231, 255)),
        EXPLORADORA("Exploradora", new Color(236, 72, 153), new Color(254, 215, 170));

        final String label;
        final Color suitColor;
        final Color skinColor;

        HeroStyle(String label, Color suitColor, Color skinColor) {
            this.label = label;
            this.suitColor = suitColor;
            this.skinColor = skinColor;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum HairStyle {
        CORTO("Corto"),
        PINCHOS("Pinchos"),
        RIZADO("Rizado"),
        COLA("Cola");

        final String label;

        HairStyle(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum HairColorPreset {
        NEGRO("Negro", new Color(23, 23, 23)),
        CASTANO("Castano", new Color(120, 72, 44)),
        RUBIO("Rubio", new Color(245, 190, 49)),
        AZUL("Azul", new Color(59, 130, 246)),
        ROSA("Rosa", new Color(244, 114, 182));

        final String label;
        final Color color;

        HairColorPreset(String label, Color color) {
            this.label = label;
            this.color = color;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum FaceTrait {
        SONRISA("Sonrisa"),
        DETERMINADO("Determinado"),
        ALEGRE("Alegre");

        final String label;

        FaceTrait(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private enum Difficulty {
        FACIL("Facil", 2),
        NORMAL("Normal", 1),
        PRO("Pro", 1);

        final String label;
        final int zombieMoveEverySteps;

        Difficulty(String label, int zombieMoveEverySteps) {
            this.label = label;
            this.zombieMoveEverySteps = zombieMoveEverySteps;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PuzzleBloquesGame game = new PuzzleBloquesGame();
            boolean start = game.showStoryAndCharacterDialog();
            if (start) {
                game.setVisible(true);
            } else {
                game.dispose();
            }
        });
    }
}
