package jy.tool.box

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/7.
 * @desciption :
 * @version :
 *</pre>
 */
class RulerView : View {
    private var UNIT_MM: Float = 0.0f
    private var CM_SIZE: Float = 0.0f
    private var MM_SIZE: Float = 0.0f
    private var SPACE = 0f
    private var RULE_HEIGHT: Float = 0f
    private var RULE_SCALE: Float = 1f
    private var RULE_SIZE: Float = 0f
    private var CYCLE_WIDTH: Float = 0f
    private var RADIUS_BIG: Float = 0f
    private var RADIUS_MEDIUM: Float = 0f
    private var RADIUS_SMALL: Float = 0f
    private var starX = 0f
    private var lastX = 0f
    private var mWidth = 0f
    private var mHeight = 0f
    private var mPaint: Paint = Paint()
    private var displayPaint: Paint = Paint()
    private var circlePaint: Paint = Paint()
    private var lineWidth = 2f
    private var lineX = 0f

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
        val dm = resources.displayMetrics
        UNIT_MM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, dm)
        CM_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30f, dm)
        MM_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, dm)
        RULE_HEIGHT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, dm)
        SPACE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, dm)
        CYCLE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, dm)
        RADIUS_BIG = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46f, dm)
        RADIUS_MEDIUM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38f, dm)
        RADIUS_SMALL = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, dm)
        RULE_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
        starX = SPACE
        lineX = starX
        mPaint.strokeWidth = lineWidth
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.DKGRAY
        mPaint.textSize = RULE_SIZE
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.style = Paint.Style.FILL
        displayPaint.strokeWidth = lineWidth
        displayPaint.isAntiAlias = true
        displayPaint.style = Paint.Style.STROKE
        displayPaint.color = Color.BLUE
        displayPaint.textSize = CM_SIZE
        displayPaint.textAlign = Paint.Align.CENTER
        displayPaint.style = Paint.Style.FILL
        circlePaint.strokeWidth = CYCLE_WIDTH
        circlePaint.isAntiAlias = true
        circlePaint.style = Paint.Style.STROKE
        circlePaint.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val availableWidth = mWidth - 2 * SPACE
        val tickCount = (availableWidth / UNIT_MM).toInt()
        //绘制刻度
        for (index in 0..tickCount) {
            RULE_SCALE = 0.5f
            val x = SPACE + index * UNIT_MM
            if (index % 5 == 0) {
                if (index and 0x1 == 0) {
                    RULE_SCALE = 1f
                    val numText = (index / 10).toString()
                    val rect = Rect()
                    mPaint.getTextBounds(numText, 0, numText.length, rect)
                    canvas?.drawText(numText, x, RULE_HEIGHT + RULE_SIZE / 2 + rect.height().toFloat(), mPaint)
                } else {
                    RULE_SCALE = 0.75f
                }
            }
            lastX = x
            canvas?.drawLine(x, 0f, x, RULE_HEIGHT * RULE_SCALE, mPaint)
        }
        //绘制指示线
        canvas?.drawLine(lineX, 0f, lineX, mHeight, mPaint)


        //绘制长度值
        circlePaint.setColor(Color.parseColor("#4e4e4e"))
        circlePaint.style = Paint.Style.FILL
        canvas?.drawCircle(mWidth / 2, mHeight / 2, RADIUS_MEDIUM, circlePaint)
        circlePaint.setColor(Color.parseColor("#292929"))
        circlePaint.style = Paint.Style.STROKE
        canvas?.drawCircle(mWidth / 2, mHeight / 2, RADIUS_MEDIUM, circlePaint)
        circlePaint.setColor(Color.parseColor("#777777"))
        circlePaint.style = Paint.Style.FILL
        canvas?.drawCircle(mWidth / 2 + RADIUS_MEDIUM, mHeight / 2, RADIUS_SMALL, circlePaint)


        val mmCount = ((lineX - SPACE) / UNIT_MM).toInt()
        val cm = (mmCount / 10).toString()
        val mm = (mmCount % 10).toString()
        displayPaint.color = Color.WHITE
        displayPaint.textSize = CM_SIZE
        val cmMetrics = displayPaint.fontMetrics
        canvas?.drawText(
            cm,
            mWidth / 2,
            mHeight / 2 - cmMetrics.top / 2 - cmMetrics.bottom / 2,
            displayPaint
        )
        displayPaint.textSize = MM_SIZE
        val mmMetrics = displayPaint.fontMetrics
        canvas?.drawText(
            mm,
            mWidth / 2 + RADIUS_MEDIUM,
            mHeight / 2 - mmMetrics.top / 2 - mmMetrics.bottom / 2,
            displayPaint
        )
    }

    private var unlockLineCanvas = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                unlockLineCanvas = false
            }
            MotionEvent.ACTION_DOWN -> {
                val lineOffset = abs(event.x - lineX)
                if (lineOffset <= SPACE) {
                    unlockLineCanvas = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (unlockLineCanvas) {
                    lineX = event.x
                    if (lineX < starX) {
                        lineX = starX
                    } else if (lineX > lastX) {
                        lineX = lastX
                    }
                    invalidate()
                }
            }
        }
        return true


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
}