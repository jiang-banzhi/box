package jy.tool.box.floating

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs

/**
 *<pre>
 * @author : jiang
 * @time : 2023/9/15.
 * @desciption :
 * @version :
 *</pre>
 */
class FloatingView : AppCompatTextView, View.OnTouchListener {

    constructor(context: Context) : this(context, null) {

    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        setOnTouchListener(this)

    }

    private var startDownX = 0
    private var startDownY = 0
    val TAG = "FloatingView"
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        Log.e(TAG, "onTouch: ================${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startDownX = event.rawX.toInt()
                startDownY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx: Int = event.rawX.toInt() - startDownX
                val dy: Int = event.rawY.toInt() - startDownY

                var left = v!!.left + dx
                var top = v.top + dy
                var right = v.right + dx
                var bottom = v.bottom + dy
                //防止超出父布局边界
                //防止超出父布局边界
                if (left < 0) {
                    left = 0
                    right = left + v.width
                }
                if (right > (parent as View).width) {
                    right = (parent as View).width
                    left = right - v.width
                }
                if (top < 0) {
                    top = 0
                    bottom = top + v.height
                }
                if (bottom > (parent as View).height) {
                    bottom = (parent as View).height
                    top = bottom - v.height
                }
                v.layout(left, top, right, bottom)
                startDownX = event.rawX.toInt()
                startDownY = event.rawY.toInt()
            }

            MotionEvent.ACTION_UP -> {
                //当抬起手指的时候如果在中间则需忘两边靠拢
                var left2 = v!!.left
                var right2 = v.right
                if (v.left + v.width / 2 >= (parent as View).width / 2) {
                    //超过一半的时候靠右
                    right2 = (parent as View).width
                    left2 = right2 - v.width
                } else if (v.left + v.width / 2 < (parent as View).width / 2) {
                    //小于一半的时候靠左
                    left2 = 0
                    right2 = left2 + v.width
                }
                val lastMoveDx = abs(event.rawX.toInt() - startDownX)
                val lastMoveDy = abs(event.rawY.toInt() - startDownY)
                startFloatAnim(v, left2) //执行靠拢动画
            }
        }
        return true
    }

    private fun startFloatAnim(v: View, endLeft: Int) {
        //创建逐渐靠边的动画
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(v.left, endLeft)
        valueAnimator.setDuration(200)
        valueAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            v.layout(animatedValue, v.top, animatedValue + v.width, v.bottom)
        }
        valueAnimator.start()
    }
}