package com.github.gjvnq.BidCraft.Model;

import java.util.UUID;

public abstract class ThingWithUUID {
	protected UUID uuid;

	ThingWithUUID() {
		uuid = UUID.randomUUID();
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public boolean sameUUID(ThingWithUUID other) {
		return uuid.equals(other.uuid);
	}

	public int compareUUID(ThingWithUUID other) {
		return uuid.compareTo(other.uuid);
	}
}
