package com.jorgemf.util.math;

public class Vector2f {

    public float x;

    public float y;

    public Vector2f() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final boolean equals(Vector2f other) {
        return this.x == other.x && this.y == other.y;
    }

    public final void add(Vector2f other) {
        this.x += other.x;
        this.y += other.y;
    }

    public final float distanceEuclidean(Vector2f point) {
        float dx = point.x - x;
        float dy = point.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public final float distanceManhatan(Vector2f point) {
        float dx = point.x - x;
        float dy = point.y - y;
        return Math.abs(dx) + Math.abs(dy);
    }

    public String toString() {
        return "[x:" + x + ",y:" + y + "]";
    }

}