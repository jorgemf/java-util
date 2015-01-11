package com.livae.util.math;

/**
 * Vector with 3 floats
 */
public class Vector3i implements Vector {

	public int x;

	public int y;

	public int z;

	public Vector3i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3i(final int[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
		this.z = coordinates[2];
	}

	public Vector3i(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i(final Vector3i v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final Vector3i v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final boolean equals(final Vector3i other) {
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public final void set(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final Vector3i add(final Vector3i v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public final Vector3i add(final Vector3i v, final Vector3i w) {
		this.x = v.x + w.x;
		this.y = v.y + w.y;
		this.z = v.z + w.z;
		return this;
	}

	public final Vector3i sub(final Vector3i v, final Vector3i w) {
		this.x = v.x - w.x;
		this.y = v.y - w.y;
		this.z = v.z - w.z;
		return this;
	}

	public final Vector3i sub(final Vector3i v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public final double distanceEuclidean(final Vector3i point) {
		return (float) Math.sqrt(distanceEuclidean2(point));
	}

	public final double distanceEuclidean2(final Vector3i point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public final double distanceManhattan(final Vector3i point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
	}

	public String toString() {
		return "[x:" + x + ",y:" + y + ",z:" + z + "]";
	}

	public final double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public final double length2() {
		return x * x + y * y + z * z;
	}
	public final boolean equals(final Vector v) {
		return v instanceof Vector3i && this.equals((Vector3i) v);
	}

	public final Vector add(final Vector v) {
		if (!(v instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.add((Vector3i) v);
	}

	public final Vector add(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3i && v2 instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.add((Vector3i) v1, (Vector3i) v2);
	}

	public final Vector sub(final Vector v) {
		if (!(v instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.sub((Vector3i) v);
	}

	public final Vector sub(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3i && v2 instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.sub((Vector3i) v1, (Vector3i) v2);
	}

	public final double distanceEuclidean(final Vector point) {
		if (!(point instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceEuclidean((Vector3i) point);
	}

	public final double distanceEuclidean2(final Vector point) {
		if (!(point instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceEuclidean2((Vector3i) point);
	}

	public final double distanceManhattan(final Vector point) {
		if (!(point instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceManhattan((Vector3i) point);
	}

	public final Vector cross(final Vector v) {
		if (!(v instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.cross((Vector3i) v);
	}

	public final Vector cross(final Vector v, final Vector w) {
		if (!(v instanceof Vector3i && w instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.cross((Vector3i) v, (Vector3i) w);
	}

	public final double dot(final Vector v) {
		if (!(v instanceof Vector3i)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.dot((Vector3i) v);
	}

	public final Vector normalize() {
		throw new UnsupportedOperationException();
	}

	public final Vector scale(final double value) {
		throw new UnsupportedOperationException();
	}

	public final Vector clone(){
		return new Vector3i(this);
	}
}
