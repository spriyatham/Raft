package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.RaftServerGrpc;
import edu.sjsu.cs259.raft.service.gen.RequestVoteRequest;
import edu.sjsu.cs259.raft.service.gen.RequestVoteResponse;
import edu.sjsu.cs259.raft.util.RandomTimeoutGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * When the candidate flag is set to true(a timeout elapsed without receiving appendEntries from the leader.. so became a candidate)
 * Will conduct an election to become the leader..and change the mode of the server accordingly.
 * */
public class Candidate{
	State state;
	AtomicBoolean abandonElection = new AtomicBoolean(false);
	AtomicBoolean electionCompleted = new AtomicBoolean(false);
	RandomTimeoutGenerator timeoutGenerator;
	/**
	 * Note: When you are in the middle of election/ waiting for a majority, you could receive an append entries
	 * RPC from another peer whose term is either
	 *   > your current term -
	 *   		abandon the election - i.e notify this code that election is not longer required
	 *   		set your CurrentTerm to the new Term. and update your votedFor to that term..and that node
	 *   	    When this happens to a node in follower mode....just update the term and votedFor
	 *   = your current term
	 *   		This means that another..node has become the leader for this term, you need not update your vote..
	 *   		but, just notify the election code that is electionCompleted..
	 * **/
	public void conductElection() throws IOException, InterruptedException {
		abandonElection = new AtomicBoolean(false);
		electionCompleted = new AtomicBoolean(false);

		ConcurrentMap<Integer, RequestVoteResponse> voteMap = new ConcurrentHashMap<>();
		Map<Integer, RaftServerGrpc.RaftServerStub> serverStubMap =  state.getNodeStubMap();

		//I am contesting to become a leader in this term..
		long currentTerm = state.getCurrentTerm();

		//If we got an RPC with a new Term, then abandon this election, because, you will transition to a follower.
		if(abandonElection.get()) return;

		//Your currentTerm has already been incremented by the follower, just before transitioning to CANDIDATE>
		selfVote(currentTerm, voteMap);

		Thread[] requestVoteThreads = new Thread[serverStubMap.size()];
		RequestVoteRequest voteRequest = buildVoteRequest(currentTerm);
		if(abandonElection.get()) return;
		long electionTimeout = timeoutGenerator.generateRandomTimeOut();
		CountDownLatch requestsSent = new CountDownLatch(serverStubMap.size());

		//Send vote requests to all the servers..
		int i=0;
		for(Map.Entry<Integer, RaftServerGrpc.RaftServerStub> entry : serverStubMap.entrySet()) {
			RequestVoteThread rvt = new RequestVoteThread(requestsSent, voteMap, voteRequest,  electionTimeout, entry.getValue(), entry.getKey());
			requestVoteThreads[i] = new Thread(rvt);
			requestVoteThreads[i++].start();
		}
		//wait until all the requests are sent and start the election time out.
		requestsSent.await();
		Thread.sleep(electionTimeout);

		//Election timeout completed.
		/**
		 * Three possibilities
		 * 1. You won the election - check the for qourum based on voteMap
		 * 2. Someone else won the election - recieved an AppendEntries RPC for this Term from a differnt server
		 * 			electionFinished == true.
		 * 3. Split Vote - electionFished == false and gotQuorum == false; Restart the election by increasing term.
		 * */
		if(abandonElection.get() || state.getCurrentTerm() > currentTerm || electionCompleted.get()) {
			state.setMode(State.FOLLOWER);
			return;
		}
		boolean gotQuorum = false;
		int majority = (serverStubMap.size() +1) / 2 ;
		int voteCount = 0;
		long newHigherTerm = currentTerm;
		for(Map.Entry<Integer, RequestVoteResponse> vote: voteMap.entrySet()) {
			RequestVoteResponse response = vote.getValue();
			if(vote.getValue().getVoteGranted()) {
				voteCount++;
			}
			else if(response.getTerm() > newHigherTerm){
				newHigherTerm = response.getTerm();
			}
		}
		if(voteCount >= majority) {
			state.setMode(State.LEADER);
			//The first step in the leader process is to send append entries RPC.
			return;
		}
		if(newHigherTerm > state.getCurrentTerm()) {
			//Looks like ther is a new Term Adopt that term and transition to the follower mode..
			state.setCurrentTerm(newHigherTerm);
			state.setMode(State.FOLLOWER);
			return;
		}
		if(!electionCompleted.get() && !gotQuorum) {
			//split vote restart the election.
			state.incrementCurrentTerm();
			conductElection();
		}
	}


	private void selfVote(long currentTerm, ConcurrentMap<Integer, RequestVoteResponse> voteMap) throws IOException {
		RequestVoteResponse vote;
		synchronized (state.votedFor) {
			state.setVotedFor(currentTerm, state.getNodeID());
			vote = RequestVoteResponse.newBuilder()
					.setTerm(currentTerm)
					.setVoteGranted(true)
					.build();
		}
		voteMap.put(state.getNodeID(), vote);
	}

	private RequestVoteRequest buildVoteRequest(long currentTerm) {
		List<LogEntry> log = state.getLog();
		int lastLogIndex = -1;
		long lastLogTerm = -1;
		synchronized (log) {
			LogEntry lastLogEntry = log.get(log.size() -1);
			lastLogTerm = lastLogEntry.getTerm();
			lastLogIndex = lastLogEntry.getIndex();
		}

		RequestVoteRequest request = RequestVoteRequest.newBuilder()
				.setTerm(currentTerm)
				.setCadidateId(state.getNodeID())
				.setLastLogIndex(lastLogIndex)
				.setLastLogTerm(lastLogTerm).build();
		return request;
	}

	public AtomicBoolean getAbandonElection() {
		return abandonElection;
	}

	public void setAbandonElection(boolean abandonElection) {
		this.abandonElection.set(abandonElection);
	}

	public AtomicBoolean getElectionCompleted() {
		return electionCompleted;
	}

	public void setElectionCompleted(boolean electionCompleted) {
		this.electionCompleted.set(electionCompleted);
	}
}
