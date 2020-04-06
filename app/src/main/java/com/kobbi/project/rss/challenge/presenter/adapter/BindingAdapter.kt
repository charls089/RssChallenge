package com.kobbi.project.rss.challenge.presenter.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.kobbi.project.rss.challenge.R
import com.kobbi.project.rss.challenge.model.FeedMessage
import com.kobbi.project.rss.challenge.presenter.viewmodel.FeedViewModel

object BindingAdapter {
    @BindingAdapter("setFeeds", "setClickItem")
    @JvmStatic
    fun setFeeds(recyclerView: RecyclerView, items: List<FeedMessage>?, feedVm: FeedViewModel?) {
        Log.e("####", "setFeeds() --> items(${items?.size ?: 0}) : $items")
        if (items != null)
            recyclerView.adapter?.let { adapter ->
                if (adapter is FeedAdapter)
                    adapter.setItems(items)
            } ?: kotlin.run {
                recyclerView.adapter = FeedAdapter().apply {
                    setItems(items)
                    setHasStableIds(true)
                    clickListener = object : FeedAdapter.ClickListener {
                        override fun onItemClick(position: Int, view: View) {
                            view.context?.let {
                                feedVm?.showDetail(position)
                            }
                        }
                    }
                }
            }
    }

    @BindingAdapter("setKeyword")
    @JvmStatic
    fun setKeyword(recyclerView: RecyclerView, items: List<String?>?) {
        if (items != null)
            recyclerView.adapter?.let { adapter ->
                if (adapter is KeywordAdapter)
                    adapter.setItems(items)
            } ?: kotlin.run {
                recyclerView.adapter = KeywordAdapter(items).apply {
                    setHasStableIds(true)
                }
            }
    }

    @BindingAdapter("setRefresh")
    @JvmStatic
    fun setRefresh(refreshLayout: SwipeRefreshLayout, feedVm: FeedViewModel) {
        refreshLayout.setOnRefreshListener {
            Log.d("####", "onRefresh()")
            feedVm.refreshFeed()
            refreshLayout.isRefreshing = false
        }
    }

    @BindingAdapter("checkComplete")
    @JvmStatic
    fun checkComplete(view: View, isComplete: Boolean?) {
        val switchValue =
            if (isComplete == true) Pair(View.GONE, View.VISIBLE) else Pair(View.VISIBLE, View.GONE)
        when (view) {
            is ProgressBar -> view.visibility = switchValue.first
            is RecyclerView -> view.visibility = switchValue.second
        }
    }

    @BindingAdapter("setImgUrl")
    @JvmStatic
    fun setImageUrl(imageView: ImageView, url: String?) {
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context).load(url).into(imageView)
        }
    }

    @BindingAdapter("setDetailUrl")
    @JvmStatic
    fun setDetailUrl(webView: WebView, url: String?) {
        webView.run {
            settings.run {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                setSupportZoom(false)
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
            loadUrl(url)
        }
    }

    @BindingAdapter("setKeyword")
    @JvmStatic
    fun setKeyword(layout: LinearLayout, keyword: Triple<String, String, String>?) {
        if (keyword != null) {
            Log.e("####", "BindingAdapter.setKeyword() --> keyword : $keyword")
            layout.context?.let { context ->
                layout.removeAllViews()
                layout.addView(createKeywordView(context, keyword.first), 0)
                layout.addView(createKeywordView(context, keyword.second), 1)
                layout.addView(createKeywordView(context, keyword.third), 2)
            }
        }
    }

    private fun createKeywordView(context: Context, value: String?): TextView? {
        return value?.let { keyword ->
            TextView(context).apply {
                setBackgroundResource(R.drawable.border_keyword)
                text = keyword
            }
        }
    }
}