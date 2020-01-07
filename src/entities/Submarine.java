package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import utils.Maths;

public class Submarine extends Entity {

    private Entity flapsLR;
    private Entity flapsUD;
    private Entity propelers;
    private Light light;
    private Camera camera;

    private final float MAX_SPEED = .6f;
    private final float MAX_ROTATION_SPEED = 70f;
    private final float ACCELERATION = .1f;
    private float speed, rotSpeedF, rotSpeedU, rotSpeedR;

    private float colSphereRadius = 1.135f;
    private Vector3f colSphereOffset = new Vector3f(-2.2857f, 0, 0);

    public Submarine(TexturedModel body, TexturedModel flapsLR, TexturedModel flapsUD, TexturedModel propelers) {
        super(body, new Vector3f(0, 0, 0), new Quaternion(), 1);

        /* Create flaps, propelers, lights, camera and attach to main body */
        this.flapsLR = new Entity(flapsLR, new Vector3f(0, 0, 0), new Quaternion(), 1);
        this.flapsUD = new Entity(flapsUD, new Vector3f(0, 0, 0), new Quaternion(), 1);
        this.propelers = new Entity(propelers, new Vector3f(0, 0, 0), new Quaternion(), 1);
        /* Actual light position: 0, 1.5502f, -3.2302f */
        this.light = new Light(new Vector3f(0, 1.5502f, -3.2302f))
                .setAmbient(new Vector3f(.05f, .05f, .05f))
                .setDiffuse(new Vector3f(.8f, .8f, .8f))
                .setSpecular(new Vector3f(.1f, .1f, .1f));
        this.camera = new Camera(new Vector3f(0, 5.5f, 17f), new Quaternion());

        this.addChild(this.flapsLR);
        this.addChild(this.flapsUD);
        this.addChild(this.propelers);
        this.addChild(this.light);
        this.addChild(this.camera);

        this.speed = 0;
        this.rotSpeedF = 0;
        this.rotSpeedR = 0;
        this.rotSpeedU = 0;
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

    public Vector3f getColSphereOffset() {
        return colSphereOffset;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Light getLight() {
        return this.light;
    }

    public void move(float dt) {
        Vector3f velocity = Maths.getAxis(rotation, "forward");

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) speed = Math.min(speed + ACCELERATION * dt, MAX_SPEED);
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) speed = Math.max(speed - ACCELERATION * dt, -MAX_SPEED);
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            speed = 0;
            rotSpeedF = 0;
            rotSpeedR = 0;
            rotSpeedU = 0;
        }
        velocity.scale(speed);
        this.position = Vector3f.add(position, velocity, null);
        if (speed != 0) dirty = true;

        updateRotation(dt);
        updateFlaps();
        updatePropelers(dt);
    }

    private void updateRotation(float dt) {
        Vector3f forward = Maths.getAxis(new Quaternion(), "forward");
        Vector3f up = Maths.getAxis(new Quaternion(), "up");
        Vector3f right = Maths.getAxis(new Quaternion(), "right");
        Quaternion r;

        float rotAcc = .2f * (MAX_SPEED - Math.abs(speed));
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            rotSpeedR += rotAcc;
            rotSpeedR = Math.min(rotSpeedR, MAX_ROTATION_SPEED);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            rotSpeedR -= rotAcc;
            rotSpeedR = Math.max(rotSpeedR, -MAX_ROTATION_SPEED);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            rotSpeedU -= rotAcc;
            rotSpeedU = Math.max(rotSpeedU, -MAX_ROTATION_SPEED);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            rotSpeedU += rotAcc;
            rotSpeedU = Math.min(rotSpeedU, MAX_ROTATION_SPEED);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            rotSpeedF += rotAcc;
            rotSpeedF = Math.min(rotSpeedF, MAX_ROTATION_SPEED);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            rotSpeedF -= rotAcc;
            rotSpeedF = Math.max(rotSpeedF, -MAX_ROTATION_SPEED);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
            rotSpeedR = 0;
            rotSpeedF = 0;
            rotSpeedU = 0;
        }

        r = Maths.createFromAxisAngle(right, rotSpeedR * dt);
        Quaternion.mul(r, rotation, rotation);
        r = Maths.createFromAxisAngle(up, rotSpeedU * dt);
        Quaternion.mul(r, rotation, rotation);
        r = Maths.createFromAxisAngle(forward, rotSpeedF * dt);
        Quaternion.mul(r, rotation, rotation);

        dirty = true;
    }

    private void updatePropelers(float dt) {
        float rotationSpeed = 720 * speed * 20;
        Quaternion r = propelers.getRotation();
        Vector3f forward = Maths.getAxis(new Quaternion(), "back");
        Quaternion rx = Maths.createFromAxisAngle(forward, rotationSpeed * dt);
        Quaternion.mul(rx, r, r);
        propelers.setRotation(r);
    }

    private void updateFlaps() {
        Vector3f right = Maths.getAxis(new Quaternion(), "right");
        Quaternion rotUD = Maths.createFromAxisAngle(right, -rotSpeedR * .15f);
        flapsUD.setRotation(rotUD);

        Vector3f up = Maths.getAxis(new Quaternion(), "up");
        Quaternion rotLR = Maths.createFromAxisAngle(up, -rotSpeedU * .15f);
        flapsLR.setRotation(rotLR);
    }
}
