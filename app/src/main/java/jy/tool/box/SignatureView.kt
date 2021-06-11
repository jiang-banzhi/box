package jy.tool.box

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
        c.drawColor(Color.WHITE)
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp

    }

}