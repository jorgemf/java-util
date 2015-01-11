package com.livae.util.shape.d3;

import com.livae.util.math.Vector3f;

public interface Cuboid {

	/**
	 * TODO set the points in the correct order
	 * <p/>
	 * <pre>
	 *        3------7
	 *       /|     /|
	 *      / |    / |
	 *     2--+---6  |
	 *     |  1---+--5
	 *     | /    | /
	 *     |/     |/
	 *     0------4
	 *
	 *     y  z
	 *     | /
	 *     |/
	 *     0---x
	 *
	 *     xyz
	 *
	 * </pre>
	 *
	 * @return
	 */
	public Vector3f[] getPoints();
}
