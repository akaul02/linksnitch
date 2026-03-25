package com.research.hci.linksnitch

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class TtsService : Service() {

    private lateinit var ttsManager: TtsManager

    override fun onCreate() {
        super.onCreate()
        ttsManager = TtsManager(this)
        Log.d("TtsService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val textToSpeak = intent?.getStringExtra("textToSpeak")
        if (textToSpeak != null) {
            ttsManager.speakWarning(textToSpeak)
            Log.d("TtsService", "Speaking: $textToSpeak")
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
        Log.d("TtsService", "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
