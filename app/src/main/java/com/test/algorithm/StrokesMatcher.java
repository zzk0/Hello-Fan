package com.test.algorithm;

import com.test.model.GPoint2D;

import java.util.List;

public class StrokesMatcher {

    private float startAndEndThreshold;
    private float directionThreshold;
    private float shapeThreshold;

    public StrokesMatcher() {

    }

    public boolean match(List<GPoint2D> userStroke, List<GPoint2D> template) {
        return false;
    }

    // 两个笔画的起点、终点之间的平方距离小于一定范围就Match
    private boolean startAndEndMatch(GPoint2D uStart, GPoint2D uEnd, GPoint2D tStart, GPoint2D tEnd) {
        float startDistance = uStart.squareDistanceTo(tStart);
        float endDistance = uEnd.squareDistanceTo(tEnd);
        if (startDistance < startAndEndThreshold && endDistance < startAndEndThreshold) {
            return true;
        }
        return false;
    }

    // 方向正确才Match，比如从左往右和从右往左
    private boolean directionMatch(List<GPoint2D> userStroke, List<GPoint2D> template) {
        return false;
    }

    // shape相似度达到一定程度才match
    // shape相似度计算方法使用$1/$P/$Q来做，或者Discrete Frechet Distance
    private boolean shapeMatch(List<GPoint2D> userStroke, List<GPoint2D> template) {
        return false;
    }
}
