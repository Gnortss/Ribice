package engineTester;

import entities.Entity;
import entities.Fish;
import entities.Node;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import utils.Maths;

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

//        for(int i = 0; i < 10; i++){
//            for(int j = 0; j < 10; j++){
//                scene.createFish(staticModel1, new Vector3f(i * 5, j * 5, 0), new Quaternion());
//            }
//        }

        Quaternion rot = Maths.createFromAxisAngle(Maths.getAxis(new Quaternion(), "up"), -180f);
        scene.createFishGroup(staticModel1, new Vector3f(0, 50, -180), rot, 30, 100);
        scene.createFishGroup(staticModel1, new Vector3f(100, 50, -180), rot, 30, 100);
        scene.createFishGroup(staticModel1, new Vector3f(-100, 50, -180), rot, 30, 100);
        scene.createFishGroup(staticModel1, new Vector3f(0, 50, 180), new Quaternion(), 30, 100);
        scene.createFishGroup(staticModel1, new Vector3f(100, 50, 180), new Quaternion(), 30, 100);
        scene.createFishGroup(staticModel1, new Vector3f(-100, 50, 180), new Quaternion(), 30, 100);

        Renderer renderer = new Renderer();
        scene.update(0f);
        long last_time = System.nanoTime();
        while(!Display.isCloseRequested()) {
            long time = System.nanoTime();

            /* IN SECONDS */
            float deltaTime = (time - last_time)/1000000000f;
            last_time = time;


            scene.getSubmarine().move(deltaTime);

            scene.update(deltaTime);
            renderer.render(scene);

            WindowManager.update();

        }

        renderer.clean();
        loader.clean();
        WindowManager.destroy();
    }
}
