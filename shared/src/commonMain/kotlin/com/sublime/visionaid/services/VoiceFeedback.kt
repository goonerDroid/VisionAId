package com.sublime.visionaid.services

import com.sublime.visionaid.models.ImageAnalysisState

interface VoiceFeedback {
    fun speakResult(analysisState: ImageAnalysisState)
    fun stop()

}