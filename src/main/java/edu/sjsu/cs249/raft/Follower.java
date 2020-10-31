package edu.sjsu.cs249.raft;

/**
 * This thread will be started when the server boots up..
 * An waits until the follower mode is turned on..
 * It will be notified once sever changes its mode to follower mode
 * 
 * */
public class Follower {

	State state;
	Server server;
	public void Follower(State state, Server server) {
		this.state = state;
		this.server = server;
	}

	public void follow(){

	}
}
