package com.sublime.visionaid

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform