package com.test.model;

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
}
