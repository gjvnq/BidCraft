package com.github.gjvnq.BidCraft.Model;

public enum OrderType {
	SELL,
	BUY;

	boolean matches(OrderType other) {
		return this != other;
	}

	public OrderType opposite() {
	 	switch (this) {
		    case SELL:
		    	return BUY;
		    case BUY:
		    	return SELL;
	    }
	    throw new IllegalArgumentException();
	}
}