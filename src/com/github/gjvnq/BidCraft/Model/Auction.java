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
public class Auction extends ThingWithUUID implements BasicMatchable {
	protected OfflinePlayer player;
	protected double unitPrice, revenue;
	protected Instant placedAt;
	protected ItemStack itemStack;
	protected OrderType type;
	protected Instant timeLimit;
	protected ArrayList<AuctionBid> bids;

	public OfflinePlayer getPlayer() {
		return player;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public double getRevenue() {
		return revenue;
	}

	public int getAmount() {
		return itemStack.getAmount();
	}

	public Instant getPlacedAt() {
		return placedAt;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public OrderType getType() {
		return type;
	}

	public Instant getTimeLimit() {
		return timeLimit;
	}

	public ArrayList<AuctionBid> getBids() {
		return (ArrayList<AuctionBid>) bids.clone();
	}

	public static Auction New(Economy econ, OfflinePlayer player, ItemStack itemStack,
	                          OrderType type, PriceType priceType, double price,
	                          Instant timeLimit)
			throws IllegalArgumentException, InsufficientFundsException {

		Ref<Double> unitPrice = new Ref<Double>(0.0);
		Ref<Double> totalPrice = new Ref<Double>(0.0);
		Utils.checkArgsAndCalcPrice(itemStack, unitPrice, totalPrice, price, priceType);

		double blockedAmount = totalPrice.val;
		if (type == OrderType.BUY) {
			Utils.withdrawMoney(econ, player, blockedAmount);
		}

		return new Auction(player, itemStack, OrderType.SELL, unitPrice.val, blockedAmount, timeLimit);
	}

	protected Auction(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                  double blockedAmount, Duration duration) {
		this(player, itemStack, type, unitPrice, blockedAmount);
		this.timeLimit = this.placedAt.plus(duration);
		this.bids = new ArrayList<AuctionBid>();

	}

	protected Auction(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                  double blockedAmount, Instant timeLimit) {
		this(player, itemStack, type, unitPrice, blockedAmount);
		this.timeLimit = timeLimit;
		this.bids = new ArrayList<AuctionBid>();
	}

	protected Auction(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                     double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack;
		this.type = type;
		this.unitPrice = unitPrice;
		this.placedAt = Instant.now();
	}

	/**
	 * @return true if there are no more items available (i.e. amount <= 0) OR the time limit for the offer has passed.
	 */
	public boolean isComplete() {
		return getAmount() <= 0 || Instant.now().isAfter(timeLimit);
	}

	/**
	 * @return true if the auction has not already closed AND it there are are bids.
	 */
	public boolean isExecutable() {
		return Instant.now().isBefore(timeLimit) && this.bids.size() != 0;
	}

	/**
	 * @return true if the auction has already closed OR it has completed.
	 */
	public boolean isDeletable() {
		return Instant.now().isAfter(timeLimit) || this.isComplete();
	}

	public void delete(Economy econ) throws Exception {
		if (!this.isDeletable()) {
			throw new IllegalStateException("this order is not currently deletable");
		}
		for (AuctionBid bid : this.bids) {
			bid.delete(econ);
		}
	}

	public void execute() {
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
