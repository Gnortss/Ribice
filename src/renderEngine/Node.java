package renderEngine;

import utils.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Node {
    private NodeType type;
    private List<Node> children;
    private Node parent;

    public Node(NodeType type){
        this.type = type;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    public void addChild(Node c){
        this.children.add(c);
        c.parent = this;
    }

    public void removeChild(Node c){
        this.children.remove(c);
    }

    public void traverse(Consumer<Node> before, Consumer<Node> after){
        before.accept(this);
        for(Node child: children)
            child.traverse(before, after);
        after.accept(this);
    };

    public NodeType getType() {
        return type;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }
}
