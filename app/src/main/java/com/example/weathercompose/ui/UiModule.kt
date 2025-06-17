package com.example.weathercompose.ui

import com.example.weathercompose.domain.mapper.LocationItemMapper
import com.example.weathercompose.domain.mapper.LocationUIStateMapper
import com.example.weathercompose.ui.viewmodel.ForecastViewModel
import com.example.weathercompose.ui.viewmodel.LocationSearchViewModel
import com.example.weathercompose.ui.viewmodel.WidgetsConfigureViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        ForecastViewModel(
            findAllLocationsUseCase = get(),
            setLocationAsHomeUseCase = get(),
            deleteLocationUseCase = get(),
            locationUIStateMapper = get(),
            locationItemsMapper = get(),
            loadForecastUseCase = get(),
            loadLocationUseCase = get(),
            setTemperatureUnitUseCase = get(),
            getTemperatureUnitUseCase = get(),
            setForecastUpdateFrequencyUseCase = get(),
            getForecastUpdateFrequencyUseCase = get(),
            getLastTimeForecastUpdatedUseCase = get(),
            networkManager = get(),
        )
    }

    viewModel {
        LocationSearchViewModel(
            searchLocationUseCase = get(),
            saveLocationUseCase = get(),
            networkManager = get(),
        )
    }

    viewModel {
        WidgetsConfigureViewModel(
            getAllowedToShowWidgetAlarmDialogState = get(),
            setAllowedToShowWidgetAlarmDialogState = get(),
            findAllLocationsUseCase = get(),
            locationItemMapper = get(),
        )
    }

    factory { LocationUIStateMapper(context = get()) }

    factory { LocationItemMapper() }
}