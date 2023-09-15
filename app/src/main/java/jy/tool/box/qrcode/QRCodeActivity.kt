package jy.tool.box.qrcode

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import jy.tool.box.R
import jy.tool.box.databinding.ActivityQrcodeBinding

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btn.setOnClickListener {
            var content = binding.editText.text.toString() ?: return@setOnClickListener
            var toBitmap =
                getDrawable(R.mipmap.ic_launcher)?.toBitmap(48, 48, Bitmap.Config.ARGB_8888)
            var bit = QRCodeUtil.createQRCodeBitmap(content, 480,toBitmap ,0.3f)
            binding.ivImage.setImageBitmap(bit)
        }
    }
}