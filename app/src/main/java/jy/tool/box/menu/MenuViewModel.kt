package jy.tool.box.menu

import android.app.Application
import android.hardware.camera2.params.Face
import com.junfa.growthcompass.terminal.base.BaseViewModel
import jy.tool.box.BR
import jy.tool.box.R
import jy.tool.box.activityResultLauncher.ActivityResultLauncherActivity
import jy.tool.box.camera.PreviewActivity
import jy.tool.box.clip.ClipActivity
import jy.tool.box.coin.CoinActivity
import jy.tool.box.crop.CropActivity
import jy.tool.box.detection.DetectionActivity
import jy.tool.box.detection.FaceTrackingActivity
import jy.tool.box.download.DownloadActivity
import jy.tool.box.exif.ExifActivity
import jy.tool.box.face.FaceDetectionMlkitActivity
import jy.tool.box.matrix.MatrixActivity
import jy.tool.box.paging.PagingActivity
import jy.tool.box.qrcode.QRCodeActivity
import jy.tool.box.recycler.RecyclerActivity
import jy.tool.box.ruler.RulerActivity
import jy.tool.box.signature.SignatureActivity
import jy.tool.box.tablayout.TabLayoutActivity
import jy.tool.library.base.ItemBinding
import jy.tool.library.base.MulteItemBinding

/**
 *<pre>
 * @author : jiang
 * @time : 2021/3/26.
 * @desciption :
 * @version :
 *</pre>
 */
class MenuViewModel(application: Application) : BaseViewModel(application) {
    var itemBindings = MulteItemBinding.of(
        ItemBinding.of<MenuItemViewModel>(
            BR.menu,
            R.layout.item_menu, 1
        )
    )
    var datas = mutableListOf<MenuItemViewModel>()
    var mAdapter: MenuAdapter = MenuAdapter()

    init {
        getDatas()
    }

    fun getDatas() {
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("recyclerview使用示例", RecyclerActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("直尺", RulerActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("图片裁剪", CropActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("图片信息", ExifActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("手机签名", SignatureActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("paging使用示例", PagingActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("图片查看(拖拽、缩放)", MatrixActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo(" Clip使用示例", ClipActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("自定义LayoutManager", CoinActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("下载示例", DownloadActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(MenuInfo("mlkt Camera拍照、识别二维码", PreviewActivity::class.java))
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "ActivityResultLauncher",
                    ActivityResultLauncherActivity::class.java
                )
            )
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "二维码",
                    QRCodeActivity::class.java
                )
            )
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "人脸识别",
                    FaceDetectionMlkitActivity::class.java
                )
            )
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "检测人脸",
                    DetectionActivity::class.java
                )
            )
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "人脸跟踪",
                    FaceTrackingActivity::class.java
                )
            )
        })
        datas.add(MenuItemViewModel(this).apply {
            menus.set(
                MenuInfo(
                    "tabLayout",
                    TabLayoutActivity::class.java
                )
            )
        })

    }

    fun getItem(position: Int) = datas[position].menus.get()
}