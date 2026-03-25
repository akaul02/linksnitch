package com.research.hci.linksnitch

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.UUID

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech
    private var isInitialized = false
    private var pendingText: String? = null
    private val TAG = "TtsManager"
    private val audioManager: AudioManager

    init {
        Log.d(TAG, "Initializing TtsManager.")
        tts = TextToSpeech(context, this)
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts.language = Locale.US
            Log.d(TAG, "TTS engine initialized successfully.")

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "TTS started speaking for utterance ID: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "TTS finished speaking for utterance ID: $utteranceId")
                    audioManager.abandonAudioFocus(null)
                }

                override fun onError(utteranceId: String?) {
                    Log.e(TAG, "TTS encountered an error for utterance ID: $utteranceId")
                    audioManager.abandonAudioFocus(null)
                }
            })

            pendingText?.let {
                Log.d(TAG, "Speaking pending text.")
                speakWarning(it)
                pendingText = null
            }
        } else {
            Log.e(TAG, "TTS Initialization failed with status: $status")
        }
    }

    fun speakWarning(text: String) {
        Log.d(TAG, "speakWarning called with text: $text")
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized yet. Storing text as pending.")
            pendingText = text
            return
        }

        val result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            val utteranceId = UUID.randomUUID().toString()
            val bundle = Bundle()
            bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

            Log.d(TAG, "TTS is initialized. Speaking with utterance ID: $utteranceId")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, utteranceId)
        } else {
            Log.e(TAG, "Could not get audio focus to speak warning.")
        }
    }

    fun shutdown() {
        Log.d(TAG, "Shutting down TtsManager.")
        tts.stop()
        tts.shutdown()
    }
}
