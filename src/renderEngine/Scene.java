package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import utils.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Scene {
    private Node root;
    private Camera mainCamera;
    private List<Light> lights = new ArrayList<>();

    private String toString = "";

    private Matrix4f globalTransform = Matrix4f.setIdentity(new Matrix4f());
    private Stack<Matrix4f> globalStack = new Stack<>();
    public Scene(){
        this.root = new Node(NodeType.ROOT);
    }

    public void addCamera(Camera c){
        this.root.addChild(c);
        this.mainCamera = c;
    }

    public void addLight(Light c){
        this.root.addChild(c);
        lights.add(c);
    }

    public <T extends Node> void addChild(T c){
        this.root.addChild(c);
    }

    public <T extends Node> void removeChild(T c){
        this.root.removeChild(c);
    }

    public void traverseForRendering(Consumer<TexturedModel> prepareModel, Consumer<Matrix4f> prepareEntity, Runnable draw, Runnable unbindModel){
        globalTransform = Matrix4f.setIdentity(new Matrix4f());
        globalStack = new Stack<>();

        this.root.traverse((Node n) -> {
            if(n instanceof TexturedModel){
                prepareModel.accept((TexturedModel) n);
            } else if(n instanceof Entity){
                globalStack.push(globalTransform);
                Matrix4f.mul(globalTransform, ((Entity) n).getLocalTransform(), globalTransform);
                prepareEntity.accept(globalTransform);
                draw.run();
            }
        }, (Node n) -> {
            if(n instanceof TexturedModel){
                unbindModel.run();
            } else if(n instanceof Entity){
                globalTransform = globalStack.pop();
            }
        });
    };

    public String toString(){
        toString = "";
        this.root.traverse((Node n) -> {
            toString = toString.concat(n.getType().toString()).concat(", ");
        }, (Node n) -> {

        });
        return toString;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public List<Light> getLights() {
        return lights;
    }
}
