package utils;

import entities.Camera;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;

import java.util.ArrayList;
import java.util.List;

public class Maths {

//    public static Model pointOnSphere(Loader loader, int n, double viewAngle) {
//        double goldenRatio = (1 + Math.sqrt(5)) / 2;
//        double angleIncrement = Math.PI * 2 * goldenRatio;
//
//        List<Double> indices = new ArrayList<>();
//        for(int i = 0; i < n; i++) {
//            double t = (double) i / n;
//            float inclination = (float) Math.acos(1 - 2 * t);
//            if (inclination <= viewAngle) {
//                indices.add(t);
//            }
//        }
//
//        float[] positions = new float[indices.size()*6];
//        for(int i = 0; i < indices.size(); i++) {
//            double t = indices.get(i);
//            float inclination = (float) Math.acos(1 - 2 * t);
//            float azimuth = (float) (angleIncrement * i);
//
//            positions[6*i] = -30f;
//            positions[6*i+1] = 2f;
//            positions[6*i+2] = 0f;
//            positions[6*i+3] = (float) (Math.cos(inclination)) * -50 -30; // X
//            positions[6*i+4] = (float) (Math.sin(inclination) * Math.sin(azimuth)) * 50 + 2; // Y
//            positions[6*i+5] = (float) (Math.sin(inclination) * Math.cos(azimuth)) * 50; // Z
//        }
//
//        return loader.loadLinesToVAO(positions);
//    }

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
        Matrix4f.rotate((float) Math.toRadians(camera.getRotX()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRotY()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRotZ()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public static Matrix4f createProjectionMatrix(float FOV){
        float NEAR_PLANE = 0.1f;
        float FAR_PLANE = 1000f;

        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;

        return projectionMatrix;
    }

}
