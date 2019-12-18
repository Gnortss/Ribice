package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import utils.Maths;

import javax.xml.crypto.dsig.Transform;

public class Submarine extends Entity {

    private Entity flapsLR;
    private Entity flapsUD;
    private Entity propelers;
    private Light light;
    private Camera camera;

    private final float MAX_SPEED = 1f;
    private final float ACCELERATION = .1f;
    private float speed;

    public Submarine(TexturedModel body, TexturedModel flapsLR, TexturedModel flapsUD, TexturedModel propelers) {
        super(body, new Vector3f(0, 0, 0), new Quaternion(), 1);

        /* Create flaps, propelers, lights, camera and attach to main body */
        this.flapsLR = new Entity(flapsLR, new Vector3f(0, 0, 0), new Quaternion(), 1);
        this.flapsUD = new Entity(flapsUD, new Vector3f(0, 0, 0), new Quaternion(), 1);
        this.propelers = new Entity(propelers, new Vector3f(0, 0, 0), new Quaternion(), 1);
        /* Actual light position: 0, 1.5502f, -3.2302f */
        this.light = new Light(new Vector3f(0f, 5.5f, 12f), new Vector3f(1, 1, 1));
        this.camera = new Camera(new Vector3f(0, 5.5f, 15f), new Quaternion());

        this.addChild(this.flapsLR);
        this.addChild(this.flapsUD);
        this.addChild(this.propelers);
        this.addChild(this.light);
        this.addChild(this.camera);

        this.speed = 0;
    }
    /* Getters */
    public Camera getCamera() { return this.camera; }

    public Light getLight() { return this.light; }

    public void move(float dt) {
        Vector3f right = Maths.getAxis(new Quaternion(), "right");
        Vector3f up = Maths.getAxis(new Quaternion(), "up");
        Vector3f forward = Maths.getAxis(new Quaternion(), "forward");
        Vector3f velocity = Maths.getAxis(rotation, "forward");


        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) speed = Math.min(speed + ACCELERATION * dt, MAX_SPEED);
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) speed = Math.max(speed - ACCELERATION * dt, -MAX_SPEED);
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) speed = 0;
        velocity.scale(speed);
        this.position = Vector3f.add(position, velocity, null);
        if(speed != 0) dirty = true;


        float angularSpeed = 30 * dt;
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            Quaternion r = Maths.createFromAxisAngle(right, angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            Quaternion r = Maths.createFromAxisAngle(right, -angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            Quaternion r = Maths.createFromAxisAngle(up, -angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            Quaternion r = Maths.createFromAxisAngle(up, angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
            Quaternion r = Maths.createFromAxisAngle(forward, -angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_E)){
            Quaternion r = Maths.createFromAxisAngle(forward, angularSpeed);
            Quaternion.mul(r, rotation, rotation);
            dirty = true;
        }
    }
}
