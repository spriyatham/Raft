package edu.sjsu.cs249.raft;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc;
import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerFutureStub;
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
	private State state;
	private Properties config;
	public static void main(String[] args) throws IOException {
		//You start here
		//1. Load all your persistent data
		//2. Instantiate the state object
		//3.
		Server raftServer = new Server();
		String propsFile = args[0];
		Properties config = raftServer.loadConfig(propsFile);
		raftServer.state = new State(config);

		//buildChannel and Stubs
		raftServer.getConnectionInformation(config);
	}

	public void initServer(String propsFile) throws IOException {
		Properties config = loadConfig(propsFile);
		setConfig(config);

		//1. init state
		setState(new State(config));
		//2.Build Channel and stubs.
		Map<Integer,String[]> connectionInfo = getConnectionInformation(config);
		buildChannelsAndStubs(connectionInfo, getState());
		//3.Make the Make the instance a follower.
		state.setMode(State.FOLLOWER);
		//4. Create objects of Follower, Candidate and Leader - All these will be threads most probably..and they will be started only when server is
		// in that respective mode.
		//5. Start GRPC server
		//5. Start the main loop
	}

	/**
	 * This loop coordinates the transition of the server from one mode to another mode
	 * */
	private void mainLoop(){
		while (!state.isShutdown()) {
			switch (state.getMode()) {
				case State.FOLLOWER: //do stuff
					break;
				case State.CANDIDATE: //do stuff
					break;
				case State.LEADER: //do stuff
					break;
			}
		}
	}

	Properties loadConfig(String configFile) throws IOException {
		InputStream input = new FileInputStream(configFile);
		Properties prop = new Properties();
		prop.load(input);
		return prop;
	}
	
	private void buildChannelsAndStubs(Map<Integer, String[]> connectionInfo, State state)
	{
		//ManagedChannelBuilder.forAddress("name", 4050).build();
		//1. build channel for each candidate and add it to the connection map
		//2. From the channel build non - blocking stubs and add it to the candidate stub map.
		state.nodeChannelMap = new HashMap<>(connectionInfo.size());
		state.nodeStubMap = new HashMap<>(connectionInfo.size());
		
		for(Map.Entry<Integer, String[]> candidate : connectionInfo.entrySet())
		{
			String[] connParams = candidate.getValue();
			Channel channel = ManagedChannelBuilder.forAddress(connParams[0], Integer.parseInt(connParams[1])).build();
			RaftServerFutureStub fstub = RaftServerGrpc.newFutureStub(channel);
			state.nodeChannelMap.put(candidate.getKey(), channel);
			state.nodeStubMap.put(candidate.getKey(), fstub);
		}
	
	}
	
	private Map<Integer,String[]> getConnectionInformation(Properties config)
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Properties getConfig() {
		return config;
	}

	public void setConfig(Properties config) {
		this.config = config;
	}
}
