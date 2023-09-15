package jy.tool.library.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import jy.tool.library.livedata.PermissionUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


/**
 *<pre>
 * @author : jiang
 * @time : 2021/9/14.
 * @desciption :
 * @version :
 *</pre>
 */

inline fun ComponentActivity.requestPermission(
    permissions: Array<String>,
    crossinline block: (deniedList: MutableList<String>, grantList: MutableList<String>) -> Unit
) {
    val deniedList = mutableListOf<String>()//拒绝的权限集合
    val grantList = mutableListOf<String>()//授予的权限集合
    val requestPermissions = mutableListOf<String>().apply { addAll(permissions) }
    if (requestPermissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
        requestInstall() {
            if (it) {
                grantList.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            } else {
                deniedList.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            }
            requestPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            if (requestPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestOverlay() { overlay ->
                    if (overlay) {
                        grantList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    } else {
                        deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    }
                    requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
                }
                PermissionUtils.requestPermission(activityResultRegistry, deniedList, grantList)
            } else {
                PermissionUtils.requestPermission(activityResultRegistry, deniedList, grantList)
            }
        }
        return
    }
    if (requestPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
        requestOverlay() { overlay ->
            if (overlay) {
                grantList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
            } else {
                deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
            requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
        }
        PermissionUtils.requestPermission(activityResultRegistry, deniedList, grantList)
        return
    }
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        it.forEach { entry ->
            val permission = entry.key
            val grant = entry.value
            if (grant) {
                grantList.add(permission)
            } else {
                deniedList.add(permission)
            }
            block.invoke(deniedList, grantList)
        }
    }.launch(requestPermissions.toTypedArray())


}


inline fun ComponentActivity.requestPermission(permissions: Array<String>, boolean: Boolean) {
    val requestPermissions = mutableListOf<String>().apply { addAll(permissions) }
    if (permissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
        requestInstall {

        }
    }
    //跳转到悬浮窗设置页面
    if (requestPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {

    }

    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

    }.launch(requestPermissions.toTypedArray())
}


/**
 * 请求安装未知来源权限
 */

inline fun ComponentActivity.requestInstall(crossinline block: (Boolean) -> Unit = {}) {
    if (PermissionUtils.hasInstallPermission(this)) {
        block.invoke(true)
        return
    }
    Intent(
        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")
    )
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.e(
            "onActivityResult",
            "requestCode=${it.toString()}  resultCode=$${it.resultCode}"
        )
        block.invoke(it.resultCode == Activity.RESULT_OK)

    }.launch(intent)

}


/**
 * 请求悬浮窗权限
 */
inline fun ComponentActivity.requestOverlay(crossinline block: (Boolean) -> Unit = {}) {
    if (PermissionUtils.hasOverlaysPermission(this)) {
        block.invoke(true)
        return
    }
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
    )
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.e(
            "onActivityResult",
            "requestCode=${it.toString()}  resultCode=$${it.resultCode}"
        )
        block.invoke(it.resultCode == Activity.RESULT_OK)


    }.launch(intent)
}

