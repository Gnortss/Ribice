package entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Light extends Node {

    private Vector3f color;

    public Light(Vector3f position, Vector3f color) {
        super();

        this.position = position;
        this.color = color;
    }

    /* Getters */
    public Vector3f getGlobalPosition() {
        Matrix4f parentTransform = this.getParent().getGlobalTransform();
        Vector4f global = new Vector4f(position.x, position.y, position.z, 1.0f);
        Matrix4f.transform(parentTransform, global, global);
        return new Vector3f(global.x, global.y, global.z);
    }

    public Vector3f getColor() {
        return color;
    }

    /* Setters */
    public void setColor(Vector3f color) {
        this.color = color;
    }
}
