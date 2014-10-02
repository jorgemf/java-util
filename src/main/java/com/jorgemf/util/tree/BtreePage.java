package com.jorgemf.util.tree;

public class BtreePage<k extends Comparable<k>> {

    private BtreePage<k> parentPage;

    private BtreePage<k>[] offspringPages;

    private k[] nodes;

    private int size;

    private Btree<k> btree;

    protected BtreePage(int numberOfNodes, Btree<k> btree) {
        size = 0;
        //noinspection unchecked
        nodes = (k[]) (new Object[numberOfNodes]);
        offspringPages = new BtreePage[numberOfNodes + 1];
        this.btree = btree;
    }

    public void visitInOrder(BtreeVisitor<k> visitor, int deep) {
        for (int i = 0; i < size; i++) {
            if (offspringPages[i] != null) {
                offspringPages[i].visitInOrder(visitor, deep + 1);
            }
            visitor.visit(nodes[i], deep);
        }
        if (offspringPages[size] != null) {
            offspringPages[size].visitInOrder(visitor, deep + 1);
        }
    }

    private boolean isFull() {
        return size == nodes.length;
    }

    private boolean isLeave() {
        return offspringPages[0] == null;
    }

    public k getFirst() {
        k object;
        if (isLeave()) {
            object = nodes[0];
        } else {
            object = offspringPages[0].getFirst();
        }
        return object;
    }

    public k getLast() {
        k object;
        if (isLeave()) {
            object = nodes[size - 1];
        } else {
            object = offspringPages[size].getLast();
        }
        return object;
    }

    public BtreePage<k> getLastPage() {
        if (isLeave()) {
            return this;
        } else {
            return offspringPages[size].getLastPage();
        }
    }

    public void add(k object) {
        if (isLeave()) {
            add(object, null);
        } else {
            offspringPages[findNextPosition(object)].add(object);
        }
    }

    private int findOne(k object) {
        int left = 0;
        int right = size - 1;
        int mid = (left + right) / 2;
        int comparison = 0;
        while ((comparison = nodes[mid].compareTo(object)) != 0 && left != right) {
            if (comparison < 0) {
                left = mid + 1;
            } else { // comparison > 0
                right = mid - 1;
            }
            mid = (left + right) / 2;
        }
        return mid;
    }

    private int findFirstPosition(k object) {
        int pos = findOne(object);
        while (pos > 0 && nodes[pos - 1].compareTo(object) == 0) {
            pos--;
        }
        return pos;
    }

    private int findNextPosition(k object) {
        int pos = findOne(object);
        while (pos < size && nodes[pos].compareTo(object) == 0) {
            pos++;
        }
        return pos;
    }

    public boolean contains(k object) {
        int pos = findFirstPosition(object);
        if (nodes[pos] == object) {
            return true;
        }
        while (pos < size && nodes[pos].compareTo(object) == 0) {
            if (offspringPages[pos].contains(object)) {
                return true;
            }
            pos++;
            if (nodes[pos] == object) {
                return true;
            }
        }
        if (offspringPages[pos].contains(object)) {
            return true;
        }
        return false;
    }

    public boolean remove(k object) {
        int pos = findFirstPosition(object);
        if (nodes[pos] == object) {
            removeFromThisPage(pos);
            return true;
        }
        while (pos < size && nodes[pos].compareTo(object) == 0) {
            if (offspringPages[pos].remove(object)) {
                return true;
            }
            pos++;
            if (nodes[pos] == object) {
                removeFromThisPage(pos);
                return true;
            }
        }
        if (offspringPages[pos].remove(object)) {
            return true;
        }
        return false;
    }

    public void removeFromThisPage(int pos) {
        if (isLeave()) {
            size--;
            // remove the element from the page
            System.arraycopy(nodes, pos + 1, nodes, pos, size - pos);
            nodes[size] = null; // just to avoid future problems
            if (size < (nodes.length * 2 / 3) && parentPage != null) {
                performPostRemovingOperations();
            }
        } else {
            BtreePage<k> lastPage = offspringPages[pos].getLastPage();
            nodes[pos] = lastPage.nodes[lastPage.size - 1];
            lastPage.removeFromThisPage(lastPage.size - 1);
        }
    }

    private void add(k object, BtreePage<k> page) {
        if (isFull()) {
            if (parentPage == null) {
                splitRoot(object, page);
            } else {
                int thisPagePos = parentPage.findFirstPosition(nodes[0]) - 1;
                if (thisPagePos < 0) {
                    thisPagePos = 0;
                }
                while (parentPage.offspringPages[thisPagePos] != this) {
                    thisPagePos++;
                }
                BtreePage rightPage = null;
                if (thisPagePos < parentPage.offspringPages.length - 1) {
                    rightPage = parentPage.offspringPages[thisPagePos + 1];
                }
                BtreePage leftPage = null;
                if (thisPagePos > 0) {
                    leftPage = parentPage.offspringPages[thisPagePos - 1];
                }
                if (rightPage != null && !rightPage.isFull()) { // rotate right
                    rotateRight(thisPagePos, rightPage);
                    k nodeParent = parentPage.nodes[thisPagePos];
                    if (nodeParent.compareTo(object) <= 0) { // add in parent
                        nodes[size] = nodeParent;
                        offspringPages[size + 1] = rightPage.offspringPages[0];
                        if (offspringPages[size + 1] != null) {
                            offspringPages[size + 1].parentPage = this;
                        }
                        rightPage.offspringPages[0] = page;
                        if (page != null) {
                            page.parentPage = rightPage;
                        }
                        parentPage.nodes[thisPagePos] = object;
                    } else { // add at the end of this page
                        nodes[size] = object;
                        offspringPages[size + 1] = page;
                        if (page != null) {
                            page.parentPage = this;
                        }
                    }
                    size++;
                } else if (leftPage != null && !leftPage.isFull()) { // rotate left
                    rotateLeft(thisPagePos, leftPage);
                    k nodeParent = parentPage.nodes[thisPagePos - 1];
                    if (nodeParent.compareTo(object) > 0) { // add in parent
                        // shift right this page
                        System.arraycopy(nodes, 0, nodes, 1, size);
                        System.arraycopy(offspringPages, 1, offspringPages, 2, size);
                        offspringPages[1] = offspringPages[0];
                        offspringPages[0] = page;
                        if (page != null) {
                            page.parentPage = this;
                        }
                        nodes[0] = nodeParent;
                        size++;
                        parentPage.nodes[thisPagePos - 1] = object;
                    } else {
                        addNodeInThisPage(object, page);
                    }
                } else if (rightPage != null) {
                    split(this, thisPagePos, rightPage, object, page);
                } else if (leftPage != null) {
                    split(leftPage, thisPagePos - 1, this, object, page);
                } else {
                    throw new RuntimeException();
                }
            }
        } else {
            addNodeInThisPage(object, page);
        }
    }

    private void addNodeInThisPage(k object, BtreePage<k> rightPage) {
        int pos = findNextPosition(object);
        // move elements
        System.arraycopy(nodes, pos, nodes, pos + 1, size - pos);
        System.arraycopy(offspringPages, pos + 1, offspringPages, pos + 1 + 1, size - pos);
        nodes[pos] = object;
        offspringPages[pos + 1] = rightPage;
        size++;
        if (rightPage != null) {
            rightPage.parentPage = this;
        }
    }

    private void rotateRight(int thisPagePos, BtreePage<k> rightPage) {
        BtreePage<k> parent = parentPage;
        // shift right page to the right
        rightPage.offspringPages[rightPage.size + 1] = rightPage.offspringPages[rightPage.size];
        System.arraycopy(rightPage.offspringPages, 0, rightPage.offspringPages, 1, rightPage.size);
        System.arraycopy(rightPage.nodes, 0, rightPage.nodes, 1, rightPage.size);
        // add the node from the parent
        rightPage.size++;
        rightPage.nodes[0] = parent.nodes[thisPagePos];
        // add the reference from this page
        rightPage.offspringPages[0] = offspringPages[size];
        if (offspringPages[size] != null) {
            rightPage.offspringPages[0].parentPage = rightPage;
        }
        // add last node of this page to the parent
        parent.nodes[thisPagePos] = nodes[size - 1];
        // remove the last node and the right reference
        nodes[size - 1] = null;
        offspringPages[size] = null;
        size--;
    }

    private void rotateLeft(int thisPagePos, BtreePage<k> leftPage) {
        BtreePage<k> parent = parentPage;
        // add the node from the parent
        leftPage.nodes[leftPage.size] = parent.nodes[thisPagePos - 1];
        // add the reference from this page
        leftPage.offspringPages[leftPage.size + 1] = offspringPages[0];
        if (offspringPages[0] != null) {
            leftPage.offspringPages[leftPage.size + 1].parentPage = leftPage;
        }
        leftPage.size++;
        // add first node of this page to the parent
        parent.nodes[thisPagePos - 1] = nodes[0];
        // shift this page to the left and remove first item
        System.arraycopy(nodes, 1, nodes, 0, size - 1);
        System.arraycopy(offspringPages, 1, offspringPages, 0, size - 1);
        offspringPages[size - 1] = offspringPages[size];
        nodes[size - 1] = null;
        offspringPages[size] = null;
        size--;
    }

    private void splitRoot(k object, BtreePage<k> page) {
        if (parentPage != null && size != nodes.length) {
            throw new RuntimeException();
        }
        int nodesFirstPage = (nodes.length + 1) / 2;
        int nodesSecondPage = nodes.length / 2;
        BtreePage<k> left = btree.getResource();
        BtreePage<k> right = btree.getResource();
        left.size = nodesFirstPage;
        right.size = nodesSecondPage;
        int newObjectPosition = findNextPosition(object);
        // copy to aux structure
        k[] auxNodes = btree.auxNodes;
        BtreePage<k>[] auxPages = btree.auxPages;
        System.arraycopy(nodes, 0, auxNodes, 0, newObjectPosition);
        auxNodes[newObjectPosition] = object;
        System.arraycopy(nodes, newObjectPosition, auxNodes, newObjectPosition + 1, size - newObjectPosition);
        // split aux structure
        System.arraycopy(auxNodes, 0, left.nodes, 0, nodesFirstPage);
        System.arraycopy(auxNodes, nodesFirstPage + 1, right.nodes, 0, nodesSecondPage);
        if (page != null) {
            // copy to aux structure
            System.arraycopy(offspringPages, 0, auxPages, 0, newObjectPosition + 1);
            auxPages[newObjectPosition + 1] = page;
            System.arraycopy(offspringPages, newObjectPosition + 1, auxPages, newObjectPosition + 2, size - newObjectPosition);
            // split aux structure
            System.arraycopy(auxPages, 0, left.offspringPages, 0, nodesFirstPage + 1);
            System.arraycopy(auxPages, nodesFirstPage + 1, right.offspringPages, 0, nodesSecondPage);
            for (int i = 0; i < left.size + 1; i++) {
                left.offspringPages[i].parentPage = left;
            }
            for (int i = 0; i < right.size + 1; i++) {
                right.offspringPages[i].parentPage = right;
            }
        }
        // root
        for (int i = 1; i < size; i++) {
            nodes[i] = null;
            offspringPages[i + 1] = null;
        }
        nodes[0] = auxNodes[nodesFirstPage];
        offspringPages[0] = left;
        offspringPages[1] = right;
        left.parentPage = this;
        right.parentPage = this;
    }

    private void split(BtreePage<k> leftPage, int thisPagePos, BtreePage<k> rightPage, k object, BtreePage<k> page) {
        int totalSize = leftPage.size + rightPage.size + 2;
        int nodesFirstPage = (totalSize - 2) / 3;
        int nodesSecondPage = nodesFirstPage;
        int nodesThirdPage = (totalSize - 2) - nodesFirstPage * 2;
        BtreePage lastPage = btree.getResource();
        k[] auxNodes = btree.auxNodes;
        BtreePage<k>[] auxPages = btree.auxPages;
        // left
        System.arraycopy(leftPage.nodes, 0, auxNodes, 0, leftPage.size);
        auxNodes[leftPage.size] = parentNode

        System.arraycopy(rightPage.nodes, 0, auxNodes, leftPage.size, rightPage.size);


        int nodesPerPage = nodes.length * 2 / 3;
        int difference = nodes.length * 2 - nodesPerPage * 3;
        int nodesFirstPage = nodesPerPage;
        int nodesSecondPage = nodesPerPage;
        if (difference == 1) {
            nodesFirstPage = nodesPerPage + 1;
        } else if (difference == 2) {
            nodesFirstPage = nodesPerPage + 1;
            nodesSecondPage = nodesPerPage + 1;
        } else if (difference != 0) {
            throw new RuntimeException();
        }
        int nodesLastPage = leftPage.size + rightPage.size + size - nodesFirstPage - nodesSecondPage - 2;

        for (int i = 0; i < auxNodes.length; i++) {
            auxNodes[i] = null;
            auxPages[i] = null;
        }
        auxPages[auxNodes.length] = null;
        int i = 0;
        boolean added = false;
        int j = 0;
        // merge all
        // left page
        auxPages[i] = leftPage.offspringPages[j];
        int stateHeuristic = object.heuristic[heuristic];
        while (j < leftPage.size) {
            if (!added && (stateHeuristic < leftPage.nodes[j].heuristic[heuristic] || (
                    stateHeuristic == leftPage.nodes[j].heuristic[heuristic] && page != null &&
                            stateHeuristic < leftPage.offspringPages[j + 1].getLast().heuristic[heuristic]))) {
                auxNodes[i] = object;
                auxPages[i + 1] = page;
                added = true;
            } else {
                auxNodes[i] = leftPage.nodes[j];
                auxPages[i + 1] = leftPage.offspringPages[j + 1];
                j++;
            }
            i++;
        }
        // parent node
        if (!added && (stateHeuristic < parentPage.nodes[thisPagePos].heuristic[heuristic] || (
                stateHeuristic == parentPage.nodes[thisPagePos].heuristic[heuristic] && page != null &&
                        stateHeuristic < rightPage.offspringPages[0].getLast().heuristic[heuristic]))) {
            auxNodes[i] = object;
            auxPages[i + 1] = page;
            added = true;
            i++;
        }
        auxNodes[i] = parentPage.nodes[thisPagePos];
        //right page
        i++;
        j = 0;
        auxPages[i] = rightPage.offspringPages[0];
        while (j < rightPage.size) {
            if (!added && (stateHeuristic < rightPage.nodes[j].heuristic[heuristic] || (
                    stateHeuristic == rightPage.nodes[j].heuristic[heuristic] && page != null &&
                            stateHeuristic < rightPage.offspringPages[j + 1].getLast().heuristic[heuristic]))) {
                auxNodes[i] = object;
                auxPages[i + 1] = page;
                added = true;
            } else {
                auxNodes[i] = rightPage.nodes[j];
                auxPages[i + 1] = rightPage.offspringPages[j + 1];
                j++;
            }
            i++;
        }
        if (!added) {
            auxNodes[i] = object;
            auxPages[i + 1] = page;
        }

        // split merged nodes
        for (i = 0; i < nodesFirstPage; i++) {
            leftPage.offspringPages[i] = auxPages[i];
            leftPage.nodes[i] = auxNodes[i];
        }
        leftPage.offspringPages[i] = auxPages[i];
        if (auxPages[0] != null) {
            for (int k = 0; k <= i; k++) {
                leftPage.offspringPages[k].parentPage = leftPage;
            }
        }
        for (int k = i; k < leftPage.size; k++) {
            leftPage.nodes[k] = null;
            leftPage.offspringPages[k + 1] = null;
        }
        leftPage.size = nodesFirstPage;
        parentPage.nodes[thisPagePos] = auxNodes[i];
        j = i + 1;
        for (i = 0; i < nodesSecondPage; i++) {
            rightPage.offspringPages[i] = auxPages[j];
            rightPage.nodes[i] = auxNodes[j];
            j++;
        }
        rightPage.offspringPages[i] = auxPages[j];
        if (auxPages[0] != null) {
            for (int k = 0; k <= i; k++) {
                rightPage.offspringPages[k].parentPage = rightPage;
            }
        }
        for (int k = i; k < rightPage.size; k++) {
            rightPage.nodes[k] = null;
            rightPage.offspringPages[k + 1] = null;
        }
        rightPage.size = nodesSecondPage;
        k newNodeToAddInParent = auxNodes[j];
        j++;
        for (i = 0; i < nodesPerPage; i++) {
            lastPage.nodes[i] = auxNodes[j];
            lastPage.offspringPages[i] = auxPages[j];
            j++;
        }
        lastPage.offspringPages[i] = auxPages[j];
        if (auxPages[0] != null) {
            for (int k = 0; k <= i; k++) {
                lastPage.offspringPages[k].parentPage = lastPage;
            }
        }
        lastPage.size = nodesPerPage;
        parentPage.add(newNodeToAddInParent, lastPage);    // bear in mind the position
    }

    private void performPostRemovingOperations() {
        BtreePage left, middle, right;
        int thisPagePos = 0;
        BtreePage parent = parentPage;
        while (parent.offspringPages[thisPagePos] != this) {
            thisPagePos++;
        }
        int middlePagePos = thisPagePos;
        if (thisPagePos == 0) {
            left = this;
            middle = parent.offspringPages[1];
            right = parent.offspringPages[2];
            middlePagePos = 1;
        } else if (thisPagePos == parent.size && parent.size > 1) {
            left = parent.offspringPages[thisPagePos - 2];
            middle = parent.offspringPages[thisPagePos - 1];
            right = this;
            middlePagePos = thisPagePos - 1;
        } else {
            left = parent.offspringPages[thisPagePos - 1];
            middle = this;
            right = parent.offspringPages[thisPagePos + 1];
        }
        if (parent.parentPage == null && (left == null || right == null)) { // special case of root
            if (left == null) {
                left = middle;
            } else {
                right = middle;
            }
            int totalSize = left.size + right.size + 1;
            if (totalSize > nodes.length) {
                balancePages(left, right);
            } else {
                mergeToRoot(left, right);
            }
        } else if ((right == null || left == null) && parent.parentPage.parentPage == null) {
            // make rotations
            BtreePage root = parent.parentPage;
            int parentPos = 0;
            while (root.offspringPages[parentPos] != parent) {
                parentPos++;
            }
            if (parentPos == 0) {
                root.offspringPages[parentPos + 1].rotateLeft(parentPos + 1, parent);
            } else if (parentPos == root.size) {
                root.offspringPages[parentPos - 1].rotateRight(parentPos - 1, parent);
            } else {
                if (root.offspringPages[parentPos - 1].size < root.offspringPages[parentPos + 1].size) {
                    root.offspringPages[parentPos + 1].rotateLeft(parentPos + 1, parent);
                } else {
                    root.offspringPages[parentPos - 1].rotateRight(parentPos - 1, parent);
                }
            }
            performPostRemovingOperations();
        } else {
            int totalSize = left.size + middle.size + right.size + 1;
            if (totalSize > nodes.length * 2) {
                balancePages(left, middle, right, middlePagePos);
            } else {
                mergePages(left, middle, right, middlePagePos);
            }
        }
    }

    private void balancePages(BtreePage<k> left, BtreePage<k> right) {
        int difference = left.size - right.size;
        BtreePage<k> parent = parentPage;
        int parentLeftPagePos = 0;
        while (parent.offspringPages[parentLeftPagePos] != left) {
            parentLeftPagePos++;
        }
        if (difference < -1) { // rotate left
            int shiftSize = -difference / 2;
            // add the node from the parent
            left.nodes[left.size] = parent.nodes[parentLeftPagePos];

            // restore node from left page
            parent.nodes[parentLeftPagePos] = right.nodes[shiftSize - 1];
            // add elements to left page
            for (int i = 0; i < shiftSize - 1; i++) {
                left.nodes[left.size + 1 + i] = right.nodes[i];
                left.offspringPages[left.size + 1 + i + 1] = right.offspringPages[i + 1];
            }
            // add the reference from right page
            left.offspringPages[left.size + 1] = right.offspringPages[0];
            if (left.offspringPages[0] != null) {
                for (int i = left.size; i <= left.size + shiftSize; i++) {
                    left.offspringPages[i].parentPage = left;
                }
            }
            // shift right page to the left
            right.offspringPages[0] = right.offspringPages[shiftSize];
            for (int i = 0; i < right.size - shiftSize; i++) {
                right.nodes[i] = right.nodes[i + shiftSize];
                right.offspringPages[i + 1] = right.offspringPages[i + shiftSize + 1];
            }
            // just for detecting problems
            for (int i = right.size - shiftSize; i < right.size; i++) {
                right.nodes[i] = null;
                right.offspringPages[i + 1] = null;
            }
            // update sizes
            right.size -= shiftSize;
            left.size += shiftSize;
        } else if (difference > 1) { // rotate right
            int shiftSize = difference / 2;
            // shift right page to the right
            right.offspringPages[right.size + shiftSize] = right.offspringPages[right.size];
            for (int i = right.size - 1; i >= 0; i--) {
                right.offspringPages[i + shiftSize] = right.offspringPages[i];
                right.nodes[i + shiftSize] = right.nodes[i];
            }
            // add the node from the parent
            right.nodes[shiftSize - 1] = parent.nodes[parentLeftPagePos];
            // restore node from left page
            parent.nodes[parentLeftPagePos] = left.nodes[left.size - shiftSize];
            // add references from left page;
            for (int i = left.size - shiftSize + 1; i < left.size; i++) {
                right.nodes[i - (left.size - shiftSize + 1)] = left.nodes[i];
                right.offspringPages[i - (left.size - shiftSize)] = left.offspringPages[i + 1];
            }
            // add the page reference of left page to the right
            right.offspringPages[0] = left.offspringPages[left.size - shiftSize + 1];
            if (right.offspringPages[0] != null) {
                for (int i = 0; i <= shiftSize; i++) {
                    right.offspringPages[i].parentPage = right;
                }
            }
            // just for detecting problems
            for (int i = left.size - shiftSize; i < left.size; i++) {
                left.nodes[i] = null;
                left.offspringPages[i + 1] = null;
            }
            // update sizes
            right.size += shiftSize;
            left.size -= shiftSize;
        }
    }

    private void mergeToRoot(BtreePage<k> left, BtreePage<k> right) {
        BtreePage<k> root = parentPage;
        if (root.size != 1) {
            throw new RuntimeException("Fatal error!");
        }
        // move root node
        root.nodes[left.size] = root.nodes[0];
        // add nodes in left page
        for (int i = 0; i < left.size; i++) {
            root.nodes[i] = left.nodes[i];
            root.offspringPages[i] = left.offspringPages[i];
        }
        root.offspringPages[left.size] = left.offspringPages[left.size];
        // add nodes in right page
        for (int i = 0; i < right.size; i++) {
            root.nodes[i + left.size + 1] = right.nodes[i];
            root.offspringPages[i + left.size + 1] = right.offspringPages[i];
        }
        root.offspringPages[right.size + left.size + 1] = right.offspringPages[right.size];
        root.size = 1 + left.size + right.size;
        clear(left);
        clear(right);
        if (root.offspringPages[0] != null) {
            for (int i = 0; i <= root.size; i++) {
                root.offspringPages[i].parentPage = root;
            }
        }
    }

    private void balancePages(BtreePage<k> left, BtreePage<k> middle, BtreePage<k> right, int middlePagePos) {
        int minimumSize = nodes.length * 2 / 3;
        boolean change;
        do {
            change = false;
            if (left.size < minimumSize) {
                middle.rotateLeft(middlePagePos, left);
                change = true;
            } else if (right.size < minimumSize) {
                middle.rotateRight(middlePagePos, right);
                change = true;
            } else if (middle.size < minimumSize) {
                if (left.size > right.size) {
                    left.rotateRight(middlePagePos - 1, middle);
                } else {
                    right.rotateLeft(middlePagePos + 1, middle);
                }
                change = true;
            }
        } while (change);
    }

    private void mergePages(BtreePage<k> left, BtreePage<k> middle, BtreePage<k> right, int middlePagePos) {
        BtreePage<k> parent = parentPage;
        int sumSizes = left.size + middle.size + right.size + 1;
        if (sumSizes > nodes.length * 2) {
            throw new RuntimeException();
        }
        int rightFinalSize = sumSizes / 2;
        int leftFinalSize = sumSizes - rightFinalSize;
        k[] auxNodes = btree.auxNodes;
        BtreePage<k>[] auxPages = btree.auxPages;
        for (int i = 0; i < sumSizes + 1; i++) {
            auxNodes[i] = null;
            auxPages[i] = null;
        }
        auxPages[sumSizes + 2] = null;
        int j = 0;
        // merge
        for (int i = 0; i < left.size; i++) {
            auxNodes[i] = left.nodes[i];
            auxPages[i] = left.offspringPages[i];
        }
        j = left.size;
        auxPages[j] = left.offspringPages[j];
        auxNodes[j] = parent.nodes[middlePagePos - 1];
        j++;
        for (int i = 0; i < middle.size; i++) {
            auxNodes[j] = middle.nodes[i];
            auxPages[j] = middle.offspringPages[i];
            j++;
        }
        auxPages[j] = middle.offspringPages[middle.size];
        auxNodes[j] = parent.nodes[middlePagePos];
        j++;
        for (int i = 0; i < right.size; i++) {
            auxNodes[j] = right.nodes[i];
            auxPages[j] = right.offspringPages[i];
            j++;
        }
        auxPages[j] = right.offspringPages[right.size];
        // split
        for (int i = 0; i < leftFinalSize; i++) {
            left.nodes[i] = auxNodes[i];
            left.offspringPages[i] = auxPages[i];
        }
        j = leftFinalSize;
        left.offspringPages[j] = auxPages[j];
        left.size = leftFinalSize;
        // remove empty pos from parent
        parent.nodes[middlePagePos - 1] = auxNodes[j];
        j++;
        for (int i = middlePagePos; i < parent.size - 1; i++) {
            parent.nodes[i] = parent.nodes[i + 1];
            parent.offspringPages[i + 1] = parent.offspringPages[i + 2];
        }
        parent.offspringPages[middlePagePos] = right;
        parent.nodes[parent.size - 1] = null;
        parent.offspringPages[parent.size] = null;
        parent.size--;
        for (int i = 0; i < rightFinalSize; i++) {
            right.nodes[i] = auxNodes[j];
            right.offspringPages[i] = auxPages[j];
            j++;
        }
        right.offspringPages[rightFinalSize] = auxPages[j];
        right.size = rightFinalSize;
        // parent pages
        if (left.offspringPages[0] != null) {
            for (int i = 0; i <= left.size; i++) {
                left.offspringPages[i].parentPage = left;
            }
            for (int i = 0; i <= right.size; i++) {
                right.offspringPages[i].parentPage = right;
            }
        }
        clear(middle);
        // perform more post removing operations in parent if needed
        if (parent.size < (nodes.length * 2 / 3) && parent.parentPage != null) {
            parent.performPostRemovingOperations();
        }
    }

    @Override
    public String toString() {
        BtreePrinter v = new BtreePrinter();
        v.toString(true);
        visit(v, 0);
        return v.getString();
    }

    protected void checkIntegrity() {
        if (offspringPages[0] != null) {
            for (int i = 0; i <= size; i++) {
                if (offspringPages[i].parentPage != this) {
                    throw new RuntimeException();
                } else {
                    offspringPages[i].checkIntegrity();
                }
            }
            for (int i = 0; i < size; i++) {
                if (nodes[i] == null) {
                    throw new RuntimeException();
                }
            }
        }
    }

    public void clear(BtreePage page) {
        for (int i = 0; i < page.nodes.length; i++) {
            page.nodes[i] = null;
            page.offspringPages[i] = null;
        }
        page.offspringPages[page.nodes.length] = null;
        page.size = 0;
        btree.releaseResource(page);
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            nodes[i] = null;
        }
        for (int i = 0; i <= size; i++) {
            if (offspringPages[i] != null) {
                offspringPages[i].clear();
                btree.releaseResource(offspringPages[i]);
            }
            offspringPages[i] = null;
        }
    }

}
