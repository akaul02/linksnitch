package com.research.hci.linksnitch

object ExplanationGenerator {

    fun generate(findings: List<Finding>): String? {
        if (findings.isEmpty()) {
            return null
        }

        val findingsByType = findings.associateBy { it.type }

        val brandFinding = findings.find { it.type == WarningType.BRAND_MISMATCH || it.type == WarningType.BRAND_IN_PATH }

        val message = if (brandFinding != null) {
            // Brand impersonation is the highest priority
            when {
                findingsByType.containsKey(WarningType.IP_ADDRESS) ->
                    "this link is pretending to be ${brandFinding.brand} using a fake address."
                findingsByType.containsKey(WarningType.URL_SHORTENER) ->
                    "this link is pretending to be ${brandFinding.brand} and hiding its destination."
                else ->
                    "this link is pretending to be ${brandFinding.brand}."
            }
        } else {
            // No brand impersonation, check for other findings in order
            val suspiciousTldFinding = findings.find { it.type == WarningType.SUSPICIOUS_TLD }
            val suspiciousKeywordFinding = findings.find { it.type == WarningType.SUSPICIOUS_KEYWORDS }

            when {
                suspiciousTldFinding != null ->
                    "this link ends in .${suspiciousTldFinding.tld} which is common in scam sites."
                suspiciousKeywordFinding != null ->
                    "this link uses words like ${suspiciousKeywordFinding.keyword} to trick you into acting fast."
                findingsByType.containsKey(WarningType.IP_ADDRESS) ->
                    "this link goes to a raw number address, not a real website."
                findingsByType.containsKey(WarningType.URL_SHORTENER) ->
                    "this link hides where it actually goes."
                findingsByType.containsKey(WarningType.PUNYCODE) ->
                    "this link uses fake characters to mimic a real website."
                else ->
                    null // No message for other finding types like EXCESSIVE_SUBDOMAINS
            }
        }

        return message?.let { "LinkSnitch: $it" }
    }
}
