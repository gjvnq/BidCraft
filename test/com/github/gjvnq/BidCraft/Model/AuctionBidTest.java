package com.github.gjvnq.BidCraft.Model;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AuctionBidTest {
	@Before
	public void setUp() throws Exception {
		Config.setTestingValues();
	}

	@Test
	public void isCancellable() throws InterruptedException {
		AuctionBid bid = new AuctionBid();
		assertTrue(bid.isCancellable());
		TimeUnit.SECONDS.sleep(Config.getBidCancelInterval().getSeconds()+1);
		assertFalse(bid.isCancellable());
	}
}