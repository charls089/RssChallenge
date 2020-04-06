package com.kobbi.project.rss.challenge

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.kobbi.project.rss.challenge.presenter.viewmodel.FeedViewModel

class RssApplication : Application() {
    val feedViewModel: FeedViewModel =
        ViewModelProvider.NewInstanceFactory().create(FeedViewModel::class.java)
}