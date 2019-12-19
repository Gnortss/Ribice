package engineTester;

import entities.Entity;
import entities.Node;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;

public class MainLoop {

    public static void main(String[] args) {

        WindowManager.init();
        Loader loader = new Loader();

        /* Load model from file */
        Model model = OBJLoader.loadObjModel("fish", loader);
        /* Load texture and create TexturedModel */

        TexturedModel staticModel1 = new TexturedModel(model, new Material(loader.loadTexture("fish_colormap")));

        Scene scene = new Scene(loader);
        scene.createSubmarine();
        Entity fish = scene.createFish(staticModel1, new Vector3f(0, 0, -15), new Quaternion());
        Node fishGroup = new Node().setPosition(new Vector3f(0, 0, -5));
        fish.addChild(fishGroup);

        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                fishGroup.addChild(new Entity(staticModel1, new Vector3f(i * 5, j * 5, 0), new Quaternion(), 1));
                scene.addLight(new Vector3f(i * 5, j * 5, -4));
            }
        }

        Renderer renderer = new Renderer();
        scene.update();
        long last_time = System.nanoTime();
        while(!Display.isCloseRequested()) {
            long time = System.nanoTime();

            /* IN SECONDS */
            float deltaTime = (time - last_time)/1000000000f;
            last_time = time;


            scene.getSubmarine().move(deltaTime);

            scene.update();
            renderer.render(scene);

            WindowManager.update();

        }

        renderer.clean();
        loader.clean();
        WindowManager.destroy();
    }
}
