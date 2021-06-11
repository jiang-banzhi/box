package jy.tool.box

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jy.tool.box.databinding.ActivityCropBinding

class CropActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCrop.setOnClickListener {
            binding.imagePreview.setImageBitmap(binding.cropView.cropImage())
        }
    }
}
