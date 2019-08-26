package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.log4j.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
	public static final int LessThan = -1;
	public static final int Equal = 0;
	public static final int GreaterThan = +1;

	static void withdrawMoney(Economy econ, OfflinePlayer player, double amount) throws InsufficientFundsException {
		if (amount < 0) {
			throw new IllegalArgumentException("amount MUST be non-negative");
		}
		EconomyResponse resp = econ.withdrawPlayer(player, amount);
		if (!resp.transactionSuccess()) {
			throw new InsufficientFundsException(player, amount, resp);
		}
	}

	static void depositMoney(Economy econ, OfflinePlayer player, double amount) throws DepositException {
		if (amount < 0) {
			throw new IllegalArgumentException("amount MUST be non-negative");
		}
		EconomyResponse resp = econ.depositPlayer(player, amount);
		if (!resp.transactionSuccess()) {
			throw new DepositException(player, amount, resp);
		}
	}

	static void moveMoney(Economy econ, OfflinePlayer player, double amount) throws DepositException,
			InsufficientFundsException {
		if (amount > 0) {
			Utils.depositMoney(econ, player, amount);
		}
		if (amount < 0) {
			Utils.withdrawMoney(econ, player, -1*amount);
		}
	}

	static void checkArgsAndCalcPrice(ItemStack itemStack, Ref<Double> unitPrice, Ref<Double> totalPrice,
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

	static Result<String> basicMatch(BasicMatchable a, BasicMatchable b) {
		if (a.getType() == b.getType()) {
			return new Result<>("equal order types");
		}
		if (a.isComplete()) {
			return new Result<>("first order is complete");
		}
		if (b.isComplete()) {
			return new Result<>("second order is complete");
		}
		if (a.getType() == OrderType.SELL && a.getUnitPrice() > b.getUnitPrice()) {
			return new Result<>("incompatible prices");
		}
		if (a.getType() == OrderType.BUY && a.getUnitPrice() < b.getUnitPrice()) {
			return new Result<>("incompatible prices");
		}
		try {
			if (!a.getItemStack().isSimilar(b.getItemStack())) {
				return new Result<>("orders are for different items");
			}
		} catch (Exception e) {
			// Yes, this looks bad, but it due to https://hub.spigotmc.org/jira/browse/SPIGOT-5288
			if (a.getItemStack().getType() != b.getItemStack().getType()) {
				return new Result<>("orders are for different items");
			}
		}
		return new Result<>();
	}

	static String itemStackToString(ItemStack itemStack) {
		String ans = "???";
		try {
			ans = itemStack.toString();
			return ans;
		} catch (Exception e) {
			// See https://hub.spigotmc.org/jira/browse/SPIGOT-5288
			ans = itemStack.getType().toString();
			return itemStack.getAmount()+"x"+ans;
		}
	}

	static String exceptionToString(Exception e) {
		StringWriter msg = new StringWriter();
		e.printStackTrace(new PrintWriter(msg));
		return msg.toString();
	}

	static void logException(Exception e, Logger logger) {
		logger.error(Utils.exceptionToString(e));
	}
}
