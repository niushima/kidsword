package com.kidsword.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechHelper(private val context: Context) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    init {
        tts = TextToSpeech(context, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isInitialized = result != TextToSpeech.LANG_MISSING_DATA &&
                           result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }
    
    fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
    
    fun speakWithRate(text: String, rate: Float = 0.75f) {
        if (isInitialized) {
            tts?.setSpeechRate(rate)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            tts?.setSpeechRate(1.0f) // 恢复默认速度
        }
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
