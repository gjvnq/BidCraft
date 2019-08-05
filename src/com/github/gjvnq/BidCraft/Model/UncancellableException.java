package com.github.gjvnq.BidCraft.Model;

public class UncancellableException extends Exception {
	UncancellableException(String s) {
		super(s+" cannot be cancelled");
	}
}
