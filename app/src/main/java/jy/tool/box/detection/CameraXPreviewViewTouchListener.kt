package jy.tool.box.detection

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

/**
 *<pre>
 * @author : jiang
 * @time : 2022/3/18.
 * @desciption :
 * @version :
 *</pre>
 */
class CameraXPreviewViewTouchListener(var context: Context) : View.OnTouchListener {
    private var mGestureDetector: GestureDetector
    private var mScaleGestureDetector: ScaleGestureDetector
    private var mCustomTouchListener: CustomTouchListener? = null

    private val onGestureListener: GestureDetector.SimpleOnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // 长按
                mCustomTouchListener?.longPress(e.x, e.y)
            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // 单击
                mCustomTouchListener?.click(e.x, e.y)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // 双击
                mCustomTouchListener?.doubleClick(e.x, e.y)
                return true
            }

        }

    // 缩放监听
    private val onScaleGestureListener: ScaleGestureDetector.OnScaleGestureListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val delta = detector.scaleFactor
                mCustomTouchListener?.zoom(delta)
                return true
            }
        }

    init {
        mGestureDetector = GestureDetector(context, onGestureListener)
        mScaleGestureDetector = ScaleGestureDetector(context, onScaleGestureListener)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        if (!mScaleGestureDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event)
        }
        return true
    }

    fun setCustomTouchListener(listener: CustomTouchListener) {
        this.mCustomTouchListener = listener

    }

    // 操作接口
    interface CustomTouchListener {
        // 放大缩小
        fun zoom(delta: Float)

        // 点击
        fun click(x: Float, y: Float)

        // 双击
        fun doubleClick(x: Float, y: Float)

        // 长按
        fun longPress(x: Float, y: Float)
    }
}