package com.nuevooooooo.puzzle.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

/** Texturas premium del menu CodeQuest. */
public final class MenuAssets implements Disposable {
    private static final String M = "images/menu/";

    public final Texture bgBase;
    public final Texture panelHologram;
    public final Texture btnGloss;
    public final Texture btnOrange;
    public final Texture btnBlue;
    public final Texture btnPurple;
    public final Texture btnGreen;
    public final Texture btnCyan;
    public final Texture btnDark;
    public final Texture logo;
    public final Texture menuFullReference;
    public final Texture footerStarPass;
    public final Texture xpBar;
    public final Texture hudPillStar;
    public final Texture avatarFrame;
    public final Texture badgeNotification;

    public final Texture[] portalSpiral = new Texture[4];
    public final Texture portalGlow;
    public final Texture worldMoon;
    public final Texture worldAcademy;
    public final Texture worldSaturn;
    public final Texture worldNebula;
    public final Texture worldStation;
    public final Texture worldLocked;

    public final Texture iconPlay;
    public final Texture iconContinue;
    public final Texture iconMap;
    public final Texture iconTrophy;
    public final Texture iconRobot;
    public final Texture iconSettings;
    public final Texture iconStarHud;
    public final Texture iconCoinHud;
    public final Texture iconGem;
    public final Texture iconGift;
    public final Texture iconMail;
    public final Texture iconDaily;
    public final Texture iconWheel;
    public final Texture iconShop;
    public final Texture[] rankBadges = new Texture[4];

    public final Texture roboIdle;
    public final Texture monoPreview;
    public final Texture shipDecor;
    public final Texture labelBack;

    public MenuAssets() {
        bgBase = loadPrefer("images/menu_background.png", M + "bg/base_space.png");
        panelHologram = loadPrefer(M + "ui/panel_hologram_premium.png", "images/panel_hologram_menu.png");
        btnGloss = loadPrefer(M + "ui/btn_gloss_base.png", "images/btn_menu_primary.png");
        btnOrange = loadPrefer(M + "ui/btn_orange.png", M + "ui/btn_gloss_base.png");
        btnBlue = loadPrefer(M + "ui/btn_blue.png", M + "ui/btn_gloss_base.png");
        btnPurple = loadPrefer(M + "ui/btn_purple.png", M + "ui/btn_gloss_base.png");
        btnGreen = loadPrefer(M + "ui/btn_green.png", M + "ui/btn_gloss_base.png");
        btnCyan = loadPrefer(M + "ui/btn_cyan.png", M + "ui/btn_gloss_base.png");
        btnDark = loadPrefer(M + "ui/btn_dark.png", M + "ui/btn_gloss_base.png");
        logo = loadPrefer("images/logo_codequest.png", M + "ui/logo_codequest.png");
        menuFullReference = loadPrefer(M + "ui/menu_full_reference.png", M + "ui/menu_full_reference.png");
        footerStarPass = load(M + "ui/footer_star_pass.png");
        xpBar = load(M + "ui/xp_bar.png");
        hudPillStar = load(M + "ui/hud_pill_star.png");
        avatarFrame = load(M + "ui/avatar_frame.png");
        badgeNotification = load(M + "ui/badge_notification_red.png");

        for (int i = 0; i < 4; i++) {
            portalSpiral[i] = load(M + "portal/portal_spiral_" + String.format("%02d", i + 1) + ".png");
        }
        portalGlow = load(M + "portal/portal_glow.png");
        worldMoon = load(M + "worlds/world_moon.png");
        worldAcademy = load(M + "worlds/world_academy.png");
        worldSaturn = load(M + "worlds/world_saturn.png");
        worldNebula = load(M + "worlds/world_nebula.png");
        worldStation = load(M + "worlds/world_station.png");
        worldLocked = load(M + "worlds/world_locked_overlay.png");

        iconPlay = load(M + "icons/icon_play.png");
        iconContinue = load(M + "icons/icon_continue.png");
        iconMap = load(M + "icons/icon_map.png");
        iconTrophy = load(M + "icons/icon_trophy.png");
        iconRobot = load(M + "icons/icon_robot.png");
        iconSettings = load(M + "icons/icon_settings.png");
        iconStarHud = loadPrefer("images/ui/icon_star_hud.png", M + "icons/icon_star_hud.png");
        iconCoinHud = loadPrefer("images/ui/icon_coin_hud.png", M + "icons/icon_coin_hud.png");
        iconGem = load(M + "icons/icon_gem.png");
        iconGift = load(M + "icons/icon_gift.png");
        iconMail = load(M + "icons/icon_mail.png");
        iconDaily = load(M + "icons/icon_daily.png");
        iconWheel = load(M + "icons/icon_wheel.png");
        iconShop = load(M + "icons/icon_shop.png");
        rankBadges[0] = load(M + "icons/rank_novato.png");
        rankBadges[1] = load(M + "icons/rank_piloto.png");
        rankBadges[2] = load(M + "icons/rank_capitan.png");
        rankBadges[3] = load(M + "icons/rank_leyenda.png");

        roboIdle = loadPrefer("images/robo_menu_pose.png", M + "robo/robo_idle.png");
        monoPreview = loadPrefer("images/mono_espacial_wave.png", M + "robo/mono_preview.png");
        shipDecor = loadPrefer(M + "bg/ship_decor.png", "images/spaceship_menu.png");
        labelBack = load(M + "labels/label_volver.png");
    }

    private static Texture load(String path) {
        Texture t = new Texture(Gdx.files.internal(path));
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return t;
    }

    private static Texture loadPrefer(String primary, String fallback) {
        if (Gdx.files.internal(primary).exists()) {
            return load(primary);
        }
        return load(fallback);
    }

    private static void disposeIfUnique(Texture... textures) {
        java.util.HashSet<Texture> seen = new java.util.HashSet<>();
        for (Texture t : textures) {
            if (t != null && seen.add(t)) {
                t.dispose();
            }
        }
    }

    @Override
    public void dispose() {
        bgBase.dispose();
        panelHologram.dispose();
        disposeIfUnique(btnGloss, btnOrange, btnBlue, btnPurple, btnGreen, btnCyan, btnDark);
        logo.dispose();
        menuFullReference.dispose();
        footerStarPass.dispose();
        xpBar.dispose();
        hudPillStar.dispose();
        avatarFrame.dispose();
        badgeNotification.dispose();
        for (Texture p : portalSpiral) p.dispose();
        portalGlow.dispose();
        worldMoon.dispose();
        worldAcademy.dispose();
        worldSaturn.dispose();
        worldNebula.dispose();
        worldStation.dispose();
        worldLocked.dispose();
        iconPlay.dispose();
        iconContinue.dispose();
        iconMap.dispose();
        iconTrophy.dispose();
        iconRobot.dispose();
        iconSettings.dispose();
        iconStarHud.dispose();
        iconCoinHud.dispose();
        iconGem.dispose();
        iconGift.dispose();
        iconMail.dispose();
        iconDaily.dispose();
        iconWheel.dispose();
        iconShop.dispose();
        for (Texture r : rankBadges) r.dispose();
        roboIdle.dispose();
        monoPreview.dispose();
        shipDecor.dispose();
        labelBack.dispose();
    }
}
