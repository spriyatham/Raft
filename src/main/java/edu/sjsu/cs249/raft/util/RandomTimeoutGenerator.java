package edu.sjsu.cs249.raft.util;

public class RandomTimeoutGenerator {
	long upperBound;
	long lowerBound;
	
	public RandomTimeoutGenerator(long upperBound, long lowerBound) {
		super();
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}
	
	public long generateRandomTimeOut()
	{
		return 0;
	}
	
}
