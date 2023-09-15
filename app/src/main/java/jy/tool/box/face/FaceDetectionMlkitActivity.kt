package jy.tool.box.face

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import jy.tool.box.databinding.ActivityFaceDetectionBinding
import java.util.concurrent.Executors

class FaceDetectionMlkitActivity : AppCompatActivity() {
    val REQUEST_PERMISSION = 713
    lateinit var binding: ActivityFaceDetectionBinding
    var detector: FaceDetector? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
        // High-accuracy landmark detection and face classification
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        detector = FaceDetection.getClient(highAccuracyOpts)
        startCamera()

    }

    private fun startCamera() {
        binding.previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
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
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
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
                            val mediaImage = imageProxy.image ?: run {
                                imageProxy.close()
                                return
                            }
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            decodeFace(image)
                        }

                    })
                // Bind use cases to camera
                var camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun decodeFace(image: InputImage) {
        detector?.let {
            it.process(image)
                .addOnSuccessListener { faces ->
                    printFace(faces)
                }
                .addOnFailureListener { e ->
                    Log.e("ERROR", e.toString())
                }
        }
    }

    private fun printFace(faces: MutableList<Face>) {
        for (face in faces) {
            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
            }

            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            // If classification was enabled:
            if (face.smilingProbability != null) {
                val smileProb = face.smilingProbability
            }
            if (face.rightEyeOpenProbability != null) {
                val rightEyeOpenProb = face.rightEyeOpenProbability
            }

            // If face tracking was enabled:
            if (face.trackingId != null) {
                val id = face.trackingId
            }
            Log.e(
                "FACEDecode",
                "leftEar=${leftEar};leftEyeContour=${leftEyeContour};upperLipBottomContour=${upperLipBottomContour};"
            )
            Log.e("FACE","LEFT_EYE=>${face.getLandmark(FaceLandmark.LEFT_EYE)}")
            Log.e("FACE","RIGHT_EYE=>${face.getLandmark(FaceLandmark.RIGHT_EYE)}")
            Log.e("FACE","LEFT_EAR=>${face.getLandmark(FaceLandmark.LEFT_EAR)}")
            Log.e("FACE","LEFT_EAR=>${face.getLandmark(FaceLandmark.RIGHT_EAR)}")
            Log.e("FACE","LEFT_CHEEK=>${face.getLandmark(FaceLandmark.LEFT_CHEEK)}")
            Log.e("FACE","RIGHT_CHEEK=>${face.getLandmark(FaceLandmark.RIGHT_CHEEK)}")
            Log.e("FACE","MOUTH_LEFT=>${face.getLandmark(FaceLandmark.MOUTH_LEFT)}")
            Log.e("FACE","MOUTH_RIGHT=>${face.getLandmark(FaceLandmark.MOUTH_RIGHT)}")
            Log.e("FACE","MOUTH_BOTTOM=>${face.getLandmark(FaceLandmark.MOUTH_BOTTOM)}")
            Log.e("FACE","NOSE_BASE=>${face.getLandmark(FaceLandmark.NOSE_BASE)}")
            Log.e("FACE",Gson().toJson(face))
        }
    }
}