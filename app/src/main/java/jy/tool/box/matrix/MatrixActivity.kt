package jy.tool.box.matrix

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jy.tool.box.R
import jy.tool.box.databinding.ActivityCropBinding
import jy.tool.box.databinding.ActivityMainBinding
import jy.tool.box.databinding.ActivityMatrixBinding

class MatrixActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMatrixBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("GlobalLayout==>", "${intent?.getStringExtra("createIntent")}")
        binding.imageView.viewTreeObserver.addOnGlobalLayoutListener {
            Log.e("GlobalLayout==", "viewTreeObserver")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        intent.putExtra("result", ":MatrixActivity")
        setResult(RESULT_OK, intent)
    }
}
