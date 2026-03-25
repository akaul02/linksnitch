package com.research.hci.linksnitch

import android.accessibilityservice.AccessibilityService
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class LinkSnitchAccessibilityService : AccessibilityService() {

    private var ttsManager: TtsManager? = null
    private lateinit var toneGenerator: ToneGenerator
    private val handler = Handler(Looper.getMainLooper())
    private var warningRunnable: Runnable? = null
    private val TAG = "LinkSnitchAccessibilityService"

    override fun onServiceConnected() {
        super.onServiceConnected()
        ttsManager = TtsManager(this)
        toneGenerator = ToneGenerator(AudioManager.STREAM_ACCESSIBILITY, 100)
        Log.d(TAG, "Accessibility Service connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Cancel any pending warning from a previous focus event
        warningRunnable?.let { handler.removeCallbacks(it) }

        if (event?.eventType != AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            return
        }

        val sourceNode = event.source
        val text = sourceNode?.text?.toString() ?: sourceNode?.contentDescription?.toString()

        if (text.isNullOrEmpty()) {
            return
        }

        val url = UrlDetector.findUrls(text).firstOrNull() ?: return
        val analysisResult = UrlAnalyzer.analyze(url)

        if (analysisResult is AnalysisResult.Suspicious) {
            val explanation = ExplanationGenerator.generate(analysisResult.findings)
            if (explanation != null) {
                Log.d(TAG, "Found a suspicious link. Scheduling a warning.")
                warningRunnable = Runnable {
                    Log.d(TAG, "Timer finished. Playing earcon and speaking.")
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP)
                    ttsManager?.speakWarning(explanation)
                }
                handler.postDelayed(warningRunnable!!, 3000)
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        warningRunnable?.let { handler.removeCallbacks(it) }
        ttsManager?.shutdown()
        if (::toneGenerator.isInitialized) {
            toneGenerator.release()
        }
        Log.d(TAG, "Accessibility service destroyed.")
    }
}
