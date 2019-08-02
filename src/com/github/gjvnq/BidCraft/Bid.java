package com.github.gjvnq.BidCraft;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Bid {
    protected OfflinePlayer player;
    protected double unitPrice, revenue;
    protected Instant placedAt;
    protected ItemStack itemStack;
    protected BidType type;

    /**
     * @return who placed this order.
     */
    public OfflinePlayer getPlayer() {
		return player;
	}

    /**
     * @return when this order was placed.
     */
	public Instant getPlacedAt() {
		return placedAt;
	}

    /**
     * @return the item and amount that is being sold or bought.
     */
	public ItemStack getItemStack() {
		return itemStack;
	}

    /**
     * @return the number of items still available in this bid.
     */
	public int getAmount() {
		return itemStack.getAmount();
	}

	/**
	 * @return if positive, how much money to deposit to the player, if negative, something went terribly wrong!
	 */
	public double getRevenue() {
		return revenue;
	}


	/**
     * @return for buy orders, returns the maximum unit price and for sell orders returns the minimum unit price.
     */
	public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * @return getUnitPrice()*getAmount()
     */
    public double getTotalPrice() {
        return getUnitPrice()*getAmount();
    }

    /**
     * @return whether this is a buy or sell bid.
     */
    public BidType getType() {
        return type;
    }

    /**
     * @return true if there are no more items available (i.e. amount <= 0).
     */
    public boolean isComplete() {
        return getAmount() <= 0;
    }

    protected boolean matches(Bid other) {
        // AutoBids MUST be the "self", i.e. the ones in control.
        if (other.getClass().equals(AutoBid.class)) {
            return false;
        }

        if (this.type == other.type) {
            return false;
        }
        if (this.isComplete() || other.isComplete()) {
            return false;
        }
        if (this.type == BidType.SELL && this.unitPrice > other.unitPrice) {
            return false;
        }
        if (this.type == BidType.BUY && this.unitPrice < other.unitPrice) {
            return false;
        }
        return this.itemStack.isSimilar(other.itemStack);
    }

    /**
     * @return a simple string describing this bid. Ex: "BUY 64xCOAL u=0.015625 f=1.00000"
     */
    public String toString() {
        return String.format("%s %dx%s u=%.6f t=%.6f",
                this.getType().name(),
                this.getAmount(),
                this.itemStack.toString(),
                this.unitPrice,
                this.getTotalPrice());
    }

	protected abstract void computePriceAndAmount(Bid bestOther);

	public void executeFromList(ArrayList<Bid> bids) {
		Bid bestOther = getBestBid(bids);
		while (!this.isComplete() && bestOther != null) {
			computePriceAndAmount(bestOther);
			bestOther = getBestBid(bids);
		}
	}

	protected Bid getBestBid(ArrayList<Bid> bids) {
		Iterator<Bid> it = bids.iterator();
		Bid bestOther = null;
		while (it.hasNext()) {
			Bid other = it.next();
			if (this.matches(other)) {
				continue;
			}
			if (bestOther == null) {
				bestOther = other;
			}
			if (this.type == BidType.SELL && other.unitPrice > bestOther.unitPrice) {
				bestOther = other;
			}
			if (this.type == BidType.BUY && other.unitPrice < bestOther.unitPrice) {
				bestOther = other;
			}
		}
		return bestOther;
	}

	protected void executeMe(int finalAmount, double finalPrice) {
		itemStack.setAmount(itemStack.getAmount()-finalAmount);
		if (type == BidType.SELL) {
			revenue += finalPrice;
		}
		if (type == BidType.BUY) {
			revenue -= finalPrice;
		}
	}
}
