package com.github.gjvnq.BidCraft;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.time.Instant;

public class Bid implements Serializable {
	BidType type;
	double askPrice, revenue;
	boolean useMarketPrice;
	ItemStack items;
	OfflinePlayer player;
	Instant placedAt;

	protected Bid(Bid bid) {
		this.type = bid.type;
		this.player = bid.player;
		this.askPrice = bid.askPrice;
		this.useMarketPrice = bid.useMarketPrice;
		this.items = bid.items.clone();
		this.placedAt = bid.placedAt;
		this.revenue = bid.revenue;
	}

	protected Bid(BidType type, OfflinePlayer player, double askPrice, ItemStack items, double revenue) {
		this.type = type;
		this.player = player;
		this.askPrice = askPrice;
		this.useMarketPrice = (askPrice <= 0);
		this.items = items;
		this.revenue = revenue;
		this.placedAt = Instant.now();
	}

	public Bid(Economy econ, BidType type, OfflinePlayer player, double askPrice, ItemStack items) throws Exception {
		this(type, player, askPrice, items, Bid.newBidGetBlockedMoney(econ, type, player, askPrice));
	}

	private static double newBidGetBlockedMoney(Economy econ, BidType type, OfflinePlayer player, double askPrice) throws Exception {
		double blockedMoney = 0;

		if (type == BidType.BUY && askPrice <= 0) {
			blockedMoney = econ.getBalance(player);
			EconomyResponse ans = econ.withdrawPlayer(player, blockedMoney);
			if (!ans.transactionSuccess()) {
				throw new Exception(ans.errorMessage);
			}
		} else if (type == BidType.BUY) {
			blockedMoney = askPrice;
			EconomyResponse ans = econ.withdrawPlayer(player, askPrice);
			if (!ans.transactionSuccess()) {
				throw new Exception(ans.errorMessage);
			}
		}
		return blockedMoney;
	}

	public BidType getType() {
		return type;
	}

	// If true, this bid will try to execute as fast as possible regardless of the askPrice.
	public boolean isUseMarketPrice() {
		return useMarketPrice;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public double getRevenue() {
		return revenue;
	}

	public double getAskUnitPrice() {
		return askPrice/this.items.getAmount();
	}

	public ItemStack getItems() {
		return items;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public Instant getPlacedAt() {
		return placedAt;
	}

	public String toString() {
		return String.format("%s %dx%s for %f",
			this.type.name(),
			this.items.getAmount(),
			this.items.getType().name(),
			this.askPrice);
	}

	public boolean isFinished() {
		return this.items.getAmount() <= 0;
	}

	public boolean execute(Bid other) {
		if (!this.type.matches(other.type)) {
			return false;
		}
		if (!this.items.isSimilar(other.items)) {
			return false;
		}

		int finalAmount = Math.max(this.items.getAmount(), other.items.getAmount());
		double finalUnitPrice;

		if (this.useMarketPrice && other.useMarketPrice) {
			return false;
		} else if (this.useMarketPrice && !other.useMarketPrice) {
			finalUnitPrice = other.getAskUnitPrice();
			finalAmount = (int) (this.revenue/finalUnitPrice);
		} else if (!this.useMarketPrice && other.useMarketPrice) {
			finalUnitPrice = this.getAskUnitPrice();
			finalAmount = (int) (other.revenue/finalUnitPrice);
		} else {
			double sellerUnitPrice;
			double buyerUnitPrice;
			if (this.type == BidType.SELL) {
				sellerUnitPrice = getAskUnitPrice();
				buyerUnitPrice = other.getAskUnitPrice();
			} else {
				sellerUnitPrice = other.getAskUnitPrice();
				buyerUnitPrice = getAskUnitPrice();
			}
			if (sellerUnitPrice > buyerUnitPrice) {
				return false;
			}
			finalUnitPrice = (sellerUnitPrice+buyerUnitPrice)/2;
		}

		double finalPrice = finalUnitPrice*finalAmount;
		this.askPrice -= finalPrice;
		other.askPrice -= finalPrice;
		this.items.setAmount(this.items.getAmount()-finalAmount);
		other.items.setAmount(other.items.getAmount()-finalAmount);

		this.revenue += this.askPrice;
		other.revenue += other.askPrice;

		return true;
	}


	public EconomyResponse cancelOrFinish(Economy econ) {
		this.revenue += this.askPrice;
		if (this.revenue > 0) {
			econ.depositPlayer(this.player, this.revenue);
		} else {
			// serious error!
		}
		return econ.depositPlayer(this.player, 0);
	}
}
