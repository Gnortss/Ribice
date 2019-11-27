package models;

import materials.Material;
import renderEngine.Node;
import utils.NodeType;

public class TexturedModel extends Node {

    private Model model;
    private Material material;

    public TexturedModel(Model model, Material material) {
        super(NodeType.MODEL);
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
