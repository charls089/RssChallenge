package com.kobbi.project.rss.challenge.model

import android.util.Xml
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

object RssFeedParser {
    @JvmStatic
    fun readFeed(url: String): Flowable<FeedMessage> {
        return Flowable.create(FlowableOnSubscribe { emitter ->
            try {
                URL(url).openStream().use { ips ->
                    val feedMessageList = mutableListOf<FeedMessage>()
                    val pullParser = Xml.newPullParser()
                    println(Xml.newPullParser())
                    pullParser?.let { parser ->
                        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                        parser.setInput(ips, null)
                        parser.nextTag()
                        parser.require(XmlPullParser.START_TAG, null, "rss")
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (emitter.isCancelled)
                                return@FlowableOnSubscribe
                            if (parser.eventType != XmlPullParser.START_TAG)
                                continue
                            if (parser.name == "item") {
                                feedMessageList.add(readFeedMessage(parser))
                            } else {
                                skip(parser)
                            }
                        }
                    }
                    var count = 0
                    feedMessageList.forEach { feedMessage ->
                        Executors.newFixedThreadPool(10).execute {
                            CrawlingController.getOpenGraph(feedMessage.link)?.let {
                                feedMessage.description = it["description"]
                                feedMessage.imageUrl = it["imageUrl"]
                                feedMessage.body = it["body"]
                                feedMessage.keywords = listOf(it["keyword_1"], it["keyword_2"], it["keyword_3"])
                                emitter.onNext(feedMessage)
                            }
                            if (++count == feedMessageList.lastIndex)
                                emitter.onComplete()
                        }
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                emitter.onError(e)
            } catch (e: IOException) {
                emitter.onError(e)
            } catch (e: XmlPullParserException) {
                emitter.onError(e)
            }
        }, BackpressureStrategy.BUFFER)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readFeedMessage(parser: XmlPullParser): FeedMessage {
        val feedMessage = FeedMessage()
        parser.require(XmlPullParser.START_TAG, null, "item")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG)
                continue
            val name = parser.name
            readTextFromParser(parser, name).let { data ->
                val value = data[name]
                when (name) {
                    "title" -> feedMessage.title = value
                    "link" -> feedMessage.link = value
                    "guid" -> feedMessage.guid = setGuid(value)
                    "source" -> feedMessage.source = FeedMessage.Source(value, data["url"])
                    else -> {
                        //Nothing.
                    }
                }
            }
            parser.nextTag()
        }
        return feedMessage
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readTextFromParser(
        parser: XmlPullParser,
        tag: String
    ): Map<String, String> {
        val map = HashMap<String, String>()
        parser.require(XmlPullParser.START_TAG, null, tag)
        if (tag == "source") {
            val url = parser.getAttributeValue(null, "url")
            map["url"] = url
        }
        if (parser.next() == XmlPullParser.TEXT) {
            map[tag] = parser.text
        }
        return map
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun skip(parser: XmlPullParser) {
        var depth = 0
        do {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        } while (depth != 0)
        if (parser.name == null)
            parser.next()
    }

    private fun setGuid(value: String?): Long {
        return value?.toLongOrNull() ?: kotlin.run {
            var guid = 0L
            value?.forEach {
                guid += it.toLong()
            }
            guid
        }
    }
}