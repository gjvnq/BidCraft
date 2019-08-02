package com.github.gjvnq.BidCraft;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

public class AutoBid extends Bid {
	protected AutoBid(OfflinePlayer player, ItemStack itemStack, BidType type,
	                  double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack;
		this.type = type;
		this.placedAt = Instant.now();
	}

	public AutoBid(Economy econ, OfflinePlayer player, ItemStack itemStack, BidType type) throws Exception {
		this(player, itemStack, type, AutoBid.getBlockedMoney(econ, player));
	}

	private static double getBlockedMoney(Economy econ, OfflinePlayer player) throws Exception {
		double balance = econ.getBalance(player);
		EconomyResponse resp = econ.withdrawPlayer(player, balance);
		if (!resp.transactionSuccess()) {
			throw new Exception("failed to withdraw all money");
		}
		return balance;
	}

	public String toString() {
		return String.format("%s %dx%s b=%.6f",
				this.getType().name(),
				this.getAmount(),
				this.itemStack.toString(),
				this.revenue);
	}

	@Override
	protected void computePriceAndAmount(Bid other) {
		double finalUnitPrice = other.unitPrice;
		int maxAmount = (int) (revenue/finalUnitPrice);
		int finalAmount = Math.min(maxAmount, other.getAmount());
		double finalPrice = finalUnitPrice*finalAmount;

		this.executeMe(finalAmount, finalPrice);
		other.executeMe(finalAmount, finalPrice);
	}
}
