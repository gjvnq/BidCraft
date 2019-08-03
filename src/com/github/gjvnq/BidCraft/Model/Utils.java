package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class Utils {
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
}
