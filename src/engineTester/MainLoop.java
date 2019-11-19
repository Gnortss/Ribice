package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;

public class MainLoop {

    public static void main(String[] args) {

        WindowManager.init();
        Loader loader = new Loader();

        /* Load model from file */
        Model model = OBJLoader.loadObjModel("fish", loader);
        /* Load texture and create TexturedModel */
        TexturedModel staticModel = new TexturedModel(model, new Material(loader.loadTexture("fish_colormap")));
        /* Create entity from TexturedModel*/
        Entity fish = new Entity(staticModel, new Vector3f(0, 0, -30), 0, 0, 0, 1);

        Light light = new Light(new Vector3f(0, 5, -0), new Vector3f(1, 1, 1));

        Camera camera = new Camera();

        Renderer renderer = new Renderer();
        while(!Display.isCloseRequested()) {

            if(Keyboard.isKeyDown(Keyboard.KEY_K))
                fish.increaseRotation(0, -0.2f, 0);
            else if(Keyboard.isKeyDown(Keyboard.KEY_L))
                fish.increaseRotation(0, 0.2f, 0);

            camera.move();

            // To render each entity call:
            renderer.addEntity(fish);

            renderer.render(camera, light);

            WindowManager.update();

        }

        renderer.clean();
        loader.clean();
        WindowManager.destroy();
    }
}
