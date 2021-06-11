package jy.tool.box.signature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jy.tool.box.databinding.ActivitySignatureBinding

class SignatureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnClear.setOnClickListener {
            binding.signatureView.clear()
        }
        binding.btnSave.setOnClickListener {
            binding.imagePreview.setImageBitmap(binding.signatureView.createBitmap())
        }
    }
}
