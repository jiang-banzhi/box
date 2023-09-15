package jy.tool.library.livedata

import androidx.lifecycle.MutableLiveData
import jy.tool.library.event.SingleLiveData


/**
 *<pre>
 * @author : jiang
 * @time : 2021/8/31.
 * @desciption :
 * @version :
 *</pre>
 */

object LiveDataBus {
    private val BUS: MutableMap<String, SingleLiveData<Any>> = mutableMapOf()

    fun <T> with(key: String, type: Class<T>?): SingleLiveData<T> {
        if (!BUS.containsKey(key)) {
            BUS[key] = SingleLiveData()
        }
        return BUS[key] as SingleLiveData<T>
    }

    fun with(key: String): MutableLiveData<Any>? {
        return with<Any>(key, Any::class.java)
    }
}