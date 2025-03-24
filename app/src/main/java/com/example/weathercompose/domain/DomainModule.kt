package com.example.weathercompose.domain

import com.example.weathercompose.domain.usecase.city.DeleteCityUseCase
import com.example.weathercompose.domain.usecase.city.LoadAllCitiesUseCase
import com.example.weathercompose.domain.usecase.city.LoadCityUseCase
import com.example.weathercompose.domain.usecase.city.SaveCityUseCase
import com.example.weathercompose.domain.usecase.city.SearchCityUseCase
import com.example.weathercompose.domain.usecase.forecast.DeleteForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.LoadForecastUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveDailyForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveForecastsUseCase
import com.example.weathercompose.domain.usecase.forecast.SaveHourlyForecastsUseCase
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

    factory { SearchCityUseCase(cityRepository = get { parametersOf(Dispatchers.IO) }) }

    factory { SaveCityUseCase(cityRepository = get { parametersOf(Dispatchers.IO) }) }

    factory {
        LoadCityUseCase(
            cityRepository = get { parametersOf(Dispatchers.IO) },
            cityCacheManager = get()
        )
    }

    factory {
        LoadAllCitiesUseCase(
            cityRepository = get { parametersOf(Dispatchers.IO) },
            cityCacheManager = get()
        )
    }

    factory {
        DeleteCityUseCase(
            cityRepository = get { parametersOf(Dispatchers.IO) },
            cityCacheManager = get()
        )
    }

    factory { SaveDailyForecastsUseCase(forecastRepository = get { parametersOf(Dispatchers.IO) }) }

    factory {
        SaveHourlyForecastsUseCase(
            forecastRepository = get { parametersOf(Dispatchers.IO) }
        )
    }

    factory {
        DeleteForecastsUseCase(
            forecastRepository = get { parametersOf(Dispatchers.IO) },
        )
    }

    factory {
        SaveForecastsUseCase(
            forecastRepository = get(),
        )
    }
}