package jy.tool.box

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import jy.tool.box.crop.CropActivity
import jy.tool.box.databinding.ActivityMainBinding
import jy.tool.box.matrix.MatrixActivity

/**
 * https://blog.csdn.net/magic0908/article/details/101029876
 * https://blog.csdn.net/weixin_42046829/article/details/110231648 ==>Progress indicators、Slider
 * https://blog.csdn.net/weixin_42046829/article/details/110224702 ==>MaterialButton、MaterialButtonToggleGroup、ShapeableImageView
 * https://blog.csdn.net/weixin_42046829/article/details/110220160 ==>SwitchMaterial、Chip、ChipGroup
 * */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rangeSlider.values = listOf(0.0f, 50.0f)
        binding.chipGroupSingleLine.setOnCheckedChangeListener { group, checkedId ->
            Log.e("MainActivity", "chipGroupSingleLine===>$checkedId")
        }

        for (i in 0 until binding.chipGroup.childCount) {
            (binding.chipGroup.getChildAt(i) as Chip).setOnCheckedChangeListener { buttonView, isChecked ->
                Log.e(
                    "MainActivity",
                    "chipGroup===>${buttonView.text}--${buttonView.id}--$isChecked"
                )
            }
        }
        binding.shapeImageView2.shapeAppearanceModel = ShapeAppearanceModel.builder()
//            .setAllCorners(CornerFamily.ROUNDED, 20f)
            .setTopLeftCorner(CornerFamily.CUT, RelativeCornerSize(0.5f))
            .setTopRightCorner(CornerFamily.CUT, RelativeCornerSize(0.5f))
            .setBottomRightCorner(CornerFamily.CUT, RelativeCornerSize(0.5f))
            .setBottomLeftCorner(CornerFamily.CUT, RelativeCornerSize(0.5f))
//            .setAllCornerSizes(ShapeAppearanceModel.PILL)
//            .setTopLeftCornerSize(20f)
//            .setTopRightCornerSize(RelativeCornerSize(0.5f))
//            .setBottomLeftCornerSize(10f)
//            .setBottomRightCornerSize(AbsoluteCornerSize(30f))
            .build()
        var lau = registerForActivityResult(
            Contract()
        ) {
            Log.e("registerForActivityResult", it)
        }
       val lau2= registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }
        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            Log.e("toggleGroup", "======>$isChecked--${findViewById<Button>(checkedId).text}")
//            startActivity(Intent(this@MainActivity, RecyclerActivity::class.java))
//            startActivity(Intent(this@MainActivity, RulerActivity::class.java))
            startActivity(Intent(this@MainActivity, CropActivity::class.java))
//            startActivity(Intent(this@MainActivity, ExifActivity::class.java))
//            startActivity(Intent(this@MainActivity, SignatureActivity::class.java))
//            startActivity(Intent(this@MainActivity, PagingActivity::class.java))
//            startActivity(Intent(this@MainActivity, MatrixActivity::class.java))
//            lau.launch("toggleGroup")
//            lau2.launch(Manifest.permission.CAMERA)
        }

    }

    inner class Contract : ActivityResultContract<String, String>() {
        override fun createIntent(context: Context, input: String?): Intent {
            val intent = Intent(Intent(this@MainActivity, MatrixActivity::class.java))
            intent.putExtra("createIntent", "createIntent${input}")
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            val stringExtra = intent?.getStringExtra("result") ?: "default"
            Log.e("parseResult", "stringExtra")
            return stringExtra
        }

    }


}
