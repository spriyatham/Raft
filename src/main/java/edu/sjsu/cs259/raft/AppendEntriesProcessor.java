package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.AppendEntriesRequest;
import edu.sjsu.cs259.raft.service.gen.AppendEntriesResponse;
import edu.sjsu.cs259.raft.service.gen.Entry;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This thread processes the queued AppendEntryRPCs and creates AppendEntriesResponse
 * and notifies the waiting gRPC call handler..
 * I am processing all the append entires in the same thread to minimize the number of appendentry failures because
 * of logMismatch - eventhough logs are not actually mismatched.
 *
 * Empty(Heartbeat RPCs) and ones with term mismatch are filtered out in the RPC handler itself.
 *
 * */
public class AppendEntriesProcessor implements  Runnable{
    State state;
    //queuedRequests, should only be emptied once this node becomes a leader/candidate. A node can transtion from follower--> follower so this queue has to be
    //maintained..
    private LinkedBlockingQueue<AppendEntriesWrapper> queuedRequests = new LinkedBlockingQueue<>();

    public void queueRequest(AppendEntriesWrapper request){
        queuedRequests.add(request);
    }

    public AppendEntriesProcessor(State state) {
        this.state = state;
    }

    @Override
    public void run() {

        while(!state.isShutdown() && state.isFollower()) {
            try {
                AppendEntriesWrapper aew = queuedRequests.take();
                AppendEntriesRequest request = aew.getRequest();
                boolean responseStatus = false; //failure
                long prevLogIndex = request.getPrevLogIndex();
                long prevLogTerm = request.getPrevLogTerm();

                List<LogEntry> log = state.getLog();
                //Log will only be modified in this thread..
                synchronized (log) {
                    /**
                     * Return false when,
                     * 1. prev log entries mismatch
                     * 2. LogEntry in the currentIndex also mismatches
                     * */
                    int logSize = log.size();
                    int logLastIndex = logSize -1;

                    if((request.getEntry().getIndex() == 0) ||
                            ((prevLogIndex <= logLastIndex) && (log.get((int)prevLogIndex).getTerm() == prevLogTerm))) {
                        //purge everything else after this point in the log and append the new entry..
                        //1. purge everything from prevLogIndex +1 to logLastIndex
                        for(int i = logLastIndex; i > prevLogIndex ; i--) {
                            log.remove(i);
                        }
                        //2. then append the new entry
                        Entry entry = request.getEntry();
                        //Note: entry.getIndex() == prevLogIndex as per protocol. So not performing additional checks.
                        state.appendLogEntry(new LogEntry((int)entry.getIndex(), entry.getTerm(), entry.getDecree()));

                        //3. Send success.
                        responseStatus = true;
                        int commitIndex = state.getCommitIndex();
                        int leaderCommit = Long.valueOf(request.getLeaderCommit()).intValue();

                        if(leaderCommit > commitIndex) {
                            //this is the new entry which we added..
                            state.setCommitIndex(Math.min(leaderCommit, (int)entry.getIndex()));
                        }
                    }

                    AppendEntriesResponse aer = AppendEntriesResponse.newBuilder().setTerm(state.getCurrentTerm()).setSuccess(responseStatus).build();
                    aew.setResponse(aer);
                }
                synchronized (aew) {
                    //notify the waitng rpc handler thread.
                    aew.notify();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
