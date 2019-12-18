package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import utils.Maths;

public class Camera extends Node {
    public Camera() { super(); }

    public Camera(Vector3f position, Quaternion rotation) {
        super();

        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getGlobalPosition() {
        Matrix4f parentTransform = this.getParent().getGlobalTransform();
        Vector4f global = new Vector4f(position.x, position.y, position.z, 1.0f);
        Matrix4f.transform(parentTransform, global, global);
        return new Vector3f(global.x, global.y, global.z);
    }

    public void move() {
//        if(Keyboard.isKeyDown(Keyboard.KEY_W))
//            rotX += .2f;
//
//        if(Keyboard.isKeyDown(Keyboard.KEY_S))
//            rotX -= .2f;
//
//        if(Keyboard.isKeyDown(Keyboard.KEY_A))
//            rotY += .2f;
//
//        if(Keyboard.isKeyDown(Keyboard.KEY_D))
//            rotY -= .2f;
    }

    public Matrix4f getViewMatrix(){
        return Maths.createViewMatrix(this);
    }
}
