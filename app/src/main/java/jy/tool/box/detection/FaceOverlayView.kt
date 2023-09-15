package jy.tool.box.detection

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.Size


/**
 *<pre>
 * @author : jiang
 * @time : 2022/3/15.
 * @desciption :
 * @version :
 *</pre>
 */
class FaceOverlayView : View {
    lateinit var mPaint: Paint
    private var faceCx = 0f
    private var faceCy = 0f
    private var radius = 0f

    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            context.resources.displayMetrics
        )
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        faceCx = width / 2f
        faceCy = height * 2f / 5
        radius = width * 2 / 5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val path = Path()
        val path2 = Path()
        path.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CCW)
        path2.addCircle(
            faceCx,
            faceCy,
            radius,
            Path.Direction.CCW
        )
        path.op(path2, Path.Op.XOR)
        canvas!!.drawPath(path, mPaint)
    }

    fun getFaceLocationInWindow(@Size(2) outLocation: IntArray) {
        outLocation[0] = (faceCx - radius).toInt()
        outLocation[1] = (faceCy - radius).toInt()
    }

    fun getFaceOverlayWidth() = radius*2
    fun getFaceOverlayHeigth() = radius*2


}