
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import stormappbeans.*;

public class StormStarter {

public static void main(String[] args) {


Config conf = new Config();
conf.setNumWorkers(2);

LocalCluster cluster = new LocalCluster();
TopologyBuilder builder = new TopologyBuilder();


builder.setSpout("A", new SpoutA(), 10);

builder.setBolt("B", new BoltB(), 3).shuffleGrouping("A");
builder.setBolt("F", new BoltF(), 3).shuffleGrouping("A");
builder.setBolt("C", new BoltC(), 3).shuffleGrouping("B");
builder.setBolt("G", new BoltG(), 3).shuffleGrouping("B");
builder.setBolt("E", new BoltE(), 3).shuffleGrouping("F");
builder.setBolt("D", new BoltD(), 3).shuffleGrouping("C");
builder.setBolt("Z", new BoltZ(), 3).shuffleGrouping("D");


cluster.submitTopology("test", conf, builder.createTopology());

System.out.println("=========================>>>> Submitted to cluster");
Utils.sleep(100000);
//     System.out.println("Killing topology");
//   cluster.killTopology("test");
// cluster.shutdown();
}


}
