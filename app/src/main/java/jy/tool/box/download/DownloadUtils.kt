package jy.tool.box.download

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import jy.tool.box.download.DownloadService.DownloadBinder
import jy.tool.box.download.DownloadService.OnProgressListener
import java.io.File
import java.util.*

/**
 * <pre>
 * @author : jiang
 * @time : 2021/8/18.
 * @desciption :
 * @version :
</pre> *
 */
class DownloadUtils(var context: Context) {

    private val TAG = DownloadUtils::class.java.simpleName
    private var isBindService = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DownloadBinder
            val downloadService = binder.service

            //接口回调，下载进度
            downloadService.setOnProgressListener(object : OnProgressListener {


                override fun onProgress(progress: Float, path: String?) {
                    Log.e(
                        TAG,
                        "下载进度：${progress}}"
                    )
                    if (progress == 100f && !path.isNullOrEmpty()) {
                        startInstall(context, path)
                    }
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }


    fun bindService(context: Context, apkUrl: String?) {
        val intent = Intent(context, DownloadService::class.java)
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, apkUrl)
        isBindService = context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        context.unbindService(conn)
    }

    fun startInstall(context: Context, path: String) {
        val uri: Uri
        val file: File
        val install = Intent(Intent.ACTION_VIEW)
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            file = File(path)
            uri = Uri.fromFile(file)
            install.setDataAndType(uri, getMIMEType(file))
        } else {
            file = File(path)
            //            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            if (!file.exists()) {
                return
            }
            uri = FileProvider.getUriForFile(
                context,
                context.applicationInfo.packageName + ".fileProvider",
                file
            )
            install.setDataAndType(uri, context.contentResolver.getType(uri))
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        //        install.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(install)
    }

    private fun getMIMEType(file: File): String? {
        var var1: String? = ""
        val var2 = file.name
        val var3 = var2.substring(var2.lastIndexOf(".") + 1).toLowerCase()
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3)
        return var1
    }

}