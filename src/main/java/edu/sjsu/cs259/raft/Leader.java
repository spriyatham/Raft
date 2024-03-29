package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.RaftServerGrpc;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class Leader{
/**
 * Initially wait to become the Leader, then send heart beats..until you are no longer the leader.
 * */
	//outStanding appends map
    ConcurrentMap<Integer, CountDownLatch> outStandingAppends;
    //TODO: Call backs have to be cleaned up; i.e waiting threads must be notified...so that the threads can exit..
    ConcurrentMap<Integer, Object> nextIndexCallBackRegister;
    State state;
    int majority;

    public Leader(State state, int majority) {
        this.state = state;
        this.majority = majority;
    }

    public void lead() throws InterruptedException {
        //0. Create all the follower Stubs.- No need to create new set of stubs, stubs are thread safe
        //1. init() heartBeat sender thread - should run only as long as you are the leader.
        //2. Create follower liason threads objects.
        Map<Integer, RaftServerGrpc.RaftServerBlockingStub> blockingStubMap = state.getNodeBlockingStubMap();
        Thread[] followerLiasons = new Thread[blockingStubMap.size()];
        int nextIndex = state.getLastLogEntry().getIndex() + 1;
        int i= 0;
        for(Map.Entry<Integer, RaftServerGrpc.RaftServerBlockingStub> entry : blockingStubMap.entrySet()) {
            FollowerLiason fl = new FollowerLiason(state, this, entry.getValue(), nextIndex);
            followerLiasons[i] = new Thread(fl);
            followerLiasons[i++].start();
        }

        for(Thread t : followerLiasons) {
            t.join();
        }

    }

    void cleanUp() {
        if(!state.isLeader()) {
            for(Object object: nextIndexCallBackRegister.values()) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
            //allow the client append calls to return..
            for(CountDownLatch latch: outStandingAppends.values()) {
                for(int i = 0; i< majority; i++) {
                    latch.countDown();
                }
            }
        }
    }
}
