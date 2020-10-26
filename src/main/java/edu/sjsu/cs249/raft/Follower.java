package edu.sjsu.cs249.raft;

/**
 * This thread will be started when the server boots up..
 * An waits until the follower mode is turned on..
 * It will be notified once sever changes its mode to follower mode
 * 
 * */
public class Follower implements Runnable {

	State state;
	Server server;
	public void Follower(State state, Server server) {
		this.state = state;
		this.server = server;
	}
	@Override
	public void run() {
		/**
		 * Invoke 2 threads.
		 * 1. Event wait thread - that does a time wait to receive an AppendEntries call..
		 * 2. ExecuteCommitted Commands - This thread executes the committed entries dumbly, as long as the server 
		 * stays a follower.
		 * */
		while(!state.shutdown.get())
		{
			//while(state.mode.get() != State.FOLLOWER)
				
		}
		
	}

}
