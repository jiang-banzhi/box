package jy.tool.box.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.hardware.camera2.params.Face
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.material.transition.Hold
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.LuminanceSource
import com.google.zxing.PlanarYUVLuminanceSource
import jy.tool.box.databinding.ActivityPreviewBinding
import jy.tool.box.detection.CameraXPreviewViewTouchListener
import jy.tool.box.detection.ImageUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PreviewActivity : AppCompatActivity() {
    private var mCameraInfo: CameraInfo? = null
    private var mCameraControl: CameraControl? = null
    val REQUEST_PERMISSION = 713
    lateinit var binding: ActivityPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
        binding.takePhoto.setOnClickListener { takePhoto() }
        binding.checkPhoto.setOnClickListener { checkPhoto() }
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()
        detector = BarcodeScanning.getClient(options)
        startCamera()
    }

    private fun checkPhoto() {
        initCheck()
    }

    class SelectPhotoContract : ActivityResultContract<Unit?, Uri?>() {
        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(Intent.ACTION_PICK).setType("image/*")
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }


    }

    var registerForActivityResult =
        registerForActivityResult(SelectPhotoContract()) { uri ->
            detector?.let {
                it.process(InputImage.fromFilePath(this, uri))
                    .addOnSuccessListener { barCodes ->
                        if (barCodes.size > 0) {
                            Log.e("扫描结果 ==>", "${barCodes.size}\n")
                            barCodes.forEach { b ->
                                Log.e("扫描结果 for==>", "${b.rawValue}\n")
                            }
                            Log.e("扫描结果", "${barCodes[0].rawValue}")
                            Toast.makeText(this, "${barCodes[0].rawValue}", Toast.LENGTH_SHORT)
                                .show()
                            //接收到结果后，就关闭解析
//                            detector?.close()
                            onBackPressed()
                        }
                    }
                    .addOnFailureListener { Log.d("扫描失败", "Error: ${it.message}") }
                    .addOnCompleteListener {
                        Log.d("扫描失败", "Error: 结束")

                    }
            }
        }

    private fun initCheck() {
        registerForActivityResult.launch(null)
    }

    var detector: BarcodeScanner? = null
    var imageCapture: ImageCapture? = null
    private fun startCamera() {

        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            // Preview
            val preview = Preview.Builder()
                .build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)
            // Select back camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                //配置图片扫描
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(720, 1920))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                //绑定图片扫描解析
                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(), object : ImageAnalysis.Analyzer {
                        @SuppressLint("UnsafeExperimentalUsageError")
                        override fun analyze(imageProxy: ImageProxy) {
                            Log.e("ERROR","analyze0")
                            val mediaImage = imageProxy.image ?: run {
                                imageProxy.close()
                                return
                            }
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            decodeQRCode(image, imageProxy)
                        }

                    })
                // Bind use cases to camera
                var camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
                    imageAnalysis/*二维码识别*/, imageCapture/*拍照*/
                )
                // 相机控制，如点击
                mCameraControl = camera.cameraControl
                mCameraInfo = camera.cameraInfo

                initCameraListener()
            } catch (exc: Exception) {
                Log.e("ERROR==>",exc.toString())
            }

        }, ContextCompat.getMainExecutor(this))
    }
    val TAG = "TAG"

    // 相机点击等相关操作监听
    private fun initCameraListener() {
        val zoomState: LiveData<ZoomState> = mCameraInfo!!.zoomState
        val cameraXPreviewViewTouchListener = CameraXPreviewViewTouchListener(this)

        cameraXPreviewViewTouchListener.setCustomTouchListener(object :
            CameraXPreviewViewTouchListener.CustomTouchListener {
            // 放大缩小操作
            override fun zoom(delta: Float) {
                Log.d(TAG, "缩放")
                zoomState.value?.let {
                    val currentZoomRatio = it.zoomRatio
                    mCameraControl!!.setZoomRatio(currentZoomRatio * delta)
                }
            }

            // 点击操作
            override fun click(x: Float, y: Float) {
                Log.d(TAG, "单击对焦")
                val factory = binding.previewView.meteringPointFactory
                // 设置对焦位置
                val point = factory.createPoint(x, y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    // 3秒内自动调用取消对焦
                    .setAutoCancelDuration(3, TimeUnit.SECONDS)
                    .build()
                // 执行对焦
                binding.focusView.startFocus(Point(x.toInt(), y.toInt()))
                val future: ListenableFuture<*> = mCameraControl!!.startFocusAndMetering(action)
                future.addListener({
                    try {
                        // 获取对焦结果
                        val result = future.get() as FocusMeteringResult
                        if (result.isFocusSuccessful) {
                            binding.focusView.onFocusSuccess()
                        } else {
                            binding.focusView.onFocusFailed()
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e(TAG, e.toString())
                    }
                }, ContextCompat.getMainExecutor(this@PreviewActivity))
            }

            // 双击操作
            override fun doubleClick(x: Float, y: Float) {
                Log.d(TAG, "双击")
                // 双击放大缩小
                val currentZoomRatio = zoomState.value!!.zoomRatio
                if (currentZoomRatio > zoomState.value!!.minZoomRatio) {
                    mCameraControl!!.setLinearZoom(0f)
                } else {
                    mCameraControl!!.setLinearZoom(0.5f)
                }
            }

            override fun longPress(x: Float, y: Float) {
                Log.d(TAG, "长按")
            }
        })
        // 添加监听事件
        binding.previewView.setOnTouchListener(cameraXPreviewViewTouchListener)
    }
    /**
     * 识别二维码
     */
    private fun decodeQRCode(
        image: InputImage,
        imageProxy: ImageProxy
    ) {
        detector?.let {
            it.process(image)
                .addOnSuccessListener { barCodes ->
                    if (barCodes.size > 0) {
                        Log.e("扫描结果 ==>", "${barCodes.size}\n")
                        barCodes.forEach { b ->
                            Log.e("扫描结果 for==>", "${b.rawValue}\n")
                        }
                        Log.e("扫描结果", "${barCodes[0].rawValue}")
                        Log.e(
                            "扫描结果",
                            "imageProxy.width==${imageProxy.width}   imageProxy.height=${imageProxy.height}"
                        )
                        Toast.makeText(this, "${barCodes[0].rawValue}", Toast.LENGTH_SHORT).show()
                        //接收到结果后，就关闭解析
                        detector?.close()
                        onBackPressed()
                    }
                }
                .addOnFailureListener { Log.d("扫描失败", "Error: ${it.message}") }
                .addOnCompleteListener { imageProxy.close() }
        }
    }


    val FILENAME_FORMAT = "yyyy-MM-dd-HH:mm:ss"
    var outputDirectory: String? = null
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the image
        outputDirectory = externalCacheDir?.path
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.CHINA
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d("TAg", msg)
                }
            })
    }
}
