package jy.tool.box.camera

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import jy.tool.box.R


class ScanOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var animator : ObjectAnimator? = null

    private var bitmap: Bitmap

    private var resultRect : RectF? = null

    private var showLine = true

    private var floatYFraction = 0f
    set(value) {field = value
    invalidate()}

    init {
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.colorAccent)
        paint.strokeWidth = 3f.toPx().toFloat()
        bitmap = BitmapFactory.decodeResource(resources,R.drawable.icon_scan_line)
        getAnimator().start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (showLine){
            canvas?.drawBitmap(bitmap,(width - bitmap.width)/2f,height * floatYFraction,paint)
        }
        resultRect?.let {rect->
            canvas?.drawCircle(rect.left + (rect.right - rect.left)/2f,rect.top+(rect.bottom - rect.top)/2f,10f.toPx().toFloat(),paint)
        }

    }
    fun Float.toPx(): Int {
        val resources = Resources.getSystem()
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            resources.displayMetrics
        ).toInt()
    }
    private fun getAnimator(): ObjectAnimator {
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(
                this,
                "floatYFraction",
                0f,
                1f
            )
            animator?.duration = 5000
            animator?.repeatCount = -1 //-1代表无限循环
        }
        return animator!!
    }

    fun addRect(rect: RectF){
        showLine = false
        resultRect = rect
        getAnimator().cancel()
        invalidate()
    }


}