package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
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

    public void move(float dt){}

}
