package com.example.weathercompose.domain

import com.example.weathercompose.domain.usecase.forecast.DeleteForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.FindAllLocationsUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import com.example.weathercompose.domain.usecase.location.SetLocationAsHomeUseCase
import com.example.weathercompose.domain.usecase.settings.GetAllowedToShowWidgetAlarmDialogState
import com.example.weathercompose.domain.usecase.settings.GetCurrentTemperatureUnitUseCase
import com.example.weathercompose.domain.usecase.settings.GetForecastUpdateFrequencyUseCase
import com.example.weathercompose.domain.usecase.settings.GetLastTimeForecastUpdatedUseCase
import com.example.weathercompose.domain.usecase.settings.SetAllowedToShowWidgetAlarmDialogState
import com.example.weathercompose.domain.usecase.settings.SetCurrentTemperatureUnitUseCase
import com.example.weathercompose.domain.usecase.settings.SetForecastUpdateFrequencyUseCase
import com.example.weathercompose.domain.usecase.settings.SetLastTimeForecastUpdatedUseCase
import com.example.weathercompose.domain.usecase.widget.FindAllWidgetLocations
import org.koin.dsl.module

val domainModule = module {
    factory {
        LoadForecastUseCase(
            forecastRepository = get(),
            saveForecastUseCase = get(),
            dailyForecastMapper = get(),
        )
    }

    factory { SearchLocationUseCase(locationRepository = get()) }

    factory { SaveLocationUseCase(locationRepository = get()) }

    factory { LoadLocationUseCase(locationRepository = get()) }

    factory { FindAllLocationsUseCase(locationRepository = get()) }

    factory { DeleteLocationUseCase(locationRepository = get()) }

    factory { SetLocationAsHomeUseCase(locationRepository = get()) }

    factory { DeleteForecastUseCase(forecastRepository = get()) }

    factory { SaveForecastUseCase(forecastRepository = get()) }

    factory { GetCurrentTemperatureUnitUseCase(appSettings = get()) }

    factory { SetCurrentTemperatureUnitUseCase(appSettings = get()) }

    factory { GetForecastUpdateFrequencyUseCase(appSettings = get()) }

    factory { SetForecastUpdateFrequencyUseCase(appSettings = get()) }

    factory { GetLastTimeForecastUpdatedUseCase(appSettings = get()) }

    factory { SetLastTimeForecastUpdatedUseCase(appSettings = get()) }

    factory { GetAllowedToShowWidgetAlarmDialogState(appSettings = get()) }

    factory { SetAllowedToShowWidgetAlarmDialogState(appSettings = get()) }

    factory { FindAllWidgetLocations(widgetLocationRepository = get()) }
}