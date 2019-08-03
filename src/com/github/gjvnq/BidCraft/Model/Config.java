package com.github.gjvnq.BidCraft.Model;

import java.time.Duration;

public class Config {
	protected static Duration bidCancelInterval = Duration.ofMinutes(1);

	protected static void setTestingValues() {
		Config.bidCancelInterval = Duration.ofSeconds(5);
	}

	public static Duration getBidCancelInterval() {
		return bidCancelInterval;
	}
}
