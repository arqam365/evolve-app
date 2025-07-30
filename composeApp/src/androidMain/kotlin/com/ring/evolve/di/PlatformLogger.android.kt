package com.ring.evolve.di

import android.util.Log

actual class PlatformLogger {
    actual fun log(message: String) {
        Log.d("KtorHttpClient", message)
    }
}