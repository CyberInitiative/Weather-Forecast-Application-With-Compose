package com.example.weathercompose

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

object CoroutineDispatchers {
    const val IO = "DispatcherIO"
    const val MAIN = "DispatcherMain"
    const val IMMEDIATE = "DispatcherImmediate"
    const val DEFAULT = "DispatcherDefault"
}

val coroutineModule = module {

    single<CoroutineDispatcher>(named(CoroutineDispatchers.IO)) { Dispatchers.IO }

    single<CoroutineDispatcher>(named(CoroutineDispatchers.MAIN)) { Dispatchers.Main }

    single<CoroutineDispatcher>(named(CoroutineDispatchers.IMMEDIATE)) {
        Dispatchers.Main.immediate
    }

    single<CoroutineDispatcher>(named(CoroutineDispatchers.DEFAULT)) { Dispatchers.Default }
}