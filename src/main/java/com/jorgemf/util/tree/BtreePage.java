package com.jorgemf.util.tree;

import com.jorgemf.util.ResourcesFactory;

import java.util.Vector;

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

    private BtreePage(BtreePage<k> pageToClone, BtreePage<k> parentPage, ResourcesFactory<BtreePage<k>> resourcesFactory) {
        this.resourcesFactory = resourcesFactory;
        this.parentPage = parentPage;
        parentPosition = pageToClone.parentPosition;
        size = pageToClone.size;
        //noinspection unchecked
        nodes = (k[]) new Object[pageToClone.nodes.length];
        System.arraycopy(pageToClone.nodes, 0, nodes, 0, nodes.length);
        //noinspection unchecked
        offspringPages = (BtreePage<k>[]) new BtreePage[pageToClone.offspringPages.length];
        for (int i = 0; i < offspringPages.length; i++) {
            if (pageToClone.offspringPages[i] != null) {
                offspringPages[i] = pageToClone.offspringPages[i].clone(this, resourcesFactory);
            }
        }
    }

    protected BtreePage<k> clone(BtreePage<k> parentPage, ResourcesFactory<BtreePage<k>> resourcesFactory) {
        return new BtreePage<k>(this, parentPage, resourcesFactory);
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

    protected k getFirstFromPage() {
        return nodes[0];
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

    protected boolean contains(k object) {
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

    protected boolean remove(k object) {
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
            if (size < (nodes.length * 2 / 3)) {
                performPostRemovingOperations();
            }
        } else {
            BtreePage<k> lastPage = offspringPages[pos].getLastPage();
            nodes[pos] = lastPage.nodes[lastPage.size - 1];
            lastPage.removeFromThisPage(lastPage.size - 1);
        }
    }

    private void rotateRight(int nodePos) {
        BtreePage<k> leftPage = offspringPages[nodePos];
        BtreePage<k> rightPage = offspringPages[nodePos + 1];
        rightPage.shiftRight(0, 1);
        rightPage.nodes[0] = nodes[nodePos];
        nodes[nodePos] = leftPage.nodes[leftPage.size - 1];
        leftPage.nodes[leftPage.size - 1] = null;
        if (!rightPage.isLeave()) {
            BtreePage<k>[] rightPages = rightPage.offspringPages;
            BtreePage<k>[] leftPages = leftPage.offspringPages;

            rightPages[1] = rightPages[0];
            rightPages[1].parentPosition = 1;
            rightPages[0] = leftPages[leftPage.size];
            rightPages[0].setParentPage(rightPage, 0);

            leftPages[leftPage.size] = null;
        }
        leftPage.size--;
    }

    private void rotateLeft(int nodePos) {
        BtreePage<k> leftPage = offspringPages[nodePos];
        BtreePage<k> rightPage = offspringPages[nodePos + 1];
        leftPage.nodes[leftPage.size] = nodes[nodePos];
        nodes[nodePos] = rightPage.nodes[0];
        leftPage.size++;
        if (!rightPage.isLeave()) {
            BtreePage<k>[] rightPages = rightPage.offspringPages;
            BtreePage<k>[] leftPages = leftPage.offspringPages;

            leftPages[leftPage.size] = rightPages[0];
            leftPages[leftPage.size].setParentPage(leftPage, leftPage.size);
        }
        rightPage.shiftLeft(1, 1);
    }

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
            int nodesLeftSecondPart = nodesFirstPage - objectPosition - 1;
            System.arraycopy(nodes, objectPosition, left.nodes, objectPosition + 1, nodesLeftSecondPart);
            // root
            nodes[0] = nodes[nodesFirstPage - 1];
            // right page
            System.arraycopy(nodes, nodesFirstPage, right.nodes, 0, nodesSecondPage);
            if (!isLeave) {
                // left page
                System.arraycopy(offspringPages, 0, left.offspringPages, 0, objectPosition + 1);
                left.offspringPages[objectPosition + 1] = page;
                System.arraycopy(offspringPages, objectPosition + 1, left.offspringPages, objectPosition + 2, nodesLeftSecondPart);
                // right page
                System.arraycopy(offspringPages, nodesFirstPage, right.offspringPages, 0, nodesSecondPage + 1);
            }
        } else if (objectPosition > nodesFirstPage) {
            // object to right

            // left page
            System.arraycopy(nodes, 0, left.nodes, 0, nodesFirstPage);
            // root
            nodes[0] = nodes[nodesFirstPage];
            // right page
            int nodesRightFirstPart = objectPosition - nodesFirstPage - 1;
            System.arraycopy(nodes, nodesFirstPage + 1, right.nodes, 0, nodesRightFirstPart);
            right.nodes[nodesRightFirstPart] = object;
            int nodesRightSecondPart = nodesSecondPage - objectPosition + nodesFirstPage + 1;
            System.arraycopy(nodes, nodesRightFirstPart + 1, right.nodes, nodesRightFirstPart + 1, nodesRightSecondPart);

            if (!isLeave) {
                // left page
                System.arraycopy(offspringPages, 0, left.offspringPages, 0, nodesFirstPage + 1);
                // right page
                System.arraycopy(offspringPages, nodesFirstPage + 1, right.offspringPages, 0, nodesRightFirstPart + 1);
                right.offspringPages[nodesRightFirstPart] = page;
                System.arraycopy(offspringPages, objectPosition + 1, right.offspringPages, nodesRightFirstPart + 2, nodesRightSecondPart);
            }
        } else {
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
                // right page
                right.offspringPages[0] = page;
                System.arraycopy(offspringPages, nodesFirstPage + 1, right.offspringPages, 1, nodesSecondPage);
            }

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
            BtreePage<k>[] leftPages = left.offspringPages;
            for (int i = 0; i < left.size + 1; i++) {
                leftPages[i].setParentPage(left, i);
            }
            BtreePage<k>[] rightPages = right.offspringPages;
            for (int i = 0; i < right.size + 1; i++) {
                rightPages[i].setParentPage(right, i);
            }
        }
    }

    private void split(int pagePosition, int objectPositionInMergedPage, k object, BtreePage<k> page) {
        BtreePage<k> leftPage = offspringPages[pagePosition];
        BtreePage<k> rightPage = offspringPages[pagePosition + 1];
        k nodeInsertParent;
        BtreePage<k> centrePage = resourcesFactory.getResource();
        int totalSize = leftPage.size + rightPage.size;
        int nodesFirstPage = totalSize / 3;
        int nodesSecondPage = nodesFirstPage;
        int nodesThirdPage = totalSize - nodesFirstPage * 2;
        boolean isLeave = leftPage.isLeave();

        if (objectPositionInMergedPage < nodesFirstPage) {
            // inside left page
            nodeInsertParent = leftPage.nodes[nodesFirstPage - 1];
            int nodesFromLeft = leftPage.size - nodesFirstPage - 1;
            int nodesFromRight = rightPage.size - nodesThirdPage;

            // centre page
            System.arraycopy(leftPage.nodes, nodesFirstPage + 1, centrePage.nodes, 0, nodesFromLeft);
            centrePage.nodes[nodesFromLeft] = parentPage.nodes[objectPositionInMergedPage];
            parentPage.nodes[objectPositionInMergedPage] = rightPage.nodes[nodesFromRight - 1];
            System.arraycopy(rightPage.nodes, 0, centrePage.nodes, nodesFromLeft + 1, nodesFromRight - 1);
            if (isLeave) {
                System.arraycopy(leftPage.offspringPages, nodesFirstPage + 1, centrePage.offspringPages, 0, nodesFromLeft + 1);
                System.arraycopy(rightPage.offspringPages, 0, centrePage.offspringPages, nodesFromLeft + 1, nodesFromRight);
            }

            // left
            leftPage.insert(objectPositionInMergedPage, object, page);

            // right
            rightPage.shiftLeft(nodesFromRight, nodesFromRight);

            // sizes and cleanup
            for (int i = nodesFirstPage; i < leftPage.size; i++) {
                leftPage.nodes[i] = null;
                leftPage.offspringPages[i + 1] = null;
            }
            // shiftLeft makes the clean up for the right page
            leftPage.size = nodesFirstPage;
            centrePage.size = nodesSecondPage;
            rightPage.size = nodesThirdPage;

        } else if (objectPositionInMergedPage == nodesFirstPage) {
            // in parent page in left position
            nodeInsertParent = object;
            int nodesFromLeft = leftPage.size - nodesFirstPage;
            int nodesFromRight = rightPage.size - nodesThirdPage;

            // centre page
            System.arraycopy(leftPage.nodes, nodesFirstPage, centrePage.nodes, 0, nodesFromLeft);
            centrePage.nodes[nodesFromLeft] = parentPage.nodes[objectPositionInMergedPage];
            parentPage.nodes[objectPositionInMergedPage] = rightPage.nodes[nodesFromRight - 1];
            System.arraycopy(rightPage.nodes, 0, centrePage.nodes, nodesFromLeft + 1, nodesFromRight - 1);
            if (isLeave) {
                centrePage.offspringPages[0] = page;
                System.arraycopy(leftPage.offspringPages, nodesFirstPage + 1, centrePage.offspringPages, 1, nodesFromLeft);
                System.arraycopy(rightPage.offspringPages, 0, centrePage.offspringPages, nodesFromLeft + 1, nodesFromRight);
            }
            // left
            // keep at it is without the moved nodes

            // right
            nodes[pagePosition] = rightPage.nodes[nodesFromRight];
            rightPage.shiftLeft(nodesFromRight + 1, nodesFromRight + 1);
            rightPage.offspringPages[0] = page;

            // sizes and cleanup
            for (int i = nodesFirstPage; i < leftPage.size; i++) {
                leftPage.nodes[i] = null;
                leftPage.offspringPages[i + 1] = null;
            }
            // shiftLeft makes the clean up for the right page
            leftPage.size = nodesFirstPage;
            centrePage.size = nodesSecondPage;
            rightPage.size = nodesThirdPage;
        } else if (objectPositionInMergedPage < nodesFirstPage + 1 + nodesSecondPage) {
            // inside center page
            nodeInsertParent = leftPage.nodes[nodesFirstPage];
            int nodesFromLeft = leftPage.size - nodesFirstPage;
            int nodesFromRight = rightPage.size - nodesThirdPage;
            // TODO


            // inside the left nodes, before the parent node, after the parent node, inside the right nodes


        } else if (objectPositionInMergedPage == nodesFirstPage + 1 + nodesSecondPage) {
            // in parent page in right position
            nodeInsertParent = object;
            int nodesFromLeft = leftPage.size - nodesFirstPage;
            int nodesFromRight = rightPage.size - nodesThirdPage;
            // TODO

        } else {
            // inside right page
            // TODO

        }
        if (isLeave) {
            for (int i = 0; i < centrePage.size + 1; i++) {
                centrePage.offspringPages[i].setParentPage(this, i);
            }
        }

        parentPage.insert(pagePosition, nodeInsertParent, centrePage);
    }

    private void balanceThreePages(int middlePagePosition) {
        // check if merge makes sense
        if (offspringPages[middlePagePosition - 1].size + offspringPages[middlePagePosition].size + offspringPages[middlePagePosition + 1].size + 1 <= nodes.length * 2) {
            merge(middlePagePosition);
        } else {
            int minNodes = nodes.length * 2 / 3;
            // rotate to left until everything is ok
            if (offspringPages[middlePagePosition - 1].size < minNodes) {
                rotateLeft(middlePagePosition - 1);
            }
            if (offspringPages[middlePagePosition].size < minNodes) {
                rotateLeft(middlePagePosition);
            }
            // rotate to the right until everything is ok
            if (offspringPages[middlePagePosition + 1].size < minNodes) {
                rotateRight(middlePagePosition);
            }
            if (offspringPages[middlePagePosition].size < minNodes) {
                rotateRight(middlePagePosition - 1);
            }
        }
    }

    private void balanceTwoPagesRoot() {
        if (offspringPages[0].size + offspringPages[1].size < nodes.length) {
            mergeRoot();
        } else {
            int minNodes = nodes.length * 2 / 3;
            if (offspringPages[0].size < minNodes) {
                rotateRight(0);
            } else if (offspringPages[1].size < minNodes) {
                rotateLeft(1);
            }
        }
    }

    private void performPostRemovingOperations() {
        if (parentPage.parentPage == null && parentPage.size < 3) {
            // special case parent page is root and could merge it self
            parentPage.balanceTwoPagesRoot();
        } else if (parentPosition == 0) {
            // first page in parent
            parentPage.balanceThreePages(1);
        } else if (parentPosition == parentPage.size) {
            // last page in parent
            parentPage.balanceThreePages(parentPosition - 1);
        } else {
            // middle page in parent
            parentPage.balanceThreePages(parentPosition);
        }
    }

    private void mergeRoot() {
        BtreePage<k> left = offspringPages[0];
        BtreePage<k> right = offspringPages[1];
        if (size != 1 || left.size + right.size + 1 > nodes.length) {
            throw new RuntimeException();
        }
        System.arraycopy(left.nodes, 0, nodes, 0, left.size);
        nodes[left.size] = nodes[0];
        System.arraycopy(right.nodes, 0, nodes, left.size + 1, right.size);

        if (!left.isLeave()) {
            System.arraycopy(left.offspringPages, 0, offspringPages, 0, left.size + 1);
            System.arraycopy(right.offspringPages, 0, offspringPages, left.size + 1, right.size + 1);
            for (int i = 0; i < size + 1; i++) {
                offspringPages[i].setParentPage(this, i);
            }
        }
        size = left.size + right.size + 1;
        clear(left);
        clear(right);
    }

    private void merge(int middlePagePos) {
        BtreePage<k> left = offspringPages[middlePagePos - 1];
        BtreePage<k> middle = offspringPages[middlePagePos];
        BtreePage<k> right = offspringPages[middlePagePos + 1];
        int sumSizes = left.size + middle.size + right.size + 1;
        if (sumSizes > nodes.length * 2) {
            throw new RuntimeException();
        }
        int leftNodes = nodes.length - left.size - 1;
        int rightNodes = middle.size - leftNodes - 1;
        left.nodes[left.size] = nodes[middlePagePos];
        System.arraycopy(middle.nodes, 0, left.nodes, left.size + 1, leftNodes);
        right.shiftRight(0, rightNodes);
        right.nodes[rightNodes - 1] = nodes[middlePagePos];
        System.arraycopy(middle.nodes, leftNodes + 2, right.nodes, 0, rightNodes - 1);
        nodes[middlePagePos] = middle.nodes[leftNodes + 1];

        if (!middle.isLeave()) {
            System.arraycopy(middle.offspringPages, 0, left.offspringPages, left.size + 1, leftNodes + 1);
            System.arraycopy(middle.offspringPages, leftNodes + 1, right.offspringPages, 0, rightNodes + 1);
            for (int i = left.size + 1; i < left.size + leftNodes + 1; i++) {
                left.offspringPages[i].setParentPage(this, i);
            }
            for (int i = 0; i < rightNodes + 1; i++) {
                right.offspringPages[i].setParentPage(this, i);
            }
        }
        left.size += leftNodes + 1;
        right.size += rightNodes + 1;

        shiftLeft(middlePagePos, 1);
        clear(middle);
    }

    public String getDebugString() {
        BtreePrinter v = new BtreePrinter();
        visitInOrder(v, 0);
        return v.getString();
    }

    protected void checkIntegrity() {
        if (parentPage != null) {
            if (parentPage.offspringPages[parentPosition] != this) {
                throw new RuntimeException();
            }
        }
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

    protected void clear(BtreePage<k> page) {
        for (int i = 0; i < page.nodes.length; i++) {
            page.nodes[i] = null;
            page.offspringPages[i] = null;
        }
        page.offspringPages[page.nodes.length] = null;
        page.size = 0;
        resourcesFactory.releaseResource(page);
    }

    protected void clear() {
        for (int i = 0; i < size; i++) {
            nodes[i] = null;
        }
        for (int i = 0; i <= size; i++) {
            if (offspringPages[i] != null) {
                offspringPages[i].clear();
                resourcesFactory.releaseResource(offspringPages[i]);
            }
            offspringPages[i] = null;
        }
    }

    private class BtreePrinter implements BtreeVisitor<k> {

        private Vector<StringBuilder> stringBuilders;

        private int charactersAdded;

        private BtreePrinter() {
            stringBuilders = new Vector<StringBuilder>();
            charactersAdded = 0;
        }

        @Override
        public void visit(k object, int deep) {
            while (stringBuilders.size() < deep) {
                StringBuilder sb = new StringBuilder();
                stringBuilders.add(sb);
            }
            StringBuilder sb = stringBuilders.get(deep);
            while (sb.length() < charactersAdded) {
                sb.append(' ');
            }
            String objectString = object.toString() + " ";
            sb.append(objectString);
            charactersAdded += objectString.length();
        }

        protected String getString() {
            StringBuilder sb = new StringBuilder();
            for (StringBuilder stringBuilder : stringBuilders) {
                sb.append(sb).append('\n');
            }
            return sb.toString();
        }
    }

}
