package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;

public class BtreePage<k extends Comparable<k>> {

    private BtreePage<k> parentPage;

    private int parentPosition;

    private BtreePage<k>[] offspringPages;

    private k[] nodes;

    private int size;

    private ResourcesFactory<BtreePage<k>> resourcesFactory;

    protected BtreePage(int numberOfNodes, ResourcesFactory<BtreePage<k>> resourcesFactory) {
        size = 0;
        //noinspection unchecked
        nodes = (k[]) (new Object[numberOfNodes]);
        //noinspection unchecked
        offspringPages = new BtreePage[numberOfNodes + 1];
        parentPosition = -1;
        parentPage = null;
        this.resourcesFactory = resourcesFactory;
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

    private void setParentPage(BtreePage<k> parent, int parentPosition) {
        this.parentPage = parent;
        this.parentPosition = parentPosition;
    }

    private void setRootPage() {
        parentPage = null;
        parentPosition = -1;
    }

    protected BtreePage<k> getLastPage() {
        if (isLeave()) {
            return this;
        } else {
            return offspringPages[size].getLastPage();
        }
    }

    protected BtreePage<k> getFirstPage() {
        if (isLeave()) {
            return this;
        } else {
            return offspringPages[0].getFirstPage();
        }
    }

    private int findOne(k object) {
        int left = 0;
        int right = size - 1;
        int mid = (left + right + 1) / 2;
        int comparison;
        while ((comparison = nodes[mid].compareTo(object)) != 0 && left < right) {
            if (comparison < 0) {
                left = mid + 1;
            } else { // comparison > 0
                right = mid - 1;
            }
            mid = (left + right + 1) / 2;
        }
        return mid;
    }

    protected int findFirstPosition(k object) {
        int pos = findOne(object);
        while (pos > 0 && nodes[pos - 1].compareTo(object) == 0) {
            pos--;
        }
        return pos;
    }

    protected int findNextPosition(k object) {
        int pos = findOne(object);
        while (pos < size && nodes[pos].compareTo(object) == 0) {
            pos++;
        }
        return pos;
    }

    public boolean contains(k object) {
        return contains(findFirstPosition(object), object);
    }

    private boolean contains(int initialSearchPosition, k object) {
        int pos = initialSearchPosition;
        if (nodes[pos] == object) {
            return true;
        }
        while (pos < size && nodes[pos].compareTo(object) == 0) {
            if (offspringPages[pos].contains(0, object)) {
                return true;
            }
            pos++;
            if (nodes[pos] == object) {
                return true;
            }
        }
        if (offspringPages[pos].contains(0, object)) {
            return true;
        }
        return false;
    }

    public void add(k object) {
        int pos = findNextPosition(object);
        if (isLeave()) {
            insert(pos, object, null);
        } else {
            offspringPages[pos].add(object);
        }
    }

    private void insert(int position, k object, BtreePage<k> page) {
        if (!isFull()) {
            shiftRight(position, 1);
            nodes[position] = object;
            if (!isLeave()) {
                offspringPages[position + 1] = page;
                page.setParentPage(this, position + 1);
            }
        } else {
            if (parentPage == null) {
                splitRoot(position, object, page);
            } else {
                BtreePage<k> leftPage = null;
                BtreePage<k> rightPage = null;
                if (parentPosition > 0 && !(leftPage = parentPage.offspringPages[parentPosition - 1]).isFull()) {
                    // rotate left and insert
                    leftPage.nodes[leftPage.size] = parentPage.nodes[parentPosition - 1];
                    leftPage.size++;
                    if (position == 0) {
                        // current node to parent page
                        parentPage.nodes[parentPosition - 1] = object;
                    } else {
                        parentPage.nodes[parentPosition - 1] = nodes[0];
                        // make a gap
                        System.arraycopy(nodes, 1, nodes, 0, position - 1);
                        nodes[position - 1] = object;
                    }
                    if (!leftPage.isLeave()) {
                        leftPage.offspringPages[leftPage.size] = offspringPages[0];
                        leftPage.offspringPages[leftPage.size].setParentPage(leftPage, leftPage.size);
                        if (position != 0) {
                            // make a gap
                            System.arraycopy(offspringPages, 1, offspringPages, 0, position);
                            for (int i = 0; i < position; i++) {
                                offspringPages[i].parentPosition = i;
                            }
                        }
                        offspringPages[position] = page;
                        offspringPages[position].setParentPage(this, position);
                    }
                } else if (parentPosition < parentPage.size && !(rightPage = parentPage.offspringPages[parentPosition + 1]).isFull()) {
                    // rotate right and insert
                    rightPage.shiftRight(0, 1);
                    rightPage.nodes[0] = parentPage.nodes[parentPosition];
                    if (position == size) {
                        // current node to parent page
                        parentPage.nodes[parentPosition] = object;
                    } else {
                        parentPage.nodes[parentPosition] = nodes[size - 1];
                        shiftRight(position, 1);
                        nodes[position] = object;
                    }
                    if (!rightPage.isLeave()) {
                        if (position == size) {
                            rightPage.offspringPages[0] = page;
                        } else {
                            rightPage.offspringPages[0] = offspringPages[size];
                            offspringPages[position + 1] = page;
                            offspringPages[position + 1].setParentPage(this, position + 1);
                        }
                        rightPage.offspringPages[0].setParentPage(rightPage, 0);
                    }
                } else {
                    if (leftPage != null) {
                        // split with left
                        parentPage.split(parentPosition - 1, leftPage.size + 1 + position, object, page);
                    } else {
                        // split with right page
                        parentPage.split(parentPosition, position, object, page);
                    }
                }
            }
        }
    }

    private void shiftRight(int initialPosition, int displacement) {
        System.arraycopy(nodes, initialPosition, nodes, initialPosition + displacement, size - initialPosition);
        for (int i = initialPosition; i < initialPosition + displacement; i++) {
            nodes[i] = null;
        }
        if (!isLeave()) {
            System.arraycopy(offspringPages, initialPosition + 1, offspringPages, initialPosition + 1 + displacement, size - initialPosition);
            for (int i = initialPosition + 1; i < initialPosition + 1 + displacement; i++) {
                offspringPages[i] = null;
            }
            for (int i = initialPosition + 1 + displacement; i < size + 1; i++) {
                offspringPages[i].parentPosition = i;
            }
        }
        size += displacement;
    }

    private void shiftLeft(int initialPosition, int displacement) {
        System.arraycopy(nodes, initialPosition, nodes, initialPosition - displacement, size - initialPosition);
        for (int i = size - displacement; i < size; i++) {
            nodes[i] = null; // safety
        }
        if (!isLeave()) {
            for (int i = initialPosition - displacement; i < initialPosition; i++) {
                offspringPages[i].setParentPage(null, -2);
            }
            System.arraycopy(offspringPages, initialPosition + 1, offspringPages, initialPosition + 1 - displacement, size - initialPosition);
            for (int i = initialPosition + displacement; i < size + 1; i++) {
                offspringPages[i].parentPosition = i;
            }
        }
        size -= displacement;
    }

    public boolean remove(k object) {
        return remove(findFirstPosition(object), object);
    }

    private boolean remove(int initialSearchPosition, k object) {
        int pos = initialSearchPosition;
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

    private void removeFromThisPage(int pos) {
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


//    private void rotateRight(int nodePos) {
//        BtreePage<k> leftPage = offspringPages[nodePos];
//        BtreePage<k> rightPage = offspringPages[nodePos + 1];
//        rightPage.shiftRight(0, 1);
//        rightPage.nodes[0] = nodes[nodePos];
//        nodes[nodePos] = leftPage.nodes[leftPage.size - 1];
//        leftPage.nodes[leftPage.size - 1] = null;
//        if (!rightPage.isLeave()) {
//            BtreePage<k>[] rightPages = rightPage.offspringPages;
//            BtreePage<k>[] leftPages = leftPage.offspringPages;
//
//            rightPages[1] = rightPages[0];
//            rightPages[1].parentPosition = 1;
//            rightPages[0] = leftPages[leftPage.size];
//            rightPages[0].setParentPage(rightPage, 0);
//
//            leftPages[leftPage.size] = null;
//        }
//        leftPage.size--;
//    }
//
//    private void rotateLeft(int nodePos) {
//        BtreePage<k> leftPage = offspringPages[nodePos];
//        BtreePage<k> rightPage = offspringPages[nodePos + 1];
//        leftPage.nodes[leftPage.size] = nodes[nodePos];
//        nodes[nodePos] = rightPage.nodes[0];
//        leftPage.size++;
//        if (!rightPage.isLeave()) {
//            BtreePage<k>[] rightPages = rightPage.offspringPages;
//            BtreePage<k>[] leftPages = leftPage.offspringPages;
//
//            leftPages[leftPage.size] = rightPages[0];
//            leftPages[leftPage.size].setParentPage(leftPage, leftPage.size);
//        }
//        rightPage.shiftLeft(1, 1);
//    }

    private void splitRoot(int objectPosition, k object, BtreePage<k> page) {
        if (parentPage != null && size != nodes.length) {
            throw new RuntimeException();
        }
        int nodesFirstPage = (nodes.length + 1) / 2;
        int nodesSecondPage = nodes.length / 2;
        BtreePage<k> left = resourcesFactory.getResource();
        BtreePage<k> right = resourcesFactory.getResource();
        left.size = nodesFirstPage;
        right.size = nodesSecondPage;
        boolean isLeave = isLeave();
        if (objectPosition < nodesFirstPage) {
            // object to left page

            // left page
            System.arraycopy(nodes, 0, left.nodes, 0, objectPosition);
            left.nodes[objectPosition] = object;
            System.arraycopy(nodes, objectPosition, left.nodes, objectPosition + 1, nodesFirstPage - objectPosition - 1);
            // root
            nodes[0] = nodes[nodesFirstPage + 2];
            // right page
            System.arraycopy(nodes, nodesFirstPage + 1, right.nodes, 0, nodesSecondPage);
            if (!isLeave) {
                // left page
                System.arraycopy(offspringPages, 0, left.offspringPages, 0, objectPosition + 1);
                left.offspringPages[objectPosition + 1] = page;
                System.arraycopy(offspringPages, objectPosition + 1, left.offspringPages, objectPosition + 2, nodesFirstPage - objectPosition - 1);
                // right page
                System.arraycopy(offspringPages, nodesFirstPage + 1, right.offspringPages, 0, nodesSecondPage);
            }
        } else if (objectPosition == nodesFirstPage) {
            // object to root page

            // left page
            System.arraycopy(nodes, 0, left.nodes, 0, nodesFirstPage);
            // root
            nodes[0] = object;
            // right page
            System.arraycopy(nodes, nodesFirstPage + 1, right.nodes, 0, nodesSecondPage);
            if (!isLeave) {
                // left page
                System.arraycopy(offspringPages, 0, left.offspringPages, 0, nodesFirstPage + 1);

                left.offspringPages[objectPosition + 1] = page;
                System.arraycopy(offspringPages, objectPosition + 1, left.offspringPages, objectPosition + 2, nodesFirstPage - objectPosition - 1);
                // root
                nodes[0] = nodes[nodesFirstPage + 2];
                // right page
                ri
                System.arraycopy(nodes, nodesFirstPage + 1, right.nodes, 0, nodesSecondPage);
            }

        } else {
            // object to right

        }
        offspringPages[0] = left;
        offspringPages[1] = right;
        offspringPages[0].setParentPage(this, 0);
        offspringPages[1].setParentPage(this, 1);
        size = 1;
        int index = 1;
        while (index < nodes.length) {
            nodes[index] = null;
            index++;
            offspringPages[index] = null;
        }
        if (!isLeave) {
            BtreePage<k>[] rightPages = right.offspringPages;
            BtreePage<k>[] leftPages = left.offspringPages;
            for (int i = 0; i < left.size + 1; i++) {
                leftPages[i].setParentPage(left, i);
            }
            for (int i = 0; i < right.size + 1; i++) {
                rightPages[i].setParentPage(right, i);
            }
        }


        //---------------------------------------------------------


        // copy to aux structure
        k[] auxNodes = btree.auxNodes;
        BtreePage<k>[] auxPages = btree.auxPages;
        System.arraycopy(nodes, 0, auxNodes, 0, objectPosition);
        auxNodes[objectPosition] = object;
        System.arraycopy(nodes, objectPosition, auxNodes, objectPosition + 1, size - objectPosition);
        // split aux structure
        System.arraycopy(auxNodes, 0, left.nodes, 0, nodesFirstPage);
        System.arraycopy(auxNodes, nodesFirstPage + 1, right.nodes, 0, nodesSecondPage);
        if (page != null) {
            // copy to aux structure
            System.arraycopy(offspringPages, 0, auxPages, 0, objectPosition + 1);
            auxPages[objectPosition + 1] = page;
            System.arraycopy(offspringPages, objectPosition + 1, auxPages, objectPosition + 2, size - objectPosition);
            // split aux structure
            System.arraycopy(auxPages, 0, left.offspringPages, 0, nodesFirstPage + 1);
            System.arraycopy(auxPages, nodesFirstPage + 1, right.offspringPages, 0, nodesSecondPage);
            BtreePage<k>[] rightPages = right.offspringPages;
            BtreePage<k>[] leftPages = left.offspringPages;
            for (int i = 0; i < left.size + 1; i++) {
                leftPages[i].setParentPage(left, i);
            }
            for (int i = 0; i < right.size + 1; i++) {
                rightPages[i].setParentPage(right, i);
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


    private void split(int pagePosition, int objectPositionInMergedPage, k object, BtreePage<k> page) {
        BtreePage<k> leftPage = offspringPages[pagePosition];
        BtreePage<k> rightPage = offspringPages[pagePosition + 1];
        BtreePage<k> centerPage = btree.getResource();


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
