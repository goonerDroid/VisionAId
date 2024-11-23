package com.sublime.visionaid.domain.di

import org.koin.core.context.startKoin
import org.koin.dsl.module

val sharedModule =
    module {
        // Will add more deps here
    }

fun initKoin() =
    startKoin {
        modules(sharedModule)
    }
