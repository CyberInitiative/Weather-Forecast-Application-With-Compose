package com.example.weathercompose.domain.model.forecast

import com.example.weathercompose.R

// https://open-meteo.com/en/docs
// Weather variable documentation section
enum class WeatherDescription {
    CLEAR_SKY,
    MAINLY_CLEAR,
    PARTLY_CLOUDY,
    OVERCAST,
    FOG,
    DEPOSITING_RIME_FOG,
    LIGHT_DRIZZLE,
    MODERATE_DRIZZLE,
    DENSE_DRIZZLE,
    LIGHT_FREEZING_DRIZZLE,
    DENSE_FREEZING_DRIZZLE,
    SLIGHT_RAIN,
    MODERATE_RAIN,
    HEAVY_RAIN,
    LIGHT_FREEZING_RAIN,
    HEAVY_FREEZING_RAIN,
    SLIGHT_SNOW_FALL,
    MODERATE_SNOW_FALL,
    HEAVY_SNOW_FALL,
    SNOW_GRAINS,
    SLIGHT_RAIN_SHOWERS,
    MODERATE_RAIN_SHOWERS,
    VIOLENT_RAIN_SHOWERS,
    SLIGHT_SNOW_SHOWERS,
    HEAVY_SNOW_SHOWERS,
    THUNDERSTORM,
    THUNDERSTORM_WITH_SLIGHT_HAIL,
    THUNDERSTORM_WITH_HEAVY_HAIL,
    NOT_EXISTING_WEATHER_CODE;

    companion object {

        fun weatherCodeToDescription(code: Int): WeatherDescription {
            return when (code) {
                0 -> CLEAR_SKY
                1 -> MAINLY_CLEAR
                2 -> PARTLY_CLOUDY
                3 -> OVERCAST
                45 -> FOG
                48 -> DEPOSITING_RIME_FOG
                51 -> LIGHT_DRIZZLE
                53 -> MODERATE_DRIZZLE
                55 -> DENSE_DRIZZLE
                56 -> LIGHT_FREEZING_DRIZZLE
                57 -> DENSE_FREEZING_DRIZZLE
                61 -> SLIGHT_RAIN
                63 -> MODERATE_RAIN
                65 -> HEAVY_RAIN
                66 -> LIGHT_FREEZING_RAIN
                67 -> HEAVY_FREEZING_RAIN
                71 -> SLIGHT_SNOW_FALL
                73 -> MODERATE_SNOW_FALL
                75 -> HEAVY_SNOW_FALL
                77 -> SNOW_GRAINS
                80 -> SLIGHT_RAIN_SHOWERS
                81 -> MODERATE_RAIN_SHOWERS
                82 -> VIOLENT_RAIN_SHOWERS
                85 -> SLIGHT_SNOW_SHOWERS
                86 -> HEAVY_SNOW_SHOWERS
                95 -> THUNDERSTORM
                96 -> THUNDERSTORM_WITH_SLIGHT_HAIL
                99 -> THUNDERSTORM_WITH_HEAVY_HAIL
                else -> NOT_EXISTING_WEATHER_CODE
            }
        }

        fun weatherDescriptionToString(
            weatherDescription: WeatherDescription,
        ): Int {
            return when (weatherDescription) {
                CLEAR_SKY -> R.string.clear_sky_weather_description
                MAINLY_CLEAR -> R.string.mainly_clear_weather_description
                PARTLY_CLOUDY -> R.string.partly_cloudy_weather_description
                OVERCAST -> R.string.overcast_weather_description
                FOG -> R.string.fog_weather_description
                DEPOSITING_RIME_FOG -> R.string.depositing_rime_fog_weather_description
                LIGHT_DRIZZLE -> R.string.light_drizzle_weather_description
                MODERATE_DRIZZLE -> R.string.moderate_drizzle_weather_description
                DENSE_DRIZZLE -> R.string.dense_drizzle_weather_description
                LIGHT_FREEZING_DRIZZLE -> R.string.light_freezing_drizzle_weather_description
                DENSE_FREEZING_DRIZZLE -> R.string.dense_freezing_drizzle_weather_description
                SLIGHT_RAIN -> R.string.slight_rain_weather_description
                MODERATE_RAIN -> R.string.moderate_rain_weather_description
                HEAVY_RAIN -> R.string.heavy_rain_weather_description
                LIGHT_FREEZING_RAIN -> R.string.light_freezing_rain_weather_description
                HEAVY_FREEZING_RAIN -> R.string.heavy_freezing_rain_weather_description
                SLIGHT_SNOW_FALL -> R.string.slight_snow_fall_weather_description
                MODERATE_SNOW_FALL -> R.string.moderate_snow_fall_weather_description
                HEAVY_SNOW_FALL -> R.string.heavy_snow_fall_weather_description
                SNOW_GRAINS -> R.string.snow_grains_weather_description
                SLIGHT_RAIN_SHOWERS -> R.string.slight_rain_showers_weather_description
                MODERATE_RAIN_SHOWERS -> R.string.moderate_rain_showers_weather_description
                VIOLENT_RAIN_SHOWERS -> R.string.violent_rain_showers_weather_description
                SLIGHT_SNOW_SHOWERS -> R.string.slight_snow_showers_weather_description
                HEAVY_SNOW_SHOWERS -> R.string.heavy_snow_showers_weather_description
                THUNDERSTORM -> R.string.thunderstorm_weather_description
                THUNDERSTORM_WITH_SLIGHT_HAIL -> R.string.thunderstorm_with_slight_hail_weather_description
                THUNDERSTORM_WITH_HEAVY_HAIL -> R.string.thunderstorm_with_heavy_hail_weather_description
                NOT_EXISTING_WEATHER_CODE -> R.string.not_existing_weather_code
            }
        }

        fun weatherDescriptionToIconRes(
            weatherDescription: WeatherDescription, isDay: Boolean = true,
        ): Int {
            return when (weatherDescription) {
                CLEAR_SKY -> if (isDay) {
                    R.drawable.sun_outlined_icon_3_contured_180
                } else {
                    R.drawable.clear_sky_night
                }

                MAINLY_CLEAR -> if (isDay) {
                    R.drawable.mainly_clear_outlined_3_contured_180
                } else {
                    R.drawable.mainly_clear_night_outlined_contured_180
                }

                PARTLY_CLOUDY -> if (isDay) {
                    R.drawable.partly_cloudy_5_outlined_contured_180
                } else {
                    R.drawable.partly_cloudy_night_outlined_contured_180
                }

                FOG -> R.drawable.fog

                OVERCAST -> R.drawable.overcast_outlined_180

                DEPOSITING_RIME_FOG -> R.drawable.depositing_rime_fog

                LIGHT_DRIZZLE -> R.drawable.light_drizzle
                MODERATE_DRIZZLE -> R.drawable.moderate_drizzle
                DENSE_DRIZZLE-> R.drawable.dense_drizzle

                LIGHT_FREEZING_DRIZZLE -> R.drawable.freezing_light_drizzle
                DENSE_FREEZING_DRIZZLE -> R.drawable.freezing_dense_drizzle

                SLIGHT_RAIN -> R.drawable.slight_rain

                MODERATE_RAIN -> R.drawable.moderate_rain

                HEAVY_RAIN -> R.drawable.heavy_rain

                LIGHT_FREEZING_RAIN -> R.drawable.freezing_light_rain
                HEAVY_FREEZING_RAIN -> R.drawable.freezing_heavy_rain

                SLIGHT_SNOW_FALL,
                MODERATE_SNOW_FALL,
                HEAVY_SNOW_FALL,
                SNOW_GRAINS,
                SLIGHT_SNOW_SHOWERS,
                HEAVY_SNOW_SHOWERS -> R.drawable.snowflake

                SLIGHT_RAIN_SHOWERS -> R.drawable.slight_rain_showers
                MODERATE_RAIN_SHOWERS -> R.drawable.moderate_rain_showers
                VIOLENT_RAIN_SHOWERS -> R.drawable.violent_rain_showers

                THUNDERSTORM -> R.drawable.thunderstorm
                THUNDERSTORM_WITH_SLIGHT_HAIL -> R.drawable.thunderstorm_with_slight_hail
                THUNDERSTORM_WITH_HEAVY_HAIL -> R.drawable.thunderstorm_with_heavy_hail
                NOT_EXISTING_WEATHER_CODE -> R.drawable.ic_launcher_background
            }
        }
    }

}