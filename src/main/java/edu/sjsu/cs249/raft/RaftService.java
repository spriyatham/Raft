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
		// TODO Auto-generated method stub
		super.requestVote(request, responseObserver);
	}

	@Override
	public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver){
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
