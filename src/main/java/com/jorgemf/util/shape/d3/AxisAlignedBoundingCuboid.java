package com.jorgemf.util.shape.d3;

import com.jorgemf.util.math.Quaternion;
import com.jorgemf.util.math.Vector3f;

public class AxisAlignedBoundingCuboid implements Cuboid {

    private static float[] auxPoints = new float[8 * 4];
    private static float[] auxPointsResult = new float[8];
    private static float[] auxMatrix1 = new float[16];
    private static float[] auxMatrix2 = new float[16];
    private Vector3f position;
    private Vector3f dimensions;
    private Vector3f baseDimensions;
    private Vector3f displacement;
    private Vector3f baseDisplacement;
    private Quaternion rotation;
    private Vector3f minPoint;
    private Vector3f maxPoint;
    private boolean needUpdatePoints;
    private Vector3f[] points;

    private float radius;

    public AxisAlignedBoundingCuboid(float x, float y, float z, float disx, float disy, float disz) {
        this.position = new Vector3f();
        this.baseDimensions = new Vector3f(x, y, z);
        this.rotation = new Quaternion();
        this.dimensions = new Vector3f(x, y, z);
        this.displacement = new Vector3f(disx, disy, disz);
        this.baseDisplacement = new Vector3f(disx, disy, disz);
        this.maxPoint = new Vector3f();
        this.minPoint = new Vector3f();
        this.points = new Vector3f[8];
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = new Vector3f();
        }
        this.needUpdatePoints = true;
    }

    public static AxisAlignedBoundingCuboid computeBoundingCuboid(float[] points, Vector3f scale) {
        float maxX = points[0];
        float minX = points[0];
        float maxY = points[1];
        float minY = points[1];
        float maxZ = points[2];
        float minZ = points[2];
        int size = points.length / 3;
        for (int i = 1; i < size; i++) {
            if (points[i * 3] < minX) {
                minX = points[i * 3];
            } else if (points[i * 3] > maxX) {
                maxX = points[i * 3];
            }
            if (points[i * 3 + 1] < minY) {
                minY = points[i * 3 + 1];
            } else if (points[i * 3 + 1] > maxY) {
                maxY = points[i * 3 + 1];
            }
            if (points[i * 3 + 2] < minZ) {
                minZ = points[i * 3 + 2];
            } else if (points[i * 3 + 2] > maxZ) {
                maxZ = points[i * 3 + 2];
            }
        }
        maxX *= scale.x;
        maxY *= scale.y;
        maxZ *= scale.z;
        minX *= scale.x;
        minY *= scale.y;
        minZ *= scale.z;
        float x = maxX - minX;
        float y = maxY - minY;
        float z = maxZ - minZ;
        float disx = (maxX + minX) / 2;
        float disy = (maxY + minY) / 2;
        float disz = (maxZ + minZ) / 2;
        return new AxisAlignedBoundingCuboid(x, y, z, disx, disy, disz);
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        float diffx = position.x - this.position.x;
        float diffy = position.y - this.position.y;
        float diffz = position.z - this.position.z;
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;

        this.minPoint.x += diffx;
        this.minPoint.y += diffy;
        this.minPoint.z += diffz;
        this.maxPoint.x += diffx;
        this.maxPoint.y += diffy;
        this.maxPoint.z += diffz;
    }

    public void scale(float s) {
        this.dimensions.x = s * baseDimensions.x;
        this.dimensions.y = s * baseDimensions.y;
        this.dimensions.z = s * baseDimensions.z;
        this.displacement.x = s * baseDisplacement.x;
        this.displacement.y = s * baseDisplacement.y;
        this.displacement.z = s * baseDisplacement.z;
        this.needUpdatePoints = true;
    }

    public void scale(float x, float y, float z) {
        this.dimensions.x = x * baseDimensions.x;
        this.dimensions.y = y * baseDimensions.y;
        this.dimensions.z = z * baseDimensions.z;
        this.displacement.x = x * baseDisplacement.x;
        this.displacement.y = y * baseDisplacement.y;
        this.displacement.z = z * baseDisplacement.z;
        this.needUpdatePoints = true;
    }

    public Vector3f getDimensions() {
        return this.dimensions;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation.set(rotation);
        this.needUpdatePoints = true;
    }

    public Vector3f getMinPoint() {
        if (this.needUpdatePoints) {
            setPoints();
        }
        return this.minPoint;
    }

    public Vector3f getMaxPoint() {
        if (this.needUpdatePoints) {
            setPoints();
        }
        return this.maxPoint;
    }

    public Vector3f[] getPoints() {
        if (this.needUpdatePoints) {
            setPoints();
        }
        return this.points;
    }

    public float getRadius() {
        if (this.needUpdatePoints) {
            setPoints();
        }
        return this.radius;
    }

    private void setPoints() {
        float maxX = this.displacement.x + this.dimensions.x / 2;
        float minX = this.displacement.x - this.dimensions.x / 2;
        float maxY = this.displacement.y + this.dimensions.y / 2;
        float minY = this.displacement.y - this.dimensions.y / 2;
        float maxZ = this.displacement.z + this.dimensions.z / 2;
        float minZ = this.displacement.z - this.dimensions.z / 2;

        synchronized (AxisAlignedBoundingCuboid.class) {
            auxPoints[0] = maxX;
            auxPoints[1] = maxY;
            auxPoints[2] = maxZ;
            auxPoints[3] = 1;

            auxPoints[4] = maxX;
            auxPoints[5] = minY;
            auxPoints[6] = maxZ;
            auxPoints[7] = 1;

            auxPoints[8] = maxX;
            auxPoints[9] = maxY;
            auxPoints[10] = minZ;
            auxPoints[11] = 1;

            auxPoints[12] = maxX;
            auxPoints[13] = minY;
            auxPoints[14] = minZ;
            auxPoints[15] = 1;

            auxPoints[16] = minX;
            auxPoints[17] = maxY;
            auxPoints[18] = maxZ;
            auxPoints[19] = 1;

            auxPoints[20] = minX;
            auxPoints[21] = minY;
            auxPoints[22] = maxZ;
            auxPoints[23] = 1;

            auxPoints[24] = minX;
            auxPoints[25] = maxY;
            auxPoints[26] = minZ;
            auxPoints[27] = 1;

            auxPoints[28] = minX;
            auxPoints[29] = minY;
            auxPoints[30] = minZ;
            auxPoints[31] = 1;

            Matrix.setIdentityM(auxMatrix1, 0);
            Matrix.translateM(auxMatrix1, 0, position.x, position.y, position.z);
            Matrix.multiplyMM(auxMatrix2, 0, auxMatrix1, 0, rotation.getMatrix(), 0);

            int size = auxPoints.length;

            Matrix.multiplyMV(auxPointsResult, 0, auxMatrix2, 0, auxPoints, 0);
            this.maxPoint.x = auxPointsResult[0];
            this.maxPoint.y = auxPointsResult[1];
            this.maxPoint.z = auxPointsResult[2];
            this.minPoint.x = auxPointsResult[0];
            this.minPoint.y = auxPointsResult[1];
            this.minPoint.z = auxPointsResult[2];
            for (int i = 4; i < size; i += 4) {
                Matrix.multiplyMV(auxPointsResult, 0, auxMatrix2, 0, auxPoints, i);
                if (this.maxPoint.x < auxPointsResult[0]) {
                    this.maxPoint.x = auxPointsResult[0];
                } else if (this.minPoint.x > auxPointsResult[0]) {
                    this.minPoint.x = auxPointsResult[0];
                }
                if (this.maxPoint.y < auxPointsResult[1]) {
                    this.maxPoint.y = auxPointsResult[1];
                } else if (this.minPoint.y > auxPointsResult[1]) {
                    this.minPoint.y = auxPointsResult[1];
                }
                if (this.maxPoint.z < auxPointsResult[2]) {
                    this.maxPoint.z = auxPointsResult[2];
                } else if (this.minPoint.z > auxPointsResult[2]) {
                    this.minPoint.z = auxPointsResult[2];
                }
            }
        }

        this.points[0].set(maxPoint.x, maxPoint.y, maxPoint.z);
        this.points[1].set(maxPoint.x, maxPoint.y, minPoint.z);
        this.points[2].set(maxPoint.x, minPoint.y, maxPoint.z);
        this.points[3].set(maxPoint.x, minPoint.y, minPoint.z);
        this.points[4].set(minPoint.x, maxPoint.y, maxPoint.z);
        this.points[5].set(minPoint.x, maxPoint.y, minPoint.z);
        this.points[6].set(minPoint.x, minPoint.y, maxPoint.z);
        this.points[7].set(minPoint.x, minPoint.y, minPoint.z);

        this.radius = this.minPoint.distanceEuclidean(this.maxPoint) / 2;
        this.needUpdatePoints = false;
    }

}
