package com.example.weathercompose.utils

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val utilsModule = module {

    single { NetworkManager(context = androidApplication()) }
}