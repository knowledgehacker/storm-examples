package trident.sample.topology;

import backtype.storm.Config;
import backtype.storm.tuple.Fields;
import backtype.storm.generated.StormTopology;
import backtype.storm.LocalCluster;
import storm.trident.TridentTopology;
import storm.trident.TridentState;
import storm.trident.operation.builtin.Count;

import trident.sample.spout.DataFileSpout;
import trident.sample.state.WordCountState;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class WordCountTopology {
	private static final Logger LOG = LoggerFactory.getLogger(WordCountTopology.class);

	public static StormTopology buildTopology(String dataFileName) {
		DataFileSpout spout = new DataFileSpout(dataFileName);
		TridentTopology topology = new TridentTopology(); 
		TridentState state = topology.newStream("data_file_spout", spout)
			.groupBy(new Fields("word"))
			.persistentAggregate(new WordCountState.Factory(), new Count(), new Fields("count"));
		
		return topology.build();
	}

	public static void main(String[] args) {
		int argNum = args.length;
		if(argNum != 1) {
			System.err.println("Illegal argument number - " + argNum);
			System.out.println("Usage: storm jar [your_topology].jar data_file_name");

			return;
		}

		String dataFileName = args[0];
		Config config = new Config();
        int workerNum = 1;
        config.setNumWorkers(workerNum);
        config.setNumAckers(workerNum);
        config.setDebug(false);

		LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("word-count-topology", config, buildTopology(dataFileName));
		try {
        	Thread.sleep(200000);
		} catch (InterruptedException ie) {
			LOG.error(ie.toString());
		}
        localCluster.shutdown();
	}
}
