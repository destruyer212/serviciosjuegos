package com.nuevooooooo.puzzle.ui.theme;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Texturas del tema espacial: menú, mapa, HUD, botones y efectos (IMG3).
 */
public final class SpaceThemeAssets {
    public final Texture spaceBackground;
    public final Texture bgGameParallaxFar;
    public final Texture bgGameParallaxStars;
    public final Texture alienPlanetBackground;
    public final Texture menuBackground;
    public final Texture logo;
    public final Texture panelHologram;
    public final Texture menuButton;
    public final Texture roboMini;

    public final Texture tileGrass;
    public final Texture tileGrassVariant2;
    public final Texture tileWallGrass;
    public final Texture tilePath;
    public final Texture tilePathCorner;
    public final Texture tileFlightSpace;
    public final Texture tileGoal;
    public final Texture tileStart;

    public final Texture rockLarge;
    public final Texture rockSmall;
    public final Texture bushGreen;
    public final Texture flowerPink;
    public final Texture plantSprout;
    public final Texture spaceshipGoal;

    public final Texture starGold;
    public final Texture starGoldSmall;
    public final Texture coinGold;

    public final Texture boardFrame;
    public final Texture boardInnerGlow;

    public final Texture hudPill;
    public final Texture iconStarHud;
    public final Texture iconCoinHud;
    public final Texture iconHeartHud;
    public final Texture btnMenu;
    public final Texture btnExecute;
    public final Texture btnStep;
    public final Texture btnUndo;
    public final Texture btnClear;
    public final Texture btnSecondary;
    public final Texture speechBubble;
    public final Texture iconChestGold;
    public final Texture iconTrophyGold;
    public final Texture iconRobotCollection;
    public final Texture badgeNotificationRed;
    public final Texture iconGlobeObjective;
    public final Texture panelHologramGame;
    public final Texture panelProgram;
    public final Texture panelDarkBlue;
    public final Texture iconPlanetMission;
    public final Texture labelInicio;
    public final Texture labelMeta;
    public final Texture btnCode;
    public final Texture modalWinPanel;
    public final Texture modalFailPanel;
    public final Texture bannerVictory;
    public final Texture pathArrowPreview;

    public final Texture blockMoveBlue;
    public final Texture blockTurnBlue;
    public final Texture blockLoopOrange;
    public final Texture blockLogicPurple;
    public final Texture blockActionGreen;
    public final Texture blockChipActive;
    public final Texture iconArrowForward;
    public final Texture iconTurnLeft;
    public final Texture iconTurnRight;
    public final Texture iconRepeat;
    public final Texture iconWall;
    public final Texture iconPathFree;
    public final Texture iconCollectStar;
    public final Texture iconUseShip;
    public final Texture loopNestBar;

    public final Texture starGlow;
    public final Texture starCollectBurst;
    public final Texture starSparkle;

    public SpaceThemeAssets() {
        spaceBackground = load("images/space_background_main.png");
        bgGameParallaxFar = load("images/bg_game_parallax_far.png");
        bgGameParallaxStars = load("images/bg_game_parallax_stars.png");
        alienPlanetBackground = load("images/bg_alien_planet.png");
        menuBackground = load("images/menu_background.png");
        logo = load("images/logo_codequest.png");
        panelHologram = load("images/panel_hologram_menu.png");
        menuButton = load("images/btn_menu_primary.png");
        roboMini = load("images/robo_menu_pose.png");

        tileGrass = load("images/tiles/tile_grass.png");
        tileGrassVariant2 = load("images/tiles/tile_grass_variant2.png");
        tileWallGrass = load("images/tiles/tile_wall_grass.png");
        tilePath = load("images/tiles/tile_path.png");
        tilePathCorner = load("images/tiles/tile_path_corner.png");
        tileFlightSpace = load("images/tiles/tile_flight_space.png");
        tileGoal = load("images/tiles/tile_goal.png");
        tileStart = load("images/tiles/tile_start.png");

        rockLarge = load("images/props/rock_large.png");
        rockSmall = load("images/props/rock_small.png");
        bushGreen = load("images/props/bush_green.png");
        flowerPink = load("images/props/flower_pink.png");
        plantSprout = load("images/props/plant_sprout.png");
        spaceshipGoal = load("images/props/spaceship_goal.png");

        starGold = load("images/collectibles/star_gold.png");
        starGoldSmall = load("images/collectibles/star_gold_small.png");
        coinGold = load("images/collectibles/coin_gold.png");

        boardFrame = load("images/board/board_frame_9slice.png");
        boardInnerGlow = load("images/board/board_inner_glow.png");

        hudPill = load("images/ui/hud_pill.png");
        iconStarHud = load("images/ui/icon_star_hud.png");
        iconCoinHud = load("images/ui/icon_coin_hud.png");
        iconHeartHud = load("images/ui/icon_heart_hud.png");
        btnMenu = load("images/ui/btn_menu.png");
        btnExecute = load("images/ui/btn_execute.png");
        btnStep = load("images/ui/btn_step.png");
        btnUndo = load("images/ui/btn_undo.png");
        btnClear = load("images/ui/btn_clear.png");
        btnSecondary = load("images/ui/btn_secondary.png");
        speechBubble = load("images/ui/speech_bubble.png");
        iconChestGold = load("images/ui/icon_chest_gold.png");
        iconTrophyGold = load("images/ui/icon_trophy_gold.png");
        iconRobotCollection = load("images/ui/icon_robot_collection.png");
        badgeNotificationRed = load("images/ui/badge_notification_red.png");
        iconGlobeObjective = load("images/ui/icon_globe_objective.png");
        panelHologramGame = load("images/ui/panel_hologram.png");
        panelProgram = load("images/ui/panel_program.png");
        panelDarkBlue = load("images/ui/panel_dark_blue.png");
        iconPlanetMission = load("images/ui/icon_planet_mission.png");
        labelInicio = load("images/ui/label_inicio.png");
        labelMeta = load("images/ui/label_meta.png");
        btnCode = load("images/ui/btn_code.png");
        modalWinPanel = load("images/ui/modal_win_panel.png");
        modalFailPanel = load("images/ui/modal_fail_panel.png");
        bannerVictory = load("images/ui/banner_victory.png");
        pathArrowPreview = load("images/fx/path_arrow_preview.png");

        blockMoveBlue = load("images/blocks/block_move_blue.png");
        blockTurnBlue = load("images/blocks/block_turn_blue.png");
        blockLoopOrange = load("images/blocks/block_loop_orange.png");
        blockLogicPurple = load("images/blocks/block_logic_purple.png");
        blockActionGreen = load("images/blocks/block_action_green.png");
        blockChipActive = load("images/blocks/block_chip_active.png");
        iconArrowForward = load("images/blocks/icon_arrow_forward.png");
        iconTurnLeft = load("images/blocks/icon_turn_left.png");
        iconTurnRight = load("images/blocks/icon_turn_right.png");
        iconRepeat = load("images/blocks/icon_repeat.png");
        iconWall = load("images/blocks/icon_wall.png");
        iconPathFree = load("images/blocks/icon_path_free.png");
        iconCollectStar = load("images/blocks/icon_collect_star.png");
        iconUseShip = load("images/blocks/icon_use_ship.png");
        loopNestBar = load("images/ui/loop_nest_bar.png");

        starGlow = load("images/fx/star_glow.png");
        starCollectBurst = load("images/fx/star_collect_burst.png");
        starSparkle = load("images/fx/star_sparkle.png");
    }

    private static Texture load(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    public void dispose() {
        spaceBackground.dispose();
        bgGameParallaxFar.dispose();
        bgGameParallaxStars.dispose();
        alienPlanetBackground.dispose();
        menuBackground.dispose();
        logo.dispose();
        panelHologram.dispose();
        menuButton.dispose();
        roboMini.dispose();

        tileGrass.dispose();
        tileGrassVariant2.dispose();
        tileWallGrass.dispose();
        tilePath.dispose();
        tilePathCorner.dispose();
        tileFlightSpace.dispose();
        tileGoal.dispose();
        tileStart.dispose();

        rockLarge.dispose();
        rockSmall.dispose();
        bushGreen.dispose();
        flowerPink.dispose();
        plantSprout.dispose();
        spaceshipGoal.dispose();

        starGold.dispose();
        starGoldSmall.dispose();
        coinGold.dispose();

        boardFrame.dispose();
        boardInnerGlow.dispose();

        hudPill.dispose();
        iconStarHud.dispose();
        iconCoinHud.dispose();
        iconHeartHud.dispose();
        btnMenu.dispose();
        btnExecute.dispose();
        btnStep.dispose();
        btnUndo.dispose();
        btnClear.dispose();
        btnSecondary.dispose();
        speechBubble.dispose();
        iconChestGold.dispose();
        iconTrophyGold.dispose();
        iconRobotCollection.dispose();
        badgeNotificationRed.dispose();
        iconGlobeObjective.dispose();
        panelHologramGame.dispose();
        panelProgram.dispose();
        panelDarkBlue.dispose();
        iconPlanetMission.dispose();
        labelInicio.dispose();
        labelMeta.dispose();
        btnCode.dispose();
        modalWinPanel.dispose();
        modalFailPanel.dispose();
        bannerVictory.dispose();
        pathArrowPreview.dispose();

        blockMoveBlue.dispose();
        blockTurnBlue.dispose();
        blockLoopOrange.dispose();
        blockLogicPurple.dispose();
        blockActionGreen.dispose();
        blockChipActive.dispose();
        iconArrowForward.dispose();
        iconTurnLeft.dispose();
        iconTurnRight.dispose();
        iconRepeat.dispose();
        iconWall.dispose();
        iconPathFree.dispose();
        iconCollectStar.dispose();
        iconUseShip.dispose();
        loopNestBar.dispose();

        starGlow.dispose();
        starCollectBurst.dispose();
        starSparkle.dispose();
    }
}
