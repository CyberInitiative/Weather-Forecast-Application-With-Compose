package com.example.weathercompose.ui

import com.example.weathercompose.ui.mapper.CityItemMapper
import com.example.weathercompose.ui.mapper.ForecastUIStateMapper
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import com.example.weathercompose.ui.viewmodel.SharedViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        ForecastViewModel(
            loadCityUseCase = get(),
            loadAllCitiesUseCase = get(),
            deleteCityUseCase = get(),
            loadForecastUseCase = get(),
            saveForecastUseCase = get(),
            deleteForecastUseCase = get(),
            networkManager = get(),
            forecastUIStateMapper = get(),
            cityItemsMapper = get(),
        )
    }

    viewModel {
        CitySearchViewModel(
            searchCityUseCase = get(),
            saveCityUseCase = get(),
        )
    }

    viewModel {
        CityManagerViewModel(
            loadAllCitiesUseCase = get(),
            deleteCityUseCase = get(),
            cityItemMapper = get(),
        )
    }

    viewModel {
        SharedViewModel(
            loadCityUseCase = get(),
            loadAllCitiesUseCase = get(),
            loadForecastUseCase = get(),
            saveForecastUseCase = get(),
            deleteForecastUseCase = get(),
            networkManager = get(),
        )
    }

    // factory { ForecastMapper(context = androidApplication()) }

    factory { ForecastUIStateMapper(context = androidApplication()) }

    factory { CityItemMapper(context = androidApplication()) }
}