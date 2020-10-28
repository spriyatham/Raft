package edu.sjsu.cs249.raft;

import edu.sjsu.cs249.raft.service.gen.AppendEntriesRequest;
import edu.sjsu.cs249.raft.service.gen.AppendEntriesResponse;
import edu.sjsu.cs249.raft.service.gen.ClientAppendRequest;
import edu.sjsu.cs249.raft.service.gen.ClientAppendResponse;
import edu.sjsu.cs249.raft.service.gen.ClientRequestIndexRequest;
import edu.sjsu.cs249.raft.service.gen.ClientRequestIndexResponse;
import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc.RaftServerImplBase;
import edu.sjsu.cs249.raft.service.gen.RequestVoteRequest;
import edu.sjsu.cs249.raft.service.gen.RequestVoteResponse;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;

/**
 * RaftService will work along with the <b>Server</b> to service client requests and requests 
 * from the leader.
 * The @Server and @RaftService will communicate through @State
 * */
public class RaftService extends RaftServerImplBase {

	AppendEntriesProcessor aep;
	State state;

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
				}
				state.setVotedFor(candidateTerm, candidateId);
				responseObserver.onNext(responseBuilder.setTerm(candidateTerm).setVoteGranted(true).build());
				responseObserver.onCompleted();
				//TODO: restart the election time out ...as you have just granted an vote.
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

		//if not Entry is null return success...basically ignore.
		if(request.getEntry() == null) {
			responseObserver.onNext(AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(true).build());
			responseObserver.onCompleted();
			return;
		}

		/**
		 * TODO: Add checks to
		 * 1. Service this request only if you are a follower.
		 * 2. If you are a candidate..and got an RPC from the leader..you have to do the transition to follower..add that logic here..
		 * */

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
		// TODO Auto-generated method stub
		super.clientAppend(request, responseObserver);
	}

	@Override
	public void clientRequestIndex(ClientRequestIndexRequest request,
			StreamObserver<ClientRequestIndexResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.clientRequestIndex(request, responseObserver);
	}
	
}
