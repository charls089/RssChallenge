package com.kobbi.project.rss.challenge.presenter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.rss.challenge.R
import com.kobbi.project.rss.challenge.databinding.ItemNewsBinding
import com.kobbi.project.rss.challenge.model.FeedMessage

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {
    var clickListener: ClickListener? = null
    private val mItems: MutableList<FeedMessage> = mutableListOf()

    fun setItems(items: List<FeedMessage>) {
        if (items.isEmpty()) {
            mItems.clear()
            notifyDataSetChanged()
        } else {
            items.forEach { item ->
                if (!mItems.any { it.guid == item.guid }) {
                    mItems.add(item)
                    Log.e("####", "new Data(${mItems.size}) : $item")
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return mItems[position].guid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DataBindingUtil.inflate<ItemNewsBinding>(
            LayoutInflater.from(parent.context), R.layout.item_news, parent, false
        ).let { binding ->
            ViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(feedMessage: FeedMessage) {
            binding.feedMessage = feedMessage
        }

        override fun onClick(v: View?) {
            v?.let { clickListener?.onItemClick(layoutPosition, v) }
        }
    }

    interface ClickListener {
        fun onItemClick(position: Int, view: View)
    }
}