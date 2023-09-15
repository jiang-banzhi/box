package jy.tool.library.livedata

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import jy.tool.library.BuildConfig
import java.util.ArrayList
import kotlin.random.Random

/**
 *<pre>
 *
 *private var requestPermissionLiveData = RequestPermissionLiveData(activityResultRegistry, "key")
 *mBinding.btRequestPermission.setOnClickListener {
 *requestPermissionLiveData.requestPermission(Manifest.permission.RECORD_AUDIO)
 *}
 *requestPermissionLiveData.observe(this) { map ->
 *key 为权限名称，value 为是否权限是否赋予
 *}
 */
class RequestPermissionLiveData(
    private val register: ActivityResultRegistry,
    private val key: String
) :
    LiveData<MutableMap<String, Boolean>>() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onActive() {
        super.onActive()
        requestPermissionLauncher =
            register.register(key, ActivityResultContracts.RequestMultiplePermissions()) {
                value = it
            }
    }


    override fun onInactive() {
        super.onInactive()
        requestPermissionLauncher.unregister()
    }

    private val requestPermissions = mutableListOf<String>()

    fun requestPermission(@SuppressLint("UnknownNullness") permission: String) =
        requestPermission(arrayOf(permission))

    fun requestPermission(permissions: Array<String>) {
        requestPermissions.addAll(permissions)
        requestPermissionLauncher.launch(requestPermissions.toTypedArray())
    }


}