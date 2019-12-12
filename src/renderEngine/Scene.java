package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Node;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;
import java.util.function.Consumer;

public class Scene {
    private Node root;
    private Entity submarine;
    private Camera mainCamera;
    private List<Light> lights;
    private HashMap<TexturedModel, ArrayList<Entity>> entities;

    private Matrix4f globalTransform;
    private Stack<Matrix4f> globalStack;
    public Scene(){
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

    /* This method automatically creates Camera and Lights
    *  It attaches all nodes correctly and adds whole tree(submarine) as a child to scene.root */

    /* NOTE: TEMPORARY BEFORE WE CREATE Submarine which extends Entity class!!!!! */
    public void createSubmarine(TexturedModel model, TexturedModel d){
        Entity submarine = new Entity(model, new Vector3f(0, 0, 0), 0, 0, 0, 1f);
        Entity e = new Entity(d, new Vector3f(0, .12f, 0.32f), 0, 0, 0, .1f);

        Camera camera = new Camera(new Vector3f(0, .2f, 0.8f), new Vector3f(0, 0, 0));
        Light light = new Light(new Vector3f(0, .12f, 0.32f), new Vector3f(1, 1, 1));

        submarine.addChild(camera);
        submarine.addChild(e);
        submarine.addChild(light);

        /*      submarine
        *       /     \
        *    camera    light  */

        this.addChild(submarine);

        this.mainCamera = camera;
        this.lights.add(light);
        this.submarine = submarine;
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
    public void buildHashMap(){
        this.globalTransform = Matrix4f.setIdentity(new Matrix4f());
        this.globalStack = new Stack<>();
        this.entities = new HashMap<>();

        this.root.traverse(
        (Node n) -> { /* Before */
            globalStack.push(new Matrix4f(globalTransform));
            globalTransform = Matrix4f.mul(globalTransform, n.getLocalTransform(), null);
            if(n instanceof Entity){
                ((Entity) n).setGlobal(globalTransform);
                addEntity((Entity) n);
            }
        }, (Node n) -> { /* After */
            globalTransform = globalStack.pop();
        });
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
