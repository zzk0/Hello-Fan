package com.test.model.entity;

public class Vector2 {

    public float x;
    public float y;

    public Vector2(float xx, float yy) {
        this.x = xx;
        this.y = yy;
    }

    public Vector2(GPoint2D a, GPoint2D b) {
        this.x = b.x - a.x;
        this.y = b.y - a.y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }
}
