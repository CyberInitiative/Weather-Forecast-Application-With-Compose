package com.example.weathercompose.domain

import com.example.weathercompose.domain.usecase.forecast.DeleteForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveHourlyForecastsUseCase
import com.example.weathercompose.domain.usecase.location.DeleteLocationUseCase
import com.example.weathercompose.domain.usecase.location.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.location.LoadLocationUseCase
import com.example.weathercompose.domain.usecase.location.SaveLocationUseCase
import com.example.weathercompose.domain.usecase.location.SearchLocationUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val domainModule = module {
    factory {
        LoadForecastUseCase(
            forecastRepository = get { parametersOf(Dispatchers.IO) },
            forecastMapper = get(),
        )
    }

    factory { SearchLocationUseCase(locationRepository = get { parametersOf(Dispatchers.IO) }) }

    factory { SaveLocationUseCase(locationRepository = get { parametersOf(Dispatchers.IO) }) }

    factory {
        LoadLocationUseCase(
            locationRepository = get { parametersOf(Dispatchers.IO) },
        )
    }

    factory {
        LoadAllCitiesUseCase(
            locationRepository = get { parametersOf(Dispatchers.IO) },
        )
    }

    factory {
        DeleteLocationUseCase(
            locationRepository = get { parametersOf(Dispatchers.IO) },
        )
    }

    factory {
        SaveHourlyForecastsUseCase(
            forecastRepository = get { parametersOf(Dispatchers.IO) }
        )
    }

    factory {
        DeleteForecastUseCase(
            forecastRepository = get { parametersOf(Dispatchers.IO) },
        )
    }

    factory {
        SaveForecastUseCase(
            forecastRepository = get(),
        )
    }
}