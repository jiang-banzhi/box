package jy.tool.box.coin

import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

/**
 *<pre>
 * @author : jiang
 * @time : 2021/7/20.
 * @desciption :
 * @version :
 *</pre>
 */
class CoinLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (childCount == 0 && state.isPreLayout) {//state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state)

    }

    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        var top = paddingTop //布局时的上偏移
        var left = width / 2 //布局时的左偏移
        for (i in 0..itemCount - 1) {
            val child = recycler.getViewForPosition(i)
            addView(child);
            measureChildWithMargins(child, 0, 0);
            if (i == 0) {
                left -= child.measuredWidth / 2
            } else {
                var line: Int = (i / 2.0 + 0.5).toInt()
                if (i % 2 == 1) {
                    left -= (child.measuredWidth * (line + 0.5)).toInt()
                } else {
                    left += (child.measuredWidth * (line - 0.5)).toInt()
                }
                left += randomWidth(child.measuredWidth)
                top += child.measuredHeight * line+randomHeight(child.measuredWidth)
            }
            layoutDecoratedWithMargins(
                child,
                left,
                top,
                left + child.measuredWidth,
                top + child.measuredHeight
            )
            left = width / 2
            top = paddingTop
        }
    }

    private fun randomWidth(width: Int): Int {
        return Random.nextInt(-width / 4, width / 4)
    }

    private fun randomHeight(height: Int): Int {
        return Random.nextInt(-height / 4, height / 4)
    }
}