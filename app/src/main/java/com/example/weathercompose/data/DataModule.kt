package com.example.weathercompose.data

import androidx.room.Room
import com.example.weathercompose.data.api.CityService
import com.example.weathercompose.data.api.ForecastService
import com.example.weathercompose.data.database.WeatherForecastDatabase
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.repository.city.CityRepositoryImpl
import com.example.weathercompose.data.repository.forecast.ForecastRepositoryImpl
import com.example.weathercompose.domain.mapper.DailyForecastMapper
import com.example.weathercompose.domain.mapper.HourlyForecastMapper
import com.example.weathercompose.domain.repository.CityRepository
import com.example.weathercompose.domain.repository.ForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ForecastRepository> { (dispatcher: CoroutineDispatcher) ->
        ForecastRepositoryImpl(
            dispatcher = dispatcher,
            forecastService = getForecastService(),
            cityDao = get(),
        )
    }

    single<CityRepository> { (dispatcher: CoroutineDispatcher) ->
        CityRepositoryImpl(
            dispatcher = dispatcher,
            cityService = getCityService(),
            cityDao = get()
        )
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            WeatherForecastDatabase::class.java,
            WeatherForecastDatabase.DATABASE_NAME,
        ).build()
    }

    single<CityDao> {
        val database = get<WeatherForecastDatabase>()
        database.cities()
    }

    factory {
        HourlyForecastMapper(context = androidApplication())
    }

    factory {
        DailyForecastMapper(
            context = androidApplication(),
            hourlyForecastMapper = get(),
        )
    }
}

private fun getForecastService(): ForecastService {
    return Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ForecastService::class.java)
}

private fun getCityService(): CityService {
    return Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com//")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CityService::class.java)
}