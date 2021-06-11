package jy.tool.box

import jy.tool.library.base.RecyclerViewBindingAdapter

/**
 *<pre>
 * @author : jiang
 * @time : 2021/3/29.
 * @desciption :
 * @version :
 *</pre>
 */
class RecyclerAdapter : RecyclerViewBindingAdapter<RecyclerItemViewModel>() {

    override fun defaultItemViewType(position: Int): Int {
        return if (position % 2 == 0) 1 else 2
//        return super.defaultItemViewType(position)
    }
}