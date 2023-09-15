package jy.tool.box.clip

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import jy.tool.box.R
import jy.tool.box.databinding.ActivityClipBinding


class ClipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityClipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTab(binding)

    }

    private fun initTab(binding: ActivityClipBinding) {
        for (i in 0..2) {
            val tab: TabLayout.Tab = binding.tabLayout.newTab()

//            tab.text = "第${i}"
//            tab.setIcon(R.drawable.test)
//            tab.view.orientation = LinearLayout.HORIZONTAL

            val view = TextView(this)
            view.text = "第${i}个萨"
//            view.setBackgroundResource(R.drawable.test2)
            view.gravity=Gravity.CENTER
            view.setCompoundDrawables(
                getDrawable(this, R.drawable.test),
                null,
                null,
                null
            )
            view.compoundDrawablePadding = 6
            tab.setCustomView(view);
            tab.view.setBackgroundResource(R.drawable.test2)
            binding.tabLayout.addTab(tab)
        }
    }

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        val dw = context.resources.getDrawable(resId)
        dw.setBounds(0, 0, dw.minimumWidth, dw.minimumHeight)
        return dw
    }
}