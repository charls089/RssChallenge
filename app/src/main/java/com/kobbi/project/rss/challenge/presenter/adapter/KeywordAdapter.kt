package com.kobbi.project.rss.challenge.presenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.rss.challenge.R
import com.kobbi.project.rss.challenge.databinding.ItemKeywordBinding

class KeywordAdapter(items: List<String?>) : RecyclerView.Adapter<KeywordAdapter.ViewHolder>() {
    private val mItems = mutableListOf<String>()

    init {
        setItems(items)
    }

    fun setItems(items: List<String?>) {
        mItems.clear()
        items.forEach { keyword ->
            if (!keyword.isNullOrEmpty())
                mItems.add(keyword)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemKeywordBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_keyword,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mItems.size > position)
            holder.bind(mItems[position])
    }

    inner class ViewHolder(private val binding: ItemKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword: String) {
            binding.keyword = keyword
        }
    }
}