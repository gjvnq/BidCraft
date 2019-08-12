package com.github.gjvnq.BidCraft.Model;

import org.bukkit.inventory.ItemStack;

public interface BasicMatchable {
	OrderType getType();
	boolean isComplete();
	double getUnitPrice();
	ItemStack getItemStack();
}
