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
 * The @Server an @RaftService will communicate through @State
 * */
public class RaftService extends RaftServerImplBase {

	@Override
	public void requestVote(RequestVoteRequest request, StreamObserver<RequestVoteResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.requestVote(request, responseObserver);
	}

	@Override
	public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.appendEntries(request, responseObserver);
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
