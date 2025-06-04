package com.example.weathercompose.data

import androidx.room.Room
import com.example.weathercompose.data.api.ForecastAPI
import com.example.weathercompose.data.api.GeocodingAPI
import com.example.weathercompose.data.database.WeatherForecastDatabase
import com.example.weathercompose.data.database.dao.ForecastDao
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.datastore.AppSettings
import com.example.weathercompose.data.mapper.DailyForecastMapper
import com.example.weathercompose.data.mapper.HourlyForecastMapper
import com.example.weathercompose.data.repository.forecast.ForecastRepositoryImpl
import com.example.weathercompose.data.repository.location.LocationRepositoryImpl
import com.example.weathercompose.domain.repository.ForecastRepository
import com.example.weathercompose.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        AppSettings(context = get())
    }

    single<ForecastRepository> { (dispatcher: CoroutineDispatcher) ->
        ForecastRepositoryImpl(
            dispatcher = dispatcher,
            forecastAPI = get(),
            forecastDao = get(),
        )
    }

    single<LocationRepository> { (dispatcher: CoroutineDispatcher) ->
        LocationRepositoryImpl(
            dispatcher = dispatcher,
            geocodingAPI = get(),
            locationDao = get(),
            widgetUpdateManager = get(),
        )
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            WeatherForecastDatabase::class.java,
            WeatherForecastDatabase.DATABASE_NAME,
        ).build()
    }

    single<LocationDao> {
        val database = get<WeatherForecastDatabase>()
        database.locationDao()
    }

    single<ForecastDao> {
        val database = get<WeatherForecastDatabase>()
        database.forecastDao()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ForecastAPI::class.java)
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com//")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingAPI::class.java)
    }

    factory {
        HourlyForecastMapper()
    }

    factory {
        DailyForecastMapper(
            hourlyForecastMapper = get(),
        )
    }
}