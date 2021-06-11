package jy.tool.box.recycler

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.junfa.growthcompass.terminal.base.BaseActivity
import jy.tool.box.BR
import jy.tool.box.R
import jy.tool.box.databinding.ActivityRecyclerBinding
import jy.tool.library.base.ItemBinding
import jy.tool.library.base.MulteItemBinding
import jy.tool.library.base.RecyclerViewBindingAdapter

class RecyclerActivity : BaseActivity<ActivityRecyclerBinding, RecyclerViewModel>() {


    override fun processClick(view: View?) {


    }

    override fun initParams() {

    }

    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_recycler

    override fun initVariableId(): Int = BR.recyclerModel

    private lateinit var mAdapter: RecyclerAdapter

    //    private lateinit var bottomAdapter: RecyclerViewBindingAdapter<RecyclerItemViewModel>
    private lateinit var bottomAdapter: RecyclerAdapter

    override fun initData() {
        binding.recyclerView.apply {
            //            layoutManager = LinearLayoutManager(this@RecyclerActivity)
            layoutManager = GridLayoutManager(this@RecyclerActivity, 3)
            mAdapter = RecyclerAdapter()
            mAdapter.setDatas(getDatas())
            mAdapter.addHeadView(getHeadView())
            mAdapter.addFootView(getHeadView())
            mAdapter.setEmptyView(getEmptyView())
            mAdapter.setItemBinding(
                MulteItemBinding.of(
                    ItemBinding.of<RecyclerItemViewModel>(
                        BR.info,
                        R.layout.item_recycler, 1
                    ),
                    ItemBinding.of<RecyclerItemViewModel>(
                        BR.info,
                        R.layout.item_recycler2, 2
                    )
                )
            )
            mAdapter.setOnItemClickListener(object :
                RecyclerViewBindingAdapter.OnItemClickListener {
                override fun onItemClickListener(view: View, position: Int) {
                    Log.e("TAG", "点击${position}")
                }

            })
            mAdapter.setSpanSizeLookup(object : RecyclerViewBindingAdapter.SpanSizeLookup {
                override fun getSpanSize(gridLayoutManager: GridLayoutManager, position: Int): Int {
                    if (position % 5 == 0) {
                        return gridLayoutManager.spanCount
                    } else {
                        return 1
                    }

                }

            })
//            mAdapter.setLayoutId(R.layout.item_recycler)
            adapter = mAdapter
        }
//        bottomAdapter = RecyclerViewBindingAdapter<RecyclerItemViewModel>()
        bottomAdapter = RecyclerAdapter()
//        bottomAdapter.setSpanSizeLookup(object : RecyclerViewBindingAdapter.SpanSizeLookup {
//            override fun getSpanSize(gridLayoutManager: GridLayoutManager, position: Int): Int {
//                if (position % 5 == 0) {
//                    return gridLayoutManager.spanCount
//                } else {
//                    return 1
//                }
//
//            }
//
//        })
        bottomAdapter.setOnItemClickListener(object :
            RecyclerViewBindingAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                Toast.makeText(this@RecyclerActivity, "点击${position}", Toast.LENGTH_SHORT).show()
            }

        })
        binding.emptyView = getEmptyView()
        binding.footView = getHeadView()
        binding.headView = getHeadView()
        val value = object : RecyclerViewBindingAdapter.SpanSizeLookup {
            override fun getSpanSize(gridLayoutManager: GridLayoutManager, position: Int): Int {
                if (position % 5 == 0) {
                    return gridLayoutManager.spanCount
                } else {
                    return 1
                }
            }

        }
        binding.lookup = value
        binding.adapter = bottomAdapter
    }

    fun getHeadView(): TextView {
        val textView = TextView(this@RecyclerActivity)
        textView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        textView.gravity = Gravity.CENTER
        textView.setPadding(12, 12, 12, 12)
        textView.text = "HEADVIEW"
        return textView
    }

    fun getEmptyView(): TextView {
        val textView = TextView(this@RecyclerActivity)
        textView.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        textView.gravity = Gravity.CENTER
        textView.setPadding(12, 12, 12, 12)
        textView.text = "EmptyView"
        return textView
    }

    private fun getDatas(): MutableList<RecyclerItemViewModel> {
        val list = mutableListOf<RecyclerItemViewModel>()
        for (i in 0..210) {
            val item = RecyclerItemViewModel(viewModel)
            item.article = "第${i}项内容"
            list.add(item)
        }
        return list
    }

    override fun initListener() {

    }


}
