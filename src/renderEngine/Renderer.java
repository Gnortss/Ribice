package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import shaders.StaticLineShader;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;
import java.util.Map;

public class Renderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;
    private StaticShader shader;
    private StaticLineShader lineShader;

    public Renderer(StaticShader shader, StaticLineShader lineShader){
        this.shader = shader;
        this.lineShader = lineShader;
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        lineShader.start();
        lineShader.loadProjectionMatrix(projectionMatrix);
        lineShader.stop();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.15f, 0.15f, 0.5f, 1);
    }

    public void render(Map<TexturedModel, List<Entity>> entities){
        for(TexturedModel model:entities.keySet()) {
            prepareTexturedModels(model);
            List<Entity> batch = entities.get(model);
            for(Entity entity:batch) {
                prepareInstance(entity, "default");
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel(model);
        }
    }

    public void renderLines(Map<TexturedModel, List<Entity>> entities) {
        for(TexturedModel model:entities.keySet()) {
            List<Entity> batch = entities.get(model);
            for(Entity entity:batch) {
                if(!entity.containsLineModel())
                    continue;
                RawModel lineModel = entity.getLineModel();

                GL30.glBindVertexArray(lineModel.getVaoID());
                GL20.glEnableVertexAttribArray(0);

                prepareInstance(entity, "lineShader");
                GL11.glDrawArrays(GL11.GL_LINES, 0, lineModel.getVertexCount());

                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
            }
        }
    }

    private void prepareTexturedModels(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());


    }

    private void unbindTexturedModel(TexturedModel model) {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity, String shader_type) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        if(shader_type.equals("default"))
            shader.loadTransformationMatrix(transformationMatrix);
        else if(shader_type.equals("lineShader"))
            lineShader.loadTransformationMatrix(transformationMatrix);
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

}
