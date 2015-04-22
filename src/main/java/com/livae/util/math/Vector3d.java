package com.livae.util.math;

/**
 * Vector with 3 floats
 */
public class Vector3d implements Vector {

	public double x;

	public double y;

	public double z;

	public Vector3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3d(final double[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
		this.z = coordinates[2];
	}

	public Vector3d(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d(final Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final Vector3d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final boolean equals(final Vector3d other) {
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public final Vector3d add(final Vector3d v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public final Vector3d add(final Vector3d v, final Vector3d w) {
		this.x = v.x + w.x;
		this.y = v.y + w.y;
		this.z = v.z + w.z;
		return this;
	}

	public final Vector3d sub(final Vector3d v, final Vector3d w) {
		this.x = v.x - w.x;
		this.y = v.y - w.y;
		this.z = v.z - w.z;
		return this;
	}

	public final Vector3d sub(final Vector3d v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public final Vector3d cross(final Vector3d w) {
		double x = this.y * w.z - this.z * w.y;
		double y = w.x * this.z - w.z * this.x;
		double z = this.x * w.y - this.y * w.x;
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public final Vector3d cross(final Vector3d v, final Vector3d w) {
		this.x = v.y * w.z - v.z * w.y;
		this.y = w.x * v.z - w.z * v.x;
		this.z = v.x * w.y - v.y * w.x;
		return this;
	}

	public final double dot(final Vector3d v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public final Vector3d scale(final double s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		return this;
	}

	public final double distanceEuclidean(final Vector3d point) {
		return Math.sqrt(distanceEuclidean2(point));
	}

	public final double distanceEuclidean2(final Vector3d point) {
		double dx = point.x - x;
		double dy = point.y - y;
		double dz = point.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public final double distanceManhattan(final Vector3d point) {
		double dx = point.x - x;
		double dy = point.y - y;
		double dz = point.z - z;
		return Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
	}

	public final Vector3d rotate(final Quaternion rotation) {
		double[] matrix = rotation.getMatrix();
		double newX = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
		double newY = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
		double newZ = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
		x = newX;
		y = newY;
		z = newZ;
		return this;
	}

	public final boolean equals(final Vector v) {
		return v instanceof Vector3d && this.equals((Vector3d) v);
	}

	public final Vector add(final Vector v) {
		if (!(v instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector3d) v);
	}

	public final Vector add(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3d && v2 instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector3d) v1, (Vector3d) v2);
	}

	public final Vector sub(final Vector v) {
		if (!(v instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector3d) v);
	}

	public final Vector sub(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3d && v2 instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector3d) v1, (Vector3d) v2);
	}

	public final double distanceEuclidean(final Vector point) {
		if (!(point instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean((Vector3d) point);
	}

	public final double distanceEuclidean2(final Vector point) {
		if (!(point instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean2((Vector3d) point);
	}

	public final double distanceManhattan(final Vector point) {
		if (!(point instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceManhattan((Vector3d) point);
	}

	public final Vector cross(final Vector v) {
		if (!(v instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.cross((Vector3d) v);
	}

	public final Vector cross(final Vector v, final Vector w) {
		if (!(v instanceof Vector3d && w instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.cross((Vector3d) v, (Vector3d) w);
	}

	public final double dot(final Vector v) {
		if (!(v instanceof Vector3d)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.dot((Vector3d) v);
	}

	public final Vector3d normalize() {
		double norm = (1.0 / Math.sqrt(x * x + y * y + z * z));
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
		return this;
	}

	public final double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public final double length2() {
		return x * x + y * y + z * z;
	}

	@Override
	public final Vector clone() {
		return new Vector3d(this);
	}

	public String toString() {
		return "[x:" + x + ",y:" + y + ",z:" + z + "]";
	}
}
