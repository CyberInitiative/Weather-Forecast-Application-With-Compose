package com.example.weathercompose.data.database

import androidx.room.TypeConverter
import com.example.weathercompose.domain.model.forecast.WeatherDescription

class Converters {

    @TypeConverter
    fun stringToWeatherDescription(description: String) =
        enumValueOf<WeatherDescription>(description)

    @TypeConverter
    fun weatherDescriptionToString(description: WeatherDescription) = description.name
}