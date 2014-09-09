package com.jorgemf.util.shape.d3;

import com.jorgemf.util.math.Vector3f;

public class Frustum {

    public static final int RIGHT_PLANE = 0;
    public static final int LEFT_PLANE = 1;
    public static final int BOTTOM_PLANE = 2;
    public static final int TOP_PLANE = 3;
    public static final int FAR_PLANE = 4;
    public static final int NEAR_PLANE = 5;
    private float[][] frustum;
    private float radius;
    private float minimumRadius;
    private float distanceFromCenter;
    private Vector3f center;
    private Vector3f aux;
    private int[] nvertices;
    private int[] pvertices;

    public Frustum() {
        this.frustum = new float[6][4];
        this.center = new Vector3f();
        this.aux = new Vector3f();
        this.nvertices = new int[6];
        this.pvertices = new int[6];
    }

    private CONTAINS frustumPlanesContains(Sphere sphere) {
        return frustumPlanesContainsSphere(sphere.getCentre(), sphere.getRadius());
    }

    private CONTAINS frustumPlanesContainsSphere(Vector3f center, float radius) {
        float distance;
        for (int p = 0; p < 6; p++) {
            distance = frustum[p][0] * center.x + frustum[p][1] * center.y + frustum[p][2] * center.z + frustum[p][3];
            if (distance <= -radius) {
                return CONTAINS.OUTSIDE;
            } else if (Math.abs(distance) < radius) {
                return CONTAINS.INTERSECTION;
            }
        }
        return CONTAINS.INSIDE;
    }

    // culling using mask for previous calculated points and the diagonal more parallel to the normal of the plane in order to only use two points (n- and p-vertices)
    // see Optimized view frutsum culling algorithms for bounding boxes, 199, ulf assarsson and tomas moller
    private CONTAINS frustumPlanesContainsAABB(Vector3f[] points) {
        boolean intersec = false;
        Vector3f nvertex;
        Vector3f pvertex;
        for (int p = 0; p < 6; p++) {
            nvertex = points[this.nvertices[p]];
            if (frustum[p][0] * nvertex.x + frustum[p][1] * nvertex.y + frustum[p][2] * nvertex.z + frustum[p][3] <= 0) {
                return CONTAINS.OUTSIDE;
            }
            pvertex = points[this.pvertices[p]];
            if (frustum[p][0] * pvertex.x + frustum[p][1] * pvertex.y + frustum[p][2] * pvertex.z + frustum[p][3] <= 0) {
                intersec = true;
            }
        }
        if (intersec) {
            return CONTAINS.INTERSECTION;
        } else {
            return CONTAINS.INSIDE;
        }
    }

	/*
    private CONTAINS frustumPlanesContainsCuboid(Vector3f[] points){
		int pointsInside = 0;
		int planes;
		for(int i = 0; i < 8; i++){
			planes = 0;
			for (int p = 0; p < 6; p++){
				if (frustum[p][0] * points[i].x + frustum[p][1] * points[i].y + frustum[p][2] * points[i].z + frustum[p][3] > 0){
					planes++;
				}else{
					break;
				}
			}
			if(planes == 6){
				pointsInside++;
			}
		}
		if(pointsInside == 0){
			return CONTAINS.OUTSIDE;
		}else if(pointsInside == 8){
			return CONTAINS.INSIDE;
		}else{
			return CONTAINS.INTERSECTION;
		}
	}
	*/

    public float distanceToPlane(Vector3f point, int plane) {
        return (float) Math.sqrt((frustum[plane][0] * point.x + frustum[plane][1] * point.y + frustum[plane][2] * point.z + frustum[plane][3])
                / (frustum[plane][0] * frustum[plane][0] + frustum[plane][1] * frustum[plane][1] + frustum[plane][2] * frustum[plane][2]));
    }

    private CONTAINS contains(Vector3f[] points, Vector3f center, float radius) {
        CONTAINS contains;
        if (radius >= this.minimumRadius) {
            float d2 = this.center.distanceEuclidean2(center);
            float r = radius + this.radius;
            if (d2 < r * r) {
                contains = CONTAINS.INTERSECTION;
            } else {
                contains = CONTAINS.OUTSIDE;
            }
        } else {
            contains = frustumPlanesContainsSphere(center, radius);
            if (contains == CONTAINS.INTERSECTION) {
                contains = frustumPlanesContainsAABB(points);
            }
        }
        return contains;
    }

    // culling using mask for previous calculated points and the diagonal more parallel to the normal of the plane in order to only use two points (n- and p-vertices)
    // see Optimized view frutsum culling algorithms for bounding boxes, 199, ulf assarsson and tomas moller
    /*
    private CONTAINS frustumPlanesContainsAABB(Vector3f[] points, CONTAINS[][] mask){
		boolean intersec = false;
		int nvert;
		int pvert;
		Vector3f nvertex;
		Vector3f pvertex;
		for(int p = 0; p < 6; p++){
			nvert = this.nvertices[p];
			if(mask[p][nvert] == CONTAINS.INTERSECTION){
				nvertex = points[nvert];
				if (frustum[p][0] * nvertex.x + frustum[p][1] * nvertex.y + frustum[p][2] * nvertex.z + frustum[p][3] > 0){
					mask[p][nvert] = CONTAINS.INSIDE;
				}else{
					mask[p][nvert] = CONTAINS.OUTSIDE;
				}
			}
			if(mask[p][nvert] == CONTAINS.OUTSIDE){
				return CONTAINS.OUTSIDE;
			}
			pvert = this.pvertices[p];
			if(mask[p][pvert] == CONTAINS.INTERSECTION){
				pvertex = points[pvert];
				if (frustum[p][0] * pvertex.x + frustum[p][1] * pvertex.y + frustum[p][2] * pvertex.z + frustum[p][3] > 0){
					mask[p][pvert] = CONTAINS.INSIDE;
				}else{
					mask[p][pvert] = CONTAINS.OUTSIDE;
				}
			}
			if(mask[p][pvert] == CONTAINS.OUTSIDE){
				intersec = true;
			}
		}
		if(intersec){
			return CONTAINS.INTERSECTION;
		}else{
			return CONTAINS.INSIDE;
		}
	}
	*/

    public CONTAINS contains(Intersectable intersectable) {
        return contains(intersectable.getPoints(), intersectable.getCentre(), intersectable.getRadius());
    }

    public CONTAINS contains(AxisAlignedBoundingCuboid cuboid) {
        return contains(cuboid.getPoints(), cuboid.getPosition(), cuboid.getRadius());
    }

    public void extractFrustum(float[] PVMatrix) {
        // http://www.crownandcutlass.com/features/technicaldetails/frustum.html
        float module;

		/* Extract the numbers for the RIGHT plane */
        frustum[RIGHT_PLANE][0] = PVMatrix[3] - PVMatrix[0];
        frustum[RIGHT_PLANE][1] = PVMatrix[7] - PVMatrix[4];
        frustum[RIGHT_PLANE][2] = PVMatrix[11] - PVMatrix[8];
        frustum[RIGHT_PLANE][3] = PVMatrix[15] - PVMatrix[12];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]);
        frustum[RIGHT_PLANE][0] /= module;
        frustum[RIGHT_PLANE][1] /= module;
        frustum[RIGHT_PLANE][2] /= module;
        frustum[RIGHT_PLANE][3] /= module;

		/* Extract the numbers for the LEFT plane */
        frustum[LEFT_PLANE][0] = PVMatrix[3] + PVMatrix[0];
        frustum[LEFT_PLANE][1] = PVMatrix[7] + PVMatrix[4];
        frustum[LEFT_PLANE][2] = PVMatrix[11] + PVMatrix[8];
        frustum[LEFT_PLANE][3] = PVMatrix[15] + PVMatrix[12];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]);
        frustum[LEFT_PLANE][0] /= module;
        frustum[LEFT_PLANE][1] /= module;
        frustum[LEFT_PLANE][2] /= module;
        frustum[LEFT_PLANE][3] /= module;

		/* Extract the BOTTOM plane */
        frustum[BOTTOM_PLANE][0] = PVMatrix[3] + PVMatrix[1];
        frustum[BOTTOM_PLANE][1] = PVMatrix[7] + PVMatrix[5];
        frustum[BOTTOM_PLANE][2] = PVMatrix[11] + PVMatrix[9];
        frustum[BOTTOM_PLANE][3] = PVMatrix[15] + PVMatrix[13];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]);
        frustum[BOTTOM_PLANE][0] /= module;
        frustum[BOTTOM_PLANE][1] /= module;
        frustum[BOTTOM_PLANE][2] /= module;
        frustum[BOTTOM_PLANE][3] /= module;

		/* Extract the TOP plane */
        frustum[TOP_PLANE][0] = PVMatrix[3] - PVMatrix[1];
        frustum[TOP_PLANE][1] = PVMatrix[7] - PVMatrix[5];
        frustum[TOP_PLANE][2] = PVMatrix[11] - PVMatrix[9];
        frustum[TOP_PLANE][3] = PVMatrix[15] - PVMatrix[13];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]);
        frustum[TOP_PLANE][0] /= module;
        frustum[TOP_PLANE][1] /= module;
        frustum[TOP_PLANE][2] /= module;
        frustum[TOP_PLANE][3] /= module;

		/* Extract the FAR plane */
        frustum[FAR_PLANE][0] = PVMatrix[3] - PVMatrix[2];
        frustum[FAR_PLANE][1] = PVMatrix[7] - PVMatrix[6];
        frustum[FAR_PLANE][2] = PVMatrix[11] - PVMatrix[10];
        frustum[FAR_PLANE][3] = PVMatrix[15] - PVMatrix[14];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]);
        frustum[FAR_PLANE][0] /= module;
        frustum[FAR_PLANE][1] /= module;
        frustum[FAR_PLANE][2] /= module;
        frustum[FAR_PLANE][3] /= module;

		/* Extract the NEAR plane */
        frustum[NEAR_PLANE][0] = PVMatrix[3] + PVMatrix[2];
        frustum[NEAR_PLANE][1] = PVMatrix[7] + PVMatrix[6];
        frustum[NEAR_PLANE][2] = PVMatrix[11] + PVMatrix[10];
        frustum[NEAR_PLANE][3] = PVMatrix[15] + PVMatrix[14];

		/* Normalize the result */
        module = (float) Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2]);
        frustum[NEAR_PLANE][0] /= module;
        frustum[NEAR_PLANE][1] /= module;
        frustum[NEAR_PLANE][2] /= module;
        frustum[NEAR_PLANE][3] /= module;

        // calculate n-vertices and p-vertices of axis aligned bounding boxes or ocnodes
        float[] f;
        float dot, aux;
        int size = this.frustum.length;
        for (int i = 0; i < size; i++) {
            // 8 diagonals
            dot = 0;
            f = frustum[i];
            // +++
            aux = f[0] + f[1] + f[2];
            if (dot < aux) {
                dot = aux;
                nvertices[i] = 0;
                pvertices[i] = 7;
            } else if (dot < -aux) {
                dot = -aux;
                nvertices[i] = 7;
                pvertices[i] = 0;
            }
            // ++-
            aux = f[0] + f[1] - f[2];
            if (dot < aux) {
                dot = aux;
                nvertices[i] = 1;
                pvertices[i] = 6;
            } else if (dot < -aux) {
                dot = -aux;
                nvertices[i] = 6;
                pvertices[i] = 1;
            }
            // +-+
            aux = f[0] - f[1] + f[2];
            if (dot < aux) {
                dot = aux;
                nvertices[i] = 2;
                pvertices[i] = 5;
            } else if (dot < -aux) {
                dot = -aux;
                nvertices[i] = 5;
                pvertices[i] = 2;
            }
            // +--
            aux = f[0] - f[1] - f[2];
            if (dot < aux) {
                dot = aux;
                nvertices[i] = 3;
                pvertices[i] = 4;
            } else if (dot < -aux) {
                dot = -aux;
                nvertices[i] = 4;
                pvertices[i] = 3;
            }
        }
    }

    public void setSphereRadius(float closeSquareWidth, float closeSquareHeight, float closeDistance, float farDistance) {
        // sphere is calculated with center in the rect between the camera point and the far plane, the radius is equals from a point in that line between the planes (use two triangles).
        float h1 = (float) Math.sqrt(closeSquareWidth / 2 * closeSquareWidth / 2 + closeSquareHeight / 2 * closeSquareHeight / 2);
        float h2 = h1 * (farDistance / closeDistance);
        float d = farDistance - closeDistance;
        float d1 = (h2 * h2 - h1 * h1 + d * d) / (2 * d);
        this.distanceFromCenter = closeDistance + d1;
        this.radius = (float) Math.sqrt(h1 * h1 + d1 * d1);
        this.minimumRadius = d / 2;
    }

    public void setSphereCenter(Vector3f cameraPoint, Vector3f targetPoint) {
        this.aux.sub(targetPoint, cameraPoint);
        this.aux.normalize();
        this.aux.scale(this.distanceFromCenter);
        this.center.add(cameraPoint, aux);
    }

    public enum CONTAINS {OUTSIDE, INSIDE, INTERSECTION}

}
