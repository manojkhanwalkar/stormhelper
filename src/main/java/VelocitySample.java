import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;

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
        template = Velocity.getTemplate("spout.vm");
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
