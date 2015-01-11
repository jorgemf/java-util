package com.livae.util.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Vector3fTest {

	@Test
	public void testRotation() throws Exception {
		Vector3f v = new Vector3f(1, 1, 1);
		v.add(new Vector3f(2, 0, 1));
		assertEquals(3, v.x, 0.00001);
		fail();
	}

}