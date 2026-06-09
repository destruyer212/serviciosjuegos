package com.nuevooooooo.puzzle;

public final class GameState {
    private static final int STARTING_LIVES = 3;

    public static final int CREDITS_PER_STAR = 15;
    public static final int LIFE_COST = 50;
    public static final int MONO_COST = 30;
    public static final int MAX_LIVES = 5;

    private static PlayerCharacter selectedCharacter = PlayerCharacter.ROBO;
    private static int sessionCoins;
    private static int starsEarned;
    private static int maxStars = 8;
    private static int lives = STARTING_LIVES;
    private static int currentLevelIndex;
    private static int achievementCount = 3;
    private static int robotsUnlocked = 2;
    private static boolean monoUnlocked;

    private GameState() {
    }

    public static PlayerCharacter selectedCharacter() {
        return selectedCharacter;
    }

    public static void selectCharacter(PlayerCharacter character) {
        if (character == PlayerCharacter.MONO_ESPACIAL && !monoUnlocked) {
            selectedCharacter = PlayerCharacter.ROBO;
            return;
        }
        selectedCharacter = character;
    }

    public static boolean isMonoUnlocked() {
        return monoUnlocked;
    }

    /** Desbloquea el mono espacial por 30 creditos (una vez por sesion). */
    public static boolean tryUnlockMono() {
        if (monoUnlocked) {
            return true;
        }
        if (sessionCoins < MONO_COST) {
            return false;
        }
        sessionCoins -= MONO_COST;
        monoUnlocked = true;
        return true;
    }

    public static int sessionCoins() {
        return sessionCoins;
    }

    public static int starsEarned() {
        return starsEarned;
    }

    public static int maxStars() {
        return maxStars;
    }

    public static int lives() {
        return lives;
    }

    public static int currentLevelIndex() {
        return currentLevelIndex;
    }

    public static int totalMissions() {
        return 3;
    }

    public static void setCurrentLevelIndex(int index) {
        currentLevelIndex = Math.max(0, Math.min(index, totalMissions() - 1));
    }

    public static void addCoins(int amount) {
        sessionCoins += Math.max(0, amount);
    }

    /** Suma estrellas recogidas en el mapa (no la calificacion 1-3 del nivel). */
    public static void addStars(int amount) {
        starsEarned = Math.min(maxStars, starsEarned + Math.max(0, amount));
    }

    public static int creditsForStars(int starCount) {
        return Math.max(0, starCount) * CREDITS_PER_STAR;
    }

    public static void setMaxStars(int max) {
        maxStars = Math.max(1, max);
        starsEarned = Math.min(starsEarned, maxStars);
    }

    public static boolean canBuyLife() {
        return lives < MAX_LIVES && sessionCoins >= LIFE_COST;
    }

    public static boolean buyLife() {
        if (!canBuyLife()) {
            return false;
        }
        sessionCoins -= LIFE_COST;
        lives++;
        return true;
    }

    /** Resta una vida y devuelve cuántas quedan. */
    public static int loseLife() {
        lives = Math.max(0, lives - 1);
        return lives;
    }

    public static boolean hasLives() {
        return lives > 0;
    }

    public static int achievementCount() {
        return achievementCount;
    }

    public static int robotsUnlocked() {
        return robotsUnlocked;
    }

    public static int robotsUnlockedCount() {
        int count = 1;
        if (monoUnlocked) {
            count++;
        }
        return count;
    }

    public static int gems() {
        return sessionCoins / 10;
    }

    public static int playerLevel() {
        return 1 + starsEarned / 2 + currentLevelIndex;
    }

    public static int rankIndex() {
        if (starsEarned >= 10) return 3;
        if (starsEarned >= 5) return 2;
        if (starsEarned >= 2) return 1;
        return 0;
    }

    public static String rankTitle() {
        return switch (rankIndex()) {
            case 3 -> "Leyenda Cosmica";
            case 2 -> "Capitan Galactico";
            case 1 -> "Piloto Estelar";
            default -> "Explorador Novato";
        };
    }

    /** Progreso XP hacia el siguiente nivel (0..1). */
    public static float xpProgress() {
        int level = playerLevel();
        int into = starsEarned % 4;
        return Math.min(1f, (into + currentLevelIndex() * 0.25f) / 4f + 0.15f * (level % 3));
    }

    public static boolean hasSaveProgress() {
        return currentLevelIndex > 0 || starsEarned > 0 || sessionCoins > 0;
    }

    /** Sin vidas: vuelve al nivel 1 con 3 vidas y progreso de sesión reiniciado. */
    public static void resetAfterGameOver() {
        currentLevelIndex = 0;
        lives = STARTING_LIVES;
        sessionCoins = 0;
        starsEarned = 0;
        monoUnlocked = false;
        selectedCharacter = PlayerCharacter.ROBO;
    }

    public static void resetSessionProgress() {
        resetAfterGameOver();
        achievementCount = 3;
        robotsUnlocked = 2;
    }
}
