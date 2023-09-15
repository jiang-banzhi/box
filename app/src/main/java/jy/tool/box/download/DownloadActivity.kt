package jy.tool.box.download

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jy.tool.box.databinding.ActivityDownloadBinding


class DownloadActivity : AppCompatActivity() {
    val path = "http://www.junfatech.com/appdownload/teacher.apk"
    private var utils: DownloadUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        utils = DownloadUtils(this)
        utils?.bindService(this, path)
    }


    override fun onDestroy() {
        super.onDestroy()
        utils?.unbindService(this)
    }
}
