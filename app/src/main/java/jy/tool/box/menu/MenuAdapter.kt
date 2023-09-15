package jy.tool.box.menu

import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import jy.tool.box.recycler.RecyclerItemViewModel
import jy.tool.library.base.RecyclerViewBindingAdapter

/**
 *<pre>
 * @author : jiang
 * @time : 2021/3/29.
 * @desciption :
 * @version :
 *</pre>
 */
class MenuAdapter : RecyclerViewBindingAdapter<MenuItemViewModel>() {

    override fun defaultItemViewType(position: Int): Int {
        return super.defaultItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        Log.e("MenuAdapter", "${mDatas?.get(position)}")
    }
}