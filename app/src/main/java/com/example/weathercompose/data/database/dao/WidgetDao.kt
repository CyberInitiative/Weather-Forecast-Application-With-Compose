package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.weathercompose.data.model.widget.WidgetDailyForecast
import com.example.weathercompose.data.model.widget.WidgetHourlyForecast
import com.example.weathercompose.data.model.widget.WidgetLocationDataModel
import com.example.weathercompose.data.model.widget.WidgetLocationWithForecasts
import com.example.weathercompose.utils.getCurrentDateInTimeZone
import com.example.weathercompose.utils.getCurrentHourInTimeZone

@Dao
abstract class WidgetDao {

    @Transaction
    open suspend fun findAllWidgetLocationsWithForecasts(): List<WidgetLocationWithForecasts> {
        val widgetLocations = findAllWidgetLocations()
        return widgetLocations.map { location ->
            val currentDate = getCurrentDateInTimeZone(location.timeZone)
            val currentHour = getCurrentHourInTimeZone(location.timeZone)

            val dailyForecast = findWidgetDailyForecastByDate(
                locationId = location.locationId,
                date = currentDate.toString(),
            )
            val hourlyForecasts = findHourlyForecastsByLocationIdAndDateAndStartHour(
                locationId = location.locationId,
                date = currentDate.toString(),
                startTime = currentHour.toString(),
                limit = 6
            )

            WidgetLocationWithForecasts(
                locationId = location.locationId,
                locationName = location.locationName,
                dailyMaxTemperature = dailyForecast.dailyMaxTemperature,
                dailyMinTemperature = dailyForecast.dailyMinTemperature,
                weatherDescription = dailyForecast.weatherDescription,
                hourlyForecasts = hourlyForecasts
            )
        }
    }

    @Transaction
    open suspend fun findWidgetLocationsWithForecastsById(
        locationId: Long
    ): WidgetLocationWithForecasts? {
        val widgetLocation = findWidgetLocationById(locationId = locationId) ?: return null

        val currentDate = getCurrentDateInTimeZone(widgetLocation.timeZone)
        val currentHour = getCurrentHourInTimeZone(widgetLocation.timeZone)

        val dailyForecast = findWidgetDailyForecastByDate(
            locationId = widgetLocation.locationId,
            date = currentDate.toString(),
        )
        val hourlyForecasts = findHourlyForecastsByLocationIdAndDateAndStartHour(
            locationId = widgetLocation.locationId,
            date = currentDate.toString(),
            startTime = currentHour.toString(),
            limit = 6
        )

        return WidgetLocationWithForecasts(
            locationId = widgetLocation.locationId,
            locationName = widgetLocation.locationName,
            dailyMaxTemperature = dailyForecast.dailyMaxTemperature,
            dailyMinTemperature = dailyForecast.dailyMinTemperature,
            weatherDescription = dailyForecast.weatherDescription,
            hourlyForecasts = hourlyForecasts
        )

    }

    @Query(
        """
        SELECT 
            locationId,
            name AS locationName,
            timeZone
        FROM locations
        WHERE locationId = :locationId
        """
    )
    abstract suspend fun findWidgetLocationById(locationId: Long): WidgetLocationDataModel?

    @Query(
        """
        SELECT 
            locationId,
            name AS locationName,
            timeZone
        FROM locations
        """
    )
    abstract suspend fun findAllWidgetLocations(): List<WidgetLocationDataModel>

    @Query(
        """
    SELECT 
        maxTemperature AS dailyMaxTemperature,
        minTemperature AS dailyMinTemperature,
        weatherDescription
    FROM daily_forecasts
    WHERE locationId = :locationId AND date = :date
    """
    )
    abstract suspend fun findWidgetDailyForecastByDate(
        locationId: Long,
        date: String,
    ): WidgetDailyForecast

    @Query(
        """
        SELECT time, temperature, weatherDescription, isDay
        FROM hourly_forecasts
        WHERE locationId = :locationId 
            AND (
                (date = :date AND time >= :startTime) 
                OR (date > :date)
            )
        ORDER BY date, time
        LIMIT :limit
        """
    )
    abstract suspend fun findHourlyForecastsByLocationIdAndDateAndStartHour(
        locationId: Long,
        date: String,
        startTime: String,
        limit: Int
    ): List<WidgetHourlyForecast>
}