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
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

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


    /**
     * Node names are interpolated into generated Java source (class names, string
     * literals) and into output file names, so only plain Java identifiers are accepted.
     * This blocks code injection into the generated classes and path traversal
     * (e.g. "../../x") when writing them.
     */
    private static final Pattern NODE_NAME = Pattern.compile("[A-Za-z_][A-Za-z0-9_]{0,63}");

    static String validateNodeName(String name)
    {
        if (name == null || !NODE_NAME.matcher(name).matches())
            throw new IllegalArgumentException("Invalid node name (must be a Java identifier): " + name);
        return name;
    }

    Map<String,Node> nodesSeen = new HashMap<>();
    private Node getNode(String name)
    {
        validateNodeName(name);
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
            String[] nodeNames = s.trim().split("\\s*,\\s*");

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

        Template template = loadTemplate("spout.vm");

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        writeFile("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/Spout"+firstNode+".java", s);



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

        Template template = loadTemplate("bolt.vm");

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        writeFile("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/Bolt"+name+".java", s);


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

        Template template = loadTemplate("stormstarter.vm");

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        writeFile("/Users/mkhanwalkar/stormhelper/src/test/java/StormStarter.java", s);


    }




    private static Template loadTemplate(String name)
    {
        try
        {
            return Velocity.getTemplate(name);
        }
        catch( Exception e )
        {
            throw new IllegalStateException("Unable to load template " + name, e);
        }
    }

    private static void writeFile(String path, String content)
    {
        try (FileWriter fW = new FileWriter(path)) {
            fW.write(content);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to write generated file " + path, e);
        }
    }

    private void compileGeneratedCode()
    {
        try {
            JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);

            File javaFile = new File("/Users/mkhanwalkar/stormhelper/src/main/java/stormappbeans/SpoutA.java");
            Iterable fileObjects = sjfm.getJavaFileObjects(javaFile);
            // Private (owner-only) temp dir: a fixed, shared path such as /tmp/tmp lets
            // another local user plant classes that would then be loaded and executed.
            Path outDir = Files.createTempDirectory("stormhelper-classes");
            String[] options = new String[]{"-d", outDir.toString()};

            jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
            sjfm.close();
            System.out.println("Class has been successfully compiled");


            URL[] urls = new URL[]{ outDir.toUri().toURL() };
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
