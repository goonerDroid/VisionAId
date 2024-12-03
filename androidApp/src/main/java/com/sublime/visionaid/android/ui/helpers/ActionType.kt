package com.sublime.visionaid.android.ui.helpers

import androidx.compose.ui.graphics.Color

enum class ActionType {
    READ_TEXT,
    DESCRIBE_SCENE,
    DETECT_OBJECTS,
    TRANSLATE,
    CUSTOM,
}

data class ActionItem(
    val name: String,
    val type: ActionType,
    val color: Color = Color.DarkGray,
)
