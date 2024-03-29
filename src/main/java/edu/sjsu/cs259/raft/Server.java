package edu.sjsu.cs259.raft;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.sjsu.cs259.raft.service.gen.RaftServerGrpc;
import edu.sjsu.cs259.raft.util.RandomTimeoutGenerator;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ServerBuilder;

public class Server {
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
	private AppendEntriesProcessor appendEntriesProcessor;
	EventWaitThread eventWaitThread;
	private Candidate candidate;
	private Leader leader;
	String[] myConnInfo;
	//private AtomicBoolean heartBeatReceived = new AtomicBoolean(False);

	public static void main(String[] args) throws Exception {
		//You start here
		//1. Load all your persistent data
		//2. Instantiate the state object
		//3.
		Server raftServer = new Server();
		String propsFile = args[0];

		raftServer.initServer(propsFile);

	}

	public void initServer(String propsFile) throws Exception {
		Properties config = loadConfig(propsFile);
		setConfig(config);

		//1. init state
		state = new State(config);
		//2.Build Channel and stubs.
		Map<Integer,String[]> connectionInfo = getConnectionInformation(config);
		buildChannelsAndStubs(connectionInfo, getState());

		//Create objects of Follower, Candidate and Leader - All these will be threads most probably..and they will be started only when server
		int majority = (connectionInfo.size() + 1) / 2;
		this.leader = new Leader(state, majority);
		this.candidate = new Candidate(state, new RandomTimeoutGenerator(state.getUpperBound(), state.getLowerBound()));
		//3.Make the Make the instance a follower.
		appendEntriesProcessor = new AppendEntriesProcessor(state);
		eventWaitThread = new EventWaitThread(state);
		state.setMode(State.FOLLOWER);

		//5. Start GRPC server
		RaftService raftService = new RaftService(appendEntriesProcessor,state, eventWaitThread,this, leader);
		io.grpc.Server server = ServerBuilder.forPort(Integer.parseInt(myConnInfo[1])).
				addService(raftService)
				.build();

		//5. Start the main loop
		mainLoop();
		server.start();
		
	}

	/**
	 * This loop coordinates the transition of the server from one mode to another mode
	 * */
	private void mainLoop() throws InterruptedException, IOException {
		while (!state.isShutdown()) {
			switch (state.getMode()) {
				case State.FOLLOWER: //do stuff
					//These threads exit when the server is no longer a follower.
					//TODO: If required add an atomic counter to determine if the threads have been inititalized.
					Thread aepThread = new Thread(appendEntriesProcessor);
					//eventWaitThread.heartBeatRecieved.set(false);
					Thread ewt = new Thread(eventWaitThread);
					aepThread.start();
					ewt.start();
					ewt.join();
					aepThread.join();
					break;
				case State.CANDIDATE:
					candidate.conductElection();
					break;
				case State.LEADER:
					leader.lead();
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
			RaftServerGrpc.RaftServerStub stub = RaftServerGrpc.newStub(channel);
			RaftServerGrpc.RaftServerBlockingStub blockingStub = RaftServerGrpc.newBlockingStub(channel);
			state.nodeChannelMap.put(candidate.getKey(), channel);
			state.nodeStubMap.put(candidate.getKey(), stub);
			state.nodeBlockingStubMap.put(candidate.getKey(), blockingStub);
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
			String[] connInfo = config.getProperty(i+"").split(":");
			if(i == cadidateID)
				myConnInfo = connInfo;
			else
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

	public AppendEntriesProcessor getAppendEntriesProcessor() {
		return appendEntriesProcessor;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
}
