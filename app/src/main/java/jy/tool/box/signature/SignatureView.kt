package jy.tool.box.signature

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/22.
 * @desciption :
 * @version :
 *</pre>
 */
class SignatureView : View {
    private var mWidth = 0f
    private var mHeight = 0f
    private var mPaint: Paint = Paint()
    private var mPath = Path()

    private val mBackColor = Color.WHITE

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

    private fun init() {
        mPaint.strokeWidth = 2f
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLUE
        mPaint.textSize = 20f
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.style = Paint.Style.STROKE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(event.x, event.y)

            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        mWidth = widthSize.toFloat()
        mHeight = heightSize.toFloat()
        setMeasuredDimension(widthSize, heightSize)
    }

    fun clear() {
        mPath.reset()
        postInvalidate()
    }

    fun createBitmap(): Bitmap {
        return createBitmap(this)
    }

    //将view生生图片
    fun createBitmap(v: View): Bitmap {

        var w = v.getWidth()
        var h = v.getHeight()
        //生成图片
        var bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        var c = Canvas(bmp)
        c.drawColor(mBackColor)
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp

    }

    fun getClearBlankBitmap(): Bitmap {
        return clearBlank(createBitmap(), 10)
    }


    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private fun clearBlank(bp: Bitmap, blank: Int): Bitmap {
        val HEIGHT = bp.height
        val WIDTH = bp.width
        var top = 0
        var left = 0
        var right = 0
        var bottom = 0
        var pixs = IntArray(WIDTH)
        var isStop: Boolean
        //扫描上边距不等于背景颜色的第一个点
        for (y in 0 until HEIGHT) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    top = y
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (y in HEIGHT - 1 downTo 0) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    bottom = y
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        pixs = IntArray(HEIGHT)
        //扫描左边距不等于背景颜色的第一个点
        for (x in 0 until WIDTH) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    left = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (x in WIDTH - 1 downTo 1) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT)
            isStop = false
            for (pix in pixs) {
                if (pix != mBackColor) {
                    right = x
                    isStop = true
                    break
                }
            }
            if (isStop) {
                break
            }
        }
        //计算加上保留空白距离之后的图像大小
        left = if (left - blank > 0) left - blank else 0
        top = if (top - blank > 0) top - blank else 0
        right = if (right + blank > WIDTH - 1) WIDTH - 1 else right + blank
        bottom = if (bottom + blank > HEIGHT - 1) HEIGHT - 1 else bottom + blank
        //防止创建null的bitmap  引发的崩溃
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            left = 1
            top = 1
            right = 351
            bottom = 251
        }
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top)
    }


}