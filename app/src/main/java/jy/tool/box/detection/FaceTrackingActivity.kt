package jy.tool.box.detection

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.media.FaceDetector
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import jy.tool.box.databinding.ActivityFaceTrackingBinding
import jy.tool.library.permission.requestPermission
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class FaceTrackingActivity : AppCompatActivity() {

    val REQUEST_PERMISSION = 713
    private lateinit var binding: ActivityFaceTrackingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
        startCamera()
        binding.btnSwitch.setOnClickListener {
            if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                lensFacing = CameraSelector.LENS_FACING_FRONT
            } else {
                lensFacing = CameraSelector.LENS_FACING_BACK
            }
            startCamera()
        }
    }

    var lensFacing = CameraSelector.LENS_FACING_BACK
    private fun startCamera() {
        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)
            // Select back camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                //配置图片扫描
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(
                        Size(
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels
                        )
                    )
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                //绑定图片扫描解析
                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(), object : ImageAnalysis.Analyzer {
                        @SuppressLint("UnsafeExperimentalUsageError")
                        override fun analyze(imageProxy: ImageProxy) {
//                            FaceTask(imageProxy).execute()
                            FaceCropTask(imageProxy).execute()
//                            //分析完成后关闭图像参考，否则会阻塞其他图像的产生 将不会获取到下一张图片
//                            imageProxy.close()
                        }

                    })
                // Bind use cases to camera
                var camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
                    imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e("ERROR1", exc.toString())
            }

        }, ContextCompat.getMainExecutor(this))

    }


    val MAX_FACE_NUM = 1

    inner class FaceTask(var imageProxy: ImageProxy) :
        AsyncTask<Void, Void, Array<FaceDetector.Face?>?>() {
        @SuppressLint("UnsafeExperimentalUsageError")
        override fun doInBackground(vararg arg: Void): Array<FaceDetector.Face?>? {
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return null
            }
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            var bm =
                ImageConvertUtils.getInstance().getUpRightBitmap(image)
            bm = bm.copy(Bitmap.Config.RGB_565, true)
            val faceDetector = FaceDetector(bm.getWidth(), bm.getHeight(), MAX_FACE_NUM)
            val faces = arrayOfNulls<FaceDetector.Face>(MAX_FACE_NUM)
            val realFaceNum = faceDetector.findFaces(bm, faces)
            return if (realFaceNum > 0) {
                faces
            } else null
        }

        override fun onPostExecute(result: Array<FaceDetector.Face?>?) {
            super.onPostExecute(result)
            if (result?.isNullOrEmpty() == false) {
                Log.e("task", "检测到人脸")
            } else {
                Log.e("task", "未检测到人脸")
            }
            Log.e(TAG, "colose")
            imageProxy.close()

        }
    }

    inner class FaceCropTask(var imageProxy: ImageProxy) :
        AsyncTask<Void, Void, File?>() {
        @SuppressLint("UnsafeExperimentalUsageError")
        override fun doInBackground(vararg arg: Void): File? {

            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return null
            }
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            var bm =
                ImageConvertUtils.getInstance().getUpRightBitmap(image)
            val faceDetector = FaceDetector(bm.getWidth(), bm.getHeight(), MAX_FACE_NUM)
//            val faces = arrayOfNulls<FaceDetector.Face>(MAX_FACE_NUM)
//            val realFaceNum = faceDetector.findFaces(bm, faces)
//            if (realFaceNum > 0) {
//                binding.trackingView.setFaces(faces)
//            }
            return cropBitmap(bm)
        }

        override fun onPostExecute(file: File?) {
            super.onPostExecute(file)
            if (file != null) {
                Log.e(TAG, "检测到人脸 路径==》${file.absolutePath}")
//                onBackPressed()
                imageProxy.close()
            } else {
                Log.e(TAG, "未检测到人脸")
//                Toast.makeText(applicationContext, "图片中未检测到张人脸", Toast.LENGTH_SHORT).show()
                imageProxy.close()
            }

        }
    }

    val TAG = "TAG"
    private fun cropBitmap(bitmap: Bitmap): File? {

        val intArray = IntArray(2)
        binding.faceView.getFaceLocationInWindow(intArray)
        val createBitmap = Bitmap.createBitmap(
            bitmap,
            intArray[0],
            intArray[1],
            binding.faceView.getFaceOverlayWidth().toInt(),
            binding.faceView.getFaceOverlayHeigth().toInt()
        )
        //必须是565才能识别
        val bitmap1: Bitmap = createBitmap.copy(Bitmap.Config.RGB_565, true)
        val faceDetector = FaceDetector(bitmap1.width, bitmap1.height, 1)
        val array = arrayOfNulls<FaceDetector.Face>(1)
        val faces = faceDetector.findFaces(bitmap1, array)
        if (faces > 0) {
            binding.trackingView.setFaces(array)
            val face = array[0] ?: return null
            Log.e("cropBitmap", "${face.eyesDistance()}")
            if (face.eyesDistance() * 5f < bitmap1.width) {//识别到的人脸眼间距区域小于识别图片的五分之一时 视为未识别到人脸
                Log.e(TAG, "距离过远")
                return null
            }
            val file = File(externalCacheDir, "head_tmp${(Math.random() * 10).toInt()}.png")
            Log.e("filePath", file.absolutePath)
            Log.e(TAG, "faces json==》${Gson().toJson(array)}")
            val fos = FileOutputStream(file.path)
            createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            return file
        }
        Log.e(TAG, "未检测到人脸")
        return null

    }

    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap? {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

}