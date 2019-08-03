package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

/**
 * An order that MUST be done instantaneously. If it is not possible, this order will be removed.
 */
public class InstantOrder extends Order {
	protected InstantOrder(OfflinePlayer player, ItemStack itemStack, OrderType type,
	                       double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack;
		this.type = type;
		this.placedAt = Instant.now();
	}

	public InstantOrder(Economy econ, OfflinePlayer player, ItemStack itemStack, OrderType type) throws Exception {
		this(player, itemStack, type, InstantOrder.getBlockedMoney(econ, player));
	}

	private static double getBlockedMoney(Economy econ, OfflinePlayer player) throws Exception {
		double balance = econ.getBalance(player);
		EconomyResponse resp = econ.withdrawPlayer(player, balance);
		if (!resp.transactionSuccess()) {
			throw new Exception("failed to withdraw all money");
		}
		return balance;
	}

	@Override
	protected boolean matches(Order other) {
		// InstantOrders can only match other StandOrders.
		if (!other.getClass().equals(StandOrder.class)) {
			return false;
		}
		return matchesBasics(other);
	}

	public String toString() {
		return String.format("%s %dx%s b=%.6f",
				this.getType().name(),
				this.getAmount(),
				this.itemStack.toString(),
				this.revenue);
	}

	@Override
	protected void computePriceAndAmount(Order other) {
		double finalUnitPrice = other.unitPrice;
		int maxAmount = (int) (revenue/finalUnitPrice);
		int finalAmount = Math.min(maxAmount, other.getAmount());
		double finalPrice = finalUnitPrice*finalAmount;

		this.executeMe(finalAmount, finalPrice);
		other.executeMe(finalAmount, finalPrice);
	}
}
