package renderEngine;

import models.Model;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public Model createModel(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        /* Create and bind VAO */
        int vao = GL30.glGenVertexArrays();
        vaos.add(vao);
        GL30.glBindVertexArray(vao);

        /* Bind indices */
        createVBOindices(indices);

        /* Create VBOs with given data (VBOs -> Attribute Lists) */
        createVBOf(0, 3, positions);
        createVBOf(1, 2, textureCoordinates);
        createVBOf(2, 3, normals);

        /* Unbind VAO */
        GL30.glBindVertexArray(0);

        /* Return Model with created VAO */
        return new Model(vao, indices.length);
    }

    public int loadTexture(String file) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/textures/" + file + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    /* Creates VBO with given data */
    private void createVBOf(int n, int size, float[] data) {
        /* Create and bind VBO */
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        /* Create Float Buffer and write data to it */
        FloatBuffer b = BufferUtils.createFloatBuffer(data.length);
        b.put(data);
        b.flip();

        /* Here we put buffer data into VBO */
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, b, GL15.GL_STATIC_DRAW);
        /* Define attribute number, size, type */
        GL20.glVertexAttribPointer(n, size, GL11.GL_FLOAT, false, 0, 0);

        /* Unbind VBO */
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /* Used for binding indices */
    private void createVBOindices(int[] data){
        /* Create and bind VBO */
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo); // GL15.GL_ELEMENT_ARRAY_BUFFER -> this buffer will be used for indices

        /* Create Int Buffer and write data to it */
        IntBuffer b = BufferUtils.createIntBuffer(data.length);
        b.put(data);
        b.flip();

        /* Put buffer data into VBO */
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, b, GL15.GL_STATIC_DRAW);
    }

    public void clean() {
        for(int vao:vaos)
            GL30.glDeleteVertexArrays(vao);

        for(int vbo:vbos)
            GL15.glDeleteBuffers(vbo);

        for(int texture:textures)
            GL15.glDeleteBuffers(texture);
    }
}
