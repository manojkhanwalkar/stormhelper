package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class Node {

    String name ;

    String parentName =null;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    List<Node> nodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node n)
    {

        nodes.add(n);
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", nodes=" + nodes +
                '}';
    }
}
