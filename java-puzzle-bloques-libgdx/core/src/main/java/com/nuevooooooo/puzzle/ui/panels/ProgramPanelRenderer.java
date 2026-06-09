package com.nuevooooooo.puzzle.ui.panels;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.nuevooooooo.puzzle.ui.theme.SpaceTheme;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeAssets;
import com.nuevooooooo.puzzle.ui.theme.SpaceThemeRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Panel derecho "MI PROGRAMA" con bloques anidados, recorte y sin desbordar botones.
 */
public final class ProgramPanelRenderer {
    private static final float ICON_SIZE = 22f;
    private static final float ICON_PAD = 8f;
    private static final float TEXT_X = 36f;

    private final SpaceThemeRenderer theme;
    private final SpaceThemeAssets assets;
    private final BitmapFont font;
    private final BitmapFont smallFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final List<ProgramChipLayout> chipLayouts = new ArrayList<>();
    private final Rectangle clipBounds = new Rectangle();
    private final Rectangle scissor = new Rectangle();

    private float chipH = 28f;
    private float chipGap = 5f;
    private int hiddenChipCount = 0;

    public ProgramPanelRenderer(SpaceThemeRenderer theme, BitmapFont font, BitmapFont smallFont) {
        this.theme = theme;
        this.assets = theme.assets();
        this.font = font;
        this.smallFont = smallFont;
    }

    public List<ProgramChipLayout> chipLayouts() {
        return chipLayouts;
    }

    public int hiddenChipCount() {
        return hiddenChipCount;
    }

    public void rebuildLayout(List<ProgramCommandView> program, int activeIndex) {
        chipLayouts.clear();
        hiddenChipCount = 0;
        if (program.isEmpty()) {
            return;
        }

        chipH = program.size() > 6 ? 24f : 28f;
        chipGap = program.size() > 6 ? 4f : 5f;

        Set<Integer> hidden = new HashSet<>();
        for (int i = 1; i < program.size(); i++) {
            if (program.get(i).repeatHeader && program.get(i - 1).repeatable) {
                hidden.add(i - 1);
            }
        }

        float contentBottom = SpaceTheme.programContentBottom();
        float y = SpaceTheme.programContentTop();
        int displayNumber = 1;

        for (int i = 0; i < program.size(); i++) {
            if (hidden.contains(i)) {
                continue;
            }

            ProgramCommandView cmd = program.get(i);
            float blockX = SpaceTheme.PROGRAM_X + SpaceTheme.PROGRAM_PAD_X;
            float blockW = SpaceTheme.PROGRAM_W - SpaceTheme.PROGRAM_PAD_X * 2f;

            if (cmd.repeatHeader) {
                float blockHeight = chipH + 4f;
                float nestedExtra = (i > 0 && program.get(i - 1).repeatable) ? chipH + chipGap : 0f;
                if (y - blockHeight - nestedExtra < contentBottom) {
                    hiddenChipCount += countRemaining(program, hidden, i);
                    break;
                }

                chipLayouts.add(new ProgramChipLayout(i, displayNumber++, cmd.label, cmd.iconKey, cmd.color,
                        blockX, y - blockHeight, blockW, blockHeight, false, true, activeIndex == i));
                y -= blockHeight + chipGap;

                if (i > 0 && program.get(i - 1).repeatable) {
                    ProgramCommandView nested = program.get(i - 1);
                    float nestX = blockX + 20f;
                    float nestW = blockW - 24f;
                    chipLayouts.add(new ProgramChipLayout(i, -1, nested.label, nested.iconKey, nested.color,
                            nestX, y - chipH, nestW, chipH, true, false, activeIndex == i));
                    y -= chipH + chipGap;
                }
                continue;
            }

            if (y - chipH < contentBottom) {
                hiddenChipCount += countRemaining(program, hidden, i);
                break;
            }

            chipLayouts.add(new ProgramChipLayout(i, displayNumber++, cmd.label, cmd.iconKey, cmd.color,
                    blockX, y - chipH, blockW, chipH, false, false, activeIndex == i));
            y -= chipH + chipGap;
        }
    }

    private int countRemaining(List<ProgramCommandView> program, Set<Integer> hidden, int fromIndex) {
        int count = 0;
        for (int i = fromIndex; i < program.size(); i++) {
            if (!hidden.contains(i)) {
                count++;
            }
        }
        return count;
    }

    public int programIndexAt(float x, float y) {
        for (ProgramChipLayout chip : chipLayouts) {
            if (chip.hitRect.contains(x, y)) {
                return chip.programIndex;
            }
        }
        return -1;
    }

    public void renderBackground(SpriteBatch batch, ShapeRenderer sr) {
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.panelProgram,
                SpaceTheme.PROGRAM_X - 8f, SpaceTheme.PROGRAM_Y - 10f,
                SpaceTheme.PROGRAM_W + 16f, SpaceTheme.PROGRAM_H + 14f);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        theme.drawNeonEdge(sr, SpaceTheme.PROGRAM_X, SpaceTheme.PROGRAM_Y,
                SpaceTheme.PROGRAM_W, SpaceTheme.PROGRAM_H, 18f, SpaceTheme.CYAN);
        sr.end();

        batch.begin();
        float headerY = SpaceTheme.PROGRAM_Y + SpaceTheme.PROGRAM_H - SpaceTheme.PROGRAM_HEADER_H;
        font.setColor(Color.WHITE);
        theme.drawCenteredText(font, batch, "MI PROGRAMA",
                SpaceTheme.PROGRAM_X + 14f, headerY,
                SpaceTheme.PROGRAM_W - SpaceTheme.CODE_BTN_W - 36f, SpaceTheme.PROGRAM_HEADER_H);
        batch.end();
    }

    public void renderChips(SpriteBatch batch, Camera camera, List<ProgramCommandView> program) {
        if (program.isEmpty()) {
            batch.begin();
            batch.setColor(1f, 1f, 1f, 0.35f);
            batch.draw(assets.panelDarkBlue,
                    SpaceTheme.PROGRAM_X + 20f, SpaceTheme.PROGRAM_Y + 28f,
                    SpaceTheme.PROGRAM_W - 40f, 58f);
            batch.setColor(Color.WHITE);
            batch.end();
            return;
        }

        clipBounds.set(
                SpaceTheme.PROGRAM_X + 8f,
                SpaceTheme.programContentBottom(),
                SpaceTheme.PROGRAM_W - 16f,
                SpaceTheme.programContentTop() - SpaceTheme.programContentBottom());

        batch.begin();
        batch.flush();
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissor);
        if (ScissorStack.pushScissors(scissor)) {
            batch.setColor(Color.WHITE);
            for (ProgramChipLayout chip : chipLayouts) {
                if (chip.nested) {
                    batch.draw(assets.loopNestBar, chip.hitRect.x - 10f, chip.hitRect.y - 1f,
                            6f, chip.hitRect.height + 2f);
                }
                batch.draw(chipBg(chip), chip.hitRect.x, chip.hitRect.y, chip.hitRect.width, chip.hitRect.height);
                if (chip.active) {
                    batch.setColor(1f, 0.92f, 0.35f, 0.40f);
                    batch.draw(assets.blockChipActive,
                            chip.hitRect.x - 2f, chip.hitRect.y - 2f,
                            chip.hitRect.width + 4f, chip.hitRect.height + 4f);
                    batch.setColor(Color.WHITE);
                }
            }
            batch.flush();
            ScissorStack.popScissors();
        }
        batch.end();
    }

    private Texture chipBg(ProgramChipLayout chip) {
        if (chip.repeatHeader) {
            return assets.blockLoopOrange;
        }
        if (chip.color.r > 0.45f && chip.color.b > 0.85f) {
            return assets.blockLogicPurple;
        }
        if (chip.color.g > 0.65f && chip.color.r < 0.5f) {
            return assets.blockActionGreen;
        }
        if (chip.color.r > 0.85f && chip.color.g > 0.45f) {
            return assets.blockLoopOrange;
        }
        return assets.blockMoveBlue;
    }

    public void renderChipLabels(SpriteBatch batch, Camera camera) {
        batch.begin();
        if (chipLayouts.isEmpty()) {
            float textX = SpaceTheme.PROGRAM_X + 24f;
            float textTop = SpaceTheme.PROGRAM_Y + SpaceTheme.PROGRAM_H - 88f;
            smallFont.setColor(0.78f, 0.93f, 1f, 1f);
            smallFont.draw(batch, "Toca bloques para crear", textX, textTop);
            smallFont.draw(batch, "tu algoritmo.", textX, textTop - 22f);
            smallFont.setColor(0.52f, 0.78f, 1f, 1f);
            smallFont.draw(batch, "El orden aparecera aqui.", textX, textTop - 44f);
            batch.end();
            return;
        }

        clipBounds.set(
                SpaceTheme.PROGRAM_X + 8f,
                SpaceTheme.programContentBottom(),
                SpaceTheme.PROGRAM_W - 16f,
                SpaceTheme.programContentTop() - SpaceTheme.programContentBottom());

        batch.flush();
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissor);
        if (ScissorStack.pushScissors(scissor)) {
            for (ProgramChipLayout chip : chipLayouts) {
                smallFont.setColor(chip.active ? new Color(1f, 0.96f, 0.58f, 1f) : Color.WHITE);
                String prefix = chip.displayNumber > 0 ? chip.displayNumber + ". " : "";
                if (chip.nested) {
                    prefix = "› ";
                }
                Texture icon = chipIcon(chip.iconKey);
                if (icon != null) {
                    batch.draw(icon, chip.hitRect.x + ICON_PAD, chip.hitRect.y + (chip.hitRect.height - ICON_SIZE) / 2f,
                            ICON_SIZE, ICON_SIZE);
                }
                smallFont.draw(batch, prefix + chip.label, chip.hitRect.x + TEXT_X, chip.hitRect.y + chip.hitRect.height * 0.68f);
            }
            batch.flush();
            ScissorStack.popScissors();
        }

        if (hiddenChipCount > 0) {
            smallFont.setColor(0.78f, 0.93f, 1f, 1f);
            smallFont.draw(batch, "+" + hiddenChipCount + " bloques mas (usa modal codigo)",
                    SpaceTheme.PROGRAM_X + 18f, SpaceTheme.programContentBottom() + 14f);
        }
        batch.end();
    }

    public void renderCodeButton(SpriteBatch batch, Rectangle button) {
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(assets.btnCode, button.x, button.y, button.width, button.height);
        smallFont.setColor(Color.WHITE);
        theme.drawCenteredText(smallFont, batch, "Ver codigo", button.x, button.y, button.width, button.height);
        batch.end();
    }

    private Texture chipIcon(String iconKey) {
        if (iconKey == null) {
            return null;
        }
        return switch (iconKey) {
            case "forward" -> assets.iconArrowForward;
            case "turn_left" -> assets.iconTurnLeft;
            case "turn_right" -> assets.iconTurnRight;
            case "loop" -> assets.iconRepeat;
            case "wall" -> assets.iconWall;
            case "path" -> assets.iconPathFree;
            case "star" -> assets.iconCollectStar;
            case "ship" -> assets.iconUseShip;
            default -> null;
        };
    }

    public static final class ProgramCommandView {
        public final String label;
        public final String iconKey;
        public final Color color;
        public final boolean repeatable;
        public final boolean repeatHeader;

        public ProgramCommandView(String label, String iconKey, Color color, boolean repeatable, boolean repeatHeader) {
            this.label = label;
            this.iconKey = iconKey;
            this.color = color;
            this.repeatable = repeatable;
            this.repeatHeader = repeatHeader;
        }
    }

    public static final class ProgramChipLayout {
        public final int programIndex;
        public final int displayNumber;
        public final String label;
        public final String iconKey;
        public final Color color;
        public final Rectangle hitRect;
        public final boolean nested;
        public final boolean repeatHeader;
        public final boolean active;

        public ProgramChipLayout(int programIndex, int displayNumber, String label, String iconKey, Color color,
                                 float x, float y, float w, float h,
                                 boolean nested, boolean repeatHeader, boolean active) {
            this.programIndex = programIndex;
            this.displayNumber = displayNumber;
            this.label = label;
            this.iconKey = iconKey;
            this.color = color.cpy();
            this.hitRect = new Rectangle(x, y, w, h);
            this.nested = nested;
            this.repeatHeader = repeatHeader;
            this.active = active;
        }
    }
}
