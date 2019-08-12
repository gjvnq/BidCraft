package com.github.gjvnq.BidCraft.Model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class AuctionOrderTest {
	@BeforeAll
	public static void setUp() {
		Config.setTestingValues();
	}

	@Test
	public void basics() throws InterruptedException {
		int seconds_duration = 1;
		FakeOfflinePlayer player = new FakeOfflinePlayer();
		ItemStack itemStack = new ItemStack(Material.DIAMOND, 4);
		Duration duration = Duration.ofSeconds(seconds_duration);
		Auction order = new Auction(player, itemStack, OrderType.BUY, 2,
		100, duration);
		assertFalse(order.isComplete());
		assertFalse(order.isExecutable());

		TimeUnit.SECONDS.sleep(seconds_duration+1);

		assertFalse(order.isExecutable());
		assertTrue(order.isComplete());
	}

	@Test
	public void noGoodBids() throws InterruptedException {
		int seconds_duration = 1;
		int unitPrice = 2;
		int amountOffered = 4;
		FakeOfflinePlayer player = new FakeOfflinePlayer();
		ItemStack itemStack = new ItemStack(Material.DIAMOND, amountOffered);
		Duration duration = Duration.ofSeconds(seconds_duration);


		Auction order = new Auction(player, itemStack, OrderType.SELL, unitPrice,
				100, duration);

		AuctionBid bid1 = new AuctionBid(order, player, unitPrice-1, amountOffered);
		assertThrows(IllegalArgumentException.class, () -> {
			order.addBid(bid1);
		});
	}

	@Test
	public void onePerfectBid() {
		int seconds_duration = 1;
		int unitPrice = 2;
		int amountOffered = 4;
		FakeOfflinePlayer player = new FakeOfflinePlayer();
		ItemStack itemStack = new ItemStack(Material.DIAMOND, amountOffered);
		Duration duration = Duration.ofSeconds(seconds_duration);


		Auction order = new Auction(player, itemStack, OrderType.SELL, unitPrice,
				100, duration);

		AuctionBid bid1 = new AuctionBid(order, player, unitPrice+1, amountOffered);
		order.addBid(bid1);

		assertTrue(order.isExecutable());
		assertFalse(order.isComplete());

		order.execute();

		assertFalse(order.isExecutable());
		assertTrue(order.isComplete());
	}
}
