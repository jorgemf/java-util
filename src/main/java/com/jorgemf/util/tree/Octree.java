package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;
import com.jorgemf.util.math.Vector3f;
import com.jorgemf.util.shape.d3.AxisAlignedBoundingCuboid;
import com.jorgemf.util.shape.d3.Frustum;
import com.jorgemf.util.shape.d3.Intersectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Octree<k extends Intersectable> extends ResourcesFactory<OctreeNode<k>> {

    private OctreeNode root;

    private HashMap<k, OctreeNode> elements;

    private ArrayList<k> elementsList;

    private Vector3f minPositionAux;

    private Vector3f maxPositionAux;

    private Vector3f midPositionAux;

    public Octree() {
        super();
        this.root = null;
        this.elements = new HashMap<k, OctreeNode>();
        this.minPositionAux = new Vector3f();
        this.maxPositionAux = new Vector3f();
        this.midPositionAux = new Vector3f();
        this.elementsList = new ArrayList<k>();
    }

    public synchronized void add(Intersectable element) {
        AxisAlignedBoundingCuboid boundingCuboid = element.getAxisAlignedBoundingCuboid();
        if (this.root == null || !this.root.inside(boundingCuboid)) {
            growRoot(boundingCuboid);
        }
        add(this.root, element, boundingCuboid);
        this.elementsList.add(element);
    }

    private void growRoot(AxisAlignedBoundingCuboid boundingCuboid) {
        Vector3f position = boundingCuboid.getPosition();
        Vector3f cubeMaxPosition = boundingCuboid.getMaxPoint();
        Vector3f cubeMinPosition = boundingCuboid.getMinPoint();
        float growingRootGrade = 0f;
        OctreeNode oldRoot = this.root;
        if (this.root != null) {
            this.minPositionAux.set(this.root.getMinPosition());
            this.maxPositionAux.set(this.root.getMaxPosition());
            this.midPositionAux.set(this.root.getMidPosition());
            growingRootGrade = this.maxPositionAux.x - this.minPositionAux.x;
            this.root = null;
        } else {
            growingRootGrade = 1f;
            this.midPositionAux.x = (float) Math.floor(position.x) + 0.5f;
            this.midPositionAux.y = (float) Math.floor(position.y) + 0.5f;
            this.midPositionAux.z = (float) Math.floor(position.z) + 0.5f;
            this.minPositionAux.set(this.midPositionAux.x - 0.5f, this.midPositionAux.y - 0.5f, this.midPositionAux.z - 0.5f);
            this.maxPositionAux.set(this.midPositionAux.x + 0.5f, this.midPositionAux.y + 0.5f, this.midPositionAux.z + 0.5f);
        }
        OctreeNode node = null;
        while (this.root == null) {
            boolean growX = true;
            boolean growY = true;
            boolean growZ = true;
            if (this.maxPositionAux.x < cubeMaxPosition.x) {
                growX = true;
            } else if (this.minPositionAux.x > cubeMinPosition.x) {
                growX = false;
            }
            if (this.maxPositionAux.y < cubeMaxPosition.y) {
                growY = true;
            } else if (this.minPositionAux.y > cubeMinPosition.y) {
                growY = false;
            }
            if (this.maxPositionAux.z < cubeMaxPosition.z) {
                growZ = true;
            } else if (this.minPositionAux.z > cubeMinPosition.z) {
                growZ = false;
            }
            if (growX) {
                this.maxPositionAux.x += growingRootGrade;
            } else {
                this.minPositionAux.x -= growingRootGrade;
            }
            if (growY) {
                this.maxPositionAux.y += growingRootGrade;
            } else {
                this.minPositionAux.y -= growingRootGrade;
            }
            if (growZ) {
                this.maxPositionAux.z += growingRootGrade;
            } else {
                this.minPositionAux.z -= growingRootGrade;
            }
            node = getResource();
            node.init(null, this.minPositionAux, this.maxPositionAux);
            if (node.inside(boundingCuboid)) {
                this.root = node;
            }
            if (oldRoot != null) {
                node.setNode(oldRoot);
                oldRoot.setParent(node);
                oldRoot = node;
            }
            growingRootGrade *= 2;
        }
    }

    private void add(OctreeNode node, Intersectable intersectable, AxisAlignedBoundingCuboid boundingCuboid) {
        int contains = node.contains(boundingCuboid);
        if (contains == 8) {
            node.getObjects().add(intersectable);
            elements.put(intersectable, node);
        } else if (contains >= 0) {
            OctreeNode nodeAux = node.getNode(contains);
            if (nodeAux == null) {
                node.createNode(contains, getResource());
                nodeAux = node.getNode(contains);
            }
            add(nodeAux, intersectable, boundingCuboid);
        } else {
            throw new RuntimeException("Object out of the octree cube");
        }
    }

    public synchronized void remove(Intersectable intersectable) {
        this.elementsList.remove(intersectable);
        OctreeNode node = this.elements.remove(intersectable);
        if (node != null) {
            node.getObjects().remove(intersectable);
            clean(node);
        }
    }

    private void pruneRoot() {
        OctreeNode[] nodes;
        OctreeNode nodeNotNull;
        boolean prune = this.root != null && this.root.getObjects().size() > 0;
        while (prune) {
            nodes = this.root.getNodes();
            nodeNotNull = null;
            int size = nodes.length;
            for (int i = 0; i < size; i++) {
                OctreeNode node = nodes[i];
                if (node != null) {
                    if (nodeNotNull == null) {
                        nodeNotNull = node;
                    } else {
                        prune = false;
                    }
                }
            }
            prune &= this.root.getObjects().size() > 0;
            if (prune) {
                this.root.deleteNode(nodeNotNull);
                releaseResource(this.root);
                if (nodeNotNull == null) {
                    this.root = null;
                } else {
                    this.root = nodeNotNull;
                    pruneRoot();
                }
            }
        }
    }

    private void clean(OctreeNode node) {
        if (node.getObjects().size() == 0 && !node.hasChilds()) {
            OctreeNode parent = node.getParent();
            if (parent != null) {
                parent.deleteNode(node);
                releaseResource(node);
                clean(parent);
            } else {
                pruneRoot();
            }
        }
    }

    public void movedObject(Drawable3D drawable) {
        OctreeNode node = this.elements.get(drawable);
        AxisAlignedBoundingCuboid alignedBox = drawable.getAxisAlignedBoundingCuboid();
        if (node != null) {
            if (!node.inside(alignedBox)) {
                remove(drawable);
                add(drawable);
            } else {
                int contains = node.contains(alignedBox);
                if (contains != 8) {
                    node.getObjects().remove(drawable);
                    OctreeNode nodeAux = node.getNode(contains);
                    if (nodeAux == null) {
                        node.createNode(contains, getResource());
                        nodeAux = node.getNode(contains);
                    }
                    add(nodeAux, drawable, alignedBox);
                }
            }
        }
    }

    public void frustumCulling(Frustum frustum, List<Drawable3D> elements) {
        if (this.root != null) {
            this.root.frustumCulling(frustum, elements);
        }
    }

    public void sphereCulling(Vector3f center, float radius, List<Drawable3D> elements) {
        if (this.root != null) {
            this.root.sphereCulling(center, radius * radius, elements);
        }
    }

    public String toString() {
        if (this.root != null) {
            return toString(this.root, "");
        } else {
            return super.toString();
        }
    }

    public String toString(OctreeNode node, String tab) {
        String s = tab + node.toString() + "\n";
        OctreeNode[] nodes = node.getNodes();
        int size = nodes.length;
        for (int i = 0; i < size; i++) {
            if (nodes[i] != null) {
                s += toString(nodes[i], tab + "\t");
            }
        }
        return s;
    }

    @Override
    protected OctreeNode createResource() {
        return new OctreeNode();
    }

    public void clear() {
        this.elementsList.clear();
        if (this.root != null) {
            clear(this.root);
            this.root = null;
        }
    }

    public void clear(OctreeNode node) {
        OctreeNode[] nodes = node.getNodes();
        int size = nodes.length;
        for (int i = 0; i < size; i++) {
            if (nodes[i] != null) {
                clear(nodes[i]);
                nodes[i] = null;
            }
        }
        releaseResource(node);
    }

}
