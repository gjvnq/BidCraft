package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

public class AuctionBid extends ThingWithUUID {
	protected boolean canceled;
	protected OfflinePlayer player;
	protected AuctionOrder order;
	protected double bestOffer, maxAmount;
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
			Utils.withdrawMoney(econ, player, bestOffer*maxAmount);
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
		order.addBid(this);
	}

	public boolean isCancellable() {
		Duration diff = Duration.between(this.placedAt, Instant.now());
		return diff.compareTo(Config.getBidCancelInterval()) <= 0;
	}

	public void cancel() throws UncancellableException {
		if (!isCancellable()) {
			throw new UncancellableException(this.toString());
		}
		canceled = true;
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
}
