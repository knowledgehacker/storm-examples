package trident.sample.spout;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class DataFileSpout implements IBatchSpout {
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private static final char DELIMITER = ' ';

	private final String _dataFileName;
	private long _batchId;
	private final Map<Long, List<Object>> _batchMap;

	// note that BufferedReader can not be serialized
	private BufferedReader _reader;

	public DataFileSpout(String dataFileName) {
		_dataFileName = dataFileName;

		_batchId = 0;
		_batchMap = new HashMap<Long, List<Object>>();
	}

	public void open(Map conf, TopologyContext context) {
		_reader = new BufferedReader(new InputStreamReader(this.getClass()
			.getResourceAsStream("/" + _dataFileName)));
	}

    public void emitBatch(long batchId, TridentCollector collector) {
		try {
			String line = _reader.readLine();
			if(line != null) {
				LOG.info("line: " + line);
				List<Object> words = new ArrayList<Object>();
				int start = 0;
				for(int end = start; end < line.length(); end = start) {
					while(line.charAt(end) != DELIMITER) {
						if(++end == line.length())
							break;
					}
					String word = line.substring(start, end);
					LOG.info("[" + start + ", " + end + "] => " + word);

					// emit word
					List<Object> emitObj = new ArrayList<Object>();
					emitObj.add(word);
					collector.emit(emitObj);

					words.add(word);

					start = end+1;
				}
				_batchMap.put(_batchId++, words);
			}
		} catch (IOException ioe) {
			LOG.error(ioe.toString());
		}
	}

    public void ack(long batchId) {
		_batchMap.remove(batchId);
	}

    public void close() {
		if(_reader != null) {
			try {
				_reader.close();
			} catch (IOException ioe) {
				LOG.error(ioe.toString());
			}
		}
	}

    public Map getComponentConfiguration() {
		return null;
	}

    public Fields getOutputFields() {
		return new Fields("word");
	}
}
