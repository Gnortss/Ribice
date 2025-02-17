package utils;

import entities.Camera;
import entities.Coin;
import entities.Submarine;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Maths {

    public static boolean isColliding(Submarine s, Coin c) {
        Vector3f p1 = s.getGlobalPosition();
        Vector3f p2 = c.getGlobalPosition();

        float d = (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) + Math.pow(p1.z - p2.z, 2));
        return d < s.getColSphereRadius() + c.getColSphereRadius();
    }

    public static Matrix4f createRotationMatrix(Quaternion r){
        Vector3f forward =  new Vector3f(2.0f * (r.getX() * r.getZ() - r.getW() * r.getY()), 2.0f * (r.getY() * r.getZ() + r.getW() * r.getX()), 1.0f - 2.0f * (r.getX() * r.getX() + r.getY() * r.getY()));
        Vector3f up = new Vector3f(2.0f * (r.getX() * r.getY() + r.getW() * r.getZ()), 1.0f - 2.0f * (r.getX() * r.getX() + r.getZ() * r.getZ()), 2.0f * (r.getY() * r.getZ() - r.getW() * r.getX()));
        Vector3f right = new Vector3f(1.0f - 2.0f * (r.getY() * r.getY() + r.getZ() * r.getZ()), 2.0f * (r.getX() * r.getY() - r.getW() * r.getZ()), 2.0f * (r.getX() * r.getZ() + r.getW() * r.getY()));

        Matrix4f m = new Matrix4f();

        m.m00 = right.getX();   m.m01 = right.getY();   m.m02 = right.getZ();   m.m03 = 0;
        m.m10 = up.getX();      m.m11 = up.getY();      m.m12 = up.getZ();      m.m13 = 0;
        m.m20 = forward.getX(); m.m21 = forward.getY(); m.m22 = forward.getZ(); m.m23 = 0;
        m.m30 = 0;              m.m31 = 0;              m.m32 = 0;              m.m33 = 1;

        return m;
    }

    public static Vector3f rotateVector(Vector3f v, Quaternion r){
        Quaternion conjugate = Quaternion.negate(r, null);
        Quaternion w = Quaternion.mul(r, r, null);
        Quaternion.mul(w, conjugate, w);

        return new Vector3f(w.getX(), w.getY(), w.getZ());
    }

    public static Vector3f getAxis(Quaternion r, String a){
        switch (a){
            case "forward":    return new Vector3f(-(2.0f * (r.getX() * r.getZ() - r.getW() * r.getY())), -(2.0f * (r.getY() * r.getZ() + r.getW() * r.getX())), -(1.0f - 2.0f * (r.getX() * r.getX() + r.getY() * r.getY())));
            case "up":      return new Vector3f(2.0f * (r.getX() * r.getY() + r.getW() * r.getZ()), 1.0f - 2.0f * (r.getX() * r.getX() + r.getZ() * r.getZ()), 2.0f * (r.getY() * r.getZ() - r.getW() * r.getX()));
            case "down":    return new Vector3f(-(2.0f * (r.getX() * r.getY() + r.getW() * r.getZ())), -(1.0f - 2.0f * (r.getX() * r.getX() + r.getZ() * r.getZ())), -(2.0f * (r.getY() * r.getZ() - r.getW() * r.getX())));
            case "right":   return new Vector3f(1.0f - 2.0f * (r.getY() * r.getY() + r.getZ() * r.getZ()), 2.0f * (r.getX() * r.getY() - r.getW() * r.getZ()), 2.0f * (r.getX() * r.getZ() + r.getW() * r.getY()));
            case "left":    return new Vector3f(-(1.0f - 2.0f * (r.getY() * r.getY() + r.getZ() * r.getZ())), -(2.0f * (r.getX() * r.getY() - r.getW() * r.getZ())), -(2.0f * (r.getX() * r.getZ() + r.getW() * r.getY())));
            default:        return new Vector3f(2.0f * (r.getX() * r.getZ() - r.getW() * r.getY()), 2.0f * (r.getY() * r.getZ() + r.getW() * r.getX()), 1.0f - 2.0f * (r.getX() * r.getX() + r.getY() * r.getY()));
        }
    }

    public static Quaternion createFromAxisAngle(Vector3f axis, float angle){
        Quaternion x = new Quaternion();
        x.setFromAxisAngle(new Vector4f(axis.x, axis.y, axis.z, (float) Math.toRadians(angle)));
        return x;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion rotation, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        /* Translate */
        Matrix4f.translate(translation, matrix, matrix);

        /* Rotate */
        Matrix4f rotationMatrix = Maths.createRotationMatrix(rotation);
        Matrix4f.mul(matrix, rotationMatrix, matrix);

        /* Scale */
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = camera.getParent().getGlobalTransform();

        /* Translate */
        Matrix4f.translate(camera.getPosition(), viewMatrix, viewMatrix);

        /* Rotate */
        Matrix4f rotationMatrix = Maths.createRotationMatrix(camera.getRotation());
        Matrix4f.mul(rotationMatrix, viewMatrix, viewMatrix);

        /* Invert to get view matrix */
        viewMatrix = Matrix4f.invert(viewMatrix, null);

        return viewMatrix;
    }

    public static Matrix4f createProjectionMatrix(float FOV){
        float NEAR_PLANE = 0.1f;
        float FAR_PLANE = 300f;

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
