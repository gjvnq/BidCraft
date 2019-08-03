package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class InsufficientFundsException extends MoneyException {
	public InsufficientFundsException(OfflinePlayer player, double amount) {
		super(String.format("%s does'nt have enough funds to withdraw %.6f", player.getName(), amount));
	}

	public InsufficientFundsException(OfflinePlayer player, double amount, EconomyResponse resp) {
		super(String.format("%s does'nt have enough funds to withdraw %.6f: %s", player.getName(), amount, resp.errorMessage));
	}
}
