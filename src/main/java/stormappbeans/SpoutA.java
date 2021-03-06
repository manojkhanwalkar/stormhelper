package stormappbeans;

import appbeans.InputProcessor;
import appbeans.A;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoutA extends BaseRichSpout {

SpoutOutputCollector _collector;

public SpoutA() {
}

InputProcessor processor = new A();

public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
this._collector = collector;
}

public void close() {
}

public void nextTuple() {
Utils.sleep(100L);

this._collector.emit(new Values(processor.getValues()));
}

public void ack(Object msgId) {
}

public void fail(Object msgId) {
}

public void declareOutputFields(OutputFieldsDeclarer declarer) {
declarer.declare(new Fields(new String[]{"word"}));
}

/*  public Map<String, Object> getComponentConfiguration() {
if(!this._isDistributed) {
HashMap ret = new HashMap();
ret.put("topology.max.task.parallelism", Integer.valueOf(1));
return ret;
} else {
return null;
}
}*/
}
