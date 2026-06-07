package com.nuevooooooo.puzzle;

public final class GameState {
    private static PlayerCharacter selectedCharacter = PlayerCharacter.ROBO;

    private GameState() {
    }

    public static PlayerCharacter selectedCharacter() {
        return selectedCharacter;
    }

    public static void selectCharacter(PlayerCharacter character) {
        selectedCharacter = character;
    }
}
