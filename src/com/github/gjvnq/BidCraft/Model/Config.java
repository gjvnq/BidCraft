package com.github.gjvnq.BidCraft.Model;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.util.Locale;

public class Config {
	protected static Logger logger = Logger.getLogger(Order.class.getName());
	protected static Duration bidCancelInterval = Duration.ofMinutes(1);

	protected static void setTestingValues() {
		Config.bidCancelInterval = Duration.ofSeconds(3);
		Locale.setDefault(new Locale("en", "US"));
	}

	protected static void enableDebug() {
		Logger.getRootLogger().setLevel(Level.DEBUG);
	}

	public static Duration getBidCancelInterval() {
		return bidCancelInterval;
	}
}
