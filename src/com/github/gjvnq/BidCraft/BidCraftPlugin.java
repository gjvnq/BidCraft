package com.github.gjvnq.BidCraft;

import org.bukkit.plugin.java.JavaPlugin;

public class BidCraftPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("[BidCraft] Starting");
	}

	@Override
	public void onDisable() {
		getLogger().info("[BidCraft] Stopping");
	}
}
