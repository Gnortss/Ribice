package renderEngine;

import entities.*;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Scene {
    private Loader loader;

    private Node root;
    private Submarine submarine;
    private Camera mainCamera;
    private List<Light> lights;
    private HashMap<TexturedModel, ArrayList<Entity>> entities;

    private Matrix4f globalTransform;
    private Stack<Matrix4f> globalStack;

    public Scene(Loader loader){
        this.loader = loader;

        this.root = new Node();
        this.lights = new ArrayList<>();
        this.entities = new HashMap<>();
        this.globalTransform = Matrix4f.setIdentity(new Matrix4f());
        this.globalStack = new Stack<>();
    }

    /* Getters */
    public Entity getSubmarine() { return this.submarine; }

    public Camera getMainCamera() { return mainCamera; }

    /* Returns sorted lights by the distance from submarine ASCENDING */
    /* Hope this works */
    public List<Light> getLights() {
        Vector3f sp = submarine.getGlobalPosition();
        lights.sort((a, b) -> {
            Vector3f p1 = a.getGlobalPosition();
            Vector3f p2 = b.getGlobalPosition();
            double d1 = Math.pow(sp.x - p1.x, 2) + Math.pow(sp.y - p1.y, 2) + Math.pow(sp.z - p1.z, 2);
            double d2 = Math.pow(sp.x - p2.x, 2) + Math.pow(sp.y - p2.y, 2) + Math.pow(sp.z - p2.z, 2);
            return (int) (d1 - d2);
        });
        return lights;
    }

    public HashMap<TexturedModel, ArrayList<Entity>> getEntities() { return this.entities; }

    /* Methods */
    private <T extends Node> void addChild(T c){
        this.root.addChild(c);
    }

    private <T extends Node> void removeChild(T c){
        this.root.removeChild(c);
    }

    public void addLight(Vector3f pos){
        Light light = new Light(pos)
                .setAmbient(new Vector3f(.05f, .05f, .05f))
                .setDiffuse(new Vector3f(.8f, .8f, .8f))
                .setSpecular(new Vector3f(.1f, .1f, .1f))
                .setAttenuation(new Vector3f(1f, 0.04f, 0.008f));
        this.addChild(light);
        this.lights.add(light);
    }

    public void createSubmarine(){
        /* Load models */
        Model mBody = OBJLoader.loadObjModel("/submarine/body", loader);
        Model mFlapsLR = OBJLoader.loadObjModel("/submarine/LR-Flaps", loader);
        Model mFlapsUD = OBJLoader.loadObjModel("/submarine/UD-Flaps", loader);
        Model mPropelers = OBJLoader.loadObjModel("/submarine/propelers", loader);

        /* Load textures */
        int whiteTex = loader.loadTexture("white");

        /* Create Materials */
        Material bodyMat = new Material(whiteTex)
                .setAmbient(new Vector3f(199/255f, 239/255f, 41/255f))
                .setDiffuse(new Vector3f(199/255f, 239/255f, 41/255f))
                .setSpecular(new Vector3f(199/255f, 239/255f, 41/255f))
                .setShininess(2);
        Material propsMat = new Material(whiteTex)
                .setAmbient(new Vector3f(55/255f, 55/255f, 55/255f))
                .setDiffuse(new Vector3f(55/255f, 55/255f, 55/255f))
                .setSpecular(new Vector3f(55/255f, 55/255f, 55/255f))
                .setShininess(512);

        /* Create Textured models */
        TexturedModel bodyModel = new TexturedModel(mBody, bodyMat);
        TexturedModel flapsLRModel = new TexturedModel(mFlapsLR, propsMat);
        TexturedModel flapsUDModel = new TexturedModel(mFlapsUD, propsMat);
        TexturedModel propelersModel = new TexturedModel(mPropelers, propsMat);

        /* Create Submarine */
        Submarine submarine = new Submarine(bodyModel, flapsLRModel, flapsUDModel, propelersModel);

        this.submarine = submarine;
        this.mainCamera = submarine.getCamera();
        this.lights.add(submarine.getLight());

        this.addChild(submarine);
    }

    /* Creates fish */
    /* NOTE: TEMPORARY BEFORE WE CREATE Fish which extends Entity class!!!!! */
    public Entity createFish(TexturedModel model, Vector3f p, Quaternion r){
        Entity fish = new Entity(model, p, r, 1f);
        this.addChild(fish);

        return fish;
    }

    /* Updates local and global transforms for each Node */
    public void calculateTransforms(){
        this.globalTransform = Matrix4f.setIdentity(new Matrix4f());
        this.globalStack = new Stack<>();

        this.root.traverse(
        (Node n) -> { /* Before */
            globalStack.push(new Matrix4f(globalTransform));
            globalTransform = Matrix4f.mul(globalTransform, n.getLocalTransform(), null);
            n.setGlobal(globalTransform);
        }, (Node n) -> { /* After */
            globalTransform = globalStack.pop();
        });
    }

    public void buildHashMap(){
        this.entities = new HashMap<>();

        this.root.traverse(
        (Node n) -> {
            if(n instanceof Entity)
                addEntity((Entity) n);
        }, (Node n) -> {});
    }

    public void update(){
        this.calculateTransforms();
    }

    private void addEntity(Entity e){
        TexturedModel m = e.getTexturedModel();
        if(this.entities.containsKey(m)){ /* Already contains TexturedModel */
            this.entities.get(m).add(e);
        } else { /* Add a new mapping for this TexturedModel */
            ArrayList<Entity> l = new ArrayList<>();
            l.add(e);
            this.entities.put(m, l);
        }
    }
}
