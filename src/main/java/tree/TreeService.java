package tree;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import server.Service;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
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

        createSpoutFromTemplate();
        createBoltFromTemplate();
       // compileGeneratedCode();  TODO - get storm jars in the class path , plus the app beans .

    }

    private void createSpoutFromTemplate()
    {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "/Users/mkhanwalkar/stormhelper/src/main/java");
        Velocity.init(p);
        //  ve.init();

        VelocityContext context = new VelocityContext();

        context.put( "name", new String(firstNode) );

        Template template = null;

        try
        {
            template = Velocity.getTemplate("spout.vm");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        try {
            FileWriter fW = new FileWriter("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/Spout"+firstNode+".java");
            fW.write(s);
            fW.flush();
            fW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    private void createBoltFromTemplate()
    {
        Map<String,Node> tmpNodesSeen = new HashMap<>();

        tmpNodesSeen.putAll(nodesSeen);



        Node node = tmpNodesSeen.remove(firstNode);

        tmpNodesSeen.values().stream().forEach(n->{

            createBoltFromTemplate(n.getName());
        });


        List<Node> processedNodes = printBF(node);

    /*    processedNodes.stream().forEach(n->{

            System.out.println( n.getName() + "  " + n.getParentName());

        });*/

        processedNodes.remove(0);

        createStarterFromTemplate(firstNode,processedNodes);



    }

    private List<Node> printBF(Node n)
    {
        List<Node> processedNodes = new ArrayList<>();
        Queue<Node> nodes = new ArrayDeque<>();

        nodes.add(n);

        while (!nodes.isEmpty())
        {
            Node node = nodes.remove();
            processedNodes.add(node);


                node.getNodes().stream().forEach((n1) -> {
                    n1.setParentName(node.getName());

                });

            nodes.addAll(node.getNodes()); // asssumes no cyclic dependency .
        }

        return processedNodes;


    }

    private void createBoltFromTemplate(String name)
    {

        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "/Users/mkhanwalkar/stormhelper/src/main/java");
        Velocity.init(p);
        //  ve.init();

        VelocityContext context = new VelocityContext();

        context.put( "name", name);

        Template template = null;

        try
        {
            template = Velocity.getTemplate("bolt.vm");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        try {
            FileWriter fW = new FileWriter("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/Bolt"+name+".java");
            fW.write(s);
            fW.flush();
            fW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void createStarterFromTemplate(String spoutName, List<Node> nodes)
    {

        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "/Users/mkhanwalkar/stormhelper/src/test/java");
        Velocity.init(p);
        //  ve.init();

        VelocityContext context = new VelocityContext();

        context.put( "spoutname", spoutName);
        context.put("nodes", nodes);

        Template template = null;

        try
        {
            template = Velocity.getTemplate("stormstarter.vm");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        try {
            FileWriter fW = new FileWriter("/Users/mkhanwalkar/stormhelper/src/test/java/StormStarter.java");
            fW.write(s);
            fW.flush();
            fW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    private void compileGeneratedCode()
    {
        try {
            JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);

            File javaFile = new File("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/SpoutA.java");
            Iterable fileObjects = sjfm.getJavaFileObjects(javaFile);
            String[] options = new String[]{"-d", "/tmp/tmp"};

            jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
            sjfm.close();
            System.out.println("Class has been successfully compiled");


            URL[] urls = new URL[]{ new URL("file:///tmp/tmp/") };
            URLClassLoader ucl = new URLClassLoader(urls);
            Class clazz = ucl.loadClass("SpoutA");
            System.out.println("Class has been successfully loaded");

            Method method = clazz.getDeclaredMethod("callMe", null);

            Object object = clazz.newInstance();
            method.invoke(object, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


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
