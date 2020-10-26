package edu.sjsu.cs249.raft;

public class LogEntry {
	int index;
	int term;
	String command;
	
	public LogEntry(int index, int term, String command) {
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
	public int getTerm() {
		return term;
	}
	public void setTerm(int term) {
		this.term = term;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
}
