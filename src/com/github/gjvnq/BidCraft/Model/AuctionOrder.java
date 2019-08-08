package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

/**
 * An order that will wait for the best possible offer until a certain time. This order ONLY executes fully.
 *
 * Note that, in case of a SELL, the buyer will pay the second highest price. And in case of a BUY, the seller will
 * get the second lowest price. This is done in order to incentivize people to bid at their break even point.
 */
public class AuctionOrder extends StandOrder {
	protected Instant timeLimit;
	protected ArrayList<AuctionBid> bids;

	public static AuctionOrder New(Economy econ, OfflinePlayer player, ItemStack itemStack,
	                               OrderType type, PriceType priceType, double price,
	                               Instant timeLimit)
			throws IllegalArgumentException, InsufficientFundsException {

		Ref<Double> unitPrice = new Ref<Double>(0.0);
		Ref<Double> totalPrice = new Ref<Double>(0.0);
		checkArgsAndCalcPrice(itemStack, unitPrice, totalPrice, price, priceType);

		double blockedAmount = totalPrice.val;
		if (type == OrderType.BUY) {
			Utils.withdrawMoney(econ, player, blockedAmount);
		}

		return new AuctionOrder(player, itemStack, OrderType.SELL, unitPrice.val, blockedAmount, timeLimit);
	}

	protected AuctionOrder(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                       double blockedAmount, Duration duration) {
		super(player, itemStack, type, unitPrice, blockedAmount);
		this.timeLimit = this.placedAt.plus(duration);
		this.bids = new ArrayList<AuctionBid>();

	}

	protected AuctionOrder(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                     double blockedAmount, Instant timeLimit) {
		super(player, itemStack, type, unitPrice, blockedAmount);
		this.timeLimit = timeLimit;
		this.bids = new ArrayList<AuctionBid>();
	}

	/**
	 * @return true if there are no more items available (i.e. amount <= 0) OR the time limit for the offer has passed.
	 */
	public boolean isComplete() {
		return getAmount() <= 0 || Instant.now().isAfter(timeLimit);
	}

	@Override
	protected boolean matches(Order other) {
		// AuctionOrders can only match other AuctionOrders
		if (!other.getClass().equals(AuctionOrder.class)) {
			return false;
		}
		return matchesBasics(other);
	}

	/**
	 * @return true if the auction has not already closed AND it there are are bids.
	 */
	@Override
	public boolean isExecutable() {
		return Instant.now().isBefore(timeLimit) && this.bids.size() != 0;
	}

	@Override
	/**
	 * @return true if the auction has already closed OR it has completed.
	 */
	public boolean isDeletable() {
		return Instant.now().isAfter(timeLimit) || this.isComplete();
	}

	@Override
	public void delete(Economy econ) throws Exception {
		if (!this.isDeletable()) {
			throw new IllegalStateException("this order is not currently deletable");
		}
		for (AuctionBid bid : this.bids) {
			bid.delete(econ);
		}
	}

	@Override
	protected void computePriceAndAmount(Order bestOther) {
		// this is left intentionally blank
	}

	@Override
	public void executeFromList(ArrayList<Order> orders) {
		execute();
	}

	protected void execute() {
		Collections.sort(this.bids);
	}

	public void addBid(@NotNull AuctionBid bid) {
		if (!bid.type.matches(this.type)) {
			throw new IllegalArgumentException("incompatible bid type");
		}
		if (bid.bestOffer < this.unitPrice) {
			throw new IllegalArgumentException("bid best offer is too low");
		}
		if (bid.maxAmount > this.getAmount()) {
			throw new IllegalArgumentException("bid best offer amount it too high");
		}
		if (bid.maxAmount < 0) {
			throw new IllegalArgumentException("bid best offer amount cannot be negative");
		}
		this.bids.add(bid);
	}
}
