package com.jorgemf.util.math;

public class Vector3f {

    public float x;

    public float y;

    public float z;

    public Vector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3f(float[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final void set(Vector3f v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final boolean equals(Vector3f other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public final void add(Vector3f v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public final void add(Vector3f v, Vector3f w) {
        this.x = v.x + w.x;
        this.y = v.y + w.y;
        this.z = v.z + w.z;
    }

    public final void sub(Vector3f v, Vector3f w) {
        this.x = v.x - w.x;
        this.y = v.y - w.y;
        this.z = v.z - w.z;
    }

    public final void sub(Vector3f v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public final void cross(Vector3f v, Vector3f w) {
        this.x = v.y * w.z - v.z * w.y;
        this.y = w.x * v.z - w.z * v.x;
        this.z = v.x * w.y - v.y * w.x;
    }

    public final float dot(Vector3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public final void normalize() {
        float norm = (1.0f / (float) Math.sqrt(x * x + y * y + z * z));
        this.x *= norm;
        this.y *= norm;
        this.z *= norm;
    }

    public final void scale(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }

    public final float distanceEuclidean(Vector3f point) {
        float dx = point.x - x;
        float dy = point.y - y;
        float dz = point.z - z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public final float distanceEuclidean2(Vector3f point) {
        float dx = point.x - x;
        float dy = point.y - y;
        float dz = point.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public final float distanceManhatan(Vector3f point) {
        float dx = point.x - x;
        float dy = point.y - y;
        float dz = point.z - z;
        return Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
    }

    public String toString() {
        return "[x:" + x + ",y:" + y + ",z:" + z + "]";
    }

    public final float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public final float length2() {
        return x * x + y * y + z * z;
    }

    public final void rotate(Quaternion rotation) {
        float[] matrix = rotation.getMatrix();
        float newX = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
        float newY = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
        float newZ = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
        x = newX;
        y = newY;
        z = newZ;
    }

}
