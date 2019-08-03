package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class DepositException extends MoneyException {
	public DepositException(OfflinePlayer player, double amount) {
		super(String.format("failed to deposit %.6f to %s",
				amount,	player.getName()));
	}

	public DepositException(OfflinePlayer player, double amount, EconomyResponse resp) {
		super(String.format("failed to deposit %.6f to %s: %s",
				amount,	player.getName(), resp.errorMessage));
	}
}
