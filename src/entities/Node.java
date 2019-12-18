package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import utils.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node {
    protected Node parent;
    protected List<Node> children;
    protected Vector3f position;
    protected Quaternion rotation;
    protected float scale;

    protected Matrix4f global;
    protected Matrix4f local;
    protected boolean dirty;

    public Node(){
        this.parent = null;
        this.children = new ArrayList<>();
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Quaternion();
        this.scale = 1;

        this.global = new Matrix4f();
        this.global.setIdentity();

        this.local = new Matrix4f();
        this.local.setIdentity();

        this.dirty = true;
    }

    /* Getters */
    public Vector3f getPosition() {
        return position;
    }

    public Quaternion getRotation() { return rotation; }

    public float getScale() {
        return scale;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public Matrix4f getGlobal() { return global; }

    /* Setters */
    public Node setPosition(Vector3f p) { this.position = p; dirty = true; return this;}

    public Node setRotation(Quaternion r) {
        this.rotation = r;
        dirty = true;
        return this;
    }

    public Node setScale(float s) { this.scale = s; dirty = true; return this;}

    public Node setGlobal(Matrix4f m) { this.global = m; return this;}

    /* Methods */
    public Node moveBy(Vector3f t) { Vector3f.add(this.position, t, this.position); dirty = true; return this; }

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
        if(dirty){
            dirty = false;
            local = Maths.createTransformationMatrix(position, rotation, scale);
            return local;
        }
        return local;
    }

    public Matrix4f getGlobalTransform() {
        if(this.parent == null) return this.getLocalTransform();
        return Matrix4f.mul(this.getLocalTransform(), this.parent.getGlobalTransform(), null);
    }
}
