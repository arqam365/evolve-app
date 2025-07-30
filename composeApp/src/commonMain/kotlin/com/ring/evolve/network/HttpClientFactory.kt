package com.ring.evolve.network

import com.ring.evolve.di.PlatformLogger
import io.ktor.client.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(): HttpClient {
        val platformLogger = PlatformLogger()

        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        platformLogger.log(message)
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 15_000
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}