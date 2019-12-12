package models;

import materials.Material;

public class TexturedModel{

    private Model model;
    private Material material;

    public TexturedModel(Model model, Material material) {
        this.model = model;
        this.material = material;
    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
