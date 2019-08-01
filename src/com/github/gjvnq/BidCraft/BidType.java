package com.github.gjvnq.BidCraft;

public enum BidType {
	SELL,
	BUY;

	 boolean matches(BidType other) {
		return this != other;
	}
}
