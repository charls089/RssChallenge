package com.kobbi.project.rss.challenge.model

import android.util.Log
import org.jsoup.Jsoup
import java.io.IOException

object CrawlingController {
    fun getOpenGraph(url: String?): Map<String, String>? {
        val map = HashMap<String, String>()
        try {
            Jsoup.connect(url).get().let { doc ->
                val baseUri = doc.baseUri()
                Log.i("####", "CrawlingController.getOpenGraph() --> baseUri : $baseUri")
                if (baseUri.contains("youtube")) {
                    baseUri.split("watch?v=").run {
                        if (this.size == 2)
                            map["imageUrl"] =
                                String.format("https://i.ytimg.com/vi/%s/0.jpg", this.last())
                    }
                    val description = StringBuilder()
                    doc.body().let { body ->
                        body.getElementsByClass("content style-scope ytd-video-secondary-info-renderer")
                            .let { snippets ->
                                Log.e("####", "snippets : $snippets")
                                snippets?.first()
                                    ?.getElementsByClass("style-scope yt-formatted-string")
                                    ?.forEach {
                                        description.append(it.text())
                                    }
                            }
                    }
                    map["description"] = description.toString()

                } else {
                    val keywordMap = HashMap<String, Int>()
                    val body = doc.body().text()
                    map["body"] = body
                    Log.i("findKeyword", "body : $body")
                    body.split(' ')
                        .forEach {
                            if (it.isNotBlank() && it.length >= 2) {
                                val count = (keywordMap[it] ?: 0) + 1
                                keywordMap[it] = count
                            }
                        }
                    val keywords = keywordMap.entries.sortedWith(Comparator { t, t2 ->
                        when {
                            t.value > t2.value -> -1
                            t.value < t2.value -> 1
                            else ->
                                when {
                                    t.key > t2.key -> 1
                                    t.key < t2.key -> -1
                                    else -> 0
                                }
                        }
                    }).map { it.key }.take(3)
                    for (i in keywords.indices) {
                        map["keyword_${i + 1}"] = keywords[i]
                    }
                    doc.head().getElementsByTag("meta").forEach { meta ->
                        when (meta.attr("property")) {
                            "og:description" -> {
                                map["description"] = meta.attr("content")
                            }
                            "og:image" -> {
                                map["imageUrl"] = meta.attr("content")
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("####", "crawling error >> ${e.cause}, ${e.message}")
        }
        Log.i("####", "CrawlingController.getOpenGraph() --> map : $map")
        return if (map.isEmpty()) null else map
    }
}