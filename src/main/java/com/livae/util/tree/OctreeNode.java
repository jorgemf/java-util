package com.livae.util.tree;

import com.livae.util.ResourcesFactory;
import com.livae.util.math.Vector3f;
import com.livae.util.shape.d3.AxisAlignedBoundingCuboid;
import com.livae.util.shape.d3.Frustum;
import com.livae.util.shape.d3.Intersectable;
import com.livae.util.shape.d3.Sphere;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode<k extends Intersectable> extends AxisAlignedBoundingCuboid
 implements Sphere {

	public static final int X_BIT = 0x4;

	public static final int Y_BIT = 0x2;

	public static final int Z_BIT = 0x1;

	private OctreeNode<k>[] nodes;

	private List<k> objects;

	private OctreeNode<k> parent;

	private float radius;

	protected OctreeNode() {
		super(0, 0, 0, 0, 0, 0);
		parent = null;
		//noinspection unchecked
		nodes = (OctreeNode<k>[]) (new OctreeNode[8]);
		objects = new ArrayList<k>();
	}

	protected void init(OctreeNode parent, Vector3f min, Vector3f max) {
		this.parent = parent;
		setMinMaxPoint(min, max);
		objects.clear();
		for (int i = 0;
		     i < nodes.length;
		     i++) {
			nodes[i] = null;
		}
		radius = (float) max.distanceEuclidean(min) / 2;
	}

	public List<k> getObjects() {
		return objects;
	}

	public OctreeNode[] getNodes() {
		return nodes;
	}

	public OctreeNode getParent() {
		return parent;
	}

	public void setParent(OctreeNode node) {
		parent = node;
	}

	public float getRadius() {
		return radius;
	}

	public Vector3f getCentre() {
		return getPosition();
	}

	private boolean contains(Vector3f minZone, Vector3f maxZone, Vector3f minPoint,
	                         Vector3f maxPoint) {
		return minPoint.x >= minZone.x && minPoint.y >= minZone.y && minPoint.z >= minZone.z &&
		       maxPoint.x <= maxZone.x && maxPoint.y <= maxZone.y && maxPoint.z <= maxZone.z;
	}

	protected boolean contains(AxisAlignedBoundingCuboid alignedBox) {
		return contains(getMinPoint(), getMaxPoint(), alignedBox.getMinPoint(),
		                alignedBox.getMaxPoint());
	}

	protected OctreeNode<k> add(k element, ResourcesFactory<OctreeNode<k>> factory) {
		AxisAlignedBoundingCuboid alignedCuboid = element.getAxisAlignedBoundingCuboid();
		Vector3f alignedMinPoint = alignedCuboid.getMinPoint();
		Vector3f alignedMaxPoint = alignedCuboid.getMaxPoint();
		Vector3f centerPoint = getCentre();
		int minPos = (centerPoint.x < alignedMinPoint.x ? X_BIT : 0) |
		             (centerPoint.y < alignedMinPoint.y ? Y_BIT : 0) |
		             (centerPoint.z < alignedMinPoint.z ? Z_BIT : 0);
		int maxPos = (centerPoint.x < alignedMaxPoint.x ? X_BIT : 0) |
		             (centerPoint.y < alignedMaxPoint.y ? Y_BIT : 0) |
		             (centerPoint.z < alignedMaxPoint.z ? Z_BIT : 0);
		if (minPos == maxPos) {
			// inside a child
			if (nodes[minPos] == null) {
				nodes[minPos] = factory.getResource();
				Vector3f minPoint = getMinPoint();
				Vector3f maxPoint = getMaxPoint();
				Vector3f newMinPoint = new Vector3f((X_BIT & minPos) > 0 ? centerPoint.x
				                                                         : minPoint.x,
				                                    (Y_BIT & minPos) > 0 ? centerPoint.y
				                                                         : minPoint.y,
				                                    (Z_BIT & minPos) > 0 ? centerPoint.z
				                                                         : minPoint.z);
				Vector3f newMaxPoint = new Vector3f((X_BIT & minPos) > 0 ? maxPoint.x
				                                                         : centerPoint.x,
				                                    (Y_BIT & minPos) > 0 ? maxPoint.y
				                                                         : centerPoint.y,
				                                    (Z_BIT & minPos) > 0 ? maxPoint.z
				                                                         : centerPoint.z);
				nodes[minPos].init(this, newMinPoint, newMaxPoint);
			}
			return nodes[minPos].add(element, factory);
		} else {
			// inside this node
			objects.add(element);
			return this;
		}
	}

	protected void deleteNode(OctreeNode node) {
		int index = 0;
		while (nodes[index] != node) {
			index++;
		}
		nodes[index] = null;
	}

	protected boolean hasChildren() {
		int index = 0;
		while (index < nodes.length && nodes[index] == null) {
			index++;
		}
		return index < nodes.length;
	}

	public void frustumCulling(Frustum frustum, List<k> elements) {
		Frustum.CONTAINS contains = frustum.contains((Sphere) this);
		if (contains == Frustum.CONTAINS.INTERSECTION) {
			contains = frustum.contains((AxisAlignedBoundingCuboid) this);
		}
		switch (contains) {
			case INTERSECTION:
				for (k intersectable : objects) {
					switch (frustum.contains(intersectable.getAxisAlignedBoundingCuboid())) {
						case INTERSECTION:
						case INSIDE:
							elements.add(intersectable);
						case OUTSIDE:
					}
				}
				for (OctreeNode<k> node : nodes) {
					if (node != null) {
						node.frustumCulling(frustum, elements);
					}
				}
				break;
			case INSIDE:
				cullingAddRecursive(elements);
				break;
			case OUTSIDE:
				// nothing
		}
	}

	private void cullingAddRecursive(List<k> elements) {
		for (k object : objects) {
			elements.add(object);
		}
		for (OctreeNode<k> node : nodes) {
			if (node != null) {
				node.cullingAddRecursive(elements);
			}
		}
	}

	public String toString() {
		String s = "[" + getMinPoint() + ";" + getMaxPoint() + "]: ";
		for (Intersectable intersectable : objects) {
			s += intersectable + ", ";
		}
		return s;
	}

}
