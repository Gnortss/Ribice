package materials;

import org.lwjgl.util.vector.Vector3f;

public class Material {
    private int texture;

    Vector3f ambient = new Vector3f(1, 1, 1);
    Vector3f diffuse = new Vector3f(1, 1, 1);
    Vector3f specular = new Vector3f(1, 1,1);
    float shininess = 32;

    public Material(int texture) {
        this.texture = texture;
    }

    public int getTexture() {
        return texture;
    }


}
