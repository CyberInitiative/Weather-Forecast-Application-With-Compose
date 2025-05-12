package com.example.weathercompose.workmanager

import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    worker { ForecastUpdatingWorker(get(), get(), get(), get()) }
}