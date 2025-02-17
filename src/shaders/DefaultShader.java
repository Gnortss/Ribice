package shaders;

import entities.Light;
import materials.Material;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import utils.MatrixType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

public class DefaultShader {
    private static final String VERTEX_SOURCE = "src/shaders/vertexShader.txt";
    private static final String FRAGMENT_SOURCE = "src/shaders/fragmentShader.txt";

    private int program;
    private int vertexShader;
    private int fragmentShader;

    private HashMap<String, Integer> uniform_location = new HashMap<>();

    public DefaultShader() {
        /* Compile shaders */
        vertexShader = compileShader(VERTEX_SOURCE, GL20.GL_VERTEX_SHADER);
        fragmentShader = compileShader(FRAGMENT_SOURCE, GL20.GL_FRAGMENT_SHADER);

        /* Create program and attach shaders */
        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);

        /* Bind all attributes */
        GL20.glBindAttribLocation(program, 0, "position");
        GL20.glBindAttribLocation(program, 1, "aTextureCoordinates");
        GL20.glBindAttribLocation(program, 2, "aNormal");

        /* Link and validate program */
        GL20.glLinkProgram(program);
        GL20.glValidateProgram(program);

        /* Get All Uniform Locations */
        uniform_location.put("trans", getLocation("trans"));
        uniform_location.put("view", getLocation("view"));
        uniform_location.put("proj", getLocation("proj"));
        uniform_location.put("lightPosition", getLocation("lightPosition"));
        uniform_location.put("lightColor", getLocation("lightColor"));
        uniform_location.put("material.ambient", getLocation("material.ambient"));
        uniform_location.put("material.diffuse", getLocation("material.diffuse"));
        uniform_location.put("material.specular", getLocation("material.specular"));
        uniform_location.put("material.shininess", getLocation("material.shininess"));

        /* Get uniform locations for point lights */
        for(int i = 0; i < 10; i++){
            uniform_location.put("pointLight[" + i + "].position", getLocation("pointLight[" + i + "].position"));
            uniform_location.put("pointLight[" + i + "].attenuation", getLocation("pointLight[" + i + "].attenuation"));
            uniform_location.put("pointLight[" + i + "].ambient", getLocation("pointLight[" + i + "].ambient"));
            uniform_location.put("pointLight[" + i + "].diffuse", getLocation("pointLight[" + i + "].diffuse"));
            uniform_location.put("pointLight[" + i + "].specular", getLocation("pointLight[" + i + "].specular"));
        }
    }

    public void use(){ GL20.glUseProgram(program); }

    public void stopUsing(){ GL20.glUseProgram(0); }

    public void clean(){
        stopUsing();
        GL20.glDetachShader(program, vertexShader);
        GL20.glDetachShader(program, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteProgram(program);
    }

    /* Loads point lights to shader */
    public void useLightSources(List<Light> lights) {
        /* lights are already sorted based on distance from camera ascending */
        int pointLightsCount = 0;
        for (Light light : lights) {
            if(pointLightsCount >= 10) break;

            /* Load point light variables to shader */
            Vector3f pos = light.getGlobalPosition();
            Vector3f att = light.getAttenuation();
            Vector3f amb = light.getAmbient();
            Vector3f dif = light.getDiffuse();
            Vector3f spec = light.getSpecular();
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].position"), pos.x, pos.y, pos.z);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].attenuation"), att.x, att.y, att.z);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].ambient"), amb.x, amb.y, amb.z);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].diffuse"), dif.x, dif.y, dif.z);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].specular"), spec.x, spec.y, spec.z);
            pointLightsCount++;
        }
        /* Load empty point lights -> it has to have some value */
        for(; pointLightsCount < 10; pointLightsCount++){
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].position"), 0, 0, 0);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].attenuation"), 1, 0, 0);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].ambient"), 0, 0, 0);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].diffuse"), 0, 0, 0);
            GL20.glUniform3f(uniform_location.get("pointLight[" + pointLightsCount + "].specular"), 0, 0, 0);
        }
    }

    public void useMaterial(Material mat) {
        Vector3f a = mat.getAmbient();
        Vector3f d = mat.getDiffuse();
        Vector3f s = mat.getSpecular();
        GL20.glUniform3f(uniform_location.get("material.ambient"), a.x, a.y, a.z);
        GL20.glUniform3f(uniform_location.get("material.diffuse"), d.x, d.y, d.z);
        GL20.glUniform3f(uniform_location.get("material.specular"), s.x, s.y, s.z);
        GL20.glUniform1f(uniform_location.get("material.shininess"), mat.getShininess());
    }

    public void loadMatrix(Matrix4f matrix, MatrixType type) {
        FloatBuffer mb = BufferUtils.createFloatBuffer(16);
        matrix.store(mb);
        mb.flip();

        int location;
        switch(type){
            case PROJECTION: location = uniform_location.get("proj"); break;
            case VIEW: location = uniform_location.get("view"); break;
            default: location = uniform_location.get("trans");
        }
        GL20.glUniformMatrix4(location, false, mb);
    }

    private int getLocation(String name) {
        return GL20.glGetUniformLocation(program, name);
    }

    private static int compileShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, shaderSource);
        GL20.glCompileShader(shader);
        if(GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(shader, 500));
            System.err.println("Failed compiling shader");
        }
        return shader;
    }
}
