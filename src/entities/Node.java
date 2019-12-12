package entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import utils.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node {
    protected Node parent;
    protected List<Node> children;
    protected Vector3f position;
    protected float rotX, rotY, rotZ;
    protected float scale;

    public Node(){
        this.parent = null;
        this.children = new ArrayList<>();
        this.position = new Vector3f(0, 0, 0);
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;
        this.scale = 1;
    }

    /* Getters */
    public Vector3f getPosition() {
        return position;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScale() {
        return scale;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    /* Setters */
    public void setPosition(Vector3f p) { this.position = p; }

    public void setRotation(Vector3f r) {
        this.rotX = r.x % 360;
        this.rotY = r.y % 360;
        this.rotZ = r.z % 360;
    }

    public void setScale(float s) { this.scale = s; }

    public void rotateBy(Vector3f r) {
        this.rotX = (this.rotX + r.x + 360) % 360;
        this.rotY = (this.rotY + r.y + 360) % 360;
        this.rotZ = (this.rotZ +  r.z + 360) % 360;
    }

    public void moveBy(Vector3f t) {
        Vector3f.add(this.position, t, this.position);
    }

    /* Methods */
    public void addChild(Node c){
        c.parent = this;
        this.children.add(c);
    }

    public void removeChild(Node c){
        this.children.remove(c);
    }

    public void traverse(Consumer<Node> before, Consumer<Node> after){
        before.accept(this);
        for(Node child: this.children)
            child.traverse(before, after);
        after.accept(this);
    };

    public Matrix4f getLocalTransform() {
        return Maths.createTransformationMatrix(this.position, this.rotX, this.rotY, this.rotZ, this.scale);
    }

    public Matrix4f getGlobalTransform() {
        if(this.parent == null) return this.getLocalTransform();
        return Matrix4f.mul(this.getLocalTransform(), this.parent.getGlobalTransform(), null);
    }
}
