package edu.sjsu.cs259.raft;

import edu.sjsu.cs259.raft.service.gen.AppendEntriesRequest;
import edu.sjsu.cs259.raft.service.gen.AppendEntriesResponse;

public class AppendEntriesWrapper {
    //TODO: If required use a separate object for waiting on
    AppendEntriesRequest request;
    AppendEntriesResponse response;
    Exception someException;

    public AppendEntriesWrapper(AppendEntriesRequest request) {
        this.request = request;
    }

    public AppendEntriesRequest getRequest() {
        return request;
    }

    public void setRequest(AppendEntriesRequest request) {
        this.request = request;
    }

    public AppendEntriesResponse getResponse() {
        return response;
    }

    public void setResponse(AppendEntriesResponse response) {
        this.response = response;
    }

    public Exception getSomeException() {
        return someException;
    }

    public void setSomeException(Exception someException) {
        this.someException = someException;
    }
}
