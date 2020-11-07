package edu.sjsu.cs249.raft;

import edu.sjsu.cs249.raft.service.gen.AppendEntriesRequest;
import edu.sjsu.cs249.raft.service.gen.AppendEntriesResponse;
import edu.sjsu.cs249.raft.service.gen.Entry;
import edu.sjsu.cs249.raft.service.gen.RaftServerGrpc;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class FollowerLiason implements Runnable{
    State state;
    Leader leader;
    RaftServerGrpc.RaftServerBlockingStub blockingStub;
    int destinationNodeId;
    int nextIndex;
    int matchIndex;

    public FollowerLiason(State state, Leader leader, RaftServerGrpc.RaftServerBlockingStub blockingStub, int nextIndex) {
        this.state = state;
        this.leader = leader;
        this.blockingStub = blockingStub;
        this.nextIndex = nextIndex;
        matchIndex = 0;
    }

    @Override
    public void run() {
        while (state.isLeader()) {
            LogEntry lastLoggedEntry = state.getLastLogEntry();

            if(lastLoggedEntry == null || (nextIndex > lastLoggedEntry.getIndex())) {
                //register for call back and continue.
                if(!leader.nextIndexCallBackRegister.containsKey(nextIndex)){
                    leader.nextIndexCallBackRegister.put(nextIndex, new Object());
                }
                Object waitObject = leader.nextIndexCallBackRegister.get(nextIndex);
                synchronized (waitObject) {
                    try {
                        //Wait until, a request is populated in that index or until you stop being a leader..
                        waitObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
            /**
             * message AppendEntriesRequest {
             *     uint64 term = 1;
             *     uint32 leaderId = 2;
             *     uint64 prevLogIndex = 3;
             *     uint64 prevLogTerm = 4;
             *     Entry entry = 5;
             *     uint64 leaderCommit = 6;
             * }
             * **/
            LogEntry nextEntry = state.getLogEntry(nextIndex);
            long prevLogIndex = -1;
            long prevLogTerm = -1;

            if(nextEntry.getIndex() != 0) {
                LogEntry prevLogEntry = state.getLogEntry(nextEntry.getIndex() -1);
                prevLogIndex = prevLogEntry.getIndex();
                prevLogTerm = prevLogEntry.getTerm();
            }

            AppendEntriesRequest request = AppendEntriesRequest.newBuilder().setTerm(state.getCurrentTerm())
                    .setLeaderId(state.getNodeID())
                    .setPrevLogIndex(prevLogIndex)
                    .setPrevLogTerm(prevLogTerm)
                    .setEntry(Entry.newBuilder().setIndex(nextEntry.getIndex()).setTerm(nextEntry.getTerm()).setDecree(nextEntry.getCommand()).build())
                    .setLeaderCommit(state.getCommitIndex())
                    .build();

            AppendEntriesResponse response = blockingStub.appendEntries(request);
            if(response.getSuccess()) {
                matchIndex = nextIndex;
                nextIndex++;
                CountDownLatch voteLatch = leader.outStandingAppends.getOrDefault(matchIndex, null);
                if(voteLatch != null) {
                    //voteLatch is initialized to the required simple majority(quorum), once a majority of servers respond with success, it means we have reached a quorum..
                    //For this log entry.
                    voteLatch.countDown();
                }
            }
            else {
                if(response.getTerm() > state.getCurrentTerm()) {
                    //There is some other node ahead of me. so I will go back to being a follower.
                    try {
                        state.setCurrentTerm(response.getTerm());
                        state.setMode(State.FOLLOWER);
                        //TODO: Trigger clean up thread..- Notify all the call backs..so that they'll free themselves up.
                        continue;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //now got to a previous index and try to send it..
                nextIndex --;
            }
        }
    }
}
