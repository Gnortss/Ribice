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
    private List<Coin> coins;
    private HashMap<TexturedModel, ArrayList<Entity>> entities;

    private Matrix4f globalTransform;
    private Stack<Matrix4f> globalStack;

    private TexturedModel fishModel;
    private float lastSpawn;

    public Scene(Loader loader){
        this.loader = loader;

        this.root = new Node();
        this.lights = new ArrayList<>();
        this.coins = new ArrayList<>();
        this.entities = new HashMap<>();
        this.globalTransform = Matrix4f.setIdentity(new Matrix4f());
        this.globalStack = new Stack<>();

        this.buildEnvironment();

        Model model = OBJLoader.loadObjModel("fish", loader);
        this.fishModel = new TexturedModel(model, new Material(loader.loadTexture("fish_colormap")));

        this.lastSpawn = 0; // in miliseconds
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

    public void addLight(Vector3f pos, Vector3f col, Vector3f att){
        Light light = new Light(pos)
                .setAmbient(col)
                .setDiffuse(col)
                .setSpecular(col)
                .setAttenuation(att);
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
        submarine.setPosition(new Vector3f(450, 20, 0));
//        submarine.setPosition(new Vector3f(0, 10, 0));
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
        /* Kill dead fish and update coins*/
        List<Fish> blacklist = new ArrayList<>();
        List<Coin> toRemove = new ArrayList<>();
        this.root.traverse(
                (Node n) -> {
                    if(n instanceof Fish)
                        if(((Fish) n).isDead()) blacklist.add((Fish) n);
                    if(n instanceof Coin) {
                        ((Coin) n).updateRotation(dt);
                        if(Maths.isColliding(submarine, ((Coin) n))) toRemove.add((Coin) n);
                    }
                }, (Node n) -> {});
        for (Fish fish : blacklist) {
            fish.kill();
        }
        for(Coin coin : toRemove) coin.remove();

        if(this.submarine != null) this.submarine.move(dt);

        this.calculateTransforms();

        /* move fish entities and update coin rotation*/
        this.root.traverse(
                (Node n) -> {
                    if(n instanceof Fish)
                        ((Fish) n).move(dt);
                }, (Node n) -> {});


        // TODO: spawn fish in front of the submarine
        float time = System.nanoTime()/1000000f;
        if(time - lastSpawn > 5000) {
            spawnNewFish();
            lastSpawn = time;
        }
    }

    private void buildEnvironment(){
        int wt = loader.loadTexture("white");
        Material gMat = new Material(wt, new Vector3f(227/255f, 214/255f, 132/255f), 512);
        Material blue = new Material(wt, new Vector3f(52/255f, 103/255f, 235/255f), 512);
        Material black = new Material(wt, new Vector3f(0, 0, 0), 512);
        Material yellow = new Material(wt, new Vector3f(245/255f, 215/255f, 69/255f), 512);
        Material green = new Material(wt, new Vector3f(155/255f, 209/255f, 40/255f), 512);
        Material bright_green = new Material(wt, new Vector3f(124/255f, 255/255f, 112/255f), 512);
        Material red = new Material(wt, new Vector3f(209/255f, 78/255f, 73/255f), 512);
        Material magenta = new Material(wt, new Vector3f(222/255f, 64/255f, 177/255f), 512);

        Model ground = OBJLoader.loadObjModel("/map1/ground", loader);
        TexturedModel gm = new TexturedModel(ground, gMat);
        Entity ge = new Entity(gm, new Vector3f(0, 0, 0), new Quaternion(), 1f);
        this.addChild(ge);

        Model m;
        TexturedModel tm;

        for(int i = 1; i <= 8; i++){
            m = OBJLoader.loadObjModel("/map1/pillar0" + i, loader);
            tm = new TexturedModel(m, green);
            this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));
        }

        for(int i = 1; i <= 6; i++){
            m = OBJLoader.loadObjModel("/map1/brick0" + i, loader);
            tm = new TexturedModel(m, i%2==0 ? red : blue);
            this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));
        }

        for(int i = 1; i <= 3; i++){
            m = OBJLoader.loadObjModel("/map1/cube0" + i, loader);
            tm = new TexturedModel(m, i%2==0 ? blue : i%3==0 ? green : red);
            this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));
        }

        for(int i = 1; i <= 5; i++){
            m = OBJLoader.loadObjModel("/map1/pillar1" + i, loader);
            tm = new TexturedModel(m, i%2==0 ? red : black);
            this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));
        }

        for(int i = 1; i <= 3; i++){
            m = OBJLoader.loadObjModel("/map1/torus0" + i, loader);
            tm = new TexturedModel(m, i%2==0 ? magenta : yellow);
            this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));
        }

        m = OBJLoader.loadObjModel("/map1/pillar21", loader);
        tm = new TexturedModel(m, yellow);
        this.addChild(new Entity(tm, new Vector3f(0, 0, 0), new Quaternion(), 1f));

        addLight(new Vector3f(35, 0, -30), new Vector3f(124/255f, 63/255f, 143/255f), new Vector3f(2f, 0.01f, 0.003f));
        addLight(new Vector3f(48, 59, -21), new Vector3f(124/255f, 63/255f, 143/255f), new Vector3f(2f, 0.01f, 0.003f));
        addLight(new Vector3f(16, 0, 62), new Vector3f(124/255f, 255/255f, 112/255f), new Vector3f(2f, 0.01f, 0.003f));
        addLight(new Vector3f(-171, 49, 135), new Vector3f(124/255f, 255/255f, 112/255f), new Vector3f(2f, 0.01f, 0.003f));

        Model coinModel = OBJLoader.loadObjModel("coin", loader);
        TexturedModel coinTexturedModel = new TexturedModel(coinModel, yellow);
        addCoin(new Coin(coinTexturedModel, new Vector3f(342, 26, 0), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(254, 51, -21), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(143, 76, -40), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(60, 38, -17), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(21, 18, 62), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(-57, 11, -25), new Quaternion(), 2f));
        addCoin(new Coin(coinTexturedModel, new Vector3f(-173, 21, 117), new Quaternion(), 2f));
    }

    private void addCoin(Coin e){
        this.coins.add(e);
        this.addChild(e);
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

    private void spawnNewFish(){
        Vector3f submarineForward = Maths.getAxis(submarine.getRotation(), "forward");
        Vector3f submarineRight = Maths.getAxis(submarine.getRotation(), "right");
        Vector3f submarineUp = Maths.getAxis(submarine.getRotation(), "up");

        Vector3f tmp = (Vector3f) new Vector3f(submarineForward).scale(80);
        Vector3f tmp2 = (Vector3f) new Vector3f(submarineForward).scale(160);
        Vector3f pos1 = Vector3f.add(tmp, (Vector3f) new Vector3f(submarineRight).scale(90f), null);
        Vector3f pos2 = Vector3f.add(tmp, (Vector3f) new Vector3f(submarineRight).scale(-90f), null);
        Vector3f pos3 = Vector3f.add(tmp2, (Vector3f) new Vector3f(submarineRight).scale(100), null);
        Vector3f pos4 = Vector3f.add(tmp2, (Vector3f) new Vector3f(submarineRight).scale(-100), null);

        Vector3f submarineGlobalPosition = submarine.getGlobalPosition();
        pos1 = Vector3f.add(submarineGlobalPosition, pos1, null);
        pos2 = Vector3f.add(submarineGlobalPosition, pos2, null);
        pos3 = Vector3f.add(submarineGlobalPosition, pos3, null);
        pos4 = Vector3f.add(submarineGlobalPosition, pos4, null);

        Quaternion rot1 = Maths.createFromAxisAngle(submarineUp, -45f);
        Quaternion rot2 = Maths.createFromAxisAngle(submarineUp, 45f);
        Quaternion submarineRotation = submarine.getRotation();
        Quaternion.mul(rot1, submarineRotation, rot1);
        Quaternion.mul(rot2, submarineRotation, rot2);


        createFishGroup(fishModel, pos1, rot1, 20f, 20);
        createFishGroup(fishModel, pos2, rot2, 20f, 20);
        createFishGroup(fishModel, pos3, rot1, 30f, 20);
        createFishGroup(fishModel, pos4, rot2, 30f, 20);
    }
}
