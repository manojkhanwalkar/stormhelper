import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Created by mkhanwalkar on 7/28/15.
 */
public class HelloStormWorld {

    public static void main(String[] args) {
        Config conf = new Config();
        //conf.setDebug(true);
        conf.setNumWorkers(2);

        LocalCluster cluster = new LocalCluster();
        TopologyBuilder builder = new TopologyBuilder();

        cluster.submitTopology("test", conf, builder.createTopology());

        System.out.println("=========================>>>> Submitted to cluster");
        Utils.sleep(100000);
   //     System.out.println("Killing topology");
     //   cluster.killTopology("test");
       // cluster.shutdown();
    }

    //TODO - spout to read from file - send to two bolts - one writes to

}
