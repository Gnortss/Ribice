package entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Light extends Node {

    protected Vector3f attenuation;
    protected Vector3f ambient;
    protected Vector3f diffuse;
    protected Vector3f specular;

    public Light(Vector3f position) {
        super();

        this.position = position;
        this.attenuation = new Vector3f(1, 0.01f, 0.002f);

        this.ambient = new Vector3f(.1f, .1f, .1f);
        this.diffuse = new Vector3f(1, 1, 1);
        this.specular = new Vector3f(.1f, .1f, .1f);
    }

    public Light(Vector3f position, Vector3f attenuation) {
        super();

        this.position = position;
        this.attenuation = attenuation;

        this.ambient = new Vector3f(.1f, .1f, .1f);
        this.diffuse = new Vector3f(1, 1, 1);
        this.specular = new Vector3f(.1f, .1f, .1f);
    }

    /* Getters */
    public Vector3f getGlobalPosition() {
        Matrix4f parentTransform = this.getParent().getGlobalTransform();
        Vector4f global = new Vector4f(position.x, position.y, position.z, 1.0f);
        Matrix4f.transform(parentTransform, global, global);
        return new Vector3f(global.x, global.y, global.z);
    }

    public Vector3f getAttenuation() { return attenuation; }

    public Vector3f getAmbient() {
        return ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    /* Setters */

    public Light setAttenuation(Vector3f att) { this.attenuation = att; return this; }

    public Light setAmbient(Vector3f ambient) {
        this.ambient = ambient;
        return this;
    }

    public Light setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public Light setSpecular(Vector3f specular) {
        this.specular = specular;
        return this;
    }
}
