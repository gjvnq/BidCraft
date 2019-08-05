package com.github.gjvnq.BidCraft.Model;

import java.util.UUID;

public abstract class ThingWithUUID {
	protected UUID uuid;

	ThingWithUUID() {
		uuid = UUID.randomUUID();
	}

	public UUID getUUID() {
		if (this == null) {
			return UUID.fromString("00000000-0000-0000-0000-000000000000");
		}
		return this.uuid;
	}
}
