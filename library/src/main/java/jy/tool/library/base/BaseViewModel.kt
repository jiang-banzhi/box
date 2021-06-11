package com.junfa.growthcompass.terminal.base

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import jy.tool.library.event.SingleLiveEvent


/**
 *<pre>
 * @author : jiang
 * @time : 2020/11/3.
 * @desciption :
 * @version :
 *</pre>
 */
open abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    var lifecycle: Lifecycle? = null

    val startActivityEvent: SingleLiveEvent<MutableMap<String, Any?>> by lazy {
        SingleLiveEvent<MutableMap<String, Any?>>()
    }
    val showDialogEvent: SingleLiveEvent<String?> by lazy { SingleLiveEvent<String?>() }
    val dismissDialogEvent: SingleLiveEvent<Void> by lazy { SingleLiveEvent<Void>() }
    val onbackpressDialogEvent: SingleLiveEvent<Void> by lazy { SingleLiveEvent<Void>() }
    val clickEvent: SingleLiveEvent<View> by lazy { SingleLiveEvent<View>() }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>) {
        startActivity(clz, null)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val params = mutableMapOf<String, Any?>()
        params.put(ParameterField.CLASS, clz)
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        startActivityEvent.postValue(params)
    }

    fun showDialog(title: String?) {
        showDialogEvent.postValue(title)
    }

    fun dismissDialog() {
        dismissDialogEvent.call()
    }

    fun onbackPress() {
        onbackpressDialogEvent.call()
    }

    fun click(view: View) {
        clickEvent.postValue(view)
    }


    object ParameterField {
        var CLASS = "CLASS"
        var CANONICAL_NAME = "CANONICAL_NAME"
        var BUNDLE = "BUNDLE"
    }
}