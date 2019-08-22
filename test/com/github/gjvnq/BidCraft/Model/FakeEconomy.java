package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;

public class FakeEconomy implements Economy {
	HashMap<String, Double> bank = new HashMap<String, Double>();
	private int fractionalDigits = 2;

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getName() {
		return "Fake Economy";
	}

	@Override
	public boolean hasBankSupport() {
		return true;
	}

	@Override
	public int fractionalDigits() {
		return fractionalDigits;
	}

	@Override
	public String format(double amount) {
		return String.format("%."+fractionalDigits+"f", amount);
	}

	@Override
	public String currencyNamePlural() {
		return "moneys";
	}

	@Override
	public String currencyNameSingular() {
		return "money";
	}

	@Override
	public boolean hasAccount(String playerName) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return true;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName) {
		return true;
	}

	@Override
	public double getBalance(String playerName) {
		if (!bank.containsKey(playerName)) {
			return 0;
		}
		return bank.get(playerName);
	}

	@Override
	public double getBalance(OfflinePlayer player) {
		return bank.get(player.getName());
	}

	@Override
	public double getBalance(String playerName, String world) {
		return bank.get(playerName);
	}

	@Override
	public double getBalance(OfflinePlayer player, String world) {
		return bank.get(player.getName());
	}

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return getBalance(player) >= amount;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount) {
		return getBalance(player) >= amount;
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (has(playerName, amount)) {
			setBalance(playerName, getBalance(playerName) - amount);
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
		}
		return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "negative money is not allowed");
	}

	public void setBalance(String playerName, double amount) {
		bank.put(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
		return withdrawPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		setBalance(playerName, getBalance(playerName) + amount);
		return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
		return depositPlayer(player.getName(), amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		throw new NotImplementedException();
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> getBanks() {
		throw new NotImplementedException();
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		throw new NotImplementedException();
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player) {
		throw new NotImplementedException();
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		throw new NotImplementedException();
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
		throw new NotImplementedException();
	}
}
