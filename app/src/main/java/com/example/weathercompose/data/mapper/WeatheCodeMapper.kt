package com.example.weathercompose.data.mapper

import android.content.Context
import com.example.weathercompose.R

fun mapWeatherCode(context: Context, weatherCode: Int): String {
    return when (weatherCode) {
        0 -> context.getString(R.string.clear_sky_weather_description)
        1 -> context.getString(R.string.mainly_clear_weather_description)
        2 -> context.getString(R.string.partly_cloudy_weather_description)
        3 -> context.getString(R.string.overcast_weather_description)
        45 -> context.getString(R.string.fog_weather_description)
        48 -> context.getString(R.string.depositing_rime_fog_weather_description)
        51 -> context.getString(R.string.light_drizzle_weather_description)
        53 -> context.getString(R.string.moderate_drizzle_weather_description)
        55 -> context.getString(R.string.dense_drizzle_weather_description)
        56 -> context.getString(R.string.light_freezing_rain_weather_description)
        57 -> context.getString(R.string.dense_freezing_drizzle_weather_description)
        61 -> context.getString(R.string.slight_rain_weather_description)
        63 -> context.getString(R.string.moderate_rain_weather_description)
        65 -> context.getString(R.string.heavy_rain_weather_description)
        66 -> context.getString(R.string.light_freezing_rain_weather_description)
        67 -> context.getString(R.string.heavy_freezing_rain_weather_description)
        71 -> context.getString(R.string.slight_snow_fall_weather_description)
        73 -> context.getString(R.string.moderate_snow_fall_weather_description)
        75 -> context.getString(R.string.heavy_snow_fall_weather_description)
        77 -> context.getString(R.string.snow_grains_weather_description)
        80 -> context.getString(R.string.slight_rain_showers_weather_description)
        81 -> context.getString(R.string.moderate_rain_showers_weather_description)
        82 -> context.getString(R.string.violent_rain_showers_weather_description)
        85 -> context.getString(R.string.slight_snow_showers_weather_description)
        86 -> context.getString(R.string.heavy_snow_showers_weather_description)
        95 -> context.getString(R.string.thunderstorm_weather_description)
        96 -> context.getString(R.string.thunderstorm_with_slight_hail_weather_description)
        99 -> context.getString(R.string.thunderstorm_with_heavy_hail_weather_description)
        else -> context.getString(R.string.not_existing_weather_code)
    }
}