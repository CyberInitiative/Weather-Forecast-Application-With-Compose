package com.example.weathercompose.data.model.forecast

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT;

    companion object {
        fun getTemperature(temperature: Double, temperatureUnit: TemperatureUnit): Int {
            return when (temperatureUnit) {
                CELSIUS -> {
                    Math.round(temperature).toInt()
                }

                FAHRENHEIT -> {
                    val temperatureInFahrenheit = (temperature * 9 / 5) + 32
                    Math.round(temperatureInFahrenheit).toInt()
                }
            }
        }
    }
}