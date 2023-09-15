package jy.tool.box.detection

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.FaceDetector
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import jy.tool.box.SelectPhotoContract
import jy.tool.box.databinding.ActivityDetectionBinding


class DetectionActivity : AppCompatActivity() {
    val REQUEST_PERMISSION = 713
    lateinit var binding: ActivityDetectionBinding
    var detector: FaceDetector? = null
    var paint: Paint? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
        paint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 2f
            style = Paint.Style.STROKE //设置话出的是空心方框而不是实心方块

        }


    }

    open var bm: Bitmap? = null

    // 选择图片
    private val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->

        if (uri != null) {
            binding.ivImage.setImageURI(uri)

            val selectedImagePath: String? = getRealFilePath(this, uri)
            bm = BitmapFactory.decodeFile(selectedImagePath)
            //要使用Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
            bm = bm?.copy(Bitmap.Config.RGB_565, true)
            binding.ivImage.setImageBitmap(bm);

        }
    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(
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

    fun choisePhoto(v: View) {
        selectPhoto.launch(null)
    }

    fun detectFace(v: View) {
        FaceTask().execute()
    }


    inner class FaceTask() : AsyncTask<Void, Void, Array<FaceDetector.Face?>?>() {
        override fun doInBackground(vararg params: Void): Array<FaceDetector.Face?>? {
            val faceDetector = FaceDetector(bm!!.getWidth(), bm!!.getHeight(), MAX_FACE_NUM)
            val faces = arrayOfNulls<FaceDetector.Face>(MAX_FACE_NUM)
            val realFaceNum = faceDetector.findFaces(bm, faces)
            return if (realFaceNum > 0) {
                faces
            } else null
        }

        override fun onPostExecute(result: Array<FaceDetector.Face?>?) {
            super.onPostExecute(result)
            if (result?.isNullOrEmpty()==false){
                drawFacesArea(result);
                binding.trackingView.setFaces(result)
            }else{
                Toast.makeText(applicationContext, "图片中未检测到张人脸", Toast.LENGTH_SHORT).show()
            }

        }
    }

    val MAX_FACE_NUM = 1


    private fun drawFacesArea(faces: Array<FaceDetector.Face?>) {
        Toast.makeText(this, "图片中检测到" + faces.size + "张人脸", Toast.LENGTH_SHORT).show()
        var eyesDistance = 0f //两眼间距
        val canvas = Canvas(bm!!)
        for (i in faces.indices) {
            val face = faces[i]
            if (face != null) {
                val pointF = PointF()
                face.getMidPoint(pointF) //获取人脸中心点
                eyesDistance = face.eyesDistance() //获取人脸两眼的间距
                //画出人脸的区域
                canvas.drawRect(
                    pointF.x - eyesDistance,
                    pointF.y - eyesDistance,
                    pointF.x + eyesDistance,
                    pointF.y + eyesDistance,
                    paint!!
                )
            }
        }
        //画出人脸区域后要刷新ImageView
        binding.ivImage.invalidate()
    }

}