package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import shaders.StaticLineShader;
import shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader = new StaticShader();
    private StaticLineShader lineShader = new StaticLineShader();
    private Renderer renderer = new Renderer(shader, lineShader);

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

    private boolean debugMode = false;

    public void render(List<Light> lights, Camera camera) {
        renderer.prepare();

        shader.start();
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);

        if(Keyboard.isKeyDown(Keyboard.KEY_P))
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        else
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        renderer.render(entities);

        shader.stop();

        if(debugMode) {
            lineShader.start();
            lineShader.loadViewMatrix(camera);
            renderer.renderLines(entities);
            lineShader.stop();
        }

        entities.clear();

    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if(batch != null){
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp(){
        shader.cleanUp();
        lineShader.cleanUp();
    }

}
