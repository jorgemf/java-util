package com.livae.util.math;

/**
 * Vector with 3 floats
 */
public class Vector3l implements Vector {

	public long x;

	public long y;

	public long z;

	public Vector3l() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3l(final long[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
		this.z = coordinates[2];
	}

	public Vector3l(final long x, final long y, final long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3l(final Vector3l v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final Vector3l v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final boolean equals(final Vector3l other) {
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public final void set(final long x, final long y, final long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final Vector3l add(final Vector3l v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public final Vector3l add(final Vector3l v, final Vector3l w) {
		this.x = v.x + w.x;
		this.y = v.y + w.y;
		this.z = v.z + w.z;
		return this;
	}

	public final Vector3l sub(final Vector3l v, final Vector3l w) {
		this.x = v.x - w.x;
		this.y = v.y - w.y;
		this.z = v.z - w.z;
		return this;
	}

	public final Vector3l sub(final Vector3l v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public final double distanceEuclidean(final Vector3l point) {
		return (float) Math.sqrt(distanceEuclidean2(point));
	}

	public final double distanceEuclidean2(final Vector3l point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public final double distanceManhattan(final Vector3l point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
	}

	public final Vector3l cross(final Vector3l w) {
		long x = this.y * w.z - this.z * w.y;
		long y = w.x * this.z - w.z * this.x;
		long z = this.x * w.y - this.y * w.x;
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public final Vector3l cross(final Vector3l v, final Vector3l w) {
		this.x = v.y * w.z - v.z * w.y;
		this.y = w.x * v.z - w.z * v.x;
		this.z = v.x * w.y - v.y * w.x;
		return this;
	}

	public final double dot(final Vector3l v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public final boolean equals(final Vector v) {
		return v instanceof Vector3l && this.equals((Vector3l) v);
	}

	public final Vector add(final Vector v) {
		if (!(v instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector3l) v);
	}

	public final Vector add(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3l && v2 instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector3l) v1, (Vector3l) v2);
	}

	public final Vector sub(final Vector v) {
		if (!(v instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector3l) v);
	}

	public final Vector sub(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3l && v2 instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector3l) v1, (Vector3l) v2);
	}

	public final double distanceEuclidean(final Vector point) {
		if (!(point instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean((Vector3l) point);
	}

	public final double distanceEuclidean2(final Vector point) {
		if (!(point instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean2((Vector3l) point);
	}

	public final double distanceManhattan(final Vector point) {
		if (!(point instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceManhattan((Vector3l) point);
	}

	public final Vector cross(final Vector v) {
		if (!(v instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.cross((Vector3l) v);
	}

	public final Vector cross(final Vector v, final Vector w) {
		if (!(v instanceof Vector3l && w instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.cross((Vector3l) v, (Vector3l) w);
	}

	public final double dot(final Vector v) {
		if (!(v instanceof Vector3l)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.dot((Vector3l) v);
	}

	public final Vector normalize() {
		throw new UnsupportedOperationException();
	}

	public final Vector scale(final double value) {
		throw new UnsupportedOperationException();
	}

	public final double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public final double length2() {
		return x * x + y * y + z * z;
	}

	@Override
	public final Vector clone() {
		return new Vector3l(this);
	}

	public String toString() {
		return "[x:" + x + ",y:" + y + ",z:" + z + "]";
	}
}
