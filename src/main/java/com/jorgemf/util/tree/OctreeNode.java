package com.jorgemf.util.tree;

import com.jorgemf.util.math.Vector3f;
import com.jorgemf.util.shape.d3.AxisAlignedBoundingCuboid;
import com.jorgemf.util.shape.d3.Frustum;
import com.jorgemf.util.shape.d3.Intersectable;
import com.jorgemf.util.shape.d3.Sphere;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode<k extends Intersectable> {

    private static float NODE_TOLERANCE = 0.0000001f;

    private Vector3f maxPosition;

    private Vector3f midPosition;

    private Vector3f minPosition;

    private Vector3f dimension;

    private OctreeNode<k>[] nodes;

    private List<k> objects;

    private Vector3f[] nodesMax;

    private Vector3f[] nodesMin;

    private OctreeNode<k> parent;

    private Vector3f[] points;

    private float radius;

    private float radiusSquared;

    private float maxDimensionSquared;

    public OctreeNode() {
        this.parent = null;
        this.minPosition = new Vector3f();
        this.maxPosition = new Vector3f();
        //noinspection unchecked
        this.nodes = (OctreeNode<k>[]) (new OctreeNode[8]);
        this.midPosition = new Vector3f();
        this.dimension = new Vector3f();
        this.objects = new ArrayList<k>();
        this.nodesMax = new Vector3f[8];
        this.nodesMin = new Vector3f[8];
        int size = this.nodesMin.length;
        for (int i = 0; i < size; i++) {
            this.nodesMax[i] = new Vector3f();
            this.nodesMin[i] = new Vector3f();
        }
        this.points = new Vector3f[8];
        size = this.points.length;
        for (int i = 0; i < size; i++) {
            this.points[i] = new Vector3f();
        }
    }

    public void init(OctreeNode parent, Vector3f min, Vector3f max) {
        this.parent = parent;
        this.minPosition.set(min);
        this.maxPosition.set(max);
        this.midPosition.set((max.x + min.x) / 2, (max.y + min.y) / 2, (max.z + min.z) / 2);
        this.dimension.set(max.x - min.x, max.y - min.y, max.z - min.z);
        this.maxDimensionSquared = Math.max(this.dimension.x, Math.max(this.dimension.y, this.dimension.z));
        this.maxDimensionSquared = this.maxDimensionSquared * this.maxDimensionSquared;
        this.objects.clear();

        this.nodesMin[0].x = minPosition.x;
        this.nodesMin[0].y = minPosition.y;
        this.nodesMin[0].z = minPosition.z;
        this.nodesMax[0].x = midPosition.x;
        this.nodesMax[0].y = midPosition.y;
        this.nodesMax[0].z = midPosition.z;

        this.nodesMin[1].x = midPosition.x;
        this.nodesMin[1].y = minPosition.y;
        this.nodesMin[1].z = minPosition.z;
        this.nodesMax[1].x = maxPosition.x;
        this.nodesMax[1].y = midPosition.y;
        this.nodesMax[1].z = midPosition.z;

        this.nodesMin[2].x = minPosition.x;
        this.nodesMin[2].y = minPosition.y;
        this.nodesMin[2].z = midPosition.z;
        this.nodesMax[2].x = midPosition.x;
        this.nodesMax[2].y = midPosition.y;
        this.nodesMax[2].z = maxPosition.z;

        this.nodesMin[3].x = midPosition.x;
        this.nodesMin[3].y = minPosition.y;
        this.nodesMin[3].z = midPosition.z;
        this.nodesMax[3].x = maxPosition.x;
        this.nodesMax[3].y = midPosition.y;
        this.nodesMax[3].z = maxPosition.z;

        this.nodesMin[4].x = minPosition.x;
        this.nodesMin[4].y = midPosition.y;
        this.nodesMin[4].z = minPosition.z;
        this.nodesMax[4].x = midPosition.x;
        this.nodesMax[4].y = maxPosition.y;
        this.nodesMax[4].z = midPosition.z;

        this.nodesMin[5].x = midPosition.x;
        this.nodesMin[5].y = midPosition.y;
        this.nodesMin[5].z = minPosition.z;
        this.nodesMax[5].x = maxPosition.x;
        this.nodesMax[5].y = maxPosition.y;
        this.nodesMax[5].z = midPosition.z;

        this.nodesMin[6].x = minPosition.x;
        this.nodesMin[6].y = midPosition.y;
        this.nodesMin[6].z = midPosition.z;
        this.nodesMax[6].x = midPosition.x;
        this.nodesMax[6].y = maxPosition.y;
        this.nodesMax[6].z = maxPosition.z;

        this.nodesMin[7].x = midPosition.x;
        this.nodesMin[7].y = midPosition.y;
        this.nodesMin[7].z = midPosition.z;
        this.nodesMax[7].x = maxPosition.x;
        this.nodesMax[7].y = maxPosition.y;
        this.nodesMax[7].z = maxPosition.z;

        this.points[0].set(max.x, max.y, max.z);
        this.points[1].set(max.x, max.y, min.z);
        this.points[2].set(max.x, min.y, max.z);
        this.points[3].set(max.x, min.y, min.z);
        this.points[4].set(min.x, max.y, max.z);
        this.points[5].set(min.x, max.y, min.z);
        this.points[6].set(min.x, min.y, max.z);
        this.points[7].set(min.x, min.y, min.z);

        this.radius = min.distanceEuclidean(max) / 2;
        this.radiusSquared = this.radius * this.radius;
    }

    public List<k> getObjects() {
        return this.objects;
    }

    public Vector3f getMaxPosition() {
        return this.maxPosition;
    }

    public Vector3f getMidPosition() {
        return this.midPosition;
    }

    public Vector3f getMinPosition() {
        return this.minPosition;
    }

    public OctreeNode[] getNodes() {
        return this.nodes;
    }

    public OctreeNode getParent() {
        return this.parent;
    }

    public void setParent(OctreeNode node) {
        this.parent = node;
    }

    public Vector3f[] getPoints() {
        return this.points;
    }

    public float getRadius() {
        return this.radius;
    }

    protected int contains(AxisAlignedBoundingCuboid alignedBox) {
        Vector3f minPoint = alignedBox.getMinPoint();
        Vector3f maxPoint = alignedBox.getMaxPoint();
        int contains = 0;
        while (contains < this.nodes.length && !contains(this.nodesMin[contains], this.nodesMax[contains], minPoint, maxPoint)) {
            contains++;
        }
        return contains;
    }

    private boolean contains(Vector3f minZone, Vector3f maxZone, Vector3f minPoint, Vector3f maxPoint) {
        return minPoint.x >= minZone.x && minPoint.y >= minZone.y && minPoint.z >= minZone.z && maxPoint.x <= maxZone.x && maxPoint.y <= maxZone.y && maxPoint.z <= maxZone.z;
    }

    protected boolean inside(AxisAlignedBoundingCuboid alignedBox) {
        return contains(this.minPosition, this.maxPosition, alignedBox.getMinPoint(), alignedBox.getMaxPoint());
    }

    protected void createNode(int nodePos, OctreeNode node) {
        node.init(this, this.nodesMin[nodePos], this.nodesMax[nodePos]);
        this.nodes[nodePos] = node;
    }

    protected void deleteNode(OctreeNode node) {
        int index = 0;
        while (this.nodes[index] != node) {
            index++;
        }
        this.nodes[index] = null;
    }

    protected OctreeNode getNode(int node) {
        return this.nodes[node];
    }

    protected void setNode(OctreeNode node) {
        int nodeIndex = 0;
        while (Math.abs(node.maxPosition.x - this.nodesMax[nodeIndex].x) >= NODE_TOLERANCE ||
                Math.abs(node.maxPosition.y - this.nodesMax[nodeIndex].y) >= NODE_TOLERANCE ||
                Math.abs(node.maxPosition.z - this.nodesMax[nodeIndex].z) >= NODE_TOLERANCE ||
                Math.abs(node.minPosition.x - this.nodesMin[nodeIndex].x) >= NODE_TOLERANCE ||
                Math.abs(node.minPosition.y - this.nodesMin[nodeIndex].y) >= NODE_TOLERANCE ||
                Math.abs(node.minPosition.z - this.nodesMin[nodeIndex].z) >= NODE_TOLERANCE) {
            nodeIndex++;
        }
        if (this.nodes[nodeIndex] != null) {
            throw new RuntimeException("The node does not suit in this node");
        }
        this.nodes[nodeIndex] = node;
    }

    protected boolean hasChildrend() {
        int index = 0;
        while (index < this.nodes.length && this.nodes[index] == null) {
            index++;
        }
        return index < this.nodes.length;
    }

    public void frustumCulling(Frustum frustum, List<Intersectable> elements) {
        Frustum.CONTAINS contains = frustum.contains(this);
        int size;
        switch (contains) {
            case INTERSECTION:
                Intersectable intersectable;
                size = this.objects.size();
                for (int i = 0; i < size; i++) {
                    intersectable = this.objects.get(i);
                    switch (frustum.contains(intersectable.getAxisAlignedBoundingCuboid())) {
                        case INTERSECTION:
                        case INSIDE:
                            elements.add(intersectable);
                        case OUTSIDE:
                    }
                }
                OctreeNode node;
                size = this.nodes.length;
                for (int i = 0; i < size; i++) {
                    node = this.nodes[i];
                    if (node != null) {
                        node.frustumCulling(frustum, elements);
                    }
                }
                break;
            case INSIDE:
                cullingAddRecursive(elements);
                break;
            case OUTSIDE:
        }
    }

    public void sphereCulling(Vector3f center, float radiusSquared, List<Sphere> elements) {
        float diffx = center.x - midPosition.x;
        float diffy = center.y - midPosition.y;
        float diffz = center.z - midPosition.z;
        float distanceSquaredCenters = diffx * diffx + diffy * diffy + diffz * diffz;
        if (distanceSquaredCenters < radiusSquared || distanceSquaredCenters < this.maxDimensionSquared) {
            if (distanceSquaredCenters + this.radiusSquared < radiusSquared) {
                // INSIDE
                cullingAddRecursive(elements);
            } else {
                // INTERSECTION
                int size = this.objects.size();
                Sphere sphere;
                Vector3f position;
                for (int i = 0; i < size; i++) {
                    sphere = this.objects.get(i);
                    position = sphere.getCentre();
                    diffx = center.x - position.x;
                    diffy = center.y - position.y;
                    diffz = center.z - position.z;
                    distanceSquaredCenters = diffx * diffx * diffy * diffy + diffz * diffz;
                    if (distanceSquaredCenters < radiusSquared) {
                        elements.add(sphere);
                    }
                }
                size = this.nodes.length;
                OctreeNode node;
                for (int i = 0; i < size; i++) {
                    node = this.nodes[i];
                    if (node != null) {
                        node.sphereCulling(center, radiusSquared, elements);
                    }
                }
            }
        }
    }

    private void cullingAddRecursive(List<Intersectable> elements) {
        int size = this.objects.size();
        for (int i = 0; i < size; i++) {
            elements.add(this.objects.get(i));
        }
        size = this.nodes.length;
        for (int i = 0; i < size; i++) {
            if (this.nodes[i] != null) {
                this.nodes[i].cullingAddRecursive(elements);
            }
        }
    }

    public String toString() {
        String s = "[" + this.minPosition + ";" + this.maxPosition + "]: ";
        int size = this.objects.size();
        for (Intersectable intersectable : this.objects) {
            s += intersectable + ", ";
        }
        return s;
    }

}
