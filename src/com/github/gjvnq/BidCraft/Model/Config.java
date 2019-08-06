package com.github.gjvnq.BidCraft.Model;

import java.time.Duration;
import java.util.Locale;

public class Config {
	protected static Duration bidCancelInterval = Duration.ofMinutes(1);

	protected static void setTestingValues() {
		Config.bidCancelInterval = Duration.ofSeconds(3);
		Locale.setDefault(new Locale("en", "US"));
	}

	public static Duration getBidCancelInterval() {
		return bidCancelInterval;
	}
}
