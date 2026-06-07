package com.nuevooooooo.puzzle;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        warnIfUnsupportedJvm();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Codea y Juega con Robo");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        new Lwjgl3Application(new PuzzleBloquesLibGDXGame(), config);
    }

    /** Java 23+ no va bien con LWJGL 3.3.x usado por LibGDX 1.13 (JNI / texto / ventana). */
    private static void warnIfUnsupportedJvm() {
        String spec = System.getProperty("java.specification.version", "17");
        try {
            int major;
            if (spec.contains(".")) {
                major = Integer.parseInt(spec.split("\\.")[0]);
            } else {
                major = Integer.parseInt(spec);
            }
            if (major >= 23) {
                System.err.println("[Puzzle Bloques] Tu JDK es Java " + spec
                        + ". LibGDX+LWJGL suelen fallar o no dibujar texto. Usa JDK 17 u 21 (Temurin) y vuelve a ejecutar.");
                System.err.println("[Puzzle Bloques] Para compilar cambios nuevos usa: run-game.cmd  (no solo mvn -pl lwjgl3 exec:java)");
            }
        } catch (NumberFormatException ignored) {
            // no-op
        }
    }
}
