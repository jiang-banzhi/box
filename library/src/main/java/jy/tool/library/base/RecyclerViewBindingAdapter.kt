package jy.tool.library.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 *<pre>
 * @author : jiang
 * @time : 2021/3/10.
 * @desciption :
 * @version :
 *</pre>
 */
open class RecyclerViewBindingAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_HEAD = 0x00000703
        const val TYPE_FOOT = 0x00000314
        const val TYPE_EMPTY = 0x00000306
        const val TYPE_NORMAL = 0
    }

    private var mHeadLayout: LinearLayout? = null
    private var mFootlayout: LinearLayout? = null
    private var mEmptyLayout: FrameLayout? = null
    private var itemBindings: MulteItemBinding<T>? = null
    var mDatas: List<T>? = null
    private var inflater: LayoutInflater? = null
    private var layoutId: Int = 0
    private var viewHolderFactory: ViewHolderFactory? = null
    private var recyclerView: RecyclerView? = null
    private var lifecycleOwner: LifecycleOwner? = null
    internal var mSpanSizeLookup: SpanSizeLookup? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val type = getItemViewType(position)
                    return if (mSpanSizeLookup == null) {
                        if (isFixedViewType(type)) layoutManager.spanCount else 1
                    } else {
                        if (isFixedViewType(type))
                            layoutManager.spanCount
                        else
                            mSpanSizeLookup!!.getSpanSize(layoutManager, position - getHeaderLayoutCount())
                    }
                }
            }
        }
    }

    protected fun isFixedViewType(type: Int): Boolean {
        return type == TYPE_EMPTY || type == TYPE_HEAD || type == TYPE_FOOT

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun setLifecycleOwner(@Nullable lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        recyclerView?.let {
            for (i in 0 until it.childCount) {
                val child = it.getChildAt(i)
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(child)
                if (binding != null) {
                    binding.lifecycleOwner = lifecycleOwner
                }
            }
        }
    }

    fun setLayoutId(layoutId: Int) {
        this.layoutId = layoutId
    }

    fun setViewHolderFactory(factory: ViewHolderFactory?) {
        this.viewHolderFactory = factory
    }

    fun setItemBinding(itemBindings: MulteItemBinding<T>) {
        this.itemBindings = MulteItemBinding.of(itemBindings)
    }

    fun setItemBinding(itemBinding: ItemBinding<T>) {
        this.itemBindings = MulteItemBinding.of(itemBinding)
    }

    fun setItemBinding(itembindings: MutableMap<Int, ItemBinding<T>>) {
        this.itemBindings = MulteItemBinding.of(itembindings)
    }

    fun addHeadView(headView: View?) {
        addHeadView(headView, 0, LinearLayout.VERTICAL)
    }

    fun addHeadView(headView: View?, index: Int) {
        addHeadView(headView, index, LinearLayout.VERTICAL)
    }

    fun addHeadView(headView: View?, index: Int, orientation: Int): Int {
        var index = index
        if (mHeadLayout == null) {
            if (orientation == LinearLayout.VERTICAL) {
                mHeadLayout = LinearLayout(headView?.context)
                mHeadLayout?.orientation = LinearLayout.VERTICAL
                mHeadLayout?.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                mHeadLayout = LinearLayout(headView?.context)
                mHeadLayout?.orientation = LinearLayout.HORIZONTAL
                mHeadLayout?.layoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = mHeadLayout?.getChildCount() ?: 0
        if (index < 0 || index > childCount) {
            index = childCount
        }
        mHeadLayout?.addView(headView, index)
        if (mHeadLayout?.childCount == 1) {//第一次添加头信息
            val position = getHeaderViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return index
    }

    fun setHeaderView(header: View?, index: Int, orientation: Int): Int {
        if (mHeadLayout == null || mHeadLayout?.childCount ?: 0 <= index) {
            return addHeadView(header, index, orientation)
        } else {
            mHeadLayout?.removeViewAt(index)
            mHeadLayout?.addView(header, index)
            return index
        }
    }

    /**
     * 移除headview
     *
     * @param header
     */
    fun removeHeaderView(header: View) {
        if (getHeaderLayoutCount() == 0) return

        mHeadLayout?.removeView(header)
        if (mHeadLayout?.childCount == 0) {
            val position = getHeaderViewPosition()
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }


    /**
     * 移除所有头布局
     */
    fun removeAllHeaderView() {
        if (getHeaderLayoutCount() == 0) return

        mHeadLayout?.removeAllViews()
        val position = getHeaderViewPosition()
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    /**
     * 获取头的位置position
     *
     * @return
     */
    private fun getHeaderViewPosition(): Int {
        return if (mHeadLayout != null) {
            0
        } else -1
    }

    /**
     * 添加尾布局
     *
     * @param footView
     */
    fun addFootView(footView: View?) {
        addFootView(footView, -1, LinearLayout.VERTICAL)
    }

    fun addFootView(footView: View?, index: Int) {
        addFootView(footView, index, LinearLayout.VERTICAL)
    }

    fun addFootView(footView: View?, index: Int, orientation: Int): Int {
        var index = index
        if (mFootlayout == null) {
            if (orientation == LinearLayout.VERTICAL) {
                mFootlayout = LinearLayout(footView?.context)
                mFootlayout?.orientation = LinearLayout.VERTICAL
                mFootlayout?.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            } else {
                mFootlayout = LinearLayout(footView?.context)
                mFootlayout?.orientation = LinearLayout.HORIZONTAL
                mFootlayout?.layoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
            }
        }
        val childCount = mFootlayout?.childCount ?: 0
        if (index < 0 || index > childCount) {
            index = childCount
        }
        mFootlayout?.addView(footView, index)
        if (mFootlayout?.childCount == 1) {//第一次添加头信息
            val position = getFootViewPosition()
            if (position != -1) {
                notifyItemInserted(position)
            }
        }
        return index
    }

    /**
     * 设置尾布局
     *
     * @param header
     * @param index
     * @param orientation
     * @return
     */
    fun setFooterView(header: View, index: Int, orientation: Int): Int {
        if (mFootlayout == null || mFootlayout?.childCount ?: 0 <= index) {
            return addFootView(header, index, orientation)
        } else {
            mFootlayout?.removeViewAt(index)
            mFootlayout?.addView(header, index)
            return index
        }
    }

    /**
     * 移除尾布局
     *
     * @param footer
     */
    fun removeFooterView(footer: View) {
        if (getFooterLayoutCount() == 0) return

        mFootlayout?.removeView(footer)
        if (mFootlayout?.childCount == 0) {
            val position = getFootViewPosition()
            if (position != -1) {
                notifyItemRemoved(position)
            }
        }
    }

    /**
     * 移除所有尾布局
     */
    fun removeAllFooterView() {
        if (getFooterLayoutCount() == 0) return

        mFootlayout?.removeAllViews()
        val position = getFootViewPosition()
        if (position != -1) {
            notifyItemRemoved(position)
        }
    }

    private fun getFootViewPosition(): Int {
        return getHeaderLayoutCount() + if (mDatas == null) 0 else mDatas?.size ?: 0
    }


    fun setEmptyView(emptyView: View?) {
        if (emptyView==null){
            return
        }
        if (mEmptyLayout == null) {
            mEmptyLayout = FrameLayout(emptyView.context)
            val layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            )
            val lp = emptyView.layoutParams
            if (lp != null) {
                lp.width = layoutParams.width
                lp.height = layoutParams.height
            }
            mEmptyLayout?.layoutParams = layoutParams
        }
        mEmptyLayout?.removeAllViews()
        mEmptyLayout?.addView(emptyView)
        if (getEmptyViewCount() == 1) {
            var position = 0
            if (getHeaderLayoutCount() != 0) {
                position++
            }
            notifyItemInserted(position)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val holder: RecyclerView.ViewHolder
        when (viewType) {
            TYPE_HEAD -> {
                holder = BindingViewHolder(mHeadLayout as View)
            }
            TYPE_FOOT -> {
                holder = BindingViewHolder(mFootlayout as View)
            }
            TYPE_EMPTY -> {
                holder = BindingViewHolder(mEmptyLayout as View)
            }
            else -> {
                val binding = onCreateBinding(inflater!!, itemBindings!!.getItemBinding(viewType).layoutRes(), parent)
                holder = onCreateViewHolder(binding)
                bindViewClickListener(holder)
                binding.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
                    override fun onPreBind(binding: ViewDataBinding): Boolean {
                        return recyclerView != null && recyclerView!!.isComputingLayout
                    }

                    override fun onCanceled(binding: ViewDataBinding) {
                        if (recyclerView == null || recyclerView!!.isComputingLayout) {
                            return
                        }
                        val position = holder.adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            try {
                                notifyItemChanged(position)
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
            }
        }
        return holder
    }

    /**
     * 绑定item监听
     *
     * @param baseViewHolder
     */
    private fun bindViewClickListener(baseViewHolder: RecyclerView.ViewHolder?) {
        if (baseViewHolder == null) {
            return
        }
        val itemView = baseViewHolder.itemView
        if (onItemClickListener != null) {
            itemView.setOnClickListener { v ->
                onItemClickListener?.onItemClickListener(
                    v, baseViewHolder.layoutPosition - getHeaderLayoutCount()
                )
            }
        }
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener { v ->
                onItemLongClickListener?.onItemLongClickListener(
                    v, baseViewHolder.layoutPosition - getHeaderLayoutCount()
                ) ?: false

            }
        }

    }

    private fun onCreateBinding(
        inflater: LayoutInflater, @LayoutRes layoutId: Int,
        viewGroup: ViewGroup
    ): ViewDataBinding {
        return DataBindingUtil.inflate(inflater, layoutId, viewGroup, false)
    }

    private fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return if (viewHolderFactory != null) {
            viewHolderFactory!!.createViewHolder(binding)
        } else {
            BindingViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getEmptyViewCount() == 1) {
            return when (position) {
                0 -> if (getHeaderLayoutCount() > 0) {
                    TYPE_HEAD
                } else {
                    TYPE_EMPTY
                }
                1 -> if (getEmptyViewCount() > 0) {
                    TYPE_EMPTY
                } else {
                    TYPE_FOOT
                }
                2 -> TYPE_FOOT
                else -> TYPE_EMPTY
            }
        }
        val headerNum = getHeaderLayoutCount()
        if (position < headerNum) {
            return TYPE_HEAD
        } else {
            val resultPosition = position - headerNum
            val adapterCount = mDatas?.size ?: 0
            return if (resultPosition < adapterCount) {//显示的列表数据
                itemBindings?.onItemBind(position, getItem(resultPosition))
                defaultItemViewType(resultPosition)
            } else {
                TYPE_FOOT

            }
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        var count = 0
        if (getEmptyViewCount() == 1) {
            count = 1
            if (getHeaderLayoutCount() > 0) {
                count++
            }
            if (getFooterLayoutCount() > 0) {
                count++
            }
        } else {
            val dataSize = if (mDatas == null) 0 else mDatas?.size ?: 0
            count = dataSize + getHeaderLayoutCount() + getFooterLayoutCount()
        }
        return count
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_HEAD -> {
            }
            TYPE_FOOT -> {
            }
            TYPE_EMPTY -> {
            }
            else -> {
                val binding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)
                val realPosition = holder.layoutPosition - getHeaderLayoutCount()
                val item = mDatas?.get(realPosition)
                onBindBinding(binding!!, position, item!!, holder.itemViewType)
            }
        }


    }

    fun onBindBinding(binding: ViewDataBinding, position: Int, item: T, itemType: Int) {
        tryGetLifecycleOwner()
        if (itemBindings?.bind(binding, item, itemType) == true) {
            binding.executePendingBindings()
            if (lifecycleOwner != null) {
                binding.lifecycleOwner = lifecycleOwner
            }
        }
    }

    fun getItem(position: Int): T? {
        return mDatas?.get(position)
    }

    private fun tryGetLifecycleOwner() {
        if (lifecycleOwner == null || lifecycleOwner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
            lifecycleOwner = findLifecycleOwner(recyclerView!!)
        }
    }

    @MainThread
    fun findLifecycleOwner(view: View): LifecycleOwner? {
        val binding = DataBindingUtil.findBinding<ViewDataBinding>(view)
        var lifecycleOwner: LifecycleOwner? = null
        if (binding != null) {
            lifecycleOwner = binding.lifecycleOwner
        }
        val ctx = view.context
        if (lifecycleOwner == null && ctx is LifecycleOwner) {
            lifecycleOwner = ctx
        }
        return lifecycleOwner
    }

    open fun defaultItemViewType(position: Int): Int {
        if (itemBindings?.itemBindings?.size == 1) {
            val itemType = itemBindings?.itemBindings?.keys?.firstOrNull()
            if (itemType != null) {
                return itemType
            }
        }
        return super.getItemViewType(position)
    }

    private fun getHeaderLayoutCount(): Int {
        return if (mHeadLayout == null || mHeadLayout?.childCount == 0) {
            0
        } else 1
    }

    private fun getFooterLayoutCount(): Int {
        return if (mFootlayout == null || mFootlayout?.childCount == 0) {
            0
        } else 1
    }

    /**
     * 如果现实emptview 返回1 否则返回0
     *
     * @return 当有设置emptviewq且数据列表不为空时 返回1 否则返回0
     */
    private fun getEmptyViewCount(): Int {
        if (mEmptyLayout == null || mEmptyLayout?.childCount == 0) {//没有设置emptview
            return 0
        }

        return if (mDatas != null && mDatas?.size != 0) {//有数据
            0
        } else 1
    }

    fun setDatas(datas: MutableList<T>?) {
        this.mDatas = datas
        notifyDataSetChanged()
    }

    internal var onItemClickListener: OnItemClickListener? = null
    internal var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener?) {
        this.onItemLongClickListener = onItemLongClickListener
    }

    fun setSpanSizeLookup(mSpanSizeLookup: SpanSizeLookup?) {
        this.mSpanSizeLookup = mSpanSizeLookup
    }


    interface OnItemClickListener {
        fun onItemClickListener(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClickListener(view: View, position: Int): Boolean
    }

    interface SpanSizeLookup {
        fun getSpanSize(gridLayoutManager: GridLayoutManager, position: Int): Int
    }
}