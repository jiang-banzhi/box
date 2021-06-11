package jy.tool.box;

import android.graphics.RectF;
import androidx.annotation.NonNull;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/4/15.
 * @desciption :
 * @version :
 * </pre>
 */
public enum CropHelper {
    /**
     * 左上角
     */
    LEFT_TOP(new LocalHelper(Edge.Local.LEFT, Edge.Local.TOP)),
    /**
     * 右上角
     */
    RIGHT_TOP(new LocalHelper(Edge.Local.RIGHT, Edge.Local.TOP)),
    /**
     * 左下角
     */
    LEFT_BOTTOM(new LocalHelper(Edge.Local.LEFT, Edge.Local.BOTTOM)),
    /**
     * 右下角
     */
    RIGHT_BOTTOM(new LocalHelper(Edge.Local.RIGHT, Edge.Local.BOTTOM)),
    /**
     * 左边框
     */
    LEFT(new LocalHelper(Edge.Local.LEFT, null)),
    /**
     * 上边框
     */
    TOP(new LocalHelper(null, Edge.Local.TOP)),
    /**
     * 右边框
     */
    RIGHT(new LocalHelper(Edge.Local.RIGHT, null)),
    /**
     * 下边框
     */
    BOTTOM(new LocalHelper(null, Edge.Local.BOTTOM)),
    /**
     * 中心
     */
    CENTER(new LocalHelper());


    private LocalHelper mHelper;

    CropHelper(LocalHelper mHelper) {
        this.mHelper = mHelper;
    }

    public void updateCropWindow(float x,
                                 float y,
                                 @NonNull RectF rect) {
        mHelper.updateCropWindow(x, y, rect);
    }

    @Override
    public String toString() {
        return "CropHelper{" +
                "mHelper=" + mHelper +
                '}';
    }}
