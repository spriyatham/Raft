package edu.sjsu.cs249.raft;

import edu.sjsu.cs249.raft.util.RandomTimeoutGenerator;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This thread do a timed wait for recieving a message.
 * if a message is received within the timeout period, it should reset the timeout period and restart the wait.
 * time out should be choosen randomly.
 * If timeout expires, the mode should be changed to candidate. and executed..
 * 
 * This thread should be starte when the servers begins to be a follower.
 * 
 */
public class EventWaitThread implements Runnable {
	AtomicBoolean heartBeatRecieved = null; //this makes more sense..
	State state;
	RandomTimeoutGenerator timeoutGenerator;

	public EventWaitThread(State state) {
		this.heartBeatRecieved = new AtomicBoolean(false);
		this.state = state;
		timeoutGenerator = new RandomTimeoutGenerator(state.getUpperBound(), state.getLowerBound());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean transitionToCandidate = false;
		
		//here basically wait(timeout period of time on the heartbeat object..)..should come out of wait..either when heartbeat is reacieved or
		//time out expire
		while (!state.isShutdown() && state.isFollower()) {
			long heartBeatTimeout = timeoutGenerator.generateRandomTimeOut();
			synchronized (heartBeatRecieved) {
				try {
					heartBeatRecieved.wait(heartBeatTimeout);
					if(!heartBeatRecieved.get()) {
						//did not recieve heartbeat(AppendEntriesRPC) from the leader within the timeout.
						transitionToCandidate = true;
					}
					//reset heartbeatRecieve to false.
					heartBeatRecieved.set(false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(transitionToCandidate) {
				try {
					state.incrementCurrentTerm();
				} catch (IOException e) {
					System.out.println("Some exception while writing term to file..shutting downn...");
					state.shutdown();
					e.printStackTrace();
				}
				state.setMode(State.CANDIDATE);
			}
		}
		
	}
}
