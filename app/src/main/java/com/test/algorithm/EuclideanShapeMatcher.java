package com.test.algorithm;

import com.test.model.GPoint2D;

import java.util.List;

public class EuclideanShapeMatcher extends ShapeMatcher {

    public EuclideanShapeMatcher(float threshold) {
        super(threshold);
    }

    @Override
    public boolean match(List<GPoint2D> a, List<GPoint2D> b) {
        return true;
    }
}
