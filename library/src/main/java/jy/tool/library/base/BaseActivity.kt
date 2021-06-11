package com.junfa.growthcompass.terminal.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jy.tool.library.app.App
import java.lang.reflect.ParameterizedType


/**
 *<pre>
 * @author : jiang
 * @time : 2020/11/3.
 * @desciption :
 * @version :
 *</pre>
 */
 open abstract class BaseActivity<T : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), View.OnClickListener {

    protected lateinit var binding: T
    protected var viewModel: VM? = null
    private var viewModelId: Int = 0
    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.addActivity(this)
        initParams()
        initViewDataBiding(savedInstanceState)
        registorUIChangeLiveDataCallBack()
        initData()
        initListener()
    }

    private fun registorUIChangeLiveDataCallBack() {
        (viewModel as BaseViewModel).lifecycle = lifecycle
        (viewModel as BaseViewModel).startActivityEvent.observe(this,
            Observer {
                val clz = it[BaseViewModel.ParameterField.CLASS] as Class<*>
                val bundle = it[BaseViewModel.ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            })
        (viewModel as BaseViewModel).showDialogEvent.observe(this, Observer {
            showDialog(it)
        })
        (viewModel as BaseViewModel).dismissDialogEvent.observe(this, Observer {
            dismissDialog()
        })
        (viewModel as BaseViewModel).onbackpressDialogEvent.observe(this, Observer {
            onBackPressed()
        })
        (viewModel as BaseViewModel).clickEvent.observe(this, Observer {
            processClick(it)
        })
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

    abstract fun processClick(view: View?)

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    fun showDialog(title: String?) {

    }

    fun dismissDialog() {

    }

    /**
     * 初始传递的参数
     */
    abstract fun initParams()

    /**
     * 出生databiding 和viewmodel
     */
    private fun initViewDataBiding(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
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
        binding.lifecycleOwner = this
        binding.setVariable(viewModelId, viewModel)
    }

    fun <VM : ViewModel> createViewModel(activity: AppCompatActivity, cls: Class<VM>): VM {
        return ViewModelProvider(activity).get(cls)
    }

    abstract fun initContentView(savedInstanceState: Bundle?): Int


    /**
     * 初始viewmode id
     * @return  BR的id
     */
    abstract fun initVariableId(): Int

    abstract fun initData()

    abstract fun initListener()


}