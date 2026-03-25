package com.research.hci.linksnitch

sealed class AnalysisResult {
    object Safe : AnalysisResult()
    data class Suspicious(val findings: List<Finding>) : AnalysisResult()
}

data class Finding(
    val type: WarningType,
    val brand: String? = null,
    val registeredDomain: String? = null,
    val tld: String? = null,
    val keyword: String? = null,
    val decodedHost: String? = null
)

enum class WarningType {
    BRAND_MISMATCH,
    BRAND_IN_PATH,
    SUSPICIOUS_TLD,
    EXCESSIVE_SUBDOMAINS,
    SUSPICIOUS_KEYWORDS,
    IP_ADDRESS,
    PUNYCODE,
    URL_SHORTENER
}
