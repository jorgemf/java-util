package com.livae.util.math;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Vector3dTest {

	@Test
	public void testBasic() throws Exception {
		Vector3d v = new Vector3d(1, 2, 3);
		v.add(new Vector3d(4, 5, 6));
		assertEquals(5, v.x, 0.00001);
		assertEquals(7, v.y, 0.00001);
		assertEquals(9, v.z, 0.00001);
		v.sub(new Vector3d(4, 5, 6));
		assertEquals(1, v.x, 0.00001);
		assertEquals(2, v.y, 0.00001);
		assertEquals(3, v.z, 0.00001);
	}

	@Test
	public void testCross() throws Exception {
		Vector3d v = new Vector3d(2, 3, 4);
		v.cross(new Vector3d(3, 2, 1));
		assertEquals(-5, v.x, 0.00001);
		assertEquals(10, v.y, 0.00001);
		assertEquals(-5, v.z, 0.00001);
	}

	@Test
	public void testDot() throws Exception {
		Vector3d v = new Vector3d(2, 3, 4);
		double val = v.dot(new Vector3d(3, 2, 1));
		assertEquals(16, val, 0.00001);
	}

	@Test
	public void testNormalize() throws Exception {
		Vector3d v = new Vector3d(2, 3, 4);
		v.normalize();
		assertEquals(0.3713906763, v.x, 0.00001);
		assertEquals(0.5570860145, v.y, 0.00001);
		assertEquals(0.7427813527, v.z, 0.00001);
	}

	@Test
	public void testRotation() throws Exception {
		Vector3d v = new Vector3d(1, 0, 0);
		Quaternion q = new Quaternion();
		q.setFromEulerXYZDegrees(0, 45, 45);
		Vector3d r = v.rotate(q);
		assertEquals(1, r.length(), 0.00001);
		assertEquals(0.5, r.x, 0.00001);
		assertEquals(-0.7071068, r.y, 0.00001);
		assertEquals(0.5, r.z, 0.00001);
		q.setFromEulerXYZDegrees(0, -45, -45);
		r = v.rotate(q);
		r.normalize();
		assertEquals(1, r.length(), 0.00001);
		assertEquals(1, r.x, 0.00001);
		assertEquals(0, r.y, 0.00001);
		assertEquals(0, r.z, 0.00001);
	}
	@Test
	public void testRotation1() throws Exception {
		Vector3d v = new Vector3d(1, 0, 0);
		Quaternion q = new Quaternion();
		q.setFromEulerXYZDegrees(0, 0, 90);
		Vector3d r = v.rotate(q);
		assertEquals(1, r.length(), 0.00001);
		assertEquals(0, r.x, 0.00001);
		assertEquals(-1, r.y, 0.00001);
		assertEquals(0, r.z, 0.00001);
		q.setFromEulerXYZDegrees(0, 0, -90);
		r = v.rotate(q);
		r.normalize();
		assertEquals(1, r.length(), 0.00001);
		System.out.println("v = " + v);
		assertEquals(1, r.x, 0.00001);
		assertEquals(0, r.y, 0.00001);
		assertEquals(0, r.z, 0.00001);
	}

}