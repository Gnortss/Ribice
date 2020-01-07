package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class Entity extends Node {

    protected TexturedModel texturedModel;
    protected Matrix4f globalTransform;

    public Entity(TexturedModel texturedModel, Vector3f position, Quaternion rotation, float scale) {
        super();

        this.texturedModel = texturedModel;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    /* Getters */
    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    @Override
    public Entity setPosition(Vector3f p) {
        this.position = p; dirty = true; return this;
    }

    @Override
    public Entity setRotation(Quaternion r) {
        this.rotation = r; dirty = true; return this;
    }

    @Override
    public Entity setScale(float s) {
        this.scale = s; dirty = true; return this;
    }

    @Override
    public Entity setGlobal(Matrix4f m) {
        this.global = m; return this;
    }

    public void move(float dt){}

}
