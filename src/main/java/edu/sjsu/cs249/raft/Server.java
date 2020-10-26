package edu.sjsu.cs249.raft;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc;
import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerFutureStub;
import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerStub;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class Server {
	//TODO:
	/**
	 * 1. Initialize the State object.
	 * 2. Load all persistent variables.
	 * 3. Start all the threads, and make them wait for a their respective state changes..
	 * 4. Initialize all the stubs by reading from the props and maintain a mapping of candidate ID..to stub and Ip port details..
	 * 5. Initialize the service..
	 * 5. Start the grpcServe..
	 * 
	 * */
	void parseClusterConfiguration(String configFile, State state)
	{
		
	}
	
	void buildChannelsAndStubs(Map<Integer, String[]> connectionInfo, State state)
	{
		//ManagedChannelBuilder.forAddress("name", 4050).build();
		//1. build channel for each candidate and add it to the connection map
		//2. From the channel build non - blocking stubs and add it to the candidate stub map.
		state.candidateChannelMap = new HashMap<>(connectionInfo.size());
		state.candidateStubMap = new HashMap<>(connectionInfo.size());
		
		for(Map.Entry<Integer, String[]> candidate : connectionInfo.entrySet())
		{
			String[] connParams = candidate.getValue();
			Channel channel = ManagedChannelBuilder.forAddress(connParams[0], Integer.parseInt(connParams[1])).build();
			RaftServerFutureStub fstub = RaftServerGrpc.newFutureStub(channel);
			state.candidateChannelMap.put(candidate.getKey(), channel);
			state.candidateStubMap.put(candidate.getKey(), fstub);
		}
	
	}
	
	Map<Integer,String[]> getConnectionInformation(Properties config)
	{
		int cadidateID = Integer.parseInt(config.getProperty("candidateID"));
		int numOfServers = Integer.parseInt(config.getProperty("numOfServers"));
		Map<Integer, String[]> connectionInfo = new HashMap<>(numOfServers);
		//candidate IDs will start from 1 to numOfServer
		for(int i = 1; i <= numOfServers; i++)
		{
			if(i == cadidateID) continue;
			String[] connInfo = config.getProperty(i+"").split(":");
			connectionInfo.put(i, connInfo);
		}
		return connectionInfo;
	}
	
	void extractTimeoutRange(Properties config, State state)
	{
		state.setLowerBound(Long.parseLong(config.getProperty("lowerBound")));
		state.setUpperBound(Long.parseLong(config.getProperty("upperBound")));
	}
}
