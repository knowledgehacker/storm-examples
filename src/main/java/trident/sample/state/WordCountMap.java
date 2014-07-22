package trident.sample.state;

import storm.trident.state.map.IBackingMap;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class WordCountMap implements IBackingMap<Long> {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final Map<String, Long> wordCountMap = new ConcurrentHashMap<String, Long>();

	public List<Long> multiGet(List<List<Object>> keys) {
		List<Long> counts = new ArrayList<Long>();
		for(List<Object> key: keys) {
			String word = (String)key.get(0);
			Long count = wordCountMap.get(word);
			counts.add(count == null ? 0 : count);
		}

		return counts;
	}

    public void multiPut(List<List<Object>> keys, List<Long> vals) {
		for(int i = 0; i < keys.size(); ++i) {
			String word = (String)keys.get(i).get(0);
            Long oldCount = wordCountMap.get(word);
            Long newCount = (oldCount == null) ? 1 : vals.get(i);
			LOG.info("word: " + word + ", oldCount: " + oldCount + ", newCount: " + newCount);
			wordCountMap.put(word, newCount);
		}
	}
}
