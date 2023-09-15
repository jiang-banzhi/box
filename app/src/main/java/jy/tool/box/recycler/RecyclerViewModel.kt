package jy.tool.box.recycler

import android.app.Application
import com.junfa.growthcompass.terminal.base.BaseViewModel
import jy.tool.box.BR
import jy.tool.box.R
import jy.tool.library.base.ItemBinding
import jy.tool.library.base.MulteItemBinding

/**
 *<pre>
 * @author : jiang
 * @time : 2021/3/26.
 * @desciption :
 * @version :
 *</pre>
 */
class RecyclerViewModel(application: Application) : BaseViewModel(application) {
    var itemBindings = MulteItemBinding.of(
        ItemBinding.of<RecyclerItemViewModel>(
            BR.info,
            R.layout.item_recycler, 1
        )
        ,
        ItemBinding.of<RecyclerItemViewModel>(
            BR.info,
            R.layout.item_recycler2, 2
        )
    )
    var datas = mutableListOf<RecyclerItemViewModel>()

    init {
        getDatas()
    }

    fun getDatas() {
        for (i in 0..210) {
            val item = RecyclerItemViewModel(this)
            item.article = "bottom第${i}项内容"
            datas.add(item)
        }
    }
}