package jy.tool.box.coin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import jy.tool.box.databinding.ActivityCoinBinding
import jy.tool.box.databinding.ActivityMainBinding

class CoinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.apply {
            layoutManager = CoinLayoutManager()
            adapter=CoinAdapter(mutableListOf())
        }
    }
}