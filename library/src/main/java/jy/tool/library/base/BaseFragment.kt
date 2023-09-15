package com.junfa.growthcompass.terminal.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType


/**
 *<pre>
 * @author : jiang
 * @time : 2020/11/3.
 * @desciption :
 * @version :
 *</pre>
 */
open abstract class BaseFragment<T : ViewDataBinding, VM : BaseViewModel> : Fragment(),
    View.OnClickListener {
    protected var binding: T? = null
    protected var viewModel: VM? = null
    private var viewModelId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding = DataBindingUtil.inflate(
                inflater, initContentView(inflater, container, savedInstanceState), container, false
            )
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding()
        registorUIChangeLiveDataCallBack()
        initData()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding?.unbind()
    }

    /**
     * view设置onclick事件
     *
     * @param view 点击的view
     */
    fun <V : View> setOnClick(view: View) {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        processClick(v)
    }

    private fun initViewDataBinding() {
        viewModelId = initVariableId()
        if (viewModel == null) {
            val modelClass: Class<VM>
            val type = this.javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<VM>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java as Class<VM>
            }
            viewModel = createViewModel(this, modelClass)
        }
        binding?.lifecycleOwner = this
        binding?.setVariable(viewModelId, viewModel)
    }

    private fun registorUIChangeLiveDataCallBack() {
        (viewModel as BaseViewModel).lifecycle = lifecycle
        (viewModel as BaseViewModel).startActivityData.observe(viewLifecycleOwner,
            Observer {
                val clz = it[BaseViewModel.ParameterField.CLASS] as Class<*>
                val bundle = it[BaseViewModel.ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            })
        (viewModel as BaseViewModel).showDialogData.observe(viewLifecycleOwner, Observer {
            showDialog(it)
        })
        (viewModel as BaseViewModel).dismissDialogData.observe(viewLifecycleOwner, Observer {
            dismissDialog()
        })
        (viewModel as BaseViewModel).onbackpressDialogData.observe(viewLifecycleOwner, Observer {
            activity?.onBackPressed()
        })
        (viewModel as BaseViewModel).clickData.observe(viewLifecycleOwner, Observer {
            processClick(it)
        })
    }

    abstract fun processClick(view: View?)

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(context, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    fun showDialog(title: String?) {

    }

    fun dismissDialog() {

    }

    fun <VM : ViewModel> createViewModel(fragment: Fragment, cls: Class<VM>): VM {
        return ViewModelProvider(fragment).get(cls)
    }

    abstract fun initVariableId(): Int

    /**
     *  /**
     * 初始化根布局
     *
     * @return 布局layout的id
    */
     */
    abstract fun initContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int

    /**
     * 初始数据
     */
    abstract fun initData()

    /**
     * 初始监听
     */
    abstract fun initListener()

}