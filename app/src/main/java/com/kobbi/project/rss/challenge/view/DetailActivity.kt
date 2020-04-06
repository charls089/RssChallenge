package com.kobbi.project.rss.challenge.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kobbi.project.rss.challenge.R
import com.kobbi.project.rss.challenge.RssApplication
import com.kobbi.project.rss.challenge.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail).run {
            feedVm = (application as RssApplication).feedViewModel
            lifecycleOwner = this@DetailActivity
        }
    }
}