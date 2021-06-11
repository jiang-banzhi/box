package jy.tool.box;

import android.graphics.RectF;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/4/15.
 * @desciption :
 * @version :
 * </pre>
 */
public class EdgeUtils {

    /**
     * @param touchX 手指触摸横坐标
     * @param touchY 手指触摸纵坐标
     * @param rectF  边框位置
     * @param offset 偏移量
     * @return
     */
    public static CropHelper getPressedHandle(float touchX,
                                              float touchY,
                                              RectF rectF,
                                              float offset) {
        CropHelper mHelper = null;
        float nearestDistance = Float.POSITIVE_INFINITY;
        //////////判断手指四个角之一/////////////////
        //手指与左上角的距离
        float distanceToTopLeft = calculateDistance(touchX, touchY, rectF.left, rectF.top);
        if (distanceToTopLeft < nearestDistance) {
            nearestDistance = distanceToTopLeft;
            mHelper = CropHelper.LEFT_TOP;
        }
        //手指与右上角的距离
        float distanceToTopRight = calculateDistance(touchX, touchY, rectF.right, rectF.top);
        if (distanceToTopRight < nearestDistance) {
            nearestDistance = distanceToTopRight;
            mHelper = CropHelper.RIGHT_TOP;
        }
        //手指与左下角的距离
        float distanceToBottomLeft = calculateDistance(touchX, touchY, rectF.left, rectF.bottom);
        if (distanceToBottomLeft < nearestDistance) {
            nearestDistance = distanceToBottomLeft;
            mHelper = CropHelper.LEFT_BOTTOM;
        }
        //手指与右下角的距离
        float distanceToBottomRight = calculateDistance(touchX, touchY, rectF.right, rectF.bottom);
        if (distanceToBottomRight < nearestDistance) {
            nearestDistance = distanceToBottomRight;
            mHelper = CropHelper.RIGHT_BOTTOM;
        }
        //如果手指选中了一个最近的角，并且在缩放范围内则返回这个角
        if (nearestDistance < offset) {
            return mHelper;
        }
        //////////判断手指是否在四个边的某条边/////////////////
        if (isInHorizontalEdge(touchX, touchY, rectF.left, rectF.right, rectF.top, offset)) {
            /*上边框*/
            return CropHelper.TOP;//说明手指在裁剪框top区域
        } else if (isInHorizontalEdge(touchX, touchY, rectF.left, rectF.right, rectF.bottom, offset)) {
            /*下边框*/
            return CropHelper.BOTTOM;//说明手指在裁剪框bottom区域
        } else if (isInVerticalEdge(touchX, touchY, rectF.left, rectF.top, rectF.bottom, offset)) {
            /*左边框*/
            return CropHelper.LEFT;//说明手指在裁剪框left区域
        } else if (isInVerticalEdge(touchX, touchY, rectF.right, rectF.top, rectF.bottom, offset)) {
            /*右边框*/
            return CropHelper.RIGHT;//说明手指在裁剪框right区域
        }
        //////////判断手指是否是裁剪框中间/////////////////
        if (isWithinBounds(touchX, touchY, rectF.left, rectF.top, rectF.right, rectF.bottom)) {
            return CropHelper.CENTER;
        }
        return null;

    }

    private static boolean isWithinBounds(float x, float y, float left, float top, float right, float bottom) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    private static boolean isInHorizontalEdge(float x,
                                              float y,
                                              float handleXStart,
                                              float handleXEnd,
                                              float handleY,
                                              float targetRadius) {

        return (x > handleXStart && x < handleXEnd && Math.abs(y - handleY) <= targetRadius);
    }


    private static boolean isInVerticalEdge(float x,
                                            float y,
                                            float handleX,
                                            float handleYStart,
                                            float handleYEnd,
                                            float targetRadius) {

        return (Math.abs(x - handleX) <= targetRadius && y > handleYStart && y < handleYEnd);
    }

    /**
     * 计算 (x1, y1) 和 (x2, y2)两个点的距离
     */
    private static float calculateDistance(float x1, float y1, float x2, float y2) {

        final float side1 = x2 - x1;
        final float side2 = y2 - y1;
        return (float) Math.sqrt(side1 * side1 + side2 * side2);
    }
}
