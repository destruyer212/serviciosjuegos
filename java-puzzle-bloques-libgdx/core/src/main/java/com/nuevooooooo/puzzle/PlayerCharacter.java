package com.nuevooooooo.puzzle;

public enum PlayerCharacter {
    ROBO("Robo", "Basico"),
    MONO_ESPACIAL("Mono espacial", "Raro");

    public final String displayName;
    public final String rarity;

    PlayerCharacter(String displayName, String rarity) {
        this.displayName = displayName;
        this.rarity = rarity;
    }
}
