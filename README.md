# LinkSnitch

Android prototype for an HCI research project. It watches for URLs in focused accessibility text or in text shared into the app, runs lightweight heuristics (punycode, raw IPs, shorteners, iffy TLDs, brand-in-hostname mismatches, and a small keyword list), then explains the outcome in plain language and speaks it with TTS. TalkBack users get audio through an accessibility service; share targets get a simple result screen plus speech.

This is research scaffolding, not production anti-phishing software. Heuristics will misfire on edge cases; the point is to study how audible, in-context warnings land with screen-reader users.

**Requires:** Android Studio / Gradle as usual. Enable the LinkSnitch accessibility service in system settings for the focus-based path; use Android’s Share sheet for the intent path.

**Stack:** Kotlin, Jetpack Compose for the share UI, `TextToSpeech` with transient audio focus so TalkBack can dip while the warning plays.
