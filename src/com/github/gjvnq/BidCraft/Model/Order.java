package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import org.apache.log4j.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * An order that will be executed as soon as possible. If there are multiple
 * matching orders, the best one will be chosen.
 *
 * It is called a Order because the order will stand for an
 * indeterminate amount of time and it mimics a shop stand.
 */
public class Order extends ThingWithUUID implements BasicMatchable {
	protected static Logger logger = Logger.getLogger(Order.class.getName());
	protected OfflinePlayer player;
	protected double unitPrice, revenue;
	protected Instant placedAt;
	protected ItemStack itemStack;
	protected OrderType type;
	protected boolean isCancelled;

	/**
	 * @return true if the player asked this order for cancellation
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

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
	 * @return a clone of the item and amount that is being sold or bought.
	 */
	public ItemStack getItemStack() {
		return itemStack.clone();
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

	/**
	 * @return true if the order can be deleted from the system.
	 */
	public boolean isDeletable() {
		return this.isComplete();
	}

	/**
	 * Cancels this order.
	 */
	public void cancel() {
		isCancelled = true;
	}

	/**
	 * @return deletes this order and refunds any necessary values and items.
	 */
	public void delete(Economy econ) throws Exception {
		if (!this.isDeletable()) {
			throw new IllegalStateException("this order is not currently deletable");
		}
		Utils.moveMoney(econ, player, revenue);
		if (this.type == OrderType.SELL) {
			Market.Main.giveTo(player, itemStack);
		}
	}

	/**
	 * @return a simple string describing this bid. Ex: "BUY 64xCOAL u=0.015625 f=1.00000"
	 */
	public String toString() {
		return String.format("%s %s u=%.6f t=%.6f",
				this.getType().name(),
				Utils.itemStackToString(this.itemStack),
				this.unitPrice,
				this.getTotalPrice());
	}

	public void execute(@NotNull ArrayList<Order> orders) {
		Order bestOther = getBestBid(orders);
		logger.debug("bestOther = "+bestOther);
		while (!this.isComplete() && bestOther != null) {
			computePriceAndAmount(bestOther);
			bestOther = getBestBid(orders);
			logger.debug("bestOther = "+bestOther);
		}
	}

	protected Order getBestBid(@NotNull ArrayList<Order> orders) {
		Iterator<Order> it = orders.iterator();
		Order bestOther = null;
		logger.debug("this = "+this);
		while (it.hasNext()) {
			Order other = it.next();
			logger.debug("other = "+other);
			if (this.matches(other)) {
				continue;
			}
			if (bestOther == null) {
				bestOther = other;
			}
			if (this.type == OrderType.SELL && other.unitPrice > bestOther.unitPrice) {
				bestOther = other;
				logger.debug("bestOther <- 1 "+bestOther);
			}
			if (this.type == OrderType.BUY && other.unitPrice < bestOther.unitPrice) {
				bestOther = other;
				logger.debug("bestOther <- 2 "+bestOther);
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

	protected boolean matches(Order other) {
		return Utils.basicMatch(this, other).isOk();
	}


	protected void computePriceAndAmount(Order other) {
		double finalUnitPrice = (this.unitPrice + other.unitPrice)/2;
		int finalAmount = Math.min(this.getAmount(), other.getAmount());
		double finalPrice = finalUnitPrice*finalAmount;

		this.executeMe(finalAmount, finalPrice);
		other.executeMe(finalAmount, finalPrice);
	}

	public static Order New(Economy econ, OfflinePlayer player, ItemStack itemStack,
	                        OrderType type, PriceType priceType, double price) throws IllegalArgumentException, InsufficientFundsException {
		Ref<Double> unitPrice = new Ref<Double>(0.0);
		Ref<Double> totalPrice = new Ref<Double>(0.0);
		Utils.checkArgsAndCalcPrice(itemStack, unitPrice, totalPrice, price, priceType);

		double blockedAmount = totalPrice.val;
		if (type == OrderType.BUY) {
			Utils.withdrawMoney(econ, player, blockedAmount);
		}

		return new Order(player, itemStack, type, unitPrice.val, blockedAmount);
	}

	protected Order(OfflinePlayer player, ItemStack itemStack, OrderType type, double unitPrice,
	                double blockedAmount) {
		this.revenue = blockedAmount;
		this.player = player;
		this.itemStack = itemStack.clone();
		this.type = type;
		this.unitPrice = unitPrice;
		this.placedAt = Instant.now();
	}
}
