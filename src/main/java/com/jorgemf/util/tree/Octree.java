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

    private static final float DEAFULT_MINIUM_CELL_SIZE = 1f;
    private OctreeNode<k> root;
    private HashMap<k, OctreeNode<k>> elements;
    private ArrayList<k> elementsList;
    private float minimumCellSize;


    public Octree() {
        this(DEAFULT_MINIUM_CELL_SIZE);
    }

    public Octree(float minimumCellSize) {
        super();
        if (minimumCellSize <= 0) {
            throw new RuntimeException("Minimum cell size should be greater than 0");
        }
        root = null;
        elements = new HashMap<k, OctreeNode<k>>();
        elementsList = new ArrayList<k>();
        this.minimumCellSize = minimumCellSize;
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
        Vector3f cubPos = boundingCuboid.getPosition();
        if (root == null) {
            Vector3f cuboidDimensions = boundingCuboid.getDimensions();
            float maxDimHalf = Math.max(cuboidDimensions.x, Math.max(cuboidDimensions.y, cuboidDimensions.z)) / 2;
            float growingRate = minimumCellSize;
            while (maxDimHalf > growingRate) {
                growingRate *= 2;
            }
            root = createResource();
            root.init(null,
                    new Vector3f(cubPos.x - growingRate, cubPos.y - growingRate, cubPos.z - growingRate),
                    new Vector3f(cubPos.x + growingRate, cubPos.y + growingRate, cubPos.z + growingRate));
        } else {
            while (!root.contains(boundingCuboid)) {
                OctreeNode oldRoot = root;
                root = createResource();
                oldRoot.setParent(root);
                Vector3f oldRootDimensions = oldRoot.getDimensions();
                float growingRate = Math.max(oldRootDimensions.x, Math.max(oldRootDimensions.y, oldRootDimensions.z));
                Vector3f oldRootPos = oldRoot.getCentre();
                int numberNode =
                        (cubPos.x < oldRootPos.x ? OctreeNode.X_BIT : 0) |
                                (cubPos.y < oldRootPos.y ? OctreeNode.Y_BIT : 0) |
                                (cubPos.z < oldRootPos.z ? OctreeNode.Z_BIT : 0);
                Vector3f oldMaxPoint = oldRoot.getMaxPoint();
                Vector3f oldMinPoint = oldRoot.getMinPoint();
                Vector3f midPoint = new Vector3f(
                        (numberNode & OctreeNode.X_BIT) > 0 ? oldMaxPoint.x : oldMinPoint.x,
                        (numberNode & OctreeNode.Y_BIT) > 0 ? oldMaxPoint.y : oldMinPoint.y,
                        (numberNode & OctreeNode.Z_BIT) > 0 ? oldMaxPoint.z : oldMinPoint.z);
                root.init(null,
                        new Vector3f(midPoint.x - growingRate, midPoint.y - growingRate, midPoint.z - growingRate),
                        new Vector3f(midPoint.x + growingRate, midPoint.y + growingRate, midPoint.z + growingRate));
                root.getNodes()[numberNode] = oldRoot;
            }
        }
    }

    public synchronized void remove(k intersectable) {
        elementsList.remove(intersectable);
        OctreeNode<k> node = elements.remove(intersectable);
        if (node != null) {
            node.getObjects().remove(intersectable);
            clean(node);
        }
    }

    private void pruneRoot() {
        OctreeNode<k>[] nodes;
        OctreeNode<k> nodeNotNull;
        boolean prune = root != null && root.getObjects().size() == 0;
        int nodesNotNullNumber = 0;
        while (prune) {
            nodes = root.getNodes();
            nodeNotNull = null;
            nodesNotNullNumber = 0;
            for (OctreeNode<k> node : nodes) {
                if (node != null) {
                    nodeNotNull = node;
                    nodesNotNullNumber++;
                }
            }
            if (nodesNotNullNumber <= 1 && root.getObjects().size() == 0) {
                releaseResource(root);
                if (nodeNotNull == null) {
                    root = null;
                } else {
                    root = nodeNotNull;
                }
            } else {
                prune = false;
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
            } else if (node == root) {
                root = null;
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
    protected OctreeNode<k> createResource() {
        return new OctreeNode();
    }

    public void clear() {
        elementsList.clear();
        if (root != null) {
            clear(root);
            root = null;
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
