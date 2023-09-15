package jy.tool.box.matrix

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt


/**
 *<pre>
 * @author : jiang
 * @time : 2021/5/6.
 * @desciption : 查看图片 支持放大缩小
 * @version :
 *</pre>
 */
class MatrixImageView : ImageView, ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {
    private val DEFAULT_MAX_SCALE = 3.0f
    private val DEFAULT_MID_SCALE = 1.75f
    private val DEFAULT_MIN_SCALE = 1.0f
    private val DEFAULT_ZOOM_DURATION = 200
    private var mIsDragging = false
    internal var mLastTouchX: Float = 0.toFloat()
    internal var mLastTouchY: Float = 0.toFloat()
    private var mVelocityTracker: VelocityTracker? = null
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var handle = false
        when (event?.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.addMovement(event)
                }

                mLastTouchX = event.x
                mLastTouchY = event.y
                mIsDragging = false
            }

            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                val y = event.y
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY

                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                }

                if (mIsDragging) {
                    onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y

                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(event)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = event.x
                        mLastTouchY = event.y

                        // Compute velocity within the last 1000ms
                        mVelocityTracker!!.addMovement(event)
                        mVelocityTracker!!.computeCurrentVelocity(1000)

                        val vX = mVelocityTracker!!.getXVelocity()
                        val vY = mVelocityTracker!!.getYVelocity()

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
//                            mListener.onFling(
//                                mLastTouchX, mLastTouchY, -vX,
//                                -vY
//                            )
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker?.recycle()
                    mVelocityTracker = null
                }
            }
        }

        handle = mScaleGestureDetector!!.onTouchEvent(event)
        if (mGestureDetector != null && mGestureDetector!!.onTouchEvent(event)) {
            return true
        }
        return handle
    }


    private val TAG = "MatrixImageView=======>"
    private var mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private val EDGE_NONE = -1
    private val EDGE_LEFT = 0
    private val EDGE_RIGHT = 1
    private val EDGE_BOTH = 2
    private var mScrollEdge = EDGE_BOTH

    private var mMinScale = DEFAULT_MIN_SCALE
    private var mMidScale = DEFAULT_MID_SCALE
    private var mMaxScale = DEFAULT_MAX_SCALE
    private val mMatrixValues = FloatArray(9)

    private var mGestureDetector: GestureDetector? = null
    /**  模板Matrix，用以初始化  */
    private val mBaseMatrix = Matrix()
    /**
     * 绘制的Matrix,用于绘制
     */
    private val mDrawMatrix = Matrix()
    /**
     * 增量绘制的Matrix,用于计算
     */
    private val mSuppMatrix = Matrix()

    private val mDisplayRect = RectF()

    private var mScaleType = ScaleType.FIT_CENTER
    private var mBaseRotation = 0.0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mTouchSlop: Float = 0f
    private var mMinimumVelocity: Float = 0f
    private fun init() {
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        viewTreeObserver.addOnGlobalLayoutListener(this)
        setOnTouchListener(this)
        mScaleGestureDetector = ScaleGestureDetector(context, object :
            ScaleGestureDetector.OnScaleGestureListener {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {

            }

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val scaleFactor = detector!!.getScaleFactor()

                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor))
                    return false

                val focusX = detector!!.getFocusX()
                val focusY = detector!!.getFocusY()

                if ((getScale() < mMaxScale || scaleFactor < 1f) && (getScale() > mMinScale || scaleFactor > 1f)) {
                    mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
                    checkAndDisplayMatrix()
                }
                return true
            }

        })
        mGestureDetector = GestureDetector(context, GestureListener())
//        mGestureDetector?.setOnDoubleTapListener(object : GestureDetector.OnDoubleTapListener {
//            override fun onDoubleTap(event: MotionEvent?): Boolean {
//                event?.let { e ->
//                    val scale = getScale()
//                    val x = e.x
//                    val y = e.y
//                    if (scale < mMidScale) {
//                        setScale(mMidScale, x, y, true)
//                    } else if (scale >= mMidScale && scale < mMaxScale) {
//                        setScale(mMaxScale, x, y, true)
//                    } else {
//                        setScale(mMinScale, x, y, true)
//                    }
//                }
//                return true
//            }
//
//            override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
//                return false
//            }
//
//            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
//                return false
//            }
//
//        })
        //背景设置为balck
        setBackgroundColor(Color.BLACK)
        //将缩放类型设置为FIT_CENTER，表示把图片按比例扩大/缩小到View的宽度，居中显示
        scaleType = ScaleType.MATRIX
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(scaleType)
        if (null != scaleType && scaleType != mScaleType && ImageView.ScaleType.MATRIX != scaleType) {
            mScaleType = scaleType
            update()
        }
    }

    override fun onGlobalLayout() {
        Log.e(TAG, "onGlobalLayout")
        updateBaseMatrix(drawable)

    }

    private fun getImageViewWidth(imageView: ImageView?): Int {
        return if (null == imageView) 0 else imageView.width - imageView.paddingLeft - imageView.paddingRight
    }

    private fun getImageViewHeight(imageView: ImageView?): Int {
        return if (null == imageView) 0 else imageView.height - imageView.paddingTop - imageView.paddingBottom
    }

    /**
     * Calculate Matrix for FIT_CENTER
     *
     * @param d - Drawable being displayed
     */
    private fun updateBaseMatrix(d: Drawable?) {
        if (null == d) {
            return
        }
        if (mBaseMatrix == null) {
            return
        }
        val viewWidth = getImageViewWidth(this).toFloat()
        val viewHeight = getImageViewHeight(this).toFloat()
        val drawableWidth = d.intrinsicWidth
        val drawableHeight = d.intrinsicHeight

        mBaseMatrix.reset()

        val widthScale = viewWidth / drawableWidth
        val heightScale = viewHeight / drawableHeight

        if (mScaleType == ScaleType.CENTER) {
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth) / 2f,
                (viewHeight - drawableHeight) / 2f
            )

        } else if (mScaleType == ScaleType.CENTER_CROP) {
            val scale = Math.max(widthScale, heightScale)
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth * scale) / 2f,
                (viewHeight - drawableHeight * scale) / 2f
            )

        } else if (mScaleType == ScaleType.CENTER_INSIDE) {
            val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
            mBaseMatrix.postScale(scale, scale)
            mBaseMatrix.postTranslate(
                (viewWidth - drawableWidth * scale) / 2f,
                (viewHeight - drawableHeight * scale) / 2f
            )

        } else {
            var mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
            val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)

            if (mBaseRotation.toInt() % 180 != 0) {
                mTempSrc = RectF(0f, 0f, drawableHeight.toFloat(), drawableWidth.toFloat())
            }

            when (mScaleType) {
                ScaleType.FIT_CENTER -> mBaseMatrix
                    .setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER)

                ScaleType.FIT_START -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.START)

                ScaleType.FIT_END -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.END)

                ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.FILL)

                else -> {
                }
            }
        }

        resetMatrix()
    }

    private fun resetMatrix() {
        mSuppMatrix.reset()
        setRotationBy(mBaseRotation)
        setImageViewMatrix(mBaseMatrix)
        checkMatrixBounds()
    }

    fun setRotationBy(degrees: Float) {
        mSuppMatrix.postRotate(degrees % 360)
        checkAndDisplayMatrix()
    }

    private fun setImageViewMatrix(matrix: Matrix) {
        imageMatrix = matrix
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        Log.e(TAG, "setImageDrawable")
        update()
    }

    private fun update() {
        updateBaseMatrix(drawable)
    }

    fun setScale(
        scale: Float, focalX: Float, focalY: Float,
        animate: Boolean
    ) {
        if (scale < mMinScale || scale > mMaxScale) {
            return
        }

        if (animate) {
            post(
                AnimatedZoomRunnable(
                    getScale(), scale,
                    focalX, focalY
                )
            )
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY)
            checkAndDisplayMatrix()
        }

    }

    private fun onDrag(dx: Float, dy: Float) {
        mSuppMatrix.postTranslate(dx, dy)
        checkAndDisplayMatrix()

        if (!mScaleGestureDetector!!.isInProgress && !mIsDragging) {
            if (mScrollEdge == EDGE_BOTH
                || mScrollEdge == EDGE_LEFT && dx >= 1f
                || mScrollEdge == EDGE_RIGHT && dx <= -1f
            ) {
                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        } else {
            if (null != parent) {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
    }

    private var mCurrentFlingRunnable: FlingRunnable? = null
    private fun onFling(
        startX: Float, startY: Float, velocityX: Float,
        velocityY: Float
    ) {
        mCurrentFlingRunnable = FlingRunnable(getContext())
        mCurrentFlingRunnable!!.fling(
            getImageViewWidth(this),
            getImageViewHeight(this), velocityX.toInt(), velocityY.toInt()
        )
        post(mCurrentFlingRunnable)
    }

    private fun checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix())
        }
    }

    private fun getDrawMatrix(): Matrix {
        mDrawMatrix.set(mBaseMatrix)
        mDrawMatrix.postConcat(mSuppMatrix)
        return mDrawMatrix
    }

    private fun checkMatrixBounds(): Boolean {

        val rect = getDisplayRect(getDrawMatrix()) ?: return false

        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f

        val viewHeight = getImageViewHeight(this)
        when {
            height <= viewHeight -> deltaY = when (mScaleType) {
                ScaleType.FIT_START -> -rect.top
                ScaleType.FIT_END -> viewHeight.toFloat() - height - rect.top
                else -> (viewHeight - height) / 2 - rect.top
            }
            rect.top > 0 -> deltaY = -rect.top
            rect.bottom < viewHeight -> deltaY = viewHeight - rect.bottom
        }

        val viewWidth = getImageViewWidth(this)
        when {
            width <= viewWidth -> {
                deltaX = when (mScaleType) {
                    ScaleType.FIT_START -> -rect.left
                    ScaleType.FIT_END -> viewWidth.toFloat() - width - rect.left
                    else -> (viewWidth - width) / 2 - rect.left
                }
                mScrollEdge = EDGE_BOTH
            }
            rect.left > 0 -> {
                mScrollEdge = EDGE_LEFT
                deltaX = -rect.left
            }
            rect.right < viewWidth -> {
                deltaX = viewWidth - rect.right
                mScrollEdge = EDGE_RIGHT
            }
            else -> mScrollEdge = EDGE_NONE
        }

        // 最终变换后的 matrix
        mSuppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    private fun getDisplayRect(): RectF? {
        checkMatrixBounds()
        return getDisplayRect(getDrawMatrix())
    }

    private fun getDisplayRect(matrix: Matrix): RectF? {
        val d = drawable
        if (null != d) {
            mDisplayRect.set(
                0f, 0f, d.intrinsicWidth.toFloat(),
                d.intrinsicHeight.toFloat()
            )
            matrix.mapRect(mDisplayRect)
            return mDisplayRect
        }
        return null
    }

    private inner class GestureListener :
        GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(event: MotionEvent?): Boolean {
            event?.let { e ->
                val scale = getScale()
                val x = e.x
                val y = e.y
                if (scale < mMidScale) {
                    setScale(mMidScale, x, y, true)
                } else if (scale >= mMidScale && scale < mMaxScale) {
                    setScale(mMaxScale, x, y, true)
                } else {
                    setScale(mMinScale, x, y, true)
                }
            }
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (getScale() > DEFAULT_MIN_SCALE) {
                Log.e("onFling==========", "onFling")
                onFling(0f, 0f, -velocityX, -velocityY)
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
        if ((getScale() < mMaxScale || scaleFactor < 1f) && (getScale() > mMinScale || scaleFactor > 1f)) {
            mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
            checkAndDisplayMatrix()
        }
    }

    fun getScale(): Float {
        return sqrt(
            (getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble().pow(2.0).toFloat()
                    + getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble().pow(2.0).toFloat()).toDouble()
        ).toFloat()
    }

    private fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    private inner class AnimatedZoomRunnable(
        private val mZoomStart: Float, private val mZoomEnd: Float,
        private val mFocalX: Float, private val mFocalY: Float
    ) : Runnable {
        private val mStartTime: Long = System.currentTimeMillis()

        override fun run() {

            val t = interpolate()
            val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
            val deltaScale = scale / getScale()

            onScale(deltaScale, mFocalX, mFocalY)

            if (t < 1f) {
                postOnAnimation(this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - mStartTime) / DEFAULT_ZOOM_DURATION
            t = min(1f, t)
            t = mInterpolator.getInterpolation(t)
            return t
        }
    }

    private inner class FlingRunnable(context: Context) : Runnable {

        private val mScroller: OverScroller
        private var mCurrentX: Int = 0
        private var mCurrentY: Int = 0

        init {
            mScroller = OverScroller(context)
        }

        fun cancelFling() {
            mScroller.forceFinished(true)
        }

        fun fling(
            viewWidth: Int, viewHeight: Int, velocityX: Int,
            velocityY: Int
        ) {
            val rect = getDisplayRect() ?: return

            val startX = Math.round(-rect.left)
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int

            if (viewWidth < rect.width()) {
                minX = 0
                maxX = Math.round(rect.width() - viewWidth)
            } else {
                maxX = startX
                minX = maxX
            }

            val startY = Math.round(-rect.top)
            if (viewHeight < rect.height()) {
                minY = 0
                maxY = Math.round(rect.height() - viewHeight)
            } else {
                maxY = startY
                minY = maxY
            }

            mCurrentX = startX
            mCurrentY = startY

            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(
                    startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0
                )
            }
        }

        override fun run() {
            if (mScroller.isFinished()) {
                return  // remaining post that should not be handled
            }


            if (mScroller.computeScrollOffset()) {

                val newX = mScroller.getCurrX()
                val newY = mScroller.getCurrY()

                mSuppMatrix.postTranslate((mCurrentX - newX).toFloat(), (mCurrentY - newY).toFloat())
                setImageViewMatrix(getDrawMatrix())

                mCurrentX = newX
                mCurrentY = newY

                // Post On animation
                postOnAnimation(this)
            }
        }
    }
}