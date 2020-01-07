package materials;

import org.lwjgl.util.vector.Vector3f;

public class Material {
    private int texture;

    private Vector3f ambient = new Vector3f(1, 1, 1);
    private Vector3f diffuse = new Vector3f(1, 1, 1);
    private Vector3f specular = new Vector3f(1, 1,1);
    private float shininess = 32;

    public Material(int texture) {
        this.texture = texture;
    }

    public Material(int texture, Vector3f ads, float s) {
        this.texture = texture;
        this.ambient = ads;
        this.diffuse = ads;
        this.specular = ads;
        this.shininess = s;
    }

    public int getTexture() {
        return texture;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public Material setAmbient(Vector3f ambient) {
        this.ambient = ambient;
        return this;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public Material setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public Material setSpecular(Vector3f specular) {
        this.specular = specular;
        return this;
    }

    public float getShininess() {
        return shininess;
    }

    public Material setShininess(float shininess) {
        this.shininess = shininess;
        return this;
    }
}
