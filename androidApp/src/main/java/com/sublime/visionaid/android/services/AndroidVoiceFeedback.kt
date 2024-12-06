package com.sublime.visionaid.android.services

import android.content.Context
import android.speech.tts.TextToSpeech
import com.sublime.visionaid.models.ImageAnalysisState
import com.sublime.visionaid.services.VoiceFeedback
import java.util.Locale

class AndroidVoiceFeedback(
    context: Context
) : VoiceFeedback {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    init {
        textToSpeech = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.apply {
                    language = Locale.getDefault()
                    setSpeechRate(0.8f)
                }
            }
        }
    }

    override fun speakResult(analysisState: ImageAnalysisState) {
        when (analysisState) {
            is ImageAnalysisState.Success -> speak(analysisState.result)
            is ImageAnalysisState.Error -> speak("Oops! Something went wrong")
            is ImageAnalysisState.Loading -> speak("Analyzing scene")
            else -> {}
        }
    }

    private fun speak(text: String) {
        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "Feedback_${System.currentTimeMillis()}"
        )
    }

    override fun stop() {
        textToSpeech?.stop()
    }

    fun cleanup() {
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}