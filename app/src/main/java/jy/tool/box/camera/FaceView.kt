package jy.tool.box.camera

import android.content.Context
import android.graphics.*
import android.media.FaceDetector
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.Toast

/**
 *<pre>
 * @author : jiang
 * @time : 2022/3/14.
 * @desciption :
 * @version :
 *</pre>
 */
class FaceView : View {

    lateinit var mPaint: Paint
    private var mCorlor = "#42ed45"
    var mFaces: ArrayList<RectF>? = null   //人脸信息

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
        mPaint = Paint()
        mPaint.color = Color.parseColor(mCorlor)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            1f,
            context.resources.displayMetrics
        )
        mPaint.isAntiAlias = true

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mFaces?.let {
            for (face in it) {           //因为会同时存在多张人脸，所以用循环
                canvas.drawRect(face, mPaint)   //绘制人脸所在位置的矩形
            }
        }
    }

    fun setFaces(faces: ArrayList<RectF>) {  //设置人脸信息，然后刷新FaceView
        this.mFaces = faces
        invalidate()
    }

    fun setFaces(faces: Array<FaceDetector.Face?>?) {
        var eyesDistance = 0f //两眼间距
        mFaces = arrayListOf<RectF>()
        faces?.forEach { face ->
            if (face != null) {
                val pointF = PointF()
                face.getMidPoint(pointF) //获取人脸中心点
                eyesDistance = face.eyesDistance() //获取人脸两眼的间距
                mFaces?.add(
                    RectF(
                        pointF.x - eyesDistance,
                        pointF.y - eyesDistance,
                        pointF.x + eyesDistance,
                        pointF.y + eyesDistance
                    )
                )
            }
        }
        invalidate()
    }
}