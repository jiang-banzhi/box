package jy.tool.box.paging

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jy.tool.box.R


/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/25.
 * @desciption :
 * @version :
 *</pre>
 */
class PagingAdapter() : PagedListAdapter<Concert, PagingAdapter.RecyclerViewHolder>(DIFF_CALLBACK) {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        return RecyclerViewHolder(view)
    }


    override fun onBindViewHolder(@NonNull holder: RecyclerViewHolder, position: Int) {
        val concert = getItem(position)
        if (concert != null) {
            holder.mTitleTextView.setText(concert.title)
            holder.mAuthorTextView.setText(concert.author)
            holder.mContentTextView.setText(concert.content)
        }
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mTitleTextView: TextView
        internal var mAuthorTextView: TextView
        internal var mContentTextView: TextView


        init {
            mTitleTextView = itemView.findViewById(R.id.title)
            mAuthorTextView = itemView.findViewById(R.id.author)
            mContentTextView = itemView.findViewById(R.id.content)
        }
    }


}

val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Concert>() {
    override fun areItemsTheSame(oldConcert: Concert, newConcert: Concert): Boolean {
        return oldConcert.title == newConcert.title
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldConcert: Concert,
        newConcert: Concert
    ): Boolean {
        return oldConcert == newConcert
    }
}