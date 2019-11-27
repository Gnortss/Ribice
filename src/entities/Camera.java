package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Node;
import utils.Maths;
import utils.NodeType;

public class Camera extends Node {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float rotX = 0;
    private float rotY = 0;
    private float rotZ = 0;

    public Camera() {
        super(NodeType.CAMERA);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        super(NodeType.CAMERA);

        this.position = position;
        this.rotX = rotation.x;
        this.rotY = rotation.y;
        this.rotZ = rotation.z;
    }

    public void move() {
        if(Keyboard.isKeyDown(Keyboard.KEY_W))
            position.z -= 0.2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_S))
            position.z += 0.2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_A))
            position.x -= 0.2f;

        if(Keyboard.isKeyDown(Keyboard.KEY_D))
            position.x += 0.2f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public Matrix4f getViewMatrix(){
        return Maths.createViewMatrix(this);
    }
}
