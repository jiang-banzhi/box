package jy.tool.box.crop;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * <pre>
 * @author : jiang
 * @time : 2021/4/15.
 * @desciption :
 * @version :
 * </pre>
 */
public class Edge extends RectF {
    public enum Local {
        /**
         * 左边框
         */
        LEFT,
        /**
         * 上边框
         */
        TOP,
        /**
         * 右边框
         */
        RIGHT,
        /**
         * 下边框
         */
        BOTTOM,
    }

    private RectF border;

    static final int MIN_CROP_LENGTH_PX = 150;

    public Edge() {
    }


    public Edge(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }


    public Edge(@androidx.annotation.Nullable RectF r) {
        if (r == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }

    public Edge(@androidx.annotation.Nullable Rect r) {
        if (r == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }

    public RectF getBorder() {
        return border;
    }

    public void setBorder(RectF border) {
        this.border = border;
    }

    public void offset(CropHelper helper, float offsetX, float offsetY) {
        switch (helper) {
            case LEFT_TOP:
                offset(Local.LEFT, offsetX);
                offset(Local.TOP, offsetY);
                break;
            case RIGHT_TOP:
                offset(Local.RIGHT, offsetX);
                offset(Local.TOP, offsetY);
                break;
            case LEFT_BOTTOM:
                offset(Local.LEFT, offsetX);
                offset(Local.BOTTOM, offsetY);
                break;
            case RIGHT_BOTTOM:
                offset(Local.RIGHT, offsetX);
                offset(Local.BOTTOM, offsetY);
                break;
            case LEFT:
                offset(Local.LEFT, offsetX);
                break;
            case TOP:
                offset(Local.TOP, offsetY);
                break;
            case RIGHT:
                offset(Local.RIGHT, offsetX);
                break;
            case BOTTOM:
                offset(Local.BOTTOM, offsetY);
                break;
            case CENTER:
                moveVertical(offsetY);
                moveHorizontal(offsetX);
                break;
            default:

                break;
        }
    }

    public void offset(Local local, float offset) {
        switch (local) {
            case LEFT:
                if (this.left + offset > this.right - MIN_CROP_LENGTH_PX) {
                    this.left = this.right - MIN_CROP_LENGTH_PX;
                } else if (this.left + offset < border.left) {
                    this.left = border.left;
                } else {
                    this.left += offset;
                }
                break;
            case RIGHT:

                if (this.right + offset < this.left + MIN_CROP_LENGTH_PX) {
                    this.right = this.left + MIN_CROP_LENGTH_PX;
                } else if (this.right + offset > border.right) {
                    this.right = border.right;
                } else {
                    this.right += offset;
                }
                break;
            case TOP:
                if (this.top + offset > this.bottom - MIN_CROP_LENGTH_PX) {
                    this.top = this.bottom - MIN_CROP_LENGTH_PX;
                } else if (this.top + offset < border.top) {
                    this.top = border.top;
                } else {
                    this.top += offset;
                }
                break;
            case BOTTOM:
                if (this.bottom + offset < this.top + MIN_CROP_LENGTH_PX) {
                    this.bottom = this.top + MIN_CROP_LENGTH_PX;
                } else if (this.bottom + offset > border.bottom) {
                    this.bottom = border.bottom;
                } else {
                    this.bottom += offset;
                }
                break;
            default:

                break;
        }

    }

    private void moveHorizontal(float offset) {
        //offset <0 左移动
        if (offset < 0) {
            if (this.left + offset <= border.left) {
                float dif = width();
                this.left = border.left;
                this.right = this.left + dif;
            } else {
                this.left += offset;
                this.right += offset;
            }
        } else {
            if (this.right + offset >= border.right) {
                float dif = width();
                this.right = border.right;
                this.left = border.right - dif;
            } else {
                this.left += offset;
                this.right += offset;
            }
        }
    }

    private void moveVertical(float offset) {
        // offset <0 上移动
        if (offset < 0) {
            if (this.top + offset <= border.top) {
                float dif = height();
                this.top = border.top;
                this.bottom = this.top + dif;
            } else {
                this.top += offset;
                this.bottom += offset;
            }
        } else {
            if (this.bottom + offset >= border.bottom) {
                float dif = height();
                this.bottom = border.bottom;
                this.top = border.bottom - dif;
            } else {
                this.top += offset;
                this.bottom += offset;
            }
        }


    }

}
