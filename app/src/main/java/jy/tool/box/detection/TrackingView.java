package jy.tool.box.detection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * <pre>
 * @author : jiang
 * @time : 2022/4/26.
 * @desciption :
 * @version :
 * </pre>
 */

public class TrackingView extends View {

    private FaceDetector.Face[] faceArray;
    private Paint paint;

    public TrackingView(Context context) {
        super(context);
        initPaint();
    }

    public TrackingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public TrackingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE); //设置话出的是空心方框而不是实心方块
    }

    public void setFaces(FaceDetector.Face[] faces) {
        this.faceArray = faces;
        invalidateOnMainThread();
    }

    private void invalidateOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceArray != null) {
            float eyesDistance = 0f;//两眼间距
            for (FaceDetector.Face face : faceArray) {
                if (face != null) {
                    PointF pointF = new PointF();
                    face.getMidPoint(pointF); //获取人脸中心点
                    eyesDistance = face.eyesDistance(); //获取人脸两眼的间距
                    //画出人脸的区域
                    canvas.drawRect(
                            pointF.x - eyesDistance,
                            pointF.y - eyesDistance,
                            pointF.x + eyesDistance,
                            pointF.y + eyesDistance,
                            paint
                    );
                }
            }
        }

    }
}
