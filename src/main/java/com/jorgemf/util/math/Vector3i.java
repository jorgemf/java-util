package com.jorgemf.util.math;

public class Vector3i {

    public int x;

    public int y;

    public int z;

    public Vector3i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3i(int[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i(Vector3i v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final void set(Vector3i v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public final boolean equals(Vector3i other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public final void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void add(Vector3i v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public final void add(Vector3i v, Vector3i w) {
        this.x = v.x + w.x;
        this.y = v.y + w.y;
        this.z = v.z + w.z;
    }

    public final void sub(Vector3i v, Vector3i w) {
        this.x = v.x - w.x;
        this.y = v.y - w.y;
        this.z = v.z - w.z;
    }

    public final void sub(Vector3i v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public final float distanceEuclidean(Vector3i point) {
        float dx = point.x - x;
        float dy = point.y - y;
        float dz = point.z - z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public final float distanceEuclidean2(Vector3i point) {
        float dx = point.x - x;
        float dy = point.y - y;
        float dz = point.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public final float distanceManhatan(Vector3i point) {
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

}
