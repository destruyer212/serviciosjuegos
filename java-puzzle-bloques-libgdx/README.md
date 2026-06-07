# Puzzle Bloques - LibGDX (Java)

Migracion inicial de tu juego a motor **LibGDX**, manteniendo:

- tablero con niveles
- bloques de comandos (avanzar, girar)
- ejecucion paso a paso y ejecutar completo
- zombies que patrullan y pueden atraparte

## Estructura

- `core`: logica y pantalla del juego
- `lwjgl3`: launcher de escritorio
- `core/src/main/resources/images`: assets finales usados por LibGDX

## Convencion de assets

Guarda los assets finales del juego con estos nombres:

- `menu_background.png`
- `logo_codequest.png`
- `robo_menu_pose.png`
- `btn_menu_primary.png`
- `panel_hologram_menu.png`
- `space_background_main.png`
- `robo_base.png`
- `robo_idle.png`
- `robo_up.png`
- `robo_down.png`
- `robo_left.png`
- `robo_right.png`
- `robo_win.png`
- `robo_error.png`

La carpeta raiz `img/` puede quedar como biblioteca de referencia o imagenes fuente. El juego carga los assets desde `core/src/main/resources/images`.
La carpeta raiz `img2/` contiene los assets fuente del menu principal.

## Ejecutar con Maven (recomendado en tu entorno)

Desde `java-puzzle-bloques-libgdx`:

```powershell
mvn -q -DskipTests install
mvn -q -pl lwjgl3 exec:java
```

Nota: usa `install`, no solo `package`, porque `lwjgl3` carga el módulo `core` desde tu repositorio Maven local cuando ejecutas con `-pl lwjgl3`.

## Ejecutar con Gradle (opcional)

Si tienes Gradle instalado:

```powershell
gradle :lwjgl3:run
```

## Siguiente paso recomendado

En la siguiente iteracion, migramos los bloques visuales tipo rompecabezas (drag & drop real) a `Scene2D` para que quede como Scratch.
