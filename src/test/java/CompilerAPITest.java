import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class CompilerAPITest {

    public static void main(String[] args) throws Exception {
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager sjfm = jc.getStandardFileManager(null, null, null);

        File javaFile = new File("/tmp/ToBeCompiled.java");
        Iterable fileObjects = sjfm.getJavaFileObjects(javaFile);
        String[] options = new String[]{"-d", "/tmp/tmp"};

        jc.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call();
        sjfm.close();
        System.out.println("Class has been successfully compiled");



        URL[] urls = new URL[]{ new URL("file:///tmp/tmp/") };
        URLClassLoader ucl = new URLClassLoader(urls);
        Class clazz = ucl.loadClass("ToBeCompiled");
        System.out.println("Class has been successfully loaded");

        Method method = clazz.getDeclaredMethod("callMe", null);

        Object object = clazz.newInstance();
        method.invoke(object, null);


    }


}
