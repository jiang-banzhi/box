package jy.tool.library.base

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 * @author : jiang
 * @time : 2020/11/9.
 * @desciption :
 * @version :
</pre> *
 */
object RecyclerViewBindAdapter {
    @JvmStatic
    @BindingAdapter(
        value = ["itemBindings", "items", "adapter", "viewHolder", "emptyView", "footView", "headView", "spanSizeLookup"],
        requireAll = false
    )
    fun <T> setAdapter(
        recyclerView: RecyclerView,
        itemBindings: MulteItemBinding<T>?,
        items: MutableList<T>?,
        adapter: RecyclerViewBindingAdapter<T>?,
        viewHolderFactory: ViewHolderFactory?,
        emptyView: View?,
        footView: View?,
        headView: View?,
        SpanSizeLookup: RecyclerViewBindingAdapter.SpanSizeLookup?
    ) {
        var mAdapter=adapter
        if (itemBindings != null) {
            val oldAdapter = recyclerView.adapter as RecyclerViewBindingAdapter<T>?
            if (mAdapter == null) {
                if (oldAdapter == null) {
                    mAdapter = RecyclerViewBindingAdapter()
                } else {
                    mAdapter = oldAdapter
                }
            }
            mAdapter.setDatas(items)
            mAdapter.setItemBinding(itemBindings)
            mAdapter.setViewHolderFactory(viewHolderFactory)
            mAdapter.setEmptyView(emptyView)
            mAdapter.addFootView(footView)
            mAdapter.addHeadView(headView)
            mAdapter.setSpanSizeLookup(SpanSizeLookup)
            if (oldAdapter !== mAdapter) {
                recyclerView.adapter = mAdapter
            } else {
                oldAdapter.notifyDataSetChanged()
            }
        } else {
            recyclerView.adapter = null
        }
    }
}