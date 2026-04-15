package com.nuevooooooo.puzzle.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.nuevooooooo.puzzle.ecs.Motion2DComponent;
import com.nuevooooooo.puzzle.ecs.Transform3DComponent;

public class MotionWorldMappingSystem extends EntitySystem {
    public interface UiToWorldMapper {
        float uiToWorldX(float uiX);

        float uiToWorldZ(float uiY);
    }

    private final UiToWorldMapper mapper;
    private ImmutableArray<Entity> entities;

    public MotionWorldMappingSystem(UiToWorldMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(Motion2DComponent.class, Transform3DComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Motion2DComponent motion = entity.getComponent(Motion2DComponent.class);
            Transform3DComponent transform = entity.getComponent(Transform3DComponent.class);
            transform.position.x = mapper.uiToWorldX(motion.x);
            transform.position.z = mapper.uiToWorldZ(motion.y);
        }
    }
}
