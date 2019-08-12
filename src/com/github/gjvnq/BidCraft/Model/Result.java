package com.github.gjvnq.BidCraft.Model;

public class Result<T> {
	T reason;
	boolean ok;

	public T getReason() {
		return reason;
	}

	public boolean isOk() {
		return ok;
	}

	public Result(T reason) {
		this.reason = reason;
		this.ok = true;
	}

	public Result() {
		this.ok = false;
	}
}
