/*
一些静态的几何相关的算法
*/

package com.test.algorithm;

import com.test.model.GPoint2D;

import java.util.ArrayList;
import java.util.List;

public class Geometry {

    public static float lengthOfPoints(List<GPoint2D> points) {
        float sum = 0.0f;
        for (int i = 0; i < points.size() - 1; i++) {
            sum = sum + points.get(i).distanceTo(points.get(i + 1));
        }
        return sum;
    }

    public static List<GPoint2D> resample(List<GPoint2D> input, int sampleNumber) {
        // Copy the data to prevent change origin input
        List<GPoint2D> points = new ArrayList<>();
        for (GPoint2D point : input) {
            points.add(new GPoint2D(point.x, point.y));
        }

        float equidistance = lengthOfPoints(points) / (sampleNumber - 1);
        float acclumateDis = 0.0f;
        List<GPoint2D> newPoints = new ArrayList<>();
        newPoints.add(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            GPoint2D point0 = points.get(i - 1);
            GPoint2D point1 = points.get(i);
            float distance = point0.distanceTo(point1);
            if (acclumateDis + distance >= equidistance) {
                float newPointX = point0.x + ((equidistance - acclumateDis) / distance) * (point1.x - point0.x);
                float newPointY = point0.y + ((equidistance - acclumateDis) / distance) * (point1.y - point0.y);
                GPoint2D newPoint = new GPoint2D(newPointX, newPointY);
                newPoints.add(newPoint);
                points.add(i, newPoint);
                acclumateDis = 0;
            }
            else {
                acclumateDis = acclumateDis + distance;
            }
        }

        // the last may not include due to float compare
        if (newPoints.size() != sampleNumber) {
            newPoints.add(points.get(points.size() - 1));
        }

        return newPoints;
    }
}
