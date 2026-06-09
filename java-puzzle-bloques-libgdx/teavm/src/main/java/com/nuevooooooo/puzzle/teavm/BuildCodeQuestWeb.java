package com.nuevooooooo.puzzle.teavm;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import com.github.xpenatan.gdx.teavm.backends.web.config.plugins.WebClassFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildCodeQuestWeb {

    public static void main(String[] args) {
        boolean run = args.length == 0 || "run".equalsIgnoreCase(args[0]);

        // AsyncExecutor$1 implementa ThreadFactory, que TeaVM no emite en JS.
        WebClassFilter.addClassToExclude("com.badlogic.gdx.utils.async.AsyncExecutor$1");
        WebClassFilter.addClassToExclude("com.badlogic.gdx.utils.async.AsyncExecutor$2");

        AssetFileHandle assetsPath = new AssetFileHandle("../core/src/main/resources");

        WebBackend backend = new WebBackend()
                .setHtmlTitle("CodeQuest")
                .setHtmlWidth(1280)
                .setHtmlHeight(720)
                .setStartJettyAfterBuild(run)
                .setJettyPort(8082);

        File outputDir = new File("build/dist/web");
        new TeaCompiler(backend)
                .addAssets(assetsPath)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(WebLauncher.class.getName())
                .setObfuscated(false)
                .addReflectionClass("com.nuevooooooo.puzzle.**")
                .build(outputDir);

        copyResponsiveIndexHtml(new File(outputDir, "webapp/index.html"));
    }

    private static void copyResponsiveIndexHtml(File target) {
        File source = new File("webapp/index.html");
        if (!source.isFile()) {
            System.err.println("[CodeQuest web] No se encontro " + source.getAbsolutePath());
            return;
        }
        try {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo copiar index.html responsive", e);
        }
    }
}
