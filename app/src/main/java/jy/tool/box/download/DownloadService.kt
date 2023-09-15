package jy.tool.box.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Service
import android.content.*
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class DownloadService : Service() {
    private val TAG = DownloadService::class.java.simpleName
    private var sp: SharedPreferences? = null
    private val SP_NAME = DownloadService::class.java.simpleName
    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE)
    }

    private fun saveDownlondId(path: String?, downloadId: Long) {
        sp?.edit()?.putLong(path, downloadId)?.apply()
    }

    private fun findDownloadId(path: String?): Long {
        return sp?.getLong(path, -1L) ?: -1L
    }

    companion object {
        val BUNDLE_KEY_DOWNLOAD_URL = "download_url"
    }

    private val HANDLE_DOWNLOAD = 0x703

    private var scheduledExecutorService: ScheduledExecutorService? = null
    private var downloadManager: DownloadManager? = null
    private var downLoadBroadcast: BroadcastReceiver? = null
    private var downloadObserver: DownloadChangeObserver? = null

    //下载任务ID
    private var downloadId: Long = 0
    private var downloadUrl: String? = null
    private var onProgressListener: OnProgressListener? = null
    var downLoadHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.e(TAG, "handleMessage${downloadId}")
            if (HANDLE_DOWNLOAD == msg.what) {
                Log.e(TAG, "handleMessage==>${msg.arg1} ${msg.arg2}")
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    onProgressListener?.onProgress((msg.arg1 * 100f / msg.arg2), null)
                }

            }
        }
    }
    private val progressRunnable = Runnable {
        Log.e(TAG, "progressRunnable")
        updateProgress()
    }

    /**
     * 发送Handler消息更新进度和状态
     */
    private fun updateProgress() {
        val bytesAndStatus: IntArray = getBytesAndStatus(downloadId)
        downLoadHandler.sendMessage(
            downLoadHandler.obtainMessage(
                HANDLE_DOWNLOAD,
                bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]
            )
        )
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private fun getBytesAndStatus(downloadId: Long): IntArray {
        Log.e(TAG, "getBytesAndStatus==>${downloadId}")
        val bytesAndStatus = intArrayOf(-1, -1, 0)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var cursor: Cursor? = null
        try {
            cursor = downloadManager?.query(query)
            if (cursor?.moveToFirst() == true) {
                //已经下载文件大小
                bytesAndStatus[0] =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                //下载文件的总大小
                bytesAndStatus[1] =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                //下载状态
                bytesAndStatus[2] =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            cursor?.close()
        }
        return bytesAndStatus
    }

    override fun onBind(intent: Intent): IBinder {
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        Log.e(TAG, "Apk下载路径传递成功：$downloadUrl");
        downloadUrl?.let { downloadApk(it) };
        return DownloadBinder()
    }


    /**
     * 下载最新APK
     */
    private fun downloadApk(url: String) {
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val oldId = findDownloadId(url)
        if (oldId != -1L) {
            downloadManager?.remove(oldId)
        }
        downloadObserver = DownloadChangeObserver()
        registerContentObserver()
        val request = DownloadManager.Request(Uri.parse(url))
        /**设置用于下载时的网络状态 */
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        /**设置通知栏是否可见 */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        /**设置漫游状态下是否可以下载 */
        request.setAllowedOverRoaming(false)
        /**如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
         * 我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true. */
        request.setVisibleInDownloadsUi(true)
        /**设置文件保存路径 */
        request.setDestinationInExternalFilesDir(applicationContext, "phoenix", "phoenix.apk")
        /**将下载请求放入队列， return下载任务的ID */
        downloadId = downloadManager?.enqueue(request) ?: -1
        Log.e("downloadId", "downloadId==>${downloadId}")
        saveDownlondId(url, downloadId)
        registerBroadcast()
    }


    /**
     * 注册广播
     */
    private fun registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击 */
        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        registerReceiver(DownLoadBroadcast().also { downLoadBroadcast = it }, intentFilter)
    }

    /**
     * 注销广播
     */
    private fun unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast)
            downLoadBroadcast = null
        }
    }

    /**
     * 注册ContentObserver
     */
    private fun registerContentObserver() {
        /** observer download change  */
        downloadObserver?.let {
            contentResolver.registerContentObserver(
                Uri.parse("content://downloads/my_downloads"), false,
                it
            )
        }
    }

    /**
     * 注销ContentObserver
     */
    private fun unregisterContentObserver() {
        if (downloadObserver != null) {
            contentResolver.unregisterContentObserver(downloadObserver!!)
        }
    }

    /**
     * 关闭定时器，线程等操作
     */
    private fun close() {
        if (scheduledExecutorService?.isShutdown == false) {
            scheduledExecutorService?.shutdown()
        }
        downLoadHandler.removeCallbacksAndMessages(null)
    }

    private var retryCount = 0
    private val MAX_COUNT = 2

    /**
     * 接受下载完成广播
     */
    private inner class DownLoadBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            when (intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {//下载完成
                    if (downloadId == downId && downId != -1L && downloadManager != null
                        && DownloadManager.STATUS_SUCCESSFUL == getDownloadStatus(downloadId)
                    ) {
                        val downIdUri: Uri? =
                            downloadManager?.getUriForDownloadedFile(downloadId)
                        close()

                        onProgressListener?.onProgress(100f, getRealFilePath(downIdUri))
                    } else {
                        close()
                        if (retryCount < MAX_COUNT) {
                            downloadUrl?.let { downloadApk(it) }
                            retryCount++
                        }

                    }
                }
                else -> {

                }
            }
        }
    }


    fun getRealFilePath(uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.ImageColumns.DATA),
                null,
                null,
                null
            )
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * 获取下载状态
     */
    fun getDownloadStatus(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val c: Cursor? = downloadManager?.query(query)
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                }
            } finally {
                c.close()
            }
        }
        return -1
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    /**
     * 监听下载进度
     */
    private inner class DownloadChangeObserver : ContentObserver(downLoadHandler) {
        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        override fun onChange(selfChange: Boolean) {
            Log.e(TAG, "onChange")
            scheduledExecutorService?.scheduleAtFixedRate(progressRunnable, 0, 2, TimeUnit.SECONDS)
        }

        init {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        }
    }

    inner class DownloadBinder : Binder() {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        val service: DownloadService
            get() = this@DownloadService
    }

    interface OnProgressListener {
        /**
         * 下载进度
         *
         * @param fraction 已下载/总大小
         */
        fun onProgress(progress: Float, path: String?)
    }

    /**
     * 对外开发的方法
     *
     * @param onProgressListener
     */
    fun setOnProgressListener(onProgressListener: OnProgressListener) {
        this.onProgressListener = onProgressListener
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
        unregisterBroadcast()
        unregisterContentObserver()
        downloadManager?.remove(downloadId)
    }
}