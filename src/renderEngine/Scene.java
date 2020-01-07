package renderEngine;

import entities.*;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import utils.Maths;

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

        this.buildEnvironment();
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
        submarine.setPosition(new Vector3f(200, 30, 0));
        submarine.setRotation(Maths.createFromAxisAngle(Maths.getAxis(new Quaternion(), "up"), -90f));


        this.submarine = submarine;
        this.mainCamera = submarine.getCamera();
        this.lights.add(submarine.getLight());

        this.addChild(submarine);
    }

    public void createFishGroup(TexturedModel model, Vector3f pos, Quaternion rot, float maxOffset, int n){
        for(int i = 0; i < n; i++){
            Vector3f p = new Vector3f(
                    pos.x + (float)Math.random() * maxOffset * 2 - maxOffset,
                    pos.y + (float)Math.random() * maxOffset * 2 - maxOffset,
                    pos.z + (float)Math.random() * maxOffset * 2 - maxOffset
            );
            Quaternion r = new Quaternion();
            Vector3f right = Maths.getAxis(new Quaternion(), "right");
            Vector3f up = Maths.getAxis(new Quaternion(), "up");
            Quaternion rx = Maths.createFromAxisAngle(right, (float)Math.random() * 60 - 30);
            Quaternion ry = Maths.createFromAxisAngle(up, (float)Math.random() * 60 - 30);
            Quaternion.mul(rot, r, r);
            Quaternion.mul(rx, ry, ry);
            Quaternion.mul(ry, r, r);

            this.addChild(new Fish(model, p, r, 2f));
        }
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

    public void update(float dt){
        this.calculateTransforms();

        /* move fish entities */
        this.root.traverse(
                (Node n) -> {
                    if(n instanceof Fish)
                        ((Fish) n).move(dt);
                }, (Node n) -> {});

    }

    private void buildEnvironment() {
        Model ground = OBJLoader.loadObjModel("/map/ground", loader);
        Model c1 = OBJLoader.loadObjModel("/map/c1", loader);
        Model c2 = OBJLoader.loadObjModel("/map/c2", loader);
        Model c3 = OBJLoader.loadObjModel("/map/c3", loader);
        Model c4 = OBJLoader.loadObjModel("/map/c4", loader);
        Model c5 = OBJLoader.loadObjModel("/map/c5", loader);
        Model c6 = OBJLoader.loadObjModel("/map/c6", loader);
        Model c7 = OBJLoader.loadObjModel("/map/c7", loader);
        Model c8 = OBJLoader.loadObjModel("/map/c8", loader);
        Model c9 = OBJLoader.loadObjModel("/map/c9", loader);

        int wt = loader.loadTexture("white");

        Material gMat = new Material(wt)
                .setAmbient(new Vector3f(227/255f, 214/255f, 132/255f))
                .setDiffuse(new Vector3f(227/255f, 214/255f, 132/255f))
                .setSpecular(new Vector3f(227/255f, 214/255f, 132/255f))
                .setShininess(512);

        Material blue = new Material(wt)
                .setAmbient(new Vector3f(52/255f, 103/255f, 235/255f))
                .setDiffuse(new Vector3f(52/255f, 103/255f, 235/255f))
                .setSpecular(new Vector3f(52/255f, 103/255f, 235/255f))
                .setShininess(512);

        Material green = new Material(wt)
                .setAmbient(new Vector3f(155/255f, 209/255f, 40/255f))
                .setDiffuse(new Vector3f(155/255f, 209/255f, 40/255f))
                .setSpecular(new Vector3f(155/255f, 209/255f, 40/255f))
                .setShininess(512);

        Material red = new Material(wt)
                .setAmbient(new Vector3f(209/255f, 78/255f, 73/255f))
                .setDiffuse(new Vector3f(209/255f, 78/255f, 73/255f))
                .setSpecular(new Vector3f(209/255f, 78/255f, 73/255f))
                .setShininess(512);

        TexturedModel gm = new TexturedModel(ground, gMat);
        TexturedModel cm1 = new TexturedModel(c1, blue);
        TexturedModel cm2 = new TexturedModel(c2, blue);
        TexturedModel cm3 = new TexturedModel(c3, blue);
        TexturedModel cm4 = new TexturedModel(c4, green);
        TexturedModel cm5 = new TexturedModel(c5, green);
        TexturedModel cm6 = new TexturedModel(c6, green);
        TexturedModel cm7 = new TexturedModel(c7, red);
        TexturedModel cm8 = new TexturedModel(c8, red);
        TexturedModel cm9 = new TexturedModel(c9, red);

        Entity ge = new Entity(gm, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce1 = new Entity(cm1, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce2 = new Entity(cm2, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce3 = new Entity(cm3, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce4 = new Entity(cm4, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce5 = new Entity(cm5, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce6 = new Entity(cm6, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce7 = new Entity(cm7, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce8 = new Entity(cm8, new Vector3f(0, 0, 0), new Quaternion(), 4f);
        Entity ce9 = new Entity(cm9, new Vector3f(0, 0, 0), new Quaternion(), 4f);

        this.addChild(ge);
        this.addChild(ce1);
        this.addChild(ce2);
        this.addChild(ce3);
        this.addChild(ce4);
        this.addChild(ce5);
        this.addChild(ce6);
        this.addChild(ce7);
        this.addChild(ce8);
        this.addChild(ce9);
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
