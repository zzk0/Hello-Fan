/*
匹配的思路参考hanzi-writer的:
1. 匹配起点终点
2. 方向匹配
3. 笔画长度匹配
4. 笔画形状匹配

如果某一项匹配值超过阈值，返回否。

这个Matcher的一个假设是：用户写下的笔画点的坐标，和模板点的坐标，这两者所在的坐标系一致。
*/

package com.test.algorithm;

import com.test.model.GPoint2D;
import com.test.model.Vector2;

import java.util.List;

public class StrokesMatcher {

    private float startAndEndThreshold;
    private float directionThreshold;
    private float lengthThreshold;
    private float shapeThreshold;

    private ShapeMatcher shapeMatcher;

    public StrokesMatcher() {
        startAndEndThreshold = 5000.0f;
        directionThreshold = 0.5f;
        lengthThreshold = 150.0f;
        shapeThreshold = 0.8f;
        shapeMatcher = new EuclideanShapeMatcher(shapeThreshold);
    }

    public boolean match(List<GPoint2D> userStroke, List<GPoint2D> template) {
        boolean startAndEnd = startAndEndMatch(userStroke.get(0), userStroke.get(userStroke.size() - 1), template.get(0), template.get(template.size() - 1));
        boolean direction = directionMatch(userStroke, template);
        boolean length = lengthMatch(userStroke, template);
        boolean shape = shapeMatch(userStroke, template);
        return startAndEnd && direction && length && shape;
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

    // 方向正确才Match，比如从左往右和从右往左就不能匹配
    private boolean directionMatch(List<GPoint2D> userStroke, List<GPoint2D> template) {
        List<GPoint2D> resampleUserStroke = Geometry.resample(userStroke, template.size());
        if (resampleUserStroke.size() != template.size()) {
            return false;
        }
        float sum = 0.0f;
        for (int i = 1; i < template.size(); i++) {
            Vector2 a = new Vector2(template.get(i - 1), template.get(i));
            Vector2 b = new Vector2(resampleUserStroke.get(i - 1), resampleUserStroke.get(i));
            sum += Geometry.cosineSimilarity(a, b);
        }
        sum = sum / template.size();
        if (sum < directionThreshold) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean lengthMatch(List<GPoint2D> userStroke, List<GPoint2D> template) {
        float userStrokeLength = Geometry.lengthOfPoints(userStroke);
        float templateLength = Geometry.lengthOfPoints(template);
        float difference = Math.abs(userStrokeLength - templateLength);
        if (difference < lengthThreshold) {
            return true;
        }
        else {
            return false;
        }
    }

    // shape相似度达到一定程度才match
    // shape相似度计算方法使用$1/$P/$Q来做，或者Discrete Frechet Distance
    private boolean shapeMatch(List<GPoint2D> userStroke, List<GPoint2D> template) {
        return shapeMatcher.match(userStroke, template);
    }
}
