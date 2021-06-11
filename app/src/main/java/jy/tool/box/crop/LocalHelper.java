package jy.tool.box.crop;

import android.graphics.RectF;
import androidx.annotation.NonNull;

import jy.tool.box.crop.Edge;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/4/15.
 * @desciption :
 * @version :
 * </pre>
 */
public class LocalHelper {
    Edge.Local mVerticalLocal;
    Edge.Local mHorizontaLocall;

    static final int MIN_CROP_LENGTH_PX = 80;

    public LocalHelper(Edge.Local mVerticalLocal, Edge.Local mHorizontaLocall) {
        this.mVerticalLocal = mVerticalLocal;
        this.mHorizontaLocall = mHorizontaLocall;
    }

    public LocalHelper() {
    }

    public void updateCropWindow(float x,
                                 float y,
                                 @NonNull RectF rect) {
        if (mVerticalLocal != null) {
            if (mVerticalLocal == Edge.Local.LEFT) {
                adjustLeft(x, rect);
            } else if (mVerticalLocal == Edge.Local.RIGHT) {
                adjustRight(x, rect);
            }
        }
        if (mHorizontaLocall != null) {
            if (mVerticalLocal == Edge.Local.TOP) {
                adjustTop(y, rect);
            } else if (mVerticalLocal == Edge.Local.BOTTOM) {
                adjustBottom(y, rect);
            }
        }

    }

    private static void adjustLeft(float x, @NonNull RectF rect) {
        final float resultX;
        if (x - rect.left < 0) {
            //左边越界
            resultX = rect.left;
        } else {
            //防止裁剪框左边超过右边或者最小范围
            if ((x + MIN_CROP_LENGTH_PX) >= rect.right) {
                x = rect.right - MIN_CROP_LENGTH_PX;
            }
            resultX = x;
        }
        rect.left = resultX;
    }

    private static void adjustRight(float x, @NonNull RectF rect) {
        final float resultX;
        if (rect.right - x < 0) {

            resultX = rect.right;

        } else {

            //防止裁剪框右边超过最小范围
            if ((x - MIN_CROP_LENGTH_PX) <= rect.left) {
                x = rect.left + MIN_CROP_LENGTH_PX;
            }
            resultX = x;
        }
        rect.right = resultX;
    }

    private static void adjustTop(float y, @NonNull RectF rect) {

        final float resultY;

        if (y - rect.top < 0) {
            resultY = rect.top;
        } else {
            //防止裁剪框上边超过最小范围或者越过最下边
            if ((y + MIN_CROP_LENGTH_PX) >= rect.bottom) {
                y = rect.bottom - MIN_CROP_LENGTH_PX;

            }

            resultY = y;
        }
        rect.top = resultY;
    }


    private static void adjustBottom(float y, @NonNull RectF rect) {
        final float resultY;
        if (rect.bottom - y < 0) {
            resultY = rect.bottom;
        } else {

            if ((y - MIN_CROP_LENGTH_PX) <= rect.top) {
                y = rect.top + MIN_CROP_LENGTH_PX;
            }

            resultY = y;
        }
        rect.bottom = resultY;
    }

    @Override
    public String toString() {
        return "LocalHelper{" +
                "mVerticalLocal=" + mVerticalLocal +
                ", mHorizontaLocall=" + mHorizontaLocall +
                '}';
    }
}
