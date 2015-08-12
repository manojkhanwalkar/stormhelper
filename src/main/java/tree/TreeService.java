package tree;

import server.Service;

import java.util.List;

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

    @Override
    public void init() {

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
