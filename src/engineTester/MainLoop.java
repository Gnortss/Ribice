package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import materials.Material;
import models.Model;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;

public class MainLoop {

    public static void main(String[] args) {

        WindowManager.init();
        Loader loader = new Loader();

        /* Load model from file */
        Model model = OBJLoader.loadObjModel("fish", loader);
        Model model1 = OBJLoader.loadObjModel("test", loader);
        Model model2 = OBJLoader.loadObjModel("debug", loader);
        /* Load texture and create TexturedModel */
        /* This debugMat is used for a cube which which will be positioned where the light is. TEMPORARY */
        Material debugMat = new Material(loader.loadTexture("white"));
        debugMat.setDiffuse(new Vector3f(0, 0, 0));
        debugMat.setSpecular(new Vector3f(0, 0, 0));

        TexturedModel staticModel = new TexturedModel(model1, new Material(loader.loadTexture("white")));
        TexturedModel staticModel1 = new TexturedModel(model, new Material(loader.loadTexture("fish_colormap")));
        TexturedModel staticModel2 = new TexturedModel(model2, debugMat);

        Scene scene = new Scene();
        scene.createSubmarine(staticModel, staticModel2);
        Entity fish = scene.createFish(staticModel1, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0));

        Renderer renderer = new Renderer();
        while(!Display.isCloseRequested()) {

            scene.getSubmarine().move();

            renderer.render(scene);

            WindowManager.update();

        }

        renderer.clean();
        loader.clean();
        WindowManager.destroy();
    }
}
