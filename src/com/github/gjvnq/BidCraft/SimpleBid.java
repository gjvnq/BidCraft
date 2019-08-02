package com.github.gjvnq.BidCraft;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import javax.naming.InsufficientResourcesException;
import java.time.Instant;

public class SimpleBid extends Bid {
	@Override
	protected void computePriceAndAmount(Bid other) {
		double finalUnitPrice = (this.unitPrice + other.unitPrice)/2;
		int finalAmount = Math.min(this.getAmount(), other.getAmount());
		double finalPrice = finalUnitPrice*finalAmount;

		this.executeMe(finalAmount, finalPrice);
		other.executeMe(finalAmount, finalPrice);
	}

	public static SimpleBid New(Economy econ, OfflinePlayer player, ItemStack itemStack,
	                                                BidType type, PriceType priceType, double price) throws IllegalArgumentException, InsufficientFundsException {
		if (itemStack.getAmount() == 0) {
			throw new IllegalArgumentException("amount must be grater than zero");
		}

		double unitPrice = 0;
		switch (priceType) {
			case UNIT:
			unitPrice = price;
			case TOTAL:
			unitPrice = price/itemStack.getAmount();
		}

		double blockedAmount = 0;
		if (type == BidType.BUY) {
			blockedAmount = price*itemStack.getAmount();
			EconomyResponse resp = econ.withdrawPlayer(player, blockedAmount);
			if (!resp.transactionSuccess()) {
				throw new InsufficientFundsException("failed to withdraw the necessary money: "+blockedAmount);
			}
		}

		return new SimpleBid(player, itemStack, BidType.SELL, unitPrice, blockedAmount);
	}

	protected SimpleBid(OfflinePlayer player, ItemStack itemStack, BidType type, double unitPrice,
	                    double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack;
		this.type = type;
		this.unitPrice = unitPrice;
		this.placedAt = Instant.now();
	}
}
