package com.nuevooooooo.puzzle.teavm;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.nuevooooooo.puzzle.PuzzleBloquesLibGDXGame;

public class WebLauncher {

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration();
        // 0 = adaptar al tamano del navegador; FitViewport (1280x720) mantiene proporcion.
        config.width = 0;
        config.height = 0;
        config.padHorizontal = 0;
        config.padVertical = 0;
        config.showDownloadLogs = true;
        new WebApplication(new PuzzleBloquesLibGDXGame(), config);
    }
}
