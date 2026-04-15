package com.nuevooooooo.puzzle.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ecs.Motion2DComponent;

public class Motion2DSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(Motion2DComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            Motion2DComponent motion = entities.get(i).getComponent(Motion2DComponent.class);
            if (!motion.moving) {
                continue;
            }
            motion.timer = Math.min(motion.duration, motion.timer + deltaTime);
            float alpha = motion.duration <= 0f ? 1f : motion.timer / motion.duration;
            float eased = Interpolation.sineOut.apply(alpha);
            motion.x = MathUtils.lerp(motion.fromX, motion.toX, eased);
            motion.y = MathUtils.lerp(motion.fromY, motion.toY, eased);
            if (motion.timer >= motion.duration) {
                motion.moving = false;
                motion.x = motion.toX;
                motion.y = motion.toY;
            }
        }
    }
}
