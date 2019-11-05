package toolbox;

import entities.Camera;
import models.RawModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import java.util.ArrayList;
import java.util.List;

public class Maths {

    public static RawModel pointOnSphere(Loader loader, int n, double viewAngle) {
        double goldenRatio = (1 + Math.sqrt(5)) / 2;
        double angleIncrement = Math.PI * 2 * goldenRatio;

        List<Double> indices = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            double t = (double) i / n;
            float inclination = (float) Math.acos(1 - 2 * t);
            if (inclination <= viewAngle) {
                indices.add(t);
            }
        }

        float[] positions = new float[indices.size()*6];
        for(int i = 0; i < indices.size(); i++) {
            double t = indices.get(i);
            float inclination = (float) Math.acos(1 - 2 * t);
            float azimuth = (float) (angleIncrement * i);

            positions[6*i] = 0f;
            positions[6*i+1] = 0f;
            positions[6*i+2] = 0f;
            positions[6*i+3] = (float) (Math.cos(inclination)) * -25; // X
            positions[6*i+4] = (float) (Math.sin(inclination) * Math.sin(azimuth)) * 25; // Y
            positions[6*i+5] = (float) (Math.sin(inclination) * Math.cos(azimuth)) * 25; // Z
        }

        return loader.loadLinesToVAO(positions);
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

}
