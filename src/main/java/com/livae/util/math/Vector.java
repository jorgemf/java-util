package com.livae.util.math;

/**
 * Common interface for vectors
 */
public interface Vector {

	/**
	 * Check if this vector and another are equals
	 *
	 * @param v The other Vector
	 * @return true if both vectors are the same, false otherwise
	 */
	public boolean equals(Vector v);

	/**
	 * Add another vector to this
	 *
	 * @param v The other vector
	 * @return This vector
	 */
	public Vector add(Vector v);

	/**
	 * Add two vectors and put the results in this vector
	 *
	 * @param v1 First vector to add
	 * @param v2 Second vector to add
	 * @return This vector with the results
	 */
	public Vector add(Vector v1, Vector v2);

	/**
	 * Subtracts vector v to this vector
	 *
	 * @param v The other vector
	 * @return This vector
	 */
	public Vector sub(Vector v);

	/**
	 * Subtracts v1 minus v2 and set the value in this vector
	 *
	 * @param v1 First vector
	 * @param v2 Second vector
	 * @return This vector with the results
	 */
	public Vector sub(Vector v1, Vector v2);

	/**
	 * Returns the distance between this vector and another one, considering both as points in the
	 * space
	 *
	 * @param point Point in the space
	 * @return Distance between both points
	 */
	public double distanceEuclidean(Vector point);

	/**
	 * Same as Vector#distanceEuclidean but does not make the square root.
	 *
	 * @param point Point in the space
	 * @return Distance squared between both points
	 */
	public double distanceEuclidean2(Vector point);

	/**
	 * Calculate the manhattan distance between two points
	 *
	 * @param point The other poin in the space
	 * @return The manhattan distance
	 */
	public double distanceManhattan(Vector point);

	/**
	 * Calculate the cross product between this vector and another
	 *
	 * @param v The other vector
	 * @return This vector with the result of the cross product.
	 */
	public Vector cross(Vector v);

	/**
	 * Calculate the cross product between two vectors and store the result in this one.
	 *
	 * @param v First vector of the cross product
	 * @param w Second vector of the cross product
	 * @return This vector with the result
	 */
	public Vector cross(Vector v, Vector w);

	/**
	 * Calculate the dot product between this vector and another
	 *
	 * @param v The other vector
	 * @return The dot product
	 */
	public double dot(Vector v);

	/**
	 * Normalize this vector to return an unit vector
	 *
	 * @return This vector normalized
	 */
	public Vector normalize();

	/**
	 * Scales this vector given a value, that is, every element of the vector is multiply by a
	 * constant
	 *
	 * @param value Constant to scale the vector
	 * @return This vector with the result
	 */
	public Vector scale(double value);

	/**
	 * Calculate the length of the vector
	 *
	 * @return The length of the vector
	 */
	public double length();

	/**
	 * As Vector#length but it does not make the sqaure root
	 *
	 * @return The length squared of the vector
	 */
	public double length2();

	/**
	 * Clones this vector
	 *
	 * @return A copy of this vector
	 */
	public Vector clone();

}
