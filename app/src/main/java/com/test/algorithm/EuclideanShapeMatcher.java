package com.test.algorithm;

import com.test.model.GPoint2D;

import java.util.ArrayList;
import java.util.List;

public class EuclideanShapeMatcher extends ShapeMatcher {

    public EuclideanShapeMatcher(float threshold) {
        super(threshold);
    }

    @Override
    public boolean match(List<GPoint2D> a, List<GPoint2D> b) {
        List<GPoint2D> candidate = Geometry.resample(a, 32);
        List<GPoint2D> sample = Geometry.resample(b, 32);
        Geometry.scaleToCanonical(candidate);
        Geometry.scaleToCanonical(sample);
        float dis = Geometry.squareEuclideanDistance(candidate, sample);
        if (dis < threshold) {
            return true;
        }
        return false;
    }
}
