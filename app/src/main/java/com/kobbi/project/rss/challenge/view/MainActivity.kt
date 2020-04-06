package com.kobbi.project.rss.challenge.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kobbi.project.rss.challenge.R
import com.kobbi.project.rss.challenge.RssApplication
import com.kobbi.project.rss.challenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            feedVm = (application as RssApplication).feedViewModel.apply {
                selectedFeed.observe(this@MainActivity, Observer { feedMessage ->
                    if (feedMessage != null)
                        startActivity(Intent(this@MainActivity, DetailActivity::class.java))
                })
            }
            lifecycleOwner = this@MainActivity
        }
    }
}
