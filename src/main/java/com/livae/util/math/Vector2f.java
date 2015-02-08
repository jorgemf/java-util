package com.livae.util.math;

/**
 * Vector with 2 floats
 */
public class Vector2f implements Vector {

	public float x;

	public float y;

	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2f v) {
		this.x = v.x;
		this.y = v.y;
	}

	public final boolean equals(final Vector2f other) {
		return this.x == other.x && this.y == other.y;
	}

	public final Vector2f add(final Vector2f other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	public final Vector2f add(final Vector2f v1, final Vector2f v2) {
		this.x = v1.x + v2.x;
		this.y = v1.y + v2.y;
		return this;
	}

	public final Vector2f sub(final Vector2f other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}

	public final Vector2f sub(final Vector2f v1, final Vector2f v2) {
		this.x = v1.x - v2.x;
		this.y = v1.y - v2.y;
		return this;
	}

	public final double distanceEuclidean(final Vector2f point) {
		return (float) Math.sqrt(distanceEuclidean2(point));
	}

	public final double distanceEuclidean2(final Vector2f point) {
		float dx = point.x - x;
		float dy = point.y - y;
		return dx * dx + dy * dy;
	}

	public final double distanceManhattan(final Vector2f point) {
		float dx = point.x - x;
		float dy = point.y - y;
		return Math.abs(dx) + Math.abs(dy);
	}

	public final boolean equals(final Vector v) {
		return v instanceof Vector2f && this.equals((Vector2f) v);
	}

	public final Vector add(final Vector v) {
		if (!(v instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector2f) v);
	}

	public final Vector add(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector2f && v2 instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.add((Vector2f) v1, (Vector2f) v2);
	}

	public final Vector sub(final Vector v) {
		if (!(v instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector2f) v);
	}

	public final Vector sub(final Vector v1, final Vector v2) {
		if (!(v1 instanceof Vector2f && v2 instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.sub((Vector2f) v1, (Vector2f) v2);
	}

	public final double distanceEuclidean(final Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean((Vector2f) point);
	}

	public final double distanceEuclidean2(final Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceEuclidean2((Vector2f) point);
	}

	public final double distanceManhattan(final Vector point) {
		if (!(point instanceof Vector2f)) {
			throw new UnsupportedOperationException("Only " + this.getClass().getName() +
			                                        " vectors supported");
		}
		return this.distanceManhattan((Vector2f) point);
	}

	public final Vector cross(final Vector v) {
		throw new UnsupportedOperationException();
	}

	public final Vector cross(final Vector v, final Vector w) {
		throw new UnsupportedOperationException();
	}

	public final double dot(final Vector v) {
		throw new UnsupportedOperationException();
	}

	public final Vector normalize() {
		return null;
	}

	public final Vector scale(final double value) {
		this.x = (float) (this.x * value);
		this.y = (float) (this.y * value);
		return this;
	}

	public final double length() {
		return Math.sqrt(length2());
	}

	public final double length2() {
		return x * x + y * y;
	}

	@Override
	public final Vector clone() {
		return new Vector2f(this);
	}

	public final String toString() {
		return "[x:" + x + ",y:" + y + "]";
	}
}
