package com.nuevooooooo.puzzle;

import com.badlogic.gdx.graphics.Color;

public class HeroConfig {
    public String name = "Valiente";
    public Suit suit = Suit.ASTRONAUTA;
    public HairStyle hairStyle = HairStyle.CORTO;
    public HairColor hairColor = HairColor.CASTANO;
    public Face face = Face.SONRISA;
    public Difficulty difficulty = Difficulty.NORMAL;

    public enum Suit {
        ASTRONAUTA("Astronauta", new Color(0.23f, 0.51f, 0.96f, 1f), new Color(1f, 0.9f, 0.6f, 1f)),
        CABALLERO("Caballero", new Color(0.40f, 0.38f, 0.95f, 1f), new Color(0.90f, 0.92f, 1f, 1f)),
        EXPLORADORA("Exploradora", new Color(0.92f, 0.28f, 0.60f, 1f), new Color(0.99f, 0.84f, 0.68f, 1f));

        public final String label;
        public final Color suitColor;
        public final Color skinColor;

        Suit(String label, Color suitColor, Color skinColor) {
            this.label = label;
            this.suitColor = suitColor;
            this.skinColor = skinColor;
        }
    }

    public enum HairStyle {
        CORTO("Corto"),
        PINCHOS("Pinchos"),
        RIZADO("Rizado"),
        COLA("Cola");
        public final String label;
        HairStyle(String label) { this.label = label; }
    }

    public enum HairColor {
        NEGRO("Negro", new Color(0.09f, 0.09f, 0.09f, 1f)),
        CASTANO("Castano", new Color(0.45f, 0.27f, 0.16f, 1f)),
        RUBIO("Rubio", new Color(0.96f, 0.76f, 0.2f, 1f)),
        AZUL("Azul", new Color(0.24f, 0.52f, 0.95f, 1f)),
        ROSA("Rosa", new Color(0.96f, 0.45f, 0.74f, 1f));
        public final String label;
        public final Color color;
        HairColor(String label, Color color) { this.label = label; this.color = color; }
    }

    public enum Face {
        SONRISA("Sonrisa"),
        DETERMINADO("Determinado"),
        ALEGRE("Alegre");
        public final String label;
        Face(String label) { this.label = label; }
    }

    public enum Difficulty {
        FACIL("Facil", 2),
        NORMAL("Normal", 1),
        PRO("Pro", 1);
        public final String label;
        public final int zombieStepRate;
        Difficulty(String label, int zombieStepRate) {
            this.label = label;
            this.zombieStepRate = zombieStepRate;
        }
    }
}
