package renderEngine;

import entities.*;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import java.util.*;
import java.util.function.Consumer;

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

    public List<Light> getLights() { return lights; }

    public HashMap<TexturedModel, ArrayList<Entity>> getEntities() { return this.entities; }

    /* Methods */
    private <T extends Node> void addChild(T c){
        this.root.addChild(c);
    }

    private <T extends Node> void removeChild(T c){
        this.root.removeChild(c);
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
                .setShininess(64);
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
    public Entity createFish(TexturedModel model, Vector3f p, Vector3f r){
        Entity fish = new Entity(model, p, r.x, r.y, r.z, 1f);
        this.addChild(fish);

        return fish;
    }

    /* Calculates global transformation for each Entity
    *  Adds Entity to this.entities */
    public void calculateTransforms(){
        this.globalTransform = Matrix4f.setIdentity(new Matrix4f());
        this.globalStack = new Stack<>();

        this.root.traverse(
        (Node n) -> { /* Before */
            globalStack.push(new Matrix4f(globalTransform));
            globalTransform = Matrix4f.mul(globalTransform, n.getLocalTransform(), null);
            if(n instanceof Entity){
                ((Entity) n).setGlobal(globalTransform);
            }
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
