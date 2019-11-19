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

    public int getTexture() {
        return texture;
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public Vector3f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
}
