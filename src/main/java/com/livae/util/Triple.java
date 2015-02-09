package com.livae.util;

public class Triple<FIRST, SECOND, THIRD> {

	public final FIRST first;

	public final SECOND second;

	public final THIRD third;

	public Triple(FIRST first, SECOND second, THIRD third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
}