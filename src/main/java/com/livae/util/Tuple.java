package com.livae.util;

public class Tuple<FIRST, SECOND> {

	public final FIRST first;

	public final SECOND second;

	public Tuple(FIRST first, SECOND second) {
		this.first = first;
		this.second = second;
	}
}