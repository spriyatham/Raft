package edu.sjsu.cs249.raft;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerFutureStub;
import io.grpc.Channel;

public class State {
	
	//Persistent Storage.
	int currentTerm;
	int candidateID; // a number to Identify the current server
	//holds candidateID of the server that received vote in this term.
	//should be initialized to -1.
	int votedFor; 	
	List<LogEntry> log;
	
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
	Map<Integer, Channel> candidateChannelMap;
	Map<Integer, RaftServerFutureStub> candidateStubMap;
	
	//TimeInfo
	long upperBound;
	long lowerBound;
	
	
	
	void initCommon()
	{
		mode = new AtomicInteger(-1); // initial value..belongs to none of the modes
		modeLock = new ReentrantLock();
		followerCondition = modeLock.newCondition();
		candidateCondition = modeLock.newCondition();
		leaderCondition = modeLock.newCondition();
		shutdown = new AtomicBoolean(false);
	}
	
	
	
	public long getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(long upperBound) {
		this.upperBound = upperBound;
	}
	public long getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(long lowerBound) {
		this.lowerBound = lowerBound;
	}
	
}
