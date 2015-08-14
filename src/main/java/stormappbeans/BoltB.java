package stormappbeans;

import appbeans.OutputProcessor;
import appbeans.B;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.util.List;


public  class BoltB implements IRichBolt {
OutputCollector _collector;

public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
_collector = collector;
}

OutputProcessor processor = new B();


public void execute(Tuple tuple) {

List<Object> objects = processor.getValues(tuple.getValues());

    _collector.emit(tuple, new Values(objects.toArray()));

    }

public void cleanup() {
}

public void declareOutputFields(OutputFieldsDeclarer declarer) {
declarer.declare(new Fields("word"));
}

public Map getComponentConfiguration() {
return null;
}
}