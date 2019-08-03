package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;

/**
 * An order that will be executed as soon as possible. If there are multiple matching orders, the best one will be
 * chosen.
 *
 * It is called a StandOrder because the order will stand for an indeterminate amount of time and it mimics a shop
 * stand.
 */
public class StandOrder extends Order {
	@Override
	protected boolean matches(Order other) {
		// Non StandOrders MUST be the "self", i.e. the ones in control.
		if (!other.getClass().equals(StandOrder.class)) {
			return false;
		}
		return matchesBasics(other);
	}

	@Override
	protected void computePriceAndAmount(Order other) {
		double finalUnitPrice = (this.unitPrice + other.unitPrice)/2;
		int finalAmount = Math.min(this.getAmount(), other.getAmount());
		double finalPrice = finalUnitPrice*finalAmount;

		this.executeMe(finalAmount, finalPrice);
		other.executeMe(finalAmount, finalPrice);
	}

	protected static void checkArgsAndCalcPrice(ItemStack itemStack, Ref<Double> unitPrice, Ref<Double> totalPrice,
	                                     double price, PriceType priceType) throws IllegalArgumentException {
		if (itemStack.getAmount() == 0) {
			throw new IllegalArgumentException("amount must be grater than zero");
		}

		switch (priceType) {
			case UNIT:
				unitPrice.val = price;
			case TOTAL:
				unitPrice.val = price/itemStack.getAmount();
		}
		totalPrice.val = unitPrice.val * itemStack.getAmount();
	}

	public static StandOrder New(Economy econ, OfflinePlayer player, ItemStack itemStack,
	                             OrderType type, PriceType priceType, double price) throws IllegalArgumentException, InsufficientFundsException {
		Ref<Double> unitPrice = new Ref<Double>(0.0);
		Ref<Double> totalPrice = new Ref<Double>(0.0);
		checkArgsAndCalcPrice(itemStack, unitPrice, totalPrice, price, priceType);

		double blockedAmount = totalPrice.val;
		if (type == OrderType.BUY) {
			Utils.withdrawMoney(econ, player, blockedAmount);
		}

		return new StandOrder(player, itemStack, OrderType.SELL, unitPrice.val, blockedAmount);
	}

	protected StandOrder(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                     double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack;
		this.type = type;
		this.unitPrice = unitPrice;
		this.placedAt = Instant.now();
	}
}
