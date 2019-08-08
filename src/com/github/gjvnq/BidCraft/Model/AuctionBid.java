package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class AuctionBid extends ThingWithUUID implements Comparable<AuctionBid>, Deletable {
	protected boolean canceled;
	protected OfflinePlayer player;
	protected AuctionOrder order;
	protected double bestOffer, maxAmount, revenue;
	protected OrderType type;
	protected Instant placedAt;

	/**
	 * @return who placed this order.
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	public AuctionOrder getOrder() {
		return order;
	}

	public double getBestOffer() {
		return bestOffer;
	}

	public OrderType getType() {
		return type;
	}

	public Instant getPlacedAt() {
		return placedAt;
	}

	public AuctionBid(Economy econ, AuctionOrder order, OfflinePlayer player, double bestOffer, double maxAmount) throws InsufficientFundsException {
		this(order, player, bestOffer, maxAmount);

		if (this.type == OrderType.BUY) {
			revenue = bestOffer*maxAmount;
			Utils.withdrawMoney(econ, player, revenue);
		}
	}

	protected AuctionBid() {
		this.placedAt = Instant.now();
	}

	protected AuctionBid(@NotNull AuctionOrder order, @NotNull OfflinePlayer player, double bestOffer, double maxAmount) {
		this();
		this.order = order;
		this.player = player;
		this.bestOffer = bestOffer;
		this.maxAmount = Math.min(maxAmount, order.getAmount());
		this.type = order.type.opposite();
	}

	public boolean isCancellable() {
		Duration diff = Duration.between(this.placedAt, Instant.now());
		return diff.compareTo(Config.getBidCancelInterval()) <= 0;
	}

	public void cancel(Economy econ) throws UncancellableException {
		if (!isCancellable()) {
			throw new UncancellableException(this.toString());
		}
		canceled = true;
		Market.Main.markForDeletion(this);
	}


	public String toString() {
		String order_str = "NULL";
		String player_str = "NULL";
		if (order != null) {
			order_str = order.getUUID().toString();
		}
		if (player != null) {
			player_str = player.getName();
		}
		return String.format("AuctionBid{uuid: %s, auctionOrder.uuid: %s, bestOffer: %.6f player: %s}", uuid, order_str, bestOffer, player_str);
	}

	@Override
	public int compareTo(@NotNull AuctionBid other) {
		if (!this.type.matches(other.type)) {
			throw new IllegalArgumentException("bids MUST be of the same type");
		}

		if (this.bestOffer == this.bestOffer) {
			if (this.placedAt.isBefore(other.placedAt)) {
				return Utils.LessThan;
			} else if (this.placedAt.isAfter(other.placedAt)) {
				return Utils.GreaterThan;
			} else {
				return Utils.Equal;
			}
		}

		OrderType orderType = this.type.opposite();
		switch (orderType) {
			case BUY:
				if (this.bestOffer < this.bestOffer) {
					return Utils.LessThan;
				} else {
					return Utils.GreaterThan;
				}
			case SELL:
				if (this.bestOffer > this.bestOffer) {
					return Utils.LessThan;
				} else {
					return Utils.GreaterThan;
				}
		}
		throw new IllegalStateException("something went terribly wrong");
	}

	void execute(int actualAmount, double actualPrice) {
		double delta = actualAmount*actualPrice;
		switch (this.type) {
			case BUY:
				revenue -= delta;
			case SELL:
				revenue += delta;
		}
	}

	@Override
	public void delete(Economy econ) throws Exception {
//		throw new NotImplementedException();
		Utils.moveMoney(econ, player, revenue);
		if (this.type == OrderType.SELL) {
			Market.Main.
		}
	}
}
