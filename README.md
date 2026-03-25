# LinkSnitch: Voice-based explanations for malicious links in screen-reader browsing

**Aryan Kaul, Daniel Itzler, Shachaf Rispler** — Columbia University

LinkSnitch is an Android research prototype for people who browse with a screen reader. It watches for URLs (via an accessibility service or the Share sheet), pulls out the registrable domain, runs lightweight phishing-oriented checks, and when something looks off it speaks a short explanation—what the domain actually is and why it is suspicious—instead of leaving the user to parse the full URL character by character in audio.

## Examples: what TalkBack reads vs. what LinkSnitch says

| Trick type | Example URL | What TalkBack reads | What LinkSnitch says |
| --- | --- | --- | --- |
| Subdomain spoofing | `amazon.login.security-verify.xyz` | “amazon dot login dot security dash verify dot x y z, link” | “Warning: the actual domain is security-verify.xyz. This link mentions Amazon, but Amazon is not the registered domain.” |
| Combosquatting | `amazon-secure-login.com` | “amazon dash secure dash login dot com, link” | “The domain is amazon-secure-login.com. This is not an official Amazon domain.” |
| Brand in path | `xyz-hosting.com/paypal/login` | “x y z dash hosting dot com slash paypal slash login, link” | “The actual domain is xyz-hosting.com. PayPal appears in the path but is not the registered domain.” |
| Homoglyph | `apple.com` (Cyrillic а for Latin a) | “apple dot com, link” | “This domain contains characters from a non-Latin script that mimic the spelling of Apple. The link may not go to the real Apple website.” |
| Legitimate | `amazon.com/dp/B09V3KXJPB` | “amazon dot com slash d p slash b zero nine v three k x j p b, link” | “This link goes to amazon.com. The domain is registered to Amazon.” |

## Architecture

![LinkSnitch system architecture](https://raw.githubusercontent.com/akaul02/linksnitch/main/docs/architecture.png)

## Build and run

- Open the project in Android Studio and run the app on a device or emulator.
- For automatic checks on focused links: enable the **LinkSnitch** accessibility service in **Settings → Accessibility**.
- To analyze text from another app: select text or use Share and choose LinkSnitch.

**Stack:** Kotlin, Jetpack Compose (share UI), Android `TextToSpeech` with transient audio focus so TalkBack can duck while a warning plays.
