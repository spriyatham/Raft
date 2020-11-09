package edu.sjsu.cs259.raft;

import java.io.Serializable;

public class VotedFor implements Serializable {
    long term;
    int candidateID;

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }

    public int getCandidateID() {
        return candidateID;
    }

    public void setCandidateID(int candidateID) {
        this.candidateID = candidateID;
    }

    @Override
    public String toString() {
        return "VotedFor{" +
                "term=" + term +
                ", candidateID=" + candidateID +
                '}';
    }
}
