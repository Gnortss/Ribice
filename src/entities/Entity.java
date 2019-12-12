package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Entity extends Node {

    private TexturedModel texturedModel;
    private Matrix4f globalTransform;

    public Entity(TexturedModel texturedModel, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super();

        this.texturedModel = texturedModel;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;

        this.globalTransform = new Matrix4f();
        this.globalTransform.setIdentity();
    }

    /* Getters */
    public TexturedModel getTexturedModel() {
        return texturedModel;
    }

    public void setGlobal(Matrix4f t) { this.globalTransform = t; }

    public Matrix4f getGlobal() { return globalTransform; }

    /* Methods */
    public void move() {
        if(Keyboard.isKeyDown(Keyboard.KEY_W))
            rotX -= .2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_S))
            rotX += .2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_A))
            rotY -= .2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_D))
            rotY += .2f;
    }
}
