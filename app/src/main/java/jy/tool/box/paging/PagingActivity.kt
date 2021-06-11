package jy.tool.box.paging

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.junfa.growthcompass.terminal.base.BaseActivity
import jy.tool.box.R
import jy.tool.box.databinding.ActivityCropBinding
import jy.tool.box.databinding.ActivityPagingBinding

class PagingActivity : BaseActivity<ViewDataBinding, PagingViewModel>() {

    override fun processClick(view: View?) {

    }

    override fun initParams() {
    }

    override fun initContentView(savedInstanceState: Bundle?): Int = R.layout.activity_paging

    override fun initVariableId(): Int = -1
    private lateinit var mAdapter: PagingAdapter
    override fun initData() {
        val binding = ActivityPagingBinding.inflate(layoutInflater)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PagingActivity)
            mAdapter = PagingAdapter()
            adapter = mAdapter

        }
        viewModel!!.getConvertList()!!.observe(this, Observer {
            mAdapter.submitList(it)
        })


    }

    override fun initListener() {
    }


}
