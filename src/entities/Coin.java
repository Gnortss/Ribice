package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import utils.Maths;

public class Coin extends Entity {

    private float colSphereRadius;

    public Coin(TexturedModel texturedModel, Vector3f position, Quaternion rotation, float scale) {
        super(texturedModel, position, rotation, scale);

        this.colSphereRadius = .25f * scale;
    }

    public void updateRotation(float dt) {
        Vector3f right = Maths.getAxis(new Quaternion(), "right");
        Vector3f up = Maths.getAxis(new Quaternion(), "up");

        float degPerSec = 180;
        Quaternion rotR = Maths.createFromAxisAngle(right, degPerSec * dt);
        Quaternion rotU = Maths.createFromAxisAngle(up, degPerSec * dt);
        Quaternion.mul(rotR, rotation, rotation);
        Quaternion.mul(rotU, rotation, rotation);
        dirty = true;
    }

    /* Getters */
    public Vector3f getGlobalPosition() {
        Matrix4f parentTransform = this.getParent().getGlobalTransform();
        Vector4f global = new Vector4f(position.x, position.y, position.z, 1.0f);
        Matrix4f.transform(parentTransform, global, global);
        return new Vector3f(global.x, global.y, global.z);
    }

    public float getColSphereRadius() {
        return colSphereRadius;
    }

    public void remove() {
        if(parent != null) parent.removeChild(this);
    }
}
