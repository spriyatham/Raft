package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.RaftServerGrpc;
import edu.sjsu.cs259.raft.service.gen.RequestVoteRequest;
import edu.sjsu.cs259.raft.service.gen.RequestVoteResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RequestVoteThread implements Runnable {
    CountDownLatch requestsSent;
    ConcurrentMap<Integer, RequestVoteResponse> voteMap;
    RequestVoteRequest voteRequest;
    State state;
    long timeOut;
    RaftServerGrpc.RaftServerStub stub;
    int destinationNodeId;

    public RequestVoteThread(CountDownLatch requestsSent, ConcurrentMap<Integer, RequestVoteResponse> voteMap, RequestVoteRequest voteRequest, long timeOut, RaftServerGrpc.RaftServerStub stub, int destinationNodeId) {
        this.requestsSent = requestsSent;
        this.voteMap = voteMap;
        this.voteRequest = voteRequest;
        this.timeOut = timeOut;
        this.stub = stub;
        this.destinationNodeId = destinationNodeId;
    }

    @Override
    public void run() {
        final RequestVoteResponse[] requestVoteResponse = {null};
        final CountDownLatch responseRecieved = new CountDownLatch(1);
        StreamObserver<RequestVoteResponse> responseObserver = new StreamObserver<RequestVoteResponse>() {
            @Override
            public void onNext(RequestVoteResponse value) {
                requestVoteResponse[0] = value;
                responseRecieved.countDown();
            }

            @Override
            public void onError(Throwable t) {
                //ignore for now..
            }

            @Override
            public void onCompleted() {
                //ignore
            }
        };


        stub.requestVote(voteRequest, responseObserver);
        requestsSent.countDown();
        try {
            responseRecieved.await(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //the wait completed.
        if(requestVoteResponse[0] != null) {
            //Two things can happen
            // 1. voteGranted==True/
            // 2. voteGranted == False and term in Response > currentTerm..//Change to follower..
            //Right now I will just add to the concurrentHashSet.
            voteMap.put(destinationNodeId, requestVoteResponse[0]);
        }

    }
}
