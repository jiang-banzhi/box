package jy.tool.box.activityResultLauncher

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import jy.tool.box.*
import jy.tool.box.databinding.ActivityResultLauncherBinding
import jy.tool.library.livedata.TakePhotoLiveData

class ActivityResultLauncherActivity : AppCompatActivity() {
    var binding: ActivityResultLauncherBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultLauncherBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding?.takePhoto?.setOnClickListener {
            needCrop = false
            takePhotoLancher.launch(null)
        }
        binding?.takePhotoCrop?.setOnClickListener {
            needCrop = true
            takePhotoLancher.launch(null)
        }
        binding?.pickPhoto?.setOnClickListener {
            needCrop = false
            selectPhoto.launch(null)
        }
        binding?.pickPhotoCrop?.setOnClickListener {
            needCrop = true
            selectPhoto.launch(null)
        }

    }

    val takePhotoLancher = registerForActivityResult(TakePhotoContract()) { uri ->
        if (uri != null) {
            if (needCrop) {
                cropPhoto.launch(CropParams(uri))
            } else {
                binding?.ivImage?.setImageURI(uri)
            }
        }
    }

    // 剪裁图片
    val cropPhoto = registerForActivityResult(CropPhotoContract()) { uri: Uri? ->
        needCrop = false
        if (uri != null) {
            binding?.ivImage?.setImageURI(uri)
        }
    }
    var needCrop = false

    // 选择图片
    val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->

        if (uri != null) {
            // 返回的选择的图片uri
            if (needCrop) {
                // 需要剪裁图片，再调用剪裁图片的launch()方法
                cropPhoto.launch(CropParams(uri))
            } else {
                // 如果不剪裁图片，则直接显示
                binding?.ivImage?.setImageURI(uri)
            }
        }
    }
}