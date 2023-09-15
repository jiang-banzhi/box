package jy.tool.box.coin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jy.tool.box.R

/**
 *<pre>
 * @author : jiang
 * @time : 2021/7/20.
 * @desciption :
 * @version :
 *</pre>
 */
class CoinAdapter(datas: MutableList<Int>) : RecyclerView.Adapter<CoinViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        return CoinViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_coin, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 5


}

class CoinViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

}