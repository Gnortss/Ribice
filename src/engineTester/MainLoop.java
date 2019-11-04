package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class MainLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        // create RawModel of the dragon and then TexturedModel from RawModel, Texture
        RawModel model = OBJLoader.loadObjModel("dragon", loader);
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(0.5f);

        Entity dragon = new Entity(staticModel, new Vector3f(0, -5, -50), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(0, 5, -30), new Vector3f(1, 1, 1), new Vector3f(1, 0.02f, 0.003f));

        Camera camera = new Camera();


        MasterRenderer renderer = new MasterRenderer();
        while(!Display.isCloseRequested()) {

            dragon.increaseRotation(0, 0.2f, 0);

            camera.move();

            // To render each entity call:
            renderer.processEntity(dragon);

            renderer.render(light, camera);

            DisplayManager.updateDisplay();

        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
