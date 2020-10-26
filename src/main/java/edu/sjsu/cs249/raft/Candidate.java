package edu.sjsu.cs249.raft;

/**
 * When the candidate flag is set to true(a timeout elapsed without receiving appendEntries from the leader.. so became a candidate)
 * Will conduct an election to become the leader..and change the mode of the server accordingly.
 * */
public class Candidate implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
