package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.util.RandomTimeoutGenerator;

public class LeaderHeartBeatSender implements Runnable{
    State state;
    RandomTimeoutGenerator timeoutGenerator;
    @Override
    public void run() {

        while (!state.isShutdown() && state.isLeader()) {
            long heartBeatTimeout = timeoutGenerator.generateRandomTimeOut();

        }
    }
}
