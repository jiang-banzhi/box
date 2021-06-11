package jy.tool.library.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import java.io.File

/**
 *<pre>
 * @author : jiang
 * @time : 2020/11/4.
 * @desciption :
 * @version :
 *</pre>
 */
val BASE_URL = "baseUrl"
val SCHOOL_ID = "schoolId"
val SCHOOL_CODE = "schoolCode"


class App : Application() {


    override fun onCreate() {
        super.onCreate()
//        Utils.init(applicationContext)
//        AppDatabase.init(applicationContext)
//        SelectManager.init(this)
//        RxHttp.init(applicationContext, "token")
//        RxHttp.getInstance(getBaseUrl())
//                .setTokenProxy(MyTokenProxy())
//                .create()
//        if (!BuildConfig.DEBUG) {
//            CrashUtils.init(getCrashDir())
//        }
    }

    private fun getCrashDir(): String {
        return externalCacheDir!!.absolutePath + packageName + File.separator + "crach"
    }

    private fun getBaseUrl(): String = ""

    companion object {
        private val apps = mutableListOf<Activity>()
        @SuppressLint("StaticFieldLeak")

                /**
                 * 将activity加入栈管理
                 *
                 * @param activity
                 */
        fun addActivity(activity: Activity?) {
            if (null != activity && !apps.contains(activity)) {
                apps.add(activity)
            }
        }

        /**
         * 将activity移除栈管理
         *
         * @param activity
         */
        fun removeActivity(activity: Activity?) {
            if (null != activity && apps.contains(activity))
                apps.remove(activity)
        }

        /**
         * 结束指定的Activity
         */
        fun finishActivity(cls: Class<*>) {
            val it = apps.iterator()
            while (it.hasNext()) {
                val s = it.next()
                if (cls == s.javaClass) {
                    it.remove()
                    s.finish()
                }
            }
        }

        /**
         * 移除所有activity
         */
        fun removeAll() {
            while (!apps.isNullOrEmpty()) {
                val activity = apps[0]
                apps.remove(activity)
                if (activity != null && !activity.isFinishing) {
                    activity.finish()
                }
            }
        }

        /**
         * 获取当前activity
         *
         * @return 当前activity
         */
        fun getCurrentActivitly(): Activity? {
            return if (!apps.isNullOrEmpty()) {
                apps[apps.size - 1]
            } else null
        }

        fun getStackActivitiesNum(): Int {
            return apps.size
        }


    }
}