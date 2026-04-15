# Puzzle Bloques - LibGDX (Java)

Migracion inicial de tu juego a motor **LibGDX**, manteniendo:

- tablero con niveles
- bloques de comandos (avanzar, girar)
- ejecucion paso a paso y ejecutar completo
- zombies que patrullan y pueden atraparte

## Estructura

- `core`: logica y pantalla del juego
- `lwjgl3`: launcher de escritorio

## Ejecutar con Maven (recomendado en tu entorno)

Desde `java-puzzle-bloques-libgdx`:

```powershell
mvn -q -DskipTests package
mvn -q -pl lwjgl3 exec:java
```

## Ejecutar con Gradle (opcional)

Si tienes Gradle instalado:

```powershell
gradle :lwjgl3:run
```

## Siguiente paso recomendado

En la siguiente iteracion, migramos los bloques visuales tipo rompecabezas (drag & drop real) a `Scene2D` para que quede como Scratch.
