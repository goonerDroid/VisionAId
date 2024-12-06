package com.sublime.visionaid.services

import com.sublime.visionaid.models.ImageAnalysisState

class VoiceFeedbackManager(
    private val voiceFeedback: VoiceFeedback
) {
    fun handleAnalysisResult(state: ImageAnalysisState) {
        voiceFeedback.speakResult(state)
    }

    fun stopSpeaking() {
        voiceFeedback.stop()
    }

}