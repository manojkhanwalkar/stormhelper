package tree;

import server.Service;

import java.util.*;

/**
 * Created by mkhanwalkar on 8/12/15.
 */
public class TreeService implements Service {

    String name ;

    List<String> nodes ;

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }


    Map<String,Node> nodesSeen = new HashMap<>();
    private Node getNode(String name)
    {
        Node currNode =  nodesSeen.get(name);
        if (currNode==null)
        {
            currNode = new Node();
            currNode.setName(name);
            nodesSeen.put(name,currNode);
        }

        return currNode ;
    }

    String firstNode =null;

    @Override
    public void init() {

        nodes.stream().forEach(s -> {
            String[] nodeNames = s.split(",");

            if (firstNode == null )
                firstNode = nodeNames[0];

            Node prevNode = getNode(nodeNames[0]);

            for (int i=1;i<nodeNames.length;i++)
            {
                String name = nodeNames[i];
                Node currNode = getNode(nodeNames[i]);
                prevNode.addNode(currNode);
                prevNode = currNode;
            }


        });

        System.out.println(nodesSeen.get(firstNode));

    }

    @Override
    public void destroy() {

    }

    @Override
    public void setName(String s) {
        name = s;
    }

    @Override
    public String getName() {
        return name;
    }
}
