package com.kobbi.project.rss.challenge.presenter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kobbi.project.rss.challenge.model.FeedMessage
import com.kobbi.project.rss.challenge.model.RssFeedParser
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import kotlin.concurrent.thread

class FeedViewModel : ViewModel() {
    val feedList: LiveData<List<FeedMessage>> get() = _feedList
    val isComplete: LiveData<Boolean> get() = _isComplete
    val selectedFeed: LiveData<FeedMessage> get() = _selectedFeed

    private val _feedList: MutableLiveData<List<FeedMessage>> = MutableLiveData()
    private val _isComplete: MutableLiveData<Boolean> = MutableLiveData()
    private val _selectedFeed: MutableLiveData<FeedMessage> = MutableLiveData()

    companion object {
        private const val RSS_URL = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"
    }

    init {
        Log.e("####", "FeedViewModel.init()")
        requestFeed()
    }

    fun refreshFeed() {
        _feedList.postValue(listOf())
        requestFeed()
    }

    fun showDetail(position: Int) {
        _feedList.value?.get(position)?.let { feedMessage ->
            Log.i("####", "showDetail() --> selectedPosition value($position) : $feedMessage")
            _selectedFeed.postValue(feedMessage)
        }
    }

    private fun requestFeed() {
        _isComplete.postValue(false)
        val feedMessageList = mutableListOf<FeedMessage>()
        thread {
            RssFeedParser.readFeed(RSS_URL)
                .observeOn(Schedulers.computation())
                .subscribe(object : Subscriber<FeedMessage> {
                    override fun onSubscribe(subscription: Subscription?) {
                        subscription?.also {
                            it.request(Long.MAX_VALUE)
                        }
                    }

                    override fun onNext(feedMessage: FeedMessage) {
                        feedMessageList.add(feedMessage)
                        Log.e("####", "mFeedList.size : ${feedMessageList.size}")
                    }

                    override fun onComplete() {
                        _feedList.postValue(feedMessageList)
                        _isComplete.postValue(true)
                        Log.e("####", "onComplete()")
                    }

                    override fun onError(t: Throwable) {
                        Log.e("####", "onError() --> ${t.cause}/${t.message}")
                        _isComplete.postValue(true)
                    }
                })
        }
    }
}