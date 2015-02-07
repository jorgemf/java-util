package com.livae.util.math;

/**
 * Vector with 3 floats
 */
public class Vector3f implements Vector {

	public float x;

	public float y;

	public float z;

	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vector3f(final float[] coordinates) {
		this.x = coordinates[0];
		this.y = coordinates[1];
		this.z = coordinates[2];
	}

	public Vector3f(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(final Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public final void set(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final boolean equals(final Vector3f other) {
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public final Vector3f add(final Vector3f v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public final Vector3f add(final Vector3f v, final Vector3f w) {
		this.x = v.x + w.x;
		this.y = v.y + w.y;
		this.z = v.z + w.z;
		return this;
	}

	public final Vector3f sub(final Vector3f v, final Vector3f w) {
		this.x = v.x - w.x;
		this.y = v.y - w.y;
		this.z = v.z - w.z;
		return this;
	}

	public final Vector3f sub(final Vector3f v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public final Vector3f cross(final Vector3f w) {
		float x = this.y * w.z - this.z * w.y;
		float y = w.x * this.z - w.z * this.x;
		float z = this.x * w.y - this.y * w.x;
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public final Vector3f cross(final Vector3f v, final Vector3f w) {
		this.x = v.y * w.z - v.z * w.y;
		this.y = w.x * v.z - w.z * v.x;
		this.z = v.x * w.y - v.y * w.x;
		return this;
	}

	public final double dot(final Vector3f v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public final Vector3f normalize() {
		float norm = (1.0f / (float) Math.sqrt(x * x + y * y + z * z));
		this.x *= norm;
		this.y *= norm;
		this.z *= norm;
		return this;
	}

	public final Vector3f scale(final float s) {
		this.x *= s;
		this.y *= s;
		this.z *= s;
		return this;
	}

	public final Vector scale(final double value) {
		this.x = (float) (this.x * value);
		this.y = (float) (this.y * value);
		return this;
	}

	public final double distanceEuclidean(final Vector3f point) {
		return Math.sqrt(distanceEuclidean2(point));
	}

	public final double distanceEuclidean2(final Vector3f point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public final double distanceManhattan(final Vector3f point) {
		float dx = point.x - x;
		float dy = point.y - y;
		float dz = point.z - z;
		return Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
	}

	public String toString() {
		return "[x:" + x + ",y:" + y + ",z:" + z + "]";
	}

	public final double length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public final double length2() {
		return x * x + y * y + z * z;
	}

	public final Vector3f rotate(final Quaternion rotation) {
		float[] matrix = rotation.getMatrix();
		float newX = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
		float newY = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
		float newZ = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
		x = newX;
		y = newY;
		z = newZ;
		return this;
	}

	public final boolean equals(final Vector v) {
		return v instanceof Vector3f && this.equals((Vector3f) v);
	}

	public final Vector add(final Vector v) {
		if (!(v instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.add((Vector3f) v);
	}

	public final Vector add(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3f && v2 instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.add((Vector3f) v1, (Vector3f) v2);
	}

	public final Vector sub(final Vector v) {
		if (!(v instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.sub((Vector3f) v);
	}

	public final Vector sub(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector3f && v2 instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.sub((Vector3f) v1, (Vector3f) v2);
	}

	public final double distanceEuclidean(final Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceEuclidean((Vector3f) point);
	}

	public final double distanceEuclidean2(final Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceEuclidean2((Vector3f) point);
	}

	public final double distanceManhattan(final Vector point) {
		if (!(point instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.distanceManhattan((Vector3f) point);
	}

	public final Vector cross(final Vector v) {
		if (!(v instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.cross((Vector3f) v);
	}

	public final Vector cross(final Vector v, final Vector w) {
		if (!(v instanceof Vector3f && w instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.cross((Vector3f) v, (Vector3f) w);
	}

	public final double dot(final Vector v) {
		if (!(v instanceof Vector3f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
					" vectors supported");
		}
		return this.dot((Vector3f) v);
	}

	@Override
	public final Vector clone() {
		return new Vector3f(this);
	}
}
