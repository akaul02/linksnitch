package com.research.hci.linksnitch

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.IDN
import java.net.URI

object UrlAnalyzer {

    // Common URL shortener domains
    private val SHORTENER_DOMAINS = setOf(
        "bit.ly", "t.co", "goo.gl", "tinyurl.com", "ow.ly", "buff.ly", "is.gd", "cli.gs"
        // ... add more as needed
    )

    // Keywords often found in phishing or malware URLs
    private val SUSPICIOUS_KEYWORDS = setOf(
        "login", "signin", "verify", "account", "update", "secure", "support", "billing", "invoice",
        "payment", "free", "winner", "prize", "congratulations", "lucky"
        // ... add more as needed
    )

    // TLDs that are often associated with spam or malware
    private val SUSPICIOUS_TLDS = setOf(
        ".top", ".xyz", ".club", ".site", ".online", ".info", ".gq", ".cf", ".tk", ".ml"
        // ... add more as needed
    )

    // Whitelisted brands to check against
    private val BRAND_WHITELIST = mapOf(
        "google.com" to "Google",
        "facebook.com" to "Facebook",
        "twitter.com" to "Twitter",
        "instagram.com" to "Instagram",
        "linkedin.com" to "LinkedIn",
        "microsoft.com" to "Microsoft",
        "apple.com" to "Apple",
        "amazon.com" to "Amazon",
        "netflix.com" to "Netflix",
        "paypal.com" to "PayPal"
        // ... add more brands as needed
    )

    fun analyze(url: String): AnalysisResult {
        val findings = mutableListOf<Finding>()

        try {
            val fullUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            // Basic URL validation
            val uri = URI(fullUrl)
            val host = uri.host ?: return AnalysisResult.Safe

            // 1. Punycode Detection
            if (host.startsWith("xn--")) {
                val decodedHost = IDN.toUnicode(host)
                findings.add(Finding(WarningType.PUNYCODE, decodedHost = decodedHost))
            }

            // 2. IP Address Detection
            if (host.matches(Regex("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))) {
                findings.add(Finding(WarningType.IP_ADDRESS))
            }

            // 3. URL Shortener Detection
            if (SHORTENER_DOMAINS.any { host.endsWith(it) }) {
                findings.add(Finding(WarningType.URL_SHORTENER))
            }

            // 4. Suspicious TLD Detection
            val tld = host.substringAfterLast('.', "")
            if (SUSPICIOUS_TLDS.contains(".$tld")) {
                findings.add(Finding(WarningType.SUSPICIOUS_TLD, tld = tld))
            }

            // 5. Suspicious Keywords Detection
            SUSPICIOUS_KEYWORDS.forEach { keyword ->
                if (fullUrl.contains(keyword, ignoreCase = true)) {
                    findings.add(Finding(WarningType.SUSPICIOUS_KEYWORDS, keyword = keyword))
                }
            }

            // 6. Excessive Subdomains
            val subdomainCount = host.split('.').size
            if (subdomainCount > 4) { // e.g., login.account.secure.google.com.scam.com
                findings.add(Finding(WarningType.EXCESSIVE_SUBDOMAINS))
            }

            // 7. Brand Impersonation check
            val registeredDomain = getRegisteredDomain(host)
            BRAND_WHITELIST.forEach { (domain, brand) ->
                if (host.contains(brand, ignoreCase = true) && registeredDomain != domain) {
                    val findingType = if (host.contains(brand, ignoreCase = true)) {
                        WarningType.BRAND_MISMATCH
                    } else {
                        WarningType.BRAND_IN_PATH
                    }
                    findings.add(Finding(findingType, brand = brand, registeredDomain = registeredDomain))
                }
            }

        } catch (e: Exception) {
            // Could be a malformed URL
            return AnalysisResult.Safe
        }

        val keywordFindings = findings.filter { it.type == WarningType.SUSPICIOUS_KEYWORDS }
        val otherFindings = findings.filter { it.type != WarningType.SUSPICIOUS_KEYWORDS }

        return if (otherFindings.isNotEmpty()) {
            // Only report suspicious if there's a finding other than keywords
            AnalysisResult.Suspicious(otherFindings + keywordFindings)
        } else {
            // If only keyword findings exist (or no findings), it's safe
            AnalysisResult.Safe
        }
    }

    /**
     * A simple approach to get the registered domain (e.g., "google.com" from "sub.google.com").
     * This may not cover all edge cases (like "google.co.uk").
     */
    private fun getRegisteredDomain(host: String): String {
        val parts = host.split('.')
        return if (parts.size >= 2) {
            // A more robust solution would use a public suffix list
            parts.takeLast(2).joinToString(".")
        } else {
            host
        }
    }
}
