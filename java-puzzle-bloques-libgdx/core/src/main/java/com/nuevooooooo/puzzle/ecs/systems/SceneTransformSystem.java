package com.nuevooooooo.puzzle.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.nuevooooooo.puzzle.ecs.BobMotionComponent;
import com.nuevooooooo.puzzle.ecs.SceneRefComponent;
import com.nuevooooooo.puzzle.ecs.Transform3DComponent;

import java.util.function.DoubleSupplier;

public class SceneTransformSystem extends EntitySystem {
    private final DoubleSupplier timeSupplier;
    private ImmutableArray<Entity> entities;

    public SceneTransformSystem(DoubleSupplier timeSupplier) {
        this.timeSupplier = timeSupplier;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        entities = engine.getEntitiesFor(Family.all(Transform3DComponent.class, BobMotionComponent.class, SceneRefComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float t = (float) timeSupplier.getAsDouble();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Transform3DComponent transform = entity.getComponent(Transform3DComponent.class);
            BobMotionComponent bob = entity.getComponent(BobMotionComponent.class);
            SceneRefComponent sceneRef = entity.getComponent(SceneRefComponent.class);
            float y = bob.baseY + bob.amplitude * MathUtils.sin((t + bob.phase) * bob.speed);
            sceneRef.scene.modelInstance.transform.setToTranslation(transform.position.x, y, transform.position.z);
        }
    }
}
