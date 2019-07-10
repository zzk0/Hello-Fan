/*
手势二维点。
*/

package com.test.model.entity;

public class GPoint2D {
    public float x;
    public float y;
    public int strokeId;

    public GPoint2D(float xx, float yy) {
        this.x = xx;
        this.y = yy;
        this.strokeId = 0;
    }

    public GPoint2D(float xx, float yy, int id) {
        this.x = xx;
        this.y = yy;
        this.strokeId = id;
    }

    public float distanceTo(GPoint2D another) {
        float disX = this.x - another.x;
        float disY = this.y - another.y;
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    public float squareDistanceTo(GPoint2D another) {
        float disX = this.x - another.x;
        float disY = this.y - another.y;
        return (disX * disX + disY * disY);
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof GPoint2D) {
            GPoint2D point = (GPoint2D) anObject;
            if (Math.abs(this.x - point.x) < 0.0001 && Math.abs(this.y - point.y) < 0.0001) {
                return true;
            }
        }
        return false;
    }

    public void scale(float sx, float sy) {
        this.x = this.x * sx;
        this.y = this.y * sy;
    }
}
