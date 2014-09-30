package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;
import com.jorgemf.util.search.State;

public class BtreePage<k extends Comparable<k>> {

    private BtreePage<k> parentPage;

    private BtreePage<k>[] offspringPages;

    private k[] nodes;

    private int size;

    private ResourcesFactory<BtreePage<k>> pagesFactory;

    private k[] auxStates;

    private BtreePage[] auxPages;

    protected BtreePage(int numberOfNodes, ResourcesFactory<BtreePage<k>> pagesFactory) {
        this.size = 0;
        //noinspection unchecked
        this.nodes = (k[]) (new Object[numberOfNodes]);
        this.offspringPages = new BtreePage[numberOfNodes + 1];
        this.pagesFactory = pagesFactory;
        //noinspection unchecked
        this.auxStates = (k[]) (new Object[this.nodes.length * 2 + 2]);
        this.auxPages = new BtreePage[this.auxStates.length + 1];
    }

    public void visitInOrder(BtreeVisitor<k> visitor, int deep) {
        for (int i = 0; i < this.size; i++) {
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
        return this.size == this.nodes.length;
    }

    private boolean isLeave() {
        return this.offspringPages[0] == null;
    }

    private int searchPos(k object) {
        int pos = 0;
        while (pos < size && nodes[pos].compareTo(object) < 0) {
            pos++;
        }
        return pos;
    }

    public k getFirst() {
        k object;
        if (this.isLeave()) {
            object = this.nodes[0];
        } else {
            object = this.offspringPages[0].getFirst();
        }
        return object;
    }

    public k getLast() {
        k object;
        if (this.isLeave()) {
            object = this.nodes[this.size - 1];
        } else {
            object = this.offspringPages[this.size].getLast();
        }
        return object;
    }

    public BtreePage getLastPage() {
        if (this.isLeave()) {
            return this;
        } else {
            return this.offspringPages[this.size].getLastPage();
        }
    }

    public void add(k object) {
        if (this.isLeave()) {
            this.add(object, null);
        } else {
            this.offspringPages[this.searchPos(object)].add(object);
        }
    }

    public boolean contains(k object) {
        int pos = 0;
        int h = state.heuristic[heuristic];
        int nodeh = this.nodes[pos].heuristic[heuristic];
        boolean contains = false;
        while (pos < this.size && nodeh <= h && !contains) {
            if (nodeh == h) {
                if (this.nodes[pos] == state) {
                    contains = true;
                } else {
                    contains = this.offspringPages[pos].contains(state);
                }
            }
            pos++;
            if (pos < this.size) {
                nodeh = this.nodes[pos].heuristic[heuristic];
            }
        }
        if (!contains && pos == this.size && nodeh == h) {
            contains = this.offspringPages[pos].contains(state);
        }
        return contains;
    }

    public boolean remove(k object) {
        int pos = 0;
        int h = state.heuristic[heuristic];
        int nodeh = this.nodes[pos].heuristic[heuristic];
        boolean removed = false;
        while (pos < this.size && nodeh <= h && !removed) {
            if (nodeh == h) {
                if (this.nodes[pos] == state) {
                    removeFromThisPage(pos);
                    removed = true;
                } else if (this.offspringPages[pos] != null) {
                    removed = this.offspringPages[pos].remove(state);
                }
            }
            pos++;
            if (pos < this.size) {
                nodeh = this.nodes[pos].heuristic[heuristic];
            }
        }
        if (!removed && pos <= this.size && this.offspringPages[pos] != null) {
            if ((pos == this.size && nodeh <= h) || (pos < this.size && nodeh > h)) {
                removed = this.offspringPages[pos].remove(state);
            }
        }
        return removed;
    }

    public void removeFromThisPage(int pos) {
        if (this.isLeave()) {
            this.size--;
            // remove the element from the page
            for (int i = pos; i < this.size; i++) {
                this.nodes[i] = this.nodes[i + 1];
            }
            this.nodes[this.size] = null; // just for avoid future problems
            if (this.size < (this.nodes.length * 2 / 3) && this.parentPage != null) {
                performPostRemovingOperations();
            }
        } else {
            BtreePage lastPage = this.offspringPages[pos].getLastPage();
            this.nodes[pos] = lastPage.nodes[lastPage.size - 1];
            lastPage.removeFromThisPage(lastPage.size - 1);
        }
    }

    private void add(k object, BtreePage page) {
        if (this.isFull()) {
            if (this.parentPage == null) {
                splitRoot(state, page);
            } else {
                int thisPagePos = 0;
                while (this.parentPage.offspringPages[thisPagePos] != this) {
                    thisPagePos++;
                }
                BtreePage rightPage = null;
                if (thisPagePos < this.parentPage.offspringPages.length - 1) {
                    rightPage = this.parentPage.offspringPages[thisPagePos + 1];
                }
                BtreePage leftPage = null;
                if (thisPagePos > 0) {
                    leftPage = this.parentPage.offspringPages[thisPagePos - 1];
                }
                if (rightPage != null && !rightPage.isFull()) { // rotate right
                    rotateRigth(thisPagePos, rightPage);
                    State nodeParent = this.parentPage.nodes[thisPagePos];
                    if (nodeParent.heuristic[heuristic] < state.heuristic[heuristic] || (
                            nodeParent.heuristic[heuristic] == state.heuristic[heuristic]
                                    && page != null && page.getLast().heuristic[heuristic] > nodeParent.heuristic[heuristic])) { // add in parent
                        //System.out.println("Rotate right add node in parent");
                        this.nodes[this.size] = nodeParent;
                        this.offspringPages[this.size + 1] = rightPage.offspringPages[0];
                        if (this.offspringPages[this.size + 1] != null) {
                            this.offspringPages[this.size + 1].parentPage = this;
                        }
                        rightPage.offspringPages[0] = page;
                        if (page != null) {
                            page.parentPage = rightPage;
                        }
                        this.size++;
                        this.parentPage.nodes[thisPagePos] = state;
                    } else {
                        this.addNodeInThisPage(state, page);
                    }
                } else if (leftPage != null && !leftPage.isFull()) { // rotate left
                    rotateLeft(thisPagePos, leftPage);
                    State nodeParent = this.parentPage.nodes[thisPagePos - 1];
                    if (nodeParent.heuristic[heuristic] > state.heuristic[heuristic]) { // add in parent
                        //System.out.println("Rotate left add node in parent");
                        // shift right this page
                        for (int i = this.size; i > 0; i--) {
                            this.nodes[i] = this.nodes[i - 1];
                            this.offspringPages[i + 1] = this.offspringPages[i];
                        }
                        this.offspringPages[1] = this.offspringPages[0];
                        this.offspringPages[0] = page;
                        if (page != null) {
                            page.parentPage = this;
                        }
                        this.nodes[0] = nodeParent;
                        this.size++;
                        this.parentPage.nodes[thisPagePos - 1] = state;
                    } else {
                        this.addNodeInThisPage(state, page);
                    }
                } else if (rightPage != null) {
                    split(this, thisPagePos, rightPage, state, page);
                } else if (leftPage != null) {
                    split(leftPage, thisPagePos - 1, this, state, page);
                } else {
                    throw new RuntimeException();
                }
            }
        } else {
            this.addNodeInThisPage(state, page);
        }
    }

    private void addNodeInThisPage(k object, BtreePage rightPage) {
        int pos = -1;
        if (rightPage == null) {
            pos = searchPos(object);
        } else {
            pos = 0;
            int h = state.heuristic[heuristic];
            int nodeH;
            while (pos < size) {
                nodeH = nodes[pos].heuristic[heuristic];
                if (nodeH < h || (nodeH == h && offspringPages[pos + 1].getLast().heuristic[heuristic] == h)) {
                    pos++;
                } else {
                    break;
                }
            }
        }
        // move elements
        for (int i = size; i > pos; i--) {
            this.nodes[i] = this.nodes[i - 1];
            this.offspringPages[i + 1] = this.offspringPages[i];
        }
        this.nodes[pos] = object;
        this.offspringPages[pos + 1] = rightPage;
        this.size++;
        if (rightPage != null) {
            rightPage.parentPage = this;
        }
    }

    private void rotateRigth(int thisPagePos, BtreePage rightPage) {
        BtreePage parent = this.parentPage;
        // shift right page to the right
        rightPage.offspringPages[rightPage.size + 1] = rightPage.offspringPages[rightPage.size];
        for (int i = rightPage.size; i > 0; i--) {
            rightPage.offspringPages[i] = rightPage.offspringPages[i - 1];
            rightPage.nodes[i] = rightPage.nodes[i - 1];
        }
        // add the node from the parent
        rightPage.size++;
        rightPage.nodes[0] = parent.nodes[thisPagePos];
        // add the reference from this page
        rightPage.offspringPages[0] = this.offspringPages[this.size];
        if (this.offspringPages[this.size] != null) {
            rightPage.offspringPages[0].parentPage = rightPage;
        }
        // add last node of this page to the parent
        parent.nodes[thisPagePos] = this.nodes[size - 1];
        // remove the last node and the right reference
        this.nodes[size - 1] = null;
        this.offspringPages[size] = null;
        this.size--;
    }

    private void rotateLeft(int thisPagePos, BtreePage leftPage) {
        BtreePage parent = this.parentPage;
        // add the node from the parent
        leftPage.nodes[leftPage.size] = parent.nodes[thisPagePos - 1];
        // add the reference from this page
        leftPage.offspringPages[leftPage.size + 1] = this.offspringPages[0];
        if (this.offspringPages[0] != null) {
            leftPage.offspringPages[leftPage.size + 1].parentPage = leftPage;
        }
        leftPage.size++;
        // add first node of this page to the parent
        parent.nodes[thisPagePos - 1] = this.nodes[0];
        // shift this page to the left and remove first item
        for (int i = 0; i < this.size - 1; i++) {
            this.nodes[i] = this.nodes[i + 1];
            this.offspringPages[i] = this.offspringPages[i + 1];
        }
        this.offspringPages[this.size - 1] = this.offspringPages[this.size];
        this.nodes[this.size - 1] = null;
        this.offspringPages[this.size] = null;
        this.size--;
    }

    private void splitRoot(State state, BtreePage page) {
        if (this.parentPage != null && this.size != this.nodes.length) {
            throw new RuntimeException();
        }
        int nodesFirstPage = (this.nodes.length + 1) / 2;
        int nodesSecondPage = this.nodes.length / 2;
        BtreePage left = this.pagesFactory.getResource();
        BtreePage right = this.pagesFactory.getResource();
        boolean added = false;
        int stateHeuristic = state.heuristic[heuristic];
        int index = 0;
        // left page
        left.size = nodesFirstPage;
        left.offspringPages[0] = this.offspringPages[0];
        for (int i = 0; i < nodesFirstPage; i++) {
            if (!added && (stateHeuristic < this.nodes[index].heuristic[heuristic] || (
                    stateHeuristic == this.nodes[index].heuristic[heuristic] && page != null &&
                            stateHeuristic < this.offspringPages[index + 1].getLast().heuristic[heuristic]))) {
                left.nodes[i] = state;
                left.offspringPages[i + 1] = page;
                added = true;
            } else {
                left.nodes[i] = this.nodes[index];
                left.offspringPages[i + 1] = this.offspringPages[index + 1];
                index++;
            }
        }
        // root
        State nodeRoot;
        if (!added && (stateHeuristic < this.nodes[index].heuristic[heuristic] || (
                stateHeuristic == this.nodes[index].heuristic[heuristic] && page != null &&
                        stateHeuristic < this.offspringPages[index + 1].getLast().heuristic[heuristic]))) {
            nodeRoot = state;
            right.offspringPages[0] = page;
            added = true;
        } else {
            nodeRoot = this.nodes[index];
            index++;
            right.offspringPages[0] = this.offspringPages[index];
        }
        // right page
        right.size = nodesSecondPage;
        for (int i = 0; i < nodesSecondPage; i++) {
            if (!added && (i == nodesSecondPage - 1 || stateHeuristic < this.nodes[index].heuristic[heuristic] || (
                    stateHeuristic == this.nodes[index].heuristic[heuristic] && page != null &&
                            stateHeuristic < this.offspringPages[index + 1].getLast().heuristic[heuristic]))) {
                right.nodes[i] = state;
                right.offspringPages[i + 1] = page;
                added = true;
            } else {
                right.nodes[i] = this.nodes[index];
                right.offspringPages[i + 1] = this.offspringPages[index + 1];
                index++;
            }
        }
        // root
        this.offspringPages[0] = left;
        this.offspringPages[1] = right;
        left.parentPage = this;
        right.parentPage = this;
        this.nodes[0] = nodeRoot;
        for (int i = 1; i < this.size; i++) {
            this.nodes[i] = null;
            this.offspringPages[i + 1] = null;
        }
        this.size = 1;
        // parent pages
        if (page != null) {
            for (int i = 0; i <= left.size; i++) {
                left.offspringPages[i].parentPage = left;
            }
            for (int i = 0; i <= right.size; i++) {
                right.offspringPages[i].parentPage = right;
            }
        }
    }

    private void split(BtreePage leftPage, int thisPagePos, BtreePage rightPage, State state, BtreePage page) {
        int nodesPerPage = this.nodes.length * 2 / 3;
        int difference = this.nodes.length * 2 - nodesPerPage * 3;
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
        BtreePage lastPage = this.pagesFactory.getResource();
        for (int i = 0; i < auxStates.length; i++) {
            auxStates[i] = null;
            auxPages[i] = null;
        }
        auxPages[auxStates.length] = null;
        int i = 0;
        boolean added = false;
        int j = 0;
        // merge all
        // left page
        auxPages[i] = leftPage.offspringPages[j];
        int stateHeuristic = state.heuristic[heuristic];
        while (j < leftPage.size) {
            if (!added && (stateHeuristic < leftPage.nodes[j].heuristic[heuristic] || (
                    stateHeuristic == leftPage.nodes[j].heuristic[heuristic] && page != null &&
                            stateHeuristic < leftPage.offspringPages[j + 1].getLast().heuristic[heuristic]))) {
                auxStates[i] = state;
                auxPages[i + 1] = page;
                added = true;
            } else {
                auxStates[i] = leftPage.nodes[j];
                auxPages[i + 1] = leftPage.offspringPages[j + 1];
                j++;
            }
            i++;
        }
        // parent node
        if (!added && (stateHeuristic < this.parentPage.nodes[thisPagePos].heuristic[heuristic] || (
                stateHeuristic == this.parentPage.nodes[thisPagePos].heuristic[heuristic] && page != null &&
                        stateHeuristic < rightPage.offspringPages[0].getLast().heuristic[heuristic]))) {
            auxStates[i] = state;
            auxPages[i + 1] = page;
            added = true;
            i++;
        }
        auxStates[i] = this.parentPage.nodes[thisPagePos];
        //right page
        i++;
        j = 0;
        auxPages[i] = rightPage.offspringPages[0];
        while (j < rightPage.size) {
            if (!added && (stateHeuristic < rightPage.nodes[j].heuristic[heuristic] || (
                    stateHeuristic == rightPage.nodes[j].heuristic[heuristic] && page != null &&
                            stateHeuristic < rightPage.offspringPages[j + 1].getLast().heuristic[heuristic]))) {
                auxStates[i] = state;
                auxPages[i + 1] = page;
                added = true;
            } else {
                auxStates[i] = rightPage.nodes[j];
                auxPages[i + 1] = rightPage.offspringPages[j + 1];
                j++;
            }
            i++;
        }
        if (!added) {
            auxStates[i] = state;
            auxPages[i + 1] = page;
        }

        // split merged nodes
        for (i = 0; i < nodesFirstPage; i++) {
            leftPage.offspringPages[i] = auxPages[i];
            leftPage.nodes[i] = auxStates[i];
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
        this.parentPage.nodes[thisPagePos] = auxStates[i];
        j = i + 1;
        for (i = 0; i < nodesSecondPage; i++) {
            rightPage.offspringPages[i] = auxPages[j];
            rightPage.nodes[i] = auxStates[j];
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
        State newStateToAddInParent = auxStates[j];
        j++;
        for (i = 0; i < nodesPerPage; i++) {
            lastPage.nodes[i] = auxStates[j];
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
        this.parentPage.add(newStateToAddInParent, lastPage);
    }

    private void performPostRemovingOperations() {
        BtreePage left, middle, right;
        int thisPagePos = 0;
        BtreePage parent = this.parentPage;
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
            if (totalSize > this.nodes.length) {
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
                root.offspringPages[parentPos - 1].rotateRigth(parentPos - 1, parent);
            } else {
                if (root.offspringPages[parentPos - 1].size < root.offspringPages[parentPos + 1].size) {
                    root.offspringPages[parentPos + 1].rotateLeft(parentPos + 1, parent);
                } else {
                    root.offspringPages[parentPos - 1].rotateRigth(parentPos - 1, parent);
                }
            }
            this.performPostRemovingOperations();
        } else {
            int totalSize = left.size + middle.size + right.size + 1;
            if (totalSize > this.nodes.length * 2) {
                balancePages(left, middle, right, middlePagePos);
            } else {
                mergePages(left, middle, right, middlePagePos);
            }
        }
    }

    private void balancePages(BtreePage left, BtreePage right) {
        int difference = left.size - right.size;
        BtreePage parent = this.parentPage;
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

    private void mergeToRoot(BtreePage left, BtreePage right) {
        BtreePage root = this.parentPage;
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

    private void balancePages(BtreePage left, BtreePage middle, BtreePage right, int middlePagePos) {
        int minimumSize = this.nodes.length * 2 / 3;
        boolean change;
        do {
            change = false;
            if (left.size < minimumSize) {
                middle.rotateLeft(middlePagePos, left);
                change = true;
            } else if (right.size < minimumSize) {
                middle.rotateRigth(middlePagePos, right);
                change = true;
            } else if (middle.size < minimumSize) {
                if (left.size > right.size) {
                    left.rotateRigth(middlePagePos - 1, middle);
                } else {
                    right.rotateLeft(middlePagePos + 1, middle);
                }
                change = true;
            }
        } while (change);
    }

    private void mergePages(BtreePage left, BtreePage middle, BtreePage right, int middlePagePos) {
        BtreePage parent = this.parentPage;
        int sumSizes = left.size + middle.size + right.size + 1;
        if (sumSizes > this.nodes.length * 2) {
            throw new RuntimeException();
        }
        int rightFinalSize = sumSizes / 2;
        int leftFinalSize = sumSizes - rightFinalSize;
        for (int i = 0; i < sumSizes + 1; i++) {
            auxStates[i] = null;
            auxPages[i] = null;
        }
        auxPages[sumSizes + 2] = null;
        int j = 0;
        // merge
        for (int i = 0; i < left.size; i++) {
            auxStates[i] = left.nodes[i];
            auxPages[i] = left.offspringPages[i];
        }
        j = left.size;
        auxPages[j] = left.offspringPages[j];
        auxStates[j] = parent.nodes[middlePagePos - 1];
        j++;
        for (int i = 0; i < middle.size; i++) {
            auxStates[j] = middle.nodes[i];
            auxPages[j] = middle.offspringPages[i];
            j++;
        }
        auxPages[j] = middle.offspringPages[middle.size];
        auxStates[j] = parent.nodes[middlePagePos];
        j++;
        for (int i = 0; i < right.size; i++) {
            auxStates[j] = right.nodes[i];
            auxPages[j] = right.offspringPages[i];
            j++;
        }
        auxPages[j] = right.offspringPages[right.size];
        // split
        for (int i = 0; i < leftFinalSize; i++) {
            left.nodes[i] = auxStates[i];
            left.offspringPages[i] = auxPages[i];
        }
        j = leftFinalSize;
        left.offspringPages[j] = auxPages[j];
        left.size = leftFinalSize;
        // remove empty pos from parent
        parent.nodes[middlePagePos - 1] = auxStates[j];
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
            right.nodes[i] = auxStates[j];
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
        if (parent.size < (this.nodes.length * 2 / 3) && parent.parentPage != null) {
            parent.performPostRemovingOperations();
        }
    }

    @Override
    public String toString() {
        BtreePrinter v = new BtreePrinter();
        v.toString(true);
        this.visit(v, 0);
        return v.getString();
    }

    protected void checkIntegrity() {
        if (this.offspringPages[0] != null) {
            for (int i = 0; i <= this.size; i++) {
                if (this.offspringPages[i].parentPage != this) {
                    throw new RuntimeException();
                } else {
                    this.offspringPages[i].checkIntegrity();
                }
            }
            for (int i = 0; i < this.size; i++) {
                if (this.nodes[i] == null) {
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
        this.pagesFactory.releaseResource(page);
    }

    public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.nodes[i] = null;
        }
        for (int i = 0; i <= this.size; i++) {
            if (this.offspringPages[i] != null) {
                this.offspringPages[i].clear();
                this.pagesFactory.releaseResource(this.offspringPages[i]);
            }
            this.offspringPages[i] = null;
        }
    }

}
