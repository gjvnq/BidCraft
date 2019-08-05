package com.github.gjvnq.BidCraft.Model;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Abstract order type. Always execute partially looking for the best  possible profit.
 */
public abstract class Order extends ThingWithUUID {
    protected OfflinePlayer player;
    protected double unitPrice, revenue;
    protected Instant placedAt;
    protected ItemStack itemStack;
    protected OrderType type;

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
    public OrderType getType() {
        return type;
    }

    /**
     * @return true if there are no more items available (i.e. amount <= 0).
     */
    public boolean isComplete() {
        return getAmount() <= 0;
    }

	/**
	 * @return true if the order can be executed now.
	 */
	public boolean isExecutable() {
		return true;
	}

	protected abstract boolean matches(Order other);

    protected boolean matchesBasics(Order other) {
        if (this.type == other.type) {
            return false;
        }
        if (this.isComplete() || other.isComplete()) {
            return false;
        }
        if (this.type == OrderType.SELL && this.unitPrice > other.unitPrice) {
            return false;
        }
        if (this.type == OrderType.BUY && this.unitPrice < other.unitPrice) {
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

	protected abstract void computePriceAndAmount(Order bestOther);

	public void executeFromList(ArrayList<Order> orders) {
		Order bestOther = getBestBid(orders);
		while (!this.isComplete() && bestOther != null) {
			computePriceAndAmount(bestOther);
			bestOther = getBestBid(orders);
		}
	}

	protected Order getBestBid(ArrayList<Order> orders) {
		Iterator<Order> it = orders.iterator();
		Order bestOther = null;
		while (it.hasNext()) {
			Order other = it.next();
			if (this.matches(other)) {
				continue;
			}
			if (bestOther == null) {
				bestOther = other;
			}
			if (this.type == OrderType.SELL && other.unitPrice > bestOther.unitPrice) {
				bestOther = other;
			}
			if (this.type == OrderType.BUY && other.unitPrice < bestOther.unitPrice) {
				bestOther = other;
			}
		}
		return bestOther;
	}

	protected void executeMe(int finalAmount, double finalPrice) {
		itemStack.setAmount(itemStack.getAmount()-finalAmount);
		if (type == OrderType.SELL) {
			revenue += finalPrice;
		}
		if (type == OrderType.BUY) {
			revenue -= finalPrice;
		}
	}
}
