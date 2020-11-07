package edu.sjsu.cs249.raft;

import edu.sjsu.cs249.raft.util.RandomTimeoutGenerator;

import java.io.IOException;

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
