package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import utils.Maths;

public class Fish extends Entity {

    private final float SPEED = 5f;

    public Fish(TexturedModel texturedModel, Vector3f position, Quaternion rotation, float scale) {
        super(texturedModel, position, rotation, scale);
    }

    @Override
    public void move(float dt) {
        /* rotation part */
        Vector3f right = Maths.getAxis(new Quaternion(), "right");
        Vector3f up = Maths.getAxis(new Quaternion(), "up");
        Vector3f forward = Maths.getAxis(new Quaternion(), "forward");
        float angVel = 90;
        float dx = (float) (Math.random() * angVel * 2 - angVel);
        float dy = (float) (Math.random() * angVel * 2 - angVel);
        float dz = (float) (Math.random() * angVel * 2 - angVel);
        Quaternion rx = Maths.createFromAxisAngle(right, dx * dt);
        Quaternion ry = Maths.createFromAxisAngle(up, dy * dt);
        Quaternion rz = Maths.createFromAxisAngle(forward, dz * dt);
        Quaternion.mul(rx, rotation, rotation);
        Quaternion.mul(ry, rotation, rotation);
        Quaternion.mul(rz, rotation, rotation);

        /* translation part */
        Vector3f velocity = Maths.getAxis(rotation, "forward");
        velocity.scale(SPEED * dt);
        position = Vector3f.add(position, velocity, null);

        dirty = true;
    }
}
