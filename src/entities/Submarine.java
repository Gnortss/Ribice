package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

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
        super(body, new Vector3f(0, 0, 0), 0, 0, 0, 1);

        /* Create flaps, propelers, lights, camera and attach to main body */
        this.flapsLR = new Entity(flapsLR, new Vector3f(0, 0, 0), 0, 0, 0, 1);
        this.flapsUD = new Entity(flapsUD, new Vector3f(0, 0, 0), 0, 0, 0, 1);
        this.propelers = new Entity(propelers, new Vector3f(0, 0, 0), 0, 0, 0, 1);
        /* Actual light position: 0, 1.5502f, -3.2302f */
        this.light = new Light(new Vector3f(0f, 5.5f, 12f), new Vector3f(1, 1, 1));
        this.camera = new Camera(new Vector3f(0, 5.5f, 15f), new Vector3f(-10, 0, 0));

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

    @Override
    public void move(float dt) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            speed = Math.min(speed + ACCELERATION * dt, MAX_SPEED);
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
            speed = Math.max(speed - ACCELERATION * dt, -MAX_SPEED);
        if(Keyboard.isKeyDown(Keyboard.KEY_W))
            this.rotX -= .2f;
        if(Keyboard.isKeyDown(Keyboard.KEY_S))
            this.rotX += .2f;
        if(Keyboard.isKeyDown(Keyboard.KEY_A))
            this.rotY -= .2f;
        if(Keyboard.isKeyDown(Keyboard.KEY_D))
            this.rotY += .2f;

        Matrix4f t = globalTransform;
        Vector3f forward = new Vector3f(-t.m20, -t.m21, -t.m22);
        System.out.println(forward);
        forward.scale(speed);
        this.position = Vector3f.add(position, forward, null);
    }
}
