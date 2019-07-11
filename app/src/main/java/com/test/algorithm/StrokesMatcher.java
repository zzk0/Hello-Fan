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

import com.test.model.entity.GPoint2D;
import com.test.model.entity.Vector2;

import java.util.List;

public class StrokesMatcher {

    private float startAndEndThreshold;
    private float directionThreshold;
    private float lengthThreshold;
    private float shapeThreshold;

    private ShapeMatcher shapeMatcher;
    private static final int WIDTH = 1024;

    // 手势开始点/结束点在模板开始点/结束点的半径为100以内的圆即可
    private static final float START_AND_END_THRESHOLD = 10000.0f;
    private static final float DIRECTION_THRESHOLD = 1.25f;
    private static final float LENGTH_THRESHOLD = 205.0f;
    private static final float SHAPE_THRESHOLD = 50.0f;

    public StrokesMatcher() {
        startAndEndThreshold = START_AND_END_THRESHOLD;
        directionThreshold = DIRECTION_THRESHOLD;
        lengthThreshold = LENGTH_THRESHOLD;
        shapeThreshold = SHAPE_THRESHOLD;
        shapeMatcher = new EuclideanShapeMatcher(shapeThreshold);
    }

    public void setThreshold(int width, boolean isQuiz) {
        float factor = (float) width / WIDTH;
        startAndEndThreshold = factor * START_AND_END_THRESHOLD;
        directionThreshold = factor * DIRECTION_THRESHOLD;
        lengthThreshold = factor * LENGTH_THRESHOLD;
        shapeThreshold = factor * SHAPE_THRESHOLD;

        if (isQuiz) {
            startAndEndThreshold = startAndEndThreshold * 1.5f;
            directionThreshold = directionThreshold * 1.5f;
            lengthThreshold = lengthThreshold * 1.5f;
            shapeThreshold = shapeThreshold * 1.5f;
        }
        shapeMatcher.setThreshold(shapeThreshold);
    }

    public boolean match(List<GPoint2D> userStroke, List<GPoint2D> template) {
        boolean startAndEnd = startAndEndMatch(userStroke.get(0), userStroke.get(userStroke.size() - 1), template.get(0), template.get(template.size() - 1));
        boolean direction = directionMatch(userStroke, template);
        boolean length = lengthMatch(userStroke, template);
//        boolean shape = shapeMatch(userStroke, template);
        return (startAndEnd && direction && length);
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
            float similarity = Geometry.cosineSimilarity(a, b);
            if (similarity < 0) {
                sum += similarity;
            }
        }
        sum = sum / template.size();
        sum = Math.abs(sum);
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
