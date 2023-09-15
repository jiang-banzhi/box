package jy.tool.library.livedata

import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData

/**
 *
 * 调用系统相机
 *<pre>
 * private var takePhotoLiveData: TakePhotoLiveData = TakePhotoLiveData(activityResultRegistry, "key")
 * 点击拍照按钮
 *mBinding.btTakePhoto.setOnClickListener {
 *takePhotoLiveData.takePhoto()
 *}
 * 拍照返回的照片
 *takePhotoLiveData.observe(this) { bitmap ->
 *mBinding.imageView.setImageBitmap(bitmap)
 *}
 *</pre>
 */
class TakePhotoLiveData(private val register: ActivityResultRegistry, private val key: String) :
    LiveData<Bitmap>() {
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Void>
    override fun onActive() {
        super.onActive()
        takePhotoLauncher = register.register(key, ActivityResultContracts.TakePicturePreview()) {
            value = it
        }
    }

    override fun onInactive() {
        super.onInactive()
        takePhotoLauncher.unregister()
    }

    fun takePhoto() = takePhotoLauncher.launch(null)
}