package jy.tool.box.tablayout

import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.google.android.material.tabs.TabLayout
import jy.tool.box.R
import jy.tool.box.databinding.ActivityTabLayoutBinding

/**
 * https://blog.csdn.net/yechaoa/article/details/122270969
TabLayout
API	含义
background	TabLayout背景颜色
tabIndicator	指示器（一般下划线）
tabIndicatorColor	指示器颜色
tabIndicatorHeight	指示器高度，不显示写0dp
tabIndicatorFullWidth	指示器宽度是否撑满item
tabMode	tab显示形式，1.auto自动，2.fixed固定宽度，3.scrollable可滑动
tabSelectedTextColor	tab选中文字颜色
tabTextColor	tab未选中文字颜色
tabRippleColor	tab点击效果颜色
tabGravity	tab对齐方式
tabTextAppearance	tab文本样式，可引用style
tabMaxWidth	tab最大宽度
tabMinWidth	tab最小宽度
setupWithViewPager	tabLayout关联ViewPager
addOnTabSelectedListener	tab选中监听事件
TabLayout.Tab
API	含义
setCustomView	设置tab自定义view
setIcon	设置tab icon
setText	设置tab文本
getOrCreateBadge	获取或创建badge（小红点）
removeBadge	移除badge（小红点）
select	设置tab选中
isSelected	获取tab选中状态
BadgeDrawable
API	含义
setVisible	设置显示状态
setBackgroundColor	设置小红点背景颜色
getBadgeTextColor	设置小红点文本颜色
setNumber	设置小红点显示数量
clearNumber	清除小红点数量
setBadgeGravity	设置小红点位置对齐方式
 */
class TabLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTabLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        for (i in 0..3) {
            binding.tabLayout2.newTab().setText("tab${i}").setIcon(R.drawable.focus_focused)
            binding.tabLayout2.addTab(
                binding.tabLayout2.newTab().setText("tab${i}").setIcon(R.drawable.focus_focused)
            )
        }
        binding.tabLayout1.getTabAt(0)?.let { hideToolTipText(it) }
        tabDivider(binding.tabLayout7)
        initBadege(binding.tabLayout9)
        lottieTab(binding)
    }

    /**
     * 隐藏长按显示文本
     */
    private fun hideToolTipText(tab: TabLayout.Tab) {
        // 取消长按事件
        tab.view.isLongClickable = false
        // api 26 以上 设置空text
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            tab.view.tooltipText = ""
        }
    }

    private fun tabDivider(tabLayout: TabLayout) {
        //设置 分割线
        for (index in 0..tabLayout.childCount) {
            val linearLayout = tabLayout.getChildAt(index) as? LinearLayout
            linearLayout?.let {
                it.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                it.dividerDrawable = ContextCompat.getDrawable(this, R.drawable.shape_tab_divider)
                it.dividerPadding = 30
            }
        }
    }

    private fun initBadege(tabLayout: TabLayout) {
        // 数字
        tabLayout.getTabAt(0)?.let { tab ->
            tab.orCreateBadge.apply {
                backgroundColor = Color.RED
                maxCharacterCount = 3
                number = 99999
                badgeTextColor = Color.WHITE
            }
        }

// 红点
        tabLayout.getTabAt(1)?.let { tab ->
            tab.orCreateBadge.backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }
    }

    private fun lottieTab(binding: ActivityTabLayoutBinding) {
        val map = mapOf(
            "pizza" to R.raw.pizza,
            "confetti" to R.raw.confetti,
            "location" to R.raw.location
        )
        map.keys.forEach {
            val tab = binding.tabLayout11.newTab()
            val view = LayoutInflater.from(this).inflate(R.layout.item_tab, null)
            val imageView = view.findViewById<LottieAnimationView>(R.id.lav_tab_img)
            val textView = view.findViewById<TextView>(R.id.tv_tab_text)
            imageView.setAnimation(map[it]!!)
            imageView.setColorFilter(Color.BLUE)
            textView.text = it
            tab.customView = view
            binding.tabLayout11.addTab(tab)
        }
        binding.tabLayout11.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.setSelected()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.setUnselected()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }
    /**
     * 选中状态
     */
    fun TabLayout.Tab.setSelected() {
        this.customView?.let {
            val textView = it.findViewById<TextView>(R.id.tv_tab_text)
            val selectedColor = ContextCompat.getColor(this@TabLayoutActivity, R.color.colorPrimary)
            textView.setTextColor(selectedColor)

            val imageView = it.findViewById<LottieAnimationView>(R.id.lav_tab_img)
            if (!imageView.isAnimating) {
                imageView.playAnimation()
            }
            setLottieColor(imageView, true)
        }
    }
    /**
     * 未选中状态
     */
    fun TabLayout.Tab.setUnselected() {
        this.customView?.let {
            val textView = it.findViewById<TextView>(R.id.tv_tab_text)
            val unselectedColor = ContextCompat.getColor(this@TabLayoutActivity,android.R.color.black)
            textView.setTextColor(unselectedColor)

            val imageView = it.findViewById<LottieAnimationView>(R.id.lav_tab_img)
            if (imageView.isAnimating) {
                imageView.cancelAnimation()
                imageView.progress = 0f // 还原初始状态
            }
            setLottieColor(imageView, false)
        }
    }
    /**
     * set lottie icon color
     */
    private fun setLottieColor(imageView: LottieAnimationView?, isSelected: Boolean) {
        imageView?.let {
            val color = if (isSelected) R.color.colorPrimary else android.R.color.black
            val csl = AppCompatResources.getColorStateList(this@TabLayoutActivity, color)
            val filter = SimpleColorFilter(csl.defaultColor)
            val keyPath = KeyPath("**")
            val callback = LottieValueCallback<ColorFilter>(filter)
            it.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
        }
    }
}