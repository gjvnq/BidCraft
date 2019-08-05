package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.time.Instant;

public class AuctionBid extends ThingWithUUID {
	protected boolean canceled;
	protected OfflinePlayer player;
	protected AuctionOrder order;
	protected double bestOffer;
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

	public AuctionBid(Economy econ, AuctionOrder order, OfflinePlayer player, double bestOffer) throws InsufficientFundsException {
		this(order, player, bestOffer);

		if (this.type == OrderType.BUY) {
			Utils.withdrawMoney(econ, player, bestOffer);
		}
	}

	protected AuctionBid() {
		this.placedAt = Instant.now();
	}

	protected AuctionBid(AuctionOrder order, OfflinePlayer player, double bestOffer) {
		this();
		this.order = order;
		this.player = player;
		this.bestOffer = bestOffer;
		this.type = order.type.opposite();
		order.addBid(this);
	}

	public boolean isCancellable() {
		Duration diff = Duration.between(this.placedAt, Instant.now());
		return diff.compareTo(Config.getBidCancelInterval()) <= 0;
	}

	public void cancel() throws UncancellableException {
		if (!isCancellable()) {
			try {
				throw new UncancellableException(this.toString());
			} catch (Exception e) {
				throw new UncancellableException(e.toString());
			}
		}
		canceled = true;
	}

	public String toString() {
		return String.format("AuctionBid{uuid: %s, auctionOrder.uuid: %s, bestOffer: %.6f player: %s}", uuid, order.getUUID(), bestOffer, player.getName());
	}
}
