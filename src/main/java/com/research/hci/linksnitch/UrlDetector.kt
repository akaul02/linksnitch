package com.research.hci.linksnitch

import android.util.Patterns
import java.util.regex.Matcher

object UrlDetector {
    /**
     * Finds all URLs in a given text.
     * @param text The text to search for URLs.
     * @return A list of found URLs.
     */
    fun findUrls(text: CharSequence): List<String> {
        val urls = mutableListOf<String>()
        val matcher: Matcher = Patterns.WEB_URL.matcher(text)
        while (matcher.find()) {
            urls.add(matcher.group())
        }
        return urls
    }
}
