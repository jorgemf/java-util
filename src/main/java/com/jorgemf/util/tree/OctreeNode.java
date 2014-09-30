package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;
import com.jorgemf.util.math.Vector3f;
import com.jorgemf.util.shape.d3.AxisAlignedBoundingCuboid;
import com.jorgemf.util.shape.d3.Frustum;
import com.jorgemf.util.shape.d3.Intersectable;
import com.jorgemf.util.shape.d3.Sphere;

import java.util.ArrayList;
import java.util.List;

public class OctreeNode<k extends Intersectable> extends AxisAlignedBoundingCuboid implements Sphere {

    private static final int X_BIT = 0x4;
    private static final int Y_BIT = 0x2;
    private static final int Z_BIT = 0x1;
    private OctreeNode<k>[] nodes;
    private List<k> objects;
    private OctreeNode<k> parent;
    private float radius;

    protected OctreeNode() {
        super(0, 0, 0, 0, 0, 0);
        this.parent = null;
        //noinspection unchecked
        this.nodes = (OctreeNode<k>[]) (new OctreeNode[8]);
        this.objects = new ArrayList<k>();
    }

    protected void init(OctreeNode parent, Vector3f min, Vector3f max) {
        this.parent = parent;
        setMinMaxPoint(min, max);
        Vector3f dimension = getDimensions();
        this.objects.clear();
    }

    public List<k> getObjects() {
        return this.objects;
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

    @Override
    public float getRadius() {
        return this.radius;
    }

    @Override
    public Vector3f getCentre() {
        return getPosition();
    }

    private boolean contains(Vector3f minZone, Vector3f maxZone, Vector3f minPoint, Vector3f maxPoint) {
        return minPoint.x >= minZone.x && minPoint.y >= minZone.y && minPoint.z >= minZone.z && maxPoint.x <= maxZone.x && maxPoint.y <= maxZone.y && maxPoint.z <= maxZone.z;
    }

    protected boolean contains(AxisAlignedBoundingCuboid alignedBox) {
        return contains(getMinPoint(), getMaxPoint(), alignedBox.getMinPoint(), alignedBox.getMaxPoint());
    }

    protected OctreeNode<k> add(k element, ResourcesFactory<OctreeNode<k>> factory) {
        AxisAlignedBoundingCuboid alignedCuboid = element.getAxisAlignedBoundingCuboid();
        Vector3f alignedMinPoint = alignedCuboid.getMinPoint();
        Vector3f alignedMaxPoint = alignedCuboid.getMaxPoint();
        Vector3f centerPoint = getCentre();
        int minPos = 0;
        if (centerPoint.x < alignedMinPoint.x) {
            minPos |= X_BIT;
        }
        if (centerPoint.y < alignedMinPoint.y) {
            minPos |= Y_BIT;
        }
        if (centerPoint.z < alignedMinPoint.z) {
            minPos |= Z_BIT;
        }
        int maxPos = 0;
        if (centerPoint.x < alignedMaxPoint.x) {
            maxPos |= X_BIT;
        }
        if (centerPoint.y < alignedMaxPoint.y) {
            maxPos |= Y_BIT;
        }
        if (centerPoint.z < alignedMaxPoint.z) {
            maxPos |= Z_BIT;
        }
        if (minPos == maxPos) {
            // inside a child
            if (nodes[minPos] == null) {
                nodes[minPos] = factory.getResource();
                Vector3f minPoint = getMinPoint();
                Vector3f maxPoint = getMaxPoint();
                Vector3f newMinPoint = new Vector3f(
                        (X_BIT & minPos) > 0 ? centerPoint.x : minPoint.x,
                        (Y_BIT & minPos) > 0 ? centerPoint.y : minPoint.y,
                        (Z_BIT & minPos) > 0 ? centerPoint.z : minPoint.z);
                Vector3f newMaxPoint = new Vector3f(
                        (X_BIT & minPos) > 0 ? maxPoint.x : centerPoint.x,
                        (Y_BIT & minPos) > 0 ? maxPoint.y : centerPoint.y,
                        (Z_BIT & minPos) > 0 ? maxPoint.z : centerPoint.z);
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
        while (this.nodes[index] != node) {
            index++;
        }
        this.nodes[index] = null;
    }

    protected boolean hasChildren() {
        int index = 0;
        while (index < this.nodes.length && this.nodes[index] == null) {
            index++;
        }
        return index < this.nodes.length;
    }

    public void frustumCulling(Frustum frustum, List<k> elements) {
        Frustum.CONTAINS contains = frustum.contains(this);
        int size;
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
        }
    }

    private void cullingAddRecursive(List<k> elements) {
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
        String s = "[" + getMinPoint() + ";" + getMaxPoint() + "]: ";
        int size = this.objects.size();
        for (Intersectable intersectable : this.objects) {
            s += intersectable + ", ";
        }
        return s;
    }

}
