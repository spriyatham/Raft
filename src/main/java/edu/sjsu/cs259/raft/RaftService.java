package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.AppendEntriesRequest;
import edu.sjsu.cs259.raft.service.gen.AppendEntriesResponse;
import edu.sjsu.cs259.raft.service.gen.ClientAppendRequest;
import edu.sjsu.cs259.raft.service.gen.ClientAppendResponse;
import edu.sjsu.cs259.raft.service.gen.ClientRequestIndexRequest;
import edu.sjsu.cs259.raft.service.gen.ClientRequestIndexResponse;
import edu.sjsu.cs259.raft.service.gen.RaftServerGrpc.RaftServerImplBase;
import edu.sjsu.cs259.raft.service.gen.RequestVoteRequest;
import edu.sjsu.cs259.raft.service.gen.RequestVoteResponse;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * RaftService will work along with the <b>Server</b> to service client requests and requests 
 * from the leader.
 * The @Server and @RaftService will communicate through @State
 * */
public class RaftService extends RaftServerImplBase {

	AppendEntriesProcessor aep;
	State state;
	EventWaitThread eventWaitThread;
	Server server;
	Leader leader;

	public RaftService(AppendEntriesProcessor aep, State state, EventWaitThread eventWaitThread, Server server, Leader leader) {
		this.aep = aep;
		this.state = state;
		this.eventWaitThread = eventWaitThread;
		this.server = server;
		this.leader = leader;
	}

	@Override
	public void requestVote(RequestVoteRequest request, StreamObserver<RequestVoteResponse> responseObserver) {
		RequestVoteResponse.Builder responseBuilder = RequestVoteResponse.newBuilder();
		long candidateTerm = request.getTerm();
		int candidateId = request.getCadidateId();
		long candLastLogIndex = request.getLastLogIndex();
		long candLastLogTerm = request.getLastLogTerm();

		long myCurrentTerm = state.getCurrentTerm();
		if(candidateTerm < myCurrentTerm) {
			responseObserver.onNext(responseBuilder.setTerm(myCurrentTerm).setVoteGranted(false).build());
			responseObserver.onCompleted();
		}
		VotedFor votedFor = state.getVotedFor();
		/**
		 * 1. I could be in one term, but my vote would have been to an different term; in that case considere the vote to be null
		 * 2. Candidate term could be ahead of my term, so i will obviously consider as not voted for that term..
		 * */
		if( (votedFor!= null) && ((votedFor.getTerm() != myCurrentTerm) || (candidateTerm > myCurrentTerm)))
			votedFor = null;
		//Here if votedFor is not null, then it means that you and candidate are in the same term.
		if((votedFor == null || votedFor.getCandidateID() == candidateId) && isCandidateLogUptoDate(candLastLogIndex, candLastLogTerm) ) {
			try {
				if(candidateTerm > myCurrentTerm) {
					state.setCurrentTerm(candidateTerm);
					state.setVotedFor(candidateTerm, candidateId);
					switch (state.getMode()) {
						case State.FOLLOWER: //You are already a follower..now you have to begin a new wait for receiving an AppendEntries RPC,
							notifyEventWaitThread();
							break;
						case State.CANDIDATE:
							//I am obviously contesting for a previous term...now that there is a new term..I will abandon election
							endElection();
							break;
						case State.LEADER:
							state.setMode(State.FOLLOWER);
							leader.cleanUp();
							break;
					}
				}
				responseObserver.onNext(responseBuilder.setTerm(candidateTerm).setVoteGranted(true).build());
				responseObserver.onCompleted();
				//TODO: restart the election time out ...as you have just granted an vote -- just notify the heartbeat object, incase it
				//TODO" is waiting..
				return;
			} catch (IOException e) {
				responseObserver.onError(e);
				e.printStackTrace();
				return;
			}
		}
		//If we reached here it means we are not granting a vote to the candidate.
		responseObserver.onNext(responseBuilder.setTerm(myCurrentTerm).setVoteGranted(false).build());
		responseObserver.onCompleted();
	}

	private boolean isCandidateLogUptoDate(long candLastLogIndex, long candLastLogTerm) {
		List<LogEntry> log = state.getLog();
		//I am synchronizing on the log while performing this check because I want the latest info..
		synchronized (log) {
			LogEntry last = log.get(log.size() - 1);
			long myLastLogTerm = last.getTerm();
			long myLastLogIndex = last.getIndex();
			if(candLastLogTerm == myLastLogTerm) {
				return candLastLogIndex >= myLastLogIndex;
			}
			else if(candLastLogTerm > myLastLogTerm) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver){
		//TODO: check if we got response from the leader to whom we voted for. Notify and restart the election time out thread..
		long currentTerm = state.getCurrentTerm();
		if(request.getTerm() < currentTerm) {
			System.out.println("appendEntries: term in request : " + request.getTerm() + " < currentTerm :" + currentTerm);
			responseObserver.onNext(AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(false).build());
			responseObserver.onCompleted();
			return;
		}

		if(state.isFollower()) {
			//notify the event wait thread that we recieve a message from the leader.
			notifyEventWaitThread();
		}
		try {
			if(request.getTerm() > currentTerm) {
				//adopt the term.
				state.setCurrentTerm(request.getTerm());
				state.setLeaderId(request.getLeaderId());

				switch (state.getMode()) {
					case State.CANDIDATE:
						//The election might be in progresss..so you inform the candidate that election is completed/abandoned because
						// you recieved an AppendEntry request from a node with higher Term.
						state.setMode(State.FOLLOWER);
						Candidate candidate = server.getCandidate();
						candidate.setAbandonElection(true);
						candidate.setElectionCompleted(true);
						break;
					case State.LEADER:
						state.setMode(State.FOLLOWER);
						leader.cleanUp();
						break;
				}
				//you can set heartbeat recieved to true..because, infact you
				//have recieved an AE rpc from the leader.
				notifyEventWaitThread();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// you are a candidate and you received append entry from another node for the same term.
		if(state.isCandidate() && request.getTerm() == state.getCurrentTerm()) {
			//Transition to follower mode and end the election
			state.setMode(State.FOLLOWER);
			endElection();
			state.setLeaderId(request.getLeaderId());
		}

		//if not Entry is null return success..It is just a heartbeat message...basically ignore.
		if(request.getEntry() == null) {
			responseObserver.onNext(AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(true).build());
			responseObserver.onCompleted();
			return;
		}

		AppendEntriesWrapper aew = new AppendEntriesWrapper(request);
		aep.queueRequest(aew);
		synchronized (aew) {
			try {
				aew.wait();
			} catch (InterruptedException e) {
				//Handle it elegantly if required.
				e.printStackTrace();
			}
		}
		if(aew.getSomeException() != null) {
			responseObserver.onError(aew.getSomeException());
		} else {
			responseObserver.onNext(aew.getResponse());
		}
		responseObserver.onCompleted();
	}

	@Override
	public void clientAppend(ClientAppendRequest request, StreamObserver<ClientAppendResponse> responseObserver) {
		if(!state.isLeader()) {
			ClientAppendResponse response = ClientAppendResponse.newBuilder().setLeader(state.getLeaderId()).setIndex(-1).setRc(1).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			return;
		}
		CountDownLatch majorityLatch = new CountDownLatch(leader.majority);
		int newIndex = -1;
		long term = state.getCurrentTerm();
		synchronized (state.log) {
			newIndex = state.log.size();
			String command = request.getDecree();
			LogEntry logEntry = new LogEntry(newIndex, term, command);
			state.log.add(logEntry);
			leader.outStandingAppends.put(newIndex, majorityLatch);
		}
		try {
			majorityLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int isSuccess = 0;
		if(!state.isLeader())
			isSuccess = 1;
		ClientAppendResponse response = ClientAppendResponse.newBuilder().setLeader(state.getLeaderId()).setIndex(newIndex).setRc(isSuccess).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void clientRequestIndex(ClientRequestIndexRequest request,
			StreamObserver<ClientRequestIndexResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.clientRequestIndex(request, responseObserver);
	}

	private void notifyEventWaitThread() {
		synchronized (eventWaitThread.heartBeatRecieved) {
			eventWaitThread.heartBeatRecieved.set(true);
			eventWaitThread.heartBeatRecieved.notify();
		}
	}

	private void endElection() {
		Candidate candidate = server.getCandidate();
		candidate.setAbandonElection(true);
		candidate.setElectionCompleted(true);
	}
	
}
