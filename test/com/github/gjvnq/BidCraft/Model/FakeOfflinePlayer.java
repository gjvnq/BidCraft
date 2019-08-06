package com.github.gjvnq.BidCraft.Model;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeOfflinePlayer implements OfflinePlayer {
	boolean isOnline, isOp, hasPlayedBefore, isWhitelisted, isBanned;
	String name;
	UUID uuid;
	long firstPlayed, lastPlayed;

	FakeOfflinePlayer() {
		uuid = UUID.randomUUID();
	}

	FakeOfflinePlayer(String name) {
		this();
		this.name = name;
	}

	FakeOfflinePlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public @Nullable String getName() {
		return name;
	}

	@Override
	public @NotNull UUID getUniqueId() {
		return uuid;
	}

	@Override
	public boolean isBanned() {
		return isBanned;
	}

	@Override
	public boolean isWhitelisted() {
		return isWhitelisted;
	}

	@Override
	public void setWhitelisted(boolean value) {
		isWhitelisted = value;
	}

	@Override
	public @Nullable Player getPlayer() {
		return null;
	}

	@Override
	public long getFirstPlayed() {
		return firstPlayed;
	}

	@Override
	public long getLastPlayed() {
		return lastPlayed;
	}

	@Override
	public boolean hasPlayedBefore() {
		return hasPlayedBefore;
	}

	@Override
	public @Nullable Location getBedSpawnLocation() {
		return null;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new HashMap<String, Object>();
	}

	@Override
	public boolean isOp() {
		return isOp;
	}

	@Override
	public void setOp(boolean value) {
		isOp = value;
	}
}
