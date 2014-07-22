package trident.sample.state;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import storm.trident.state.StateFactory;
import storm.trident.state.State;
import storm.trident.state.map.NonTransactionalMap;

public class WordCountState extends NonTransactionalMap<Long> {

	public WordCountState() {
		super(new WordCountMap());
	}

	public static class Factory implements StateFactory {
    	public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
       		return new WordCountState();
    	}
	}
}
