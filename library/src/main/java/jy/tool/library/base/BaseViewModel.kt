package com.junfa.growthcompass.terminal.base

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import jy.tool.library.event.SingleLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


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

    val startActivityData: SingleLiveData<MutableMap<String, Any?>> by lazy {
        SingleLiveData<MutableMap<String, Any?>>()
    }
    val showDialogData: SingleLiveData<String?> by lazy { SingleLiveData<String?>() }
    val dismissDialogData: SingleLiveData<Void> by lazy { SingleLiveData<Void>() }
    val onbackpressDialogData: SingleLiveData<Void> by lazy { SingleLiveData<Void>() }
    val clickData: SingleLiveData<View> by lazy { SingleLiveData<View>() }

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
        startActivityData.postValue(params)
    }

    fun showDialog(title: String?) {
        showDialogData.postValue(title)
    }

    fun dismissDialog() {
        dismissDialogData.call()
    }

    fun onbackPress() {
        onbackpressDialogData.call()
    }

    fun click(view: View) {
        clickData.postValue(view)
    }


    fun launch(
        block: suspend () -> Unit,
        error: suspend (Throwable) -> Unit,
        complete: suspend () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                error(e)
            } finally {
                complete()
            }
        }
    }


    object ParameterField {
        var CLASS = "CLASS"
        var CANONICAL_NAME = "CANONICAL_NAME"
        var BUNDLE = "BUNDLE"
    }
}