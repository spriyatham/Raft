package edu.sjsu.cs259.raft;

import java.io.Serializable;

public class LogEntry implements Serializable {
	int index;
	long term;
	String command;
	
	public LogEntry(int index, long term, String command) {
		super();
		this.index = index;
		this.term = term;
		this.command = command;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public long getTerm() {
		return term;
	}
	public void setTerm(long term) {
		this.term = term;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
}
