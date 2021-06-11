package jy.tool.box.crop

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import java.lang.Math.min


/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/12.
 * @desciption :
 * @version :
 *</pre>
 */
class CropView : androidx.appcompat.widget.AppCompatImageView {

    private var STROKE_WIDTH = 2f
    private var STROKE_WIDTH_WIDE = 5f
    private var mWidth = 0f
    private var mHeight = 0f
    private var OFFSET = 40f
    private var DEF_SIZE = 200f
    private var mEdge = Edge()
    private var mPaint: Paint = Paint()
    /**
     * 加载的图片边界
     */
    private var imageRect: RectF? = null
    private var canCrop = true

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
        mPaint.strokeWidth = STROKE_WIDTH
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.DKGRAY
        mPaint.textAlign = Paint.Align.CENTER

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawEdge(canvas)
    }

    val LINE_WIDTH = 50f
    private fun drawEdge(canvas: Canvas?) {
        val path = Path()
        mPaint.strokeWidth = STROKE_WIDTH
        path.addRect(mEdge, Path.Direction.CCW)
        canvas?.drawPath(path, mPaint)

        drawCorner(canvas)
    }

    /**
     * 绘制边角
     */
    private fun drawCorner(canvas: Canvas?) {
        mPaint.strokeWidth = STROKE_WIDTH_WIDE
        val leftTopPath = Path()
        leftTopPath.moveTo(mEdge.left, mEdge.top + LINE_WIDTH)
        leftTopPath.lineTo(mEdge.left, mEdge.top)
        leftTopPath.lineTo(mEdge.left + LINE_WIDTH, mEdge.top)
        canvas?.drawPath(leftTopPath, mPaint)

        val rigthTopPath = Path()
        rigthTopPath.moveTo(mEdge.right - LINE_WIDTH, mEdge.top)
        rigthTopPath.lineTo(mEdge.right, mEdge.top)
        rigthTopPath.lineTo(mEdge.right ,  mEdge.top + LINE_WIDTH)
        canvas?.drawPath(rigthTopPath, mPaint)

        val leftBottomPath = Path()
        leftBottomPath.moveTo(mEdge.right, mEdge.bottom - LINE_WIDTH)
        leftBottomPath.lineTo(mEdge.right, mEdge.bottom)
        leftBottomPath.lineTo(mEdge.right - LINE_WIDTH, mEdge.bottom)
        canvas?.drawPath(leftBottomPath, mPaint)

        val rightBottomPath = Path()
        rightBottomPath.moveTo(mEdge.left, mEdge.bottom - LINE_WIDTH)
        rightBottomPath.lineTo( mEdge.left, mEdge.bottom)
        rightBottomPath.lineTo(mEdge.left + LINE_WIDTH, mEdge.bottom)
        canvas?.drawPath(rightBottomPath, mPaint)
    }

    var cropHelper: CropHelper? = null
    var touchX = 0f
    var touchY = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!canCrop) {
            return true
        }
        when (event?.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
//                isCorner = false
                cropHelper = null
                invalidate()
            }
            MotionEvent.ACTION_DOWN -> {
                touchX = event.getX()
                touchY = event.getY()
                cropHelper = EdgeUtils.getPressedHandle(event.getX(), event.getY(), mEdge, OFFSET)
                if (cropHelper != null) {
                    Log.e("ACTION++>", "${cropHelper.toString()}")
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val tempX = event.getX()
                val tempY = event.getY()
                if (cropHelper != null) {
                    cropHelper?.updateCropWindow(event.getX(), event.getX(), mEdge)
                    val offsetX = tempX - touchX
                    val offsetY = tempY - touchY
                    mEdge.offset(cropHelper, offsetX, offsetY)
                    touchX = tempX
                    touchY = tempY
                    invalidate()
                }
            }
        }
        return true
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initCropWindow()
    }

    private fun initCropWindow() {
        val centerX = mWidth / 2
        val centerY = mHeight / 2
        imageRect = getImageDisplayRect(centerX, centerY)
        mEdge.border = imageRect

        val imageSize = min(imageRect?.width() ?: DEF_SIZE, imageRect?.height() ?: DEF_SIZE)
        var size = DEF_SIZE

        if (imageSize < DEF_SIZE) {
            canCrop = false
            size = imageSize / 2
        }
        mEdge.left = centerX - size
        mEdge.right = centerX + size
        mEdge.top = centerY - size
        mEdge.bottom = centerY + size
    }

    private fun getImageDisplayRect(centerX: Float, centerY: Float): RectF {
        // 得到imageview中的矩阵，准备得到drawable的拉伸比率
        val values = FloatArray(10)
        imageMatrix.getValues(values)

        // drawable的本身宽高
        val drawable = drawable
        val originalWidth = drawable.intrinsicWidth
        val originalHeight = drawable.intrinsicHeight

        //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数  value[0],[4]
        //得到drawable的实际显示时的宽高
        val displayWidth = originalWidth * values[0]
        val displayHeight = originalHeight * values[4]
        // 计算出对应的Rect
        val rect = RectF()
        rect.left = centerX - displayWidth / 2
        rect.top = centerY - displayHeight / 2
        rect.right = rect.left + displayWidth
        rect.bottom = rect.top + displayWidth

        return rect
    }

    fun cropImage(): Bitmap? {

        val drawable = drawable
        if (drawable == null || drawable !is BitmapDrawable) {
            return null
        }

        val matrixValues = FloatArray(9)
        imageMatrix.getValues(matrixValues)

        val scaleX = matrixValues[Matrix.MSCALE_X]
        val scaleY = matrixValues[Matrix.MSCALE_Y]
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        val originalBitmap = drawable.bitmap
        //转换剪切位置到图片
        var cropX = (mEdge.left - transX) / scaleX
        var cropY = (mEdge.top - transY) / scaleY
        //修正裁剪起点
        cropX = if (cropX < 0) 0f else cropX
        cropY = if (cropY < 0) 0f else cropY
        val cropWidth = min(mEdge.width() / scaleX, originalBitmap.width - cropX)
        val cropHeight = min(mEdge.height() / scaleY, originalBitmap.height - cropY)

        return Bitmap.createBitmap(
            originalBitmap,
            cropX.toInt(),
            cropY.toInt(),
            cropWidth.toInt(),
            cropHeight.toInt()
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = widthSize.toFloat()
        mHeight = heightSize.toFloat()
    }

}