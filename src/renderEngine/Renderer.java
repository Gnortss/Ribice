package renderEngine;

import entities.Entity;
import materials.Material;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.DefaultShader;
import utils.Maths;
import utils.MatrixType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Renderer {
    private DefaultShader shader;
    private List<Entity> entities = new ArrayList<>();

    public Renderer(){
        this.shader = new DefaultShader();
        glSettings();
        Matrix4f projectionMatrix = Maths.createProjectionMatrix(70);

        shader.use();
        shader.loadMatrix(projectionMatrix, MatrixType.PROJECTION);
        shader.stopUsing();
    }

    public void render(Scene scene){
        clear();
        shader.use();
        /* NOTE: scene.lights can't be empty */
        shader.useLightSources(scene.getLights());
        /* Note: scene.mainCamera can't be null */
        shader.loadMatrix(scene.getMainCamera().getViewMatrix(), MatrixType.VIEW);

        scene.buildHashMap(); /* groups entities by textured model */
        HashMap<TexturedModel, ArrayList<Entity>> m = scene.getEntities();
        m.forEach((TexturedModel model, ArrayList<Entity> list) -> {
            int vCount = model.getModel().getVertexCount();

            /* Bind VAOs and VBOs */
            GL30.glBindVertexArray(model.getModel().getVao());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            GL20.glEnableVertexAttribArray(2);

            /* Load material specific variables to shader */
            Material mat = model.getMaterial();
            shader.useMaterial(mat);

            /* Bind texture */
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getTexture());

            /* Load Entity specific variables:
            *   - global transformation matrix
            *  AND render Entity*/
            for (Entity e: list) {
                shader.loadMatrix(e.getGlobal(), MatrixType.TRANSFORMATION);
                GL11.glDrawElements(GL11.GL_TRIANGLES, vCount, GL11.GL_UNSIGNED_INT, 0);
            }

            /* Unbind VBOs and VAO */
            GL20.glDisableVertexAttribArray(2);
            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);
        });

        shader.stopUsing();
    }

    public void clean() { shader.clean(); }

    private void clear() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.3f, 0.3f, 0.7f, 1);
    }

    private void glSettings() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public DefaultShader getShader() {
        return shader;
    }
}
