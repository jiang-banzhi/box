package jy.tool.box.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.junfa.growthcompass.terminal.base.BaseActivity
import jy.tool.box.BR
import jy.tool.box.R
import jy.tool.box.databinding.ActivityMainBinding
import jy.tool.box.databinding.ActivityMenuBinding
import jy.tool.box.recycler.RecyclerAdapter
import jy.tool.box.recycler.RecyclerItemViewModel
import jy.tool.library.base.ItemBinding
import jy.tool.library.base.MulteItemBinding
import jy.tool.library.base.RecyclerViewBindingAdapter

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuViewModel>() {

     override fun processClick(view: View?) {

    }

    override fun initParams() {
    }

    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_menu

    override fun initVariableId(): Int = BR.menuViewModel

    override fun initData() {
        viewModel?.mAdapter?.setOnItemClickListener(object :
            RecyclerViewBindingAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                Log.e("TAG", "点击${position}")
                viewModel?.getItem(position)?.let {
                    startActivity(Intent(this@MenuActivity, it.cls))
                }
            }

        })
    }

    override fun initListener() {

    }
}