package com.ring.evolve.di

import com.ring.evolve.network.HttpClientFactory
import com.ring.evolve.viewmodel.BleViewModel
import org.koin.dsl.module

fun commonModule() = module {
    factory { BleViewModel(get(), get()) }

    single { PlatformLogger() }

    single { HttpClientFactory.create() }
}