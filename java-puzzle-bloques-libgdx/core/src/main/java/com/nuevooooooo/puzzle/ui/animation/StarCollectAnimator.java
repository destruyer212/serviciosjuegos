package com.nuevooooooo.puzzle.ui.animation;

import com.badlogic.gdx.utils.Array;

/**
 * Efectos breves al recoger estrellas en el mapa.
 */
public final class StarCollectAnimator {
    public static final class Burst {
        public final float x;
        public final float y;
        public float age;
        public final float duration;

        public Burst(float x, float y, float duration) {
            this.x = x;
            this.y = y;
            this.duration = duration;
            this.age = 0f;
        }

        public float progress() {
            return Math.min(1f, age / duration);
        }

        public boolean finished() {
            return age >= duration;
        }
    }

    private final Array<Burst> bursts = new Array<>();

    public void spawn(float x, float y) {
        bursts.add(new Burst(x, y, 0.45f));
    }

    public void update(float delta) {
        for (int i = bursts.size - 1; i >= 0; i--) {
            Burst burst = bursts.get(i);
            burst.age += delta;
            if (burst.finished()) {
                bursts.removeIndex(i);
            }
        }
    }

    public Array<Burst> bursts() {
        return bursts;
    }
}
