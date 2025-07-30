package com.ring.evolve.di

import platform.Foundation.NSLog

actual class PlatformLogger {
    actual fun log(message: String) {
        NSLog("KtorHttpClient: $message")
    }
}