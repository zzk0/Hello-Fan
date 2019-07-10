/*
一些静态的几何相关的算法
*/

package com.test.algorithm;

import com.test.model.entity.GPoint2D;
import com.test.model.entity.Vector2;

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

    /**
     * 余弦相似度，暂未实现
     * @param a
     * @param b
     * @return
     */
    public static float cosineSimilarity(Vector2 a, Vector2 b) {
        float atb = a.x * b.x + a.y * b.y;
        float aa = a.length();
        float bb = b.length();
        return atb / (aa * bb);
    }

    /**
     * 将点归一化到[-1, 1]x[-1, 1]这个范围
     * @param points
     */
    public static void scaleToCanonical(List<GPoint2D> points) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for (GPoint2D point : points) {
            if (minX > point.x) minX = point.x;
            if (maxX < point.x) maxX = point.x;
            if (minY > point.y) minY = point.y;
            if (maxY < point.y) maxY = point.y;
        }
        float width = maxX - minX;
        float height = maxY - minY;
        for (GPoint2D point : points) {
            point.x = point.x * (1 / width);
            point.y = point.y * (1 / height);
        }
    }

    /**
     * 计算两个手势之间的欧几里得距离
     * @param candidate
     * @param sample
     * @return
     */
    public static float euclideanDistance(List<GPoint2D> candidate, List<GPoint2D> sample) {
        float sum = 0.0f;
        for (int i = 0; i < candidate.size(); i++) {
            sum = sum + candidate.get(i).distanceTo(sample.get(i));
        }
        return sum / candidate.size();
    }

    /**
     * 计算平方欧几里得距离，不做开方运算的目的是减少运算量
     * @param candidate
     * @param sample
     * @return
     */
    public static float squareEuclideanDistance(List<GPoint2D> candidate, List<GPoint2D> sample) {
        float sum = 0.0f;
        for (int i = 0; i < candidate.size(); i++) {
            sum = sum + candidate.get(i).squareDistanceTo(sample.get(i));
        }
        return sum / candidate.size();
    }
}
