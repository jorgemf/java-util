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

    private OctreeNode<k> root;

    private HashMap<k, OctreeNode<k>> elements;

    private ArrayList<k> elementsList;

    public Octree() {
        super();
        root = null;
        elements = new HashMap<k, OctreeNode<k>>();
        elementsList = new ArrayList<k>();
    }

    public void add(k element) {
        AxisAlignedBoundingCuboid boundingCuboid = element.getAxisAlignedBoundingCuboid();
        if (root == null || !root.contains(boundingCuboid)) {
            growRoot(boundingCuboid);
        }
        root.add(element, this);
        elementsList.add(element);
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

    public synchronized void remove(k intersectable) {
        this.elementsList.remove(intersectable);
        OctreeNode<k> node = this.elements.remove(intersectable);
        if (node != null) {
            node.getObjects().remove(intersectable);
            clean(node);
        }
    }

    private void pruneRoot() {
        OctreeNode<k>[] nodes;
        OctreeNode<k> nodeNotNull;
        boolean prune = root != null && root.getObjects().size() > 0;
        while (prune) {
            nodes = root.getNodes();
            nodeNotNull = null;
            for (OctreeNode<k> node : nodes) {
                if (node != null) {
                    if (nodeNotNull == null) {
                        nodeNotNull = node;
                    } else {
                        prune = false;
                    }
                }
            }
            prune &= root.getObjects().size() > 0;
            if (prune) {
                root.deleteNode(nodeNotNull);
                releaseResource(root);
                if (nodeNotNull == null) {
                    root = null;
                } else {
                    root = nodeNotNull;
                    pruneRoot();
                }
            }
        }
    }

    private void clean(OctreeNode node) {
        if (node.getObjects().size() == 0 && !node.hasChildren()) {
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

    public void movedObject(k intersectable) {
        OctreeNode<k> node = elements.get(intersectable);
        AxisAlignedBoundingCuboid alignedBox = intersectable.getAxisAlignedBoundingCuboid();
        if (node != null) {
            remove(intersectable);
            if (!node.contains(alignedBox)) {
                add(intersectable);
            } else {
                node.add(intersectable, this);
            }
        }
    }

    public void frustumCulling(Frustum frustum, List<k> elements) {
        if (root != null) {
            root.frustumCulling(frustum, elements);
        }
    }

    public String toString() {
        if (root != null) {
            return toString(root, "");
        } else {
            return super.toString();
        }
    }

    public String toString(OctreeNode node, String tab) {
        String s = tab + node.toString() + "\n";
        for (OctreeNode auxNode : node.getNodes()) {
            if (auxNode != null) {
                s += toString(auxNode, tab + "\t");
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
