package edu.sjsu.cs249.raft;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerFutureStub;
import io.grpc.Channel;

public class State {
	/**
	 * Intializing state invovles.
	 * 1. Loading all persistent data
	 * 2. Intiatlizing the volatile state variable to their respective init values.
	 * 		-> Common state
	 * 	 	-> LeaderState - has to be initialized only when the the server becomes a leader.
	 *
	 * TODO: Seperate control variables and logic..like shutdown etc ..to a sepearte controller class.
	 * **/
	//Persistent Storage.
	AtomicLong currentTerm = new AtomicLong(-1);
	Integer nodeID = -1; // a number to Identify the current server
	//holds candidateID of the server that received vote in this term.
	//should be initialized to -1.
	VotedFor votedFor = null;

	//FUTURE: See if any blocking collection implementation can be used here.
	List<LogEntry> log;

	//paths to files where this info will be stored.
	String CURRENT_TERM_FILE;
	String VOTED_FOR_FILE;
	String LOG_FILE;
	//Volatile State on all Servers
	
	int commitIndex;
	int lastApplied;
	
	//volatile State on Leaders
	
	int nextIndex[]; //number of servers..
	int matchIndex[];// number of servers..
	
	//TODO: Variables Required..
	/**
	 * mode values - 1, 2, 3
	 *  1 - follower
	 *  2 - candidate
	 *  3 - Leader
	 * */
	AtomicInteger mode;
	public final static int FOLLOWER = 1;
	public final static int CANDIDATE = 2;
	public final static int LEADER = 3;
	
	Lock modeLock;
	Condition followerCondition;
	Condition candidateCondition;
	Condition leaderCondition;
	
	AtomicBoolean shutdown;
	Map<Integer, String> connectionInfo;
	Map<Integer, Channel> nodeChannelMap;
	Map<Integer, RaftServerFutureStub> nodeStubMap;
	
	//TimeOutInfo in milli seconds.
	int upperBound;
	int lowerBound;

	public State(Properties config) throws Exception {
		/**
		 * lowerBound=150
		 * upperBound=300
		 * currentTermFile=currentTermFile
		 * votedForFile=votedForFile
		 * logFile=logFile
		 * */
		this.CURRENT_TERM_FILE = config.getProperty("currentTermFile");
		this.VOTED_FOR_FILE = config.getProperty("votedForFile");
		this.LOG_FILE = config.getProperty("logFile");
		this.upperBound = Integer.parseInt(config.getProperty("lowerBound"));
		this.lowerBound = Integer.parseInt(config.getProperty("upperBound"));
		initCommon();
		initPersistentState();
		initVolatileState();
	}

	void initCommon()
	{
		mode = new AtomicInteger(-1); // initial value..belongs to none of the modes
		modeLock = new ReentrantLock();
		followerCondition = modeLock.newCondition();
		candidateCondition = modeLock.newCondition();
		leaderCondition = modeLock.newCondition();
		shutdown = new AtomicBoolean(false);
	}
	
	
	
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * TODO: Complete this when you are writing leader code..
	 * */
	public void initLeaderState() {

	}

	public void initVolatileState() {
		this.commitIndex = 0;
		this.lastApplied = 0;
	}

	public void initPersistentState() throws Exception {
		loadCurrentTerm();
		loadVotedFor();
		loadLog();
	}

	public int getCommitIndex() {
		return commitIndex;
	}

	public void setCommitIndex(int commitIndex) {
		this.commitIndex = commitIndex;
	}

	public int getLastApplied() {
		return lastApplied;
	}

	public void setLastApplied(int lastApplied) {
		this.lastApplied = lastApplied;
	}

	public List<LogEntry> getLog(){
		return log;
	}

	/**
	 * FUTURE: All the set operations which invovle writing the value to a file,
	 * should do it more robustly. The current file should be copied first, then write should be performed,
	 * and then delete the copy once the write is successfull. This will prevent data corrruption.
	 * Right now, I am simply writing to the file because, my focus is on implementing RAFT.
	 * Refer my implementation of ABD to check how you safely persist..a value.
	 * */

	public void appendLogEntry(LogEntry logEntry) throws Exception {
		synchronized (log){
			log.add(logEntry);
			persistObject(LOG_FILE, log);
		}
	}

	public long getCurrentTerm(){
		return currentTerm.get();
	}

	public void incrementCurrentTerm() throws IOException {
		synchronized (currentTerm) {
			try {
				writeIntToFile(CURRENT_TERM_FILE, currentTerm.incrementAndGet());
			} catch (IOException e) {
				System.out.println("Exception occurred while writing current term to file");
				e.printStackTrace();
				//TODO: Handle this if required..
				//Throwing from synchronized block will not have any side effect, lock will be released
				throw e;
			}
		}
	}

	public void setCurrentTerm(long term) throws IOException {
		synchronized (currentTerm) {
			try {
				writeIntToFile(CURRENT_TERM_FILE, term);
				currentTerm.set(term);
			} catch (IOException e) {
				System.out.println("Exception occurred while writing current term to file");
				e.printStackTrace();
				//TODO: Handle this if required..
				//Throwing from synchronized block will not have any side effect, lock will be released
				throw e;
			}
		}
	}

	public VotedFor getVotedFor() {
		return votedFor;
	}

	public void setVotedFor(long term, int nodeID) throws IOException {
		if(votedFor == null) {
			votedFor = new VotedFor();
		}
		synchronized (votedFor) {
			try {
				votedFor.setTerm(term);
				votedFor.setCandidateID(nodeID);
				persistObject(VOTED_FOR_FILE, votedFor);
			} catch (IOException e) {
				System.out.println("Exception occurred while writing the current vote to file");
				e.printStackTrace();
				//TODO: Handle this if required..
				//Throwing from synchronized block will not have any side effect, lock will be released
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * During the first boot, the file will not exist..and we'll leave it like that,
	 * dont create new file, it will anyways be set when a vote is being casted..
	 * */
	private void loadVotedFor() throws Exception {
		ObjectInputStream in = null;
		File votedForFile = new File(VOTED_FOR_FILE);
		try {
			if (!votedForFile.exists()) {
				votedFor = null;
				return;
			}
			in = new ObjectInputStream(new FileInputStream(votedForFile));
			votedFor = (VotedFor) in.readObject();
			if(votedFor.getTerm() != currentTerm.get()) {
				//The current term of the node and the votedfor does not match..it means that you have not voted for...any candidate in this term.
				//This could happen because, you have changed your current term, but failed before updating your vote.
				votedFor = null;
				votedForFile.delete();
			}
			System.out.println(votedFor.toString());
		}
		catch(Exception e)
		{
			System.out.println("loadVotedFor: some exception, re-throwing error");
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * This method loads the log from a file.
	 * */
	public void loadLog() throws Exception {
		ObjectInputStream in = null;
		File logFile = new File(LOG_FILE);
		try {
			if (!logFile.exists()) {
				//create a file an return
				logFile.createNewFile();
				log = new ArrayList<>();
				return;
			}
			in = new ObjectInputStream(new FileInputStream(logFile));
			log = (List<LogEntry>) in.readObject();
			System.out.println("loadLog: logSize = " + log.size());
		}
		catch(Exception e)
		{
			System.out.println("loadLog: some exception, re-throwing error");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Loads the currentTerm  from file.
	 * During first boot, the file will not be present, so it will create the file and
	 * initializes the currentTerm to 0 as specified in the protocol.
	 * */
	private void loadCurrentTerm() throws IOException {
		int currentTermInFile = readIntFromFile(CURRENT_TERM_FILE);
		if(currentTermInFile == -1) {
			//THis is the first boot, initialize the term to 0 and write to the file.
			this.currentTerm.set(0);
			writeIntToFile(CURRENT_TERM_FILE, this.currentTerm.get());
		}
		else {
			this.currentTerm.set(currentTermInFile);
		}
	}

	private int readIntFromFile(String name) throws IOException {
		File file = new File(name);
		if(!file.exists()) {
			return -1;
		}
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		try {
			line = br.readLine();
			return Integer.parseInt(line);
		} catch (NumberFormatException e) {
			throw new IOException("Found " + line + " in " + file);
		} finally {
			br.close();
		}
	}

	private void writeIntToFile(String name, final long value) throws IOException {
		File file = new File(name);
		if(!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(name);
		fw.write(value+"");
	}

	/**
	 * Currently I just serilaize the List<LongEntry> and wrtie it to a file. This is very brute.
	 * FUTURE: Use a more effecient way to incrementally persist logEntires. Look at what mapDB does.
	 * */
	private void persistObject(String fileName, Object obj) throws Exception{
		ObjectOutputStream out = null;
		try {
			File file = new File(fileName); //this file always exists at this place because we create it in loadLog() if it is absent
			if(!file.exists()) file.createNewFile();
			out = new ObjectOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println("persistObject: "+ fileName+ " absent, re-throwing error.");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			System.out.println("persistObject: some IO exception, re-throwing error");
			e.printStackTrace();
			throw e;
		}
		out.writeObject(obj);
		out.close();
		System.out.println("persistObject:  saved successfully.");
	}

	public int getMode() {
		return mode.get();
	}

	public void setMode(int mode) {
		this.mode.set(mode);
	}

	public boolean isFollower() {
		return mode.get() == FOLLOWER;
	}

	public boolean isCandidate() {
		return mode.get() == CANDIDATE;
	}

	public boolean isLeader() {
		return mode.get() == LEADER;
	}

	public boolean isShutdown() {
		return shutdown.get();
	}

	public void shutdown(){
		shutdown.set(true);
	}
}
