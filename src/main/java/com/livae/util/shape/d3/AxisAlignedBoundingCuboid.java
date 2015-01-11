package com.livae.util.shape.d3;

import com.livae.util.math.Vector3f;

public class AxisAlignedBoundingCuboid {

	private Vector3f position;

	private Vector3f dimensions;

	private Vector3f minPoint;
	private Vector3f maxPoint;

	public AxisAlignedBoundingCuboid(Vector3f dimensions) {
		this(dimensions.x, dimensions.y, dimensions.z, 0, 0, 0);
	}

	public AxisAlignedBoundingCuboid(Vector3f dimensions, Vector3f position) {
		this(dimensions.x, dimensions.y, dimensions.z, position.x, position.y, position.z);
	}

	public AxisAlignedBoundingCuboid(float dimenX, float dimenY, float dimenZ,
	                                 float posX, float posY, float posZ) {
		if (dimenX < 0 || dimenY < 0 || dimenZ < 0) {
			throw new RuntimeException("Dimensions should be positive and greater than 0");
		}
		dimensions = new Vector3f(dimenX, dimenY, dimenZ);
		position = new Vector3f(posX, posY, posZ);
		minPoint = new Vector3f(
				position.x - dimensions.x / 2,
				position.y - dimensions.y / 2,
				position.z - dimensions.z / 2);
		maxPoint = new Vector3f(
				position.x + dimensions.x / 2,
				position.y + dimensions.y / 2,
				position.z + dimensions.z / 2);
	}

	public static AxisAlignedBoundingCuboid computeBoundingCuboid(float[] points) {
		float maxX = points[0];
		float minX = points[0];
		float maxY = points[1];
		float minY = points[1];
		float maxZ = points[2];
		float minZ = points[2];
		int size = points.length / 3;
		for (int i = 1; i < size; i++) {
			if (points[i * 3] < minX) {
				minX = points[i * 3];
			} else if (points[i * 3] > maxX) {
				maxX = points[i * 3];
			}
			if (points[i * 3 + 1] < minY) {
				minY = points[i * 3 + 1];
			} else if (points[i * 3 + 1] > maxY) {
				maxY = points[i * 3 + 1];
			}
			if (points[i * 3 + 2] < minZ) {
				minZ = points[i * 3 + 2];
			} else if (points[i * 3 + 2] > maxZ) {
				maxZ = points[i * 3 + 2];
			}
		}
		float dimenX = maxX - minX;
		float dimenY = maxY - minY;
		float dimenZ = maxZ - minZ;
		float x = (maxX + minX) / 2;
		float y = (maxY + minY) / 2;
		float z = (maxZ + minZ) / 2;
		return new AxisAlignedBoundingCuboid(dimenX, dimenY, dimenZ, x, y, z);
	}

	public void updateBounds(Sphere sphere) {
		position.set(sphere.getCentre());
		float radius = sphere.getRadius();
		dimensions.set(radius, radius, radius);
		minPoint.set(
				position.x - radius / 2,
				position.y - radius / 2,
				position.z - radius / 2);
		maxPoint.set(
				position.x + radius / 2,
				position.y + radius / 2,
				position.z + radius / 2);
	}

	public void updateBounds(Cuboid cuboid) {
		Vector3f[] points = cuboid.getPoints();
		minPoint.set(points[0]);
		maxPoint.set(points[0]);
		int size = points.length;
		for (int i = 1; i < size; i++) {
			if (points[i].x < minPoint.x) {
				minPoint.x = points[i].x;
			} else if (points[i].x > maxPoint.x) {
				maxPoint.x = points[i].x;
			}
			if (points[i].y < minPoint.y) {
				minPoint.y = points[i].y;
			} else if (points[i].y > maxPoint.y) {
				maxPoint.y = points[i].y;
			}
			if (points[i].z < minPoint.z) {
				minPoint.z = points[i].z;
			} else if (points[i].z > maxPoint.z) {
				maxPoint.z = points[i].z;
			}
		}
		setMinMaxPoint(minPoint, maxPoint);

	}

	public void setMinMaxPoint(Vector3f minPoint, Vector3f maxPoint) {
		minPoint.set(minPoint);
		maxPoint.set(maxPoint);
		dimensions.set(
				maxPoint.x - minPoint.x,
				maxPoint.y - minPoint.y,
				maxPoint.z - minPoint.z);
		position.set(
				(maxPoint.x + minPoint.x) / 2,
				(maxPoint.y + minPoint.y) / 2,
				(maxPoint.z + minPoint.z) / 2);

	}

	public Vector3f getPosition() {
		return position;
	}

	public void move(Vector3f movement) {
		move(movement.x, movement.y, movement.z);
	}

	public void move(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;

		minPoint.x += x;
		minPoint.y += y;
		minPoint.z += z;
		maxPoint.x += x;
		maxPoint.y += y;
		maxPoint.z += z;
	}

	public Vector3f getDimensions() {
		return dimensions;
	}

	public Vector3f getMinPoint() {
		return minPoint;
	}

	public Vector3f getMaxPoint() {
		return maxPoint;
	}

}
