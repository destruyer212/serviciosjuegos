# Puzzle de Bloques en Java

Juego tipo puzzle inspirado en Scratch/Code.org:

- Lado izquierdo: tablero del juego.
- Lado derecho: bloques tipo rompecabezas para construir el programa.
- Objetivo: llevar el personaje hasta la flor.
- Puedes arrastrar bloques y encajarlos, incluso dentro de bloques contenedor.

## Requisitos

- Java 17 o superior.

## Ejecutar

Desde la carpeta `java-puzzle-bloques`:

```powershell
javac -d out src\PuzzleBloquesGame.java
java -cp out PuzzleBloquesGame
```

## Controles

- `Avanzar`: mueve una casilla adelante.
- `Girar Izquierda` / `Girar Derecha`: cambia orientación.
- `Si hay camino al frente/izquierda/derecha`: condiciones.
- `Repetir x2` y `x3` avanzar: repetición rápida.
- `Si izquierda { }` y `Repetir x3 { }`: bloques contenedor con piezas anidadas.
- `Alinear`: ordena visualmente las piezas de arriba hacia abajo.
- `Paso`: ejecuta el bloque seleccionado.
- `Ejecutar`: corre todo el programa.
- `Reiniciar`: vuelve al inicio.
- `Nivel 1/2/3`: cambia mapa y reinicia el programa.
