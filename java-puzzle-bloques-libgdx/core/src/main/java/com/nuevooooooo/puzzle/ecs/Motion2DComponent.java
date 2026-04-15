package com.nuevooooooo.puzzle.ecs;

import com.badlogic.ashley.core.Component;

public class Motion2DComponent implements Component {
    public float x;
    public float y;
    public float fromX;
    public float fromY;
    public float toX;
    public float toY;
    public float timer;
    public float duration = 0.17f;
    public boolean moving;
}
