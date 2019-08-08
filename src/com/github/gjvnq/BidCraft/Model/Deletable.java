package com.github.gjvnq.BidCraft.Model;

import net.milkbowl.vault.economy.Economy;

public interface Deletable {
	void delete(Economy econ) throws Exception;
}
