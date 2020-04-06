package com.kobbi.project.rss.challenge.model

data class FeedMessage(
    var title: String? = null,
    var link: String? = null,
    var guid: Long = Long.MIN_VALUE,
    var description: String? = null,
    var body: String? = null,
    var imageUrl: String? = null,
    var keywords: List<String?> = mutableListOf(),
    var source: Source? = null

) {
    data class Source(
        val author: String? = null,
        val url: String? = null
    )
}