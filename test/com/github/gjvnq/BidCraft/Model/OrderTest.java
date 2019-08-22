package com.github.gjvnq.BidCraft.Model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {
	@Test
	public void execute() throws Exception {
		FakeEconomy econ = new FakeEconomy();
		FakeOfflinePlayer p1 = new FakeOfflinePlayer("A");
		FakeOfflinePlayer p2 = new FakeOfflinePlayer("B");
		econ.setBalance(p2.name, 10);
		ItemStack itemStack = new ItemStack(Material.DIAMOND, 1);
		Order sell = Order.New(econ, p1, itemStack, OrderType.SELL, PriceType.UNIT, 2);
		Order buy = Order.New(econ, p2, itemStack, OrderType.BUY, PriceType.UNIT, 3);

		ArrayList<Order> sellList = new ArrayList<Order>();
		sellList.add(sell);
		ArrayList<Order> buyList = new ArrayList<Order>();
		buyList.add(sell);

		buy.execute(sellList);
		assertTrue(buy.isComplete());
		assertEquals(0, buy.getAmount());
		assertEquals(0, sell.getAmount());
	}
}