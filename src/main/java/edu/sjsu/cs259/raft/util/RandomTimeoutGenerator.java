package edu.sjsu.cs259.raft.util;

import java.util.Random;

public class RandomTimeoutGenerator {
	int upperBound;
	int lowerBound;
	Random random;
	public RandomTimeoutGenerator(int upperBound, int lowerBound) {
		super();
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		random = new Random();
	}

	public long generateRandomTimeOut()
	{
		return random.ints(lowerBound, upperBound)
				.findFirst()
				.getAsInt();
	}

}
