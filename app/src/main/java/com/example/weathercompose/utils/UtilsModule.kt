package com.example.weathercompose.utils

import com.example.weathercompose.widget.WidgetUpdateManager
import org.koin.dsl.module

val utilsModule = module {

    single { NetworkManager(context = get()) }

    single { WidgetUpdateManager(context = get()) }
}