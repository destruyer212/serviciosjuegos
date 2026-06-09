package com.nuevooooooo.puzzle.ui.hud;

/**
 * Datos de solo lectura para pintar el HUD sin acoplar a la lógica del juego.
 */
public final class HudState {
    public final int levelNumber;
    public final String missionTitle;
    public final String missionGoal;
    public final int starsEarned;
    public final int maxStars;
    public final int coins;
    public final int lives;
    public final String roboMessage;
    public final String characterName;
    public final int achievementBadge;
    public final int robotsBadge;
    public final int chestReward;
    public final int lifeCost;
    public final boolean canBuyLife;

    public HudState(int levelNumber, String missionTitle, String missionGoal,
                    int starsEarned, int maxStars, int coins, int lives,
                    String roboMessage, String characterName,
                    int achievementBadge, int robotsBadge, int chestReward,
                    int lifeCost, boolean canBuyLife) {
        this.levelNumber = levelNumber;
        this.missionTitle = missionTitle;
        this.missionGoal = missionGoal;
        this.starsEarned = starsEarned;
        this.maxStars = maxStars;
        this.coins = coins;
        this.lives = lives;
        this.roboMessage = roboMessage;
        this.characterName = characterName;
        this.achievementBadge = achievementBadge;
        this.robotsBadge = robotsBadge;
        this.chestReward = chestReward;
        this.lifeCost = lifeCost;
        this.canBuyLife = canBuyLife;
    }
}
