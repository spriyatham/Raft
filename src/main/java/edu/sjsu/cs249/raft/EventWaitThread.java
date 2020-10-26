package edu.sjsu.cs249.raft;

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
	Boolean heartBeatObj = null; //this makes more sense..
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean isFollower = true; //this thread should run as long as the server is the follower. This should Ideally be a global mode variale.
		
		//here basically wait(timeout period of time on the heartbeat object..)..should come out of wait..either when heartbeat is reacieved or
		//time out expire
		
	}
}
