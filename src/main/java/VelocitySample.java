import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocitySample {

    public static void main(String[] args) {

       // VelocityEngine ve = new VelocityEngine();
       // ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        //ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", "/Users/mkhanwalkar/stormhelper/src/main/java");
        Velocity.init( p );
      //  ve.init();

        VelocityContext context = new VelocityContext();

        context.put( "name", new String("Velocity") );

        Template template = null;

        try
        {
        template = Velocity.getTemplate("test.vm");
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        StringWriter sw = new StringWriter();

        template.merge( context, sw );

        String s = sw.toString();

        System.out.println(s);




}


}
