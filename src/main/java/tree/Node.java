package tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class Node {

    String name ;

    String level =null;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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
