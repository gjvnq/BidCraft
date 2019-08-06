package com.github.gjvnq.BidCraft.Model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@Execution(ExecutionMode.CONCURRENT)
public class AuctionBidTest {
	@BeforeAll
	public static void setUp() {
		Config.setTestingValues();
	}

	@Test
	public void isCancellable() throws InterruptedException {
		AuctionBid bid = new AuctionBid();
		assertTrue(bid.isCancellable());
		TimeUnit.SECONDS.sleep(Config.getBidCancelInterval().getSeconds()+1);
		assertFalse(bid.isCancellable());
	}

	@Test
	public void cancel1() throws UncancellableException {
		AuctionBid bid = new AuctionBid();
		bid.cancel();
	}

	@Test
	public void cancel2() throws UncancellableException, InterruptedException {
		AuctionBid bid = new AuctionBid();
		TimeUnit.SECONDS.sleep(Config.getBidCancelInterval().getSeconds()-1);
		bid.cancel();
	}

	@Test
	public void cancel3() throws InterruptedException {
		AuctionBid bid = new AuctionBid();
		TimeUnit.SECONDS.sleep(Config.getBidCancelInterval().getSeconds()+1);
		assertFalse(bid.isCancellable());
		assertThrows(UncancellableException.class, bid::cancel);
	}

	@Test
	public void toStringTest1() {
		AuctionBid bid = new AuctionBid();
		String expected = "AuctionBid{uuid: " + bid.getUUID() + ", auctionOrder.uuid: NULL, bestOffer: 0.000000 player: NULL}";
		assertEquals(expected, bid.toString());
	}

	@Test
	public void toStringTest2() {
		AuctionBid bid = new AuctionBid();
		bid.bestOffer = 3.14159265359;
		String expected = "AuctionBid{uuid: " + bid.getUUID() + ", auctionOrder.uuid: NULL, bestOffer: 3.141593 player: NULL}";
		assertEquals(expected, bid.toString());
	}
}