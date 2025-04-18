package com.example.weathercompose

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathercompose.data.database.WeatherForecastDatabase
import com.example.weathercompose.data.database.dao.LocationDao
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
import com.example.weathercompose.data.database.entity.location.LocationEntity
import com.example.weathercompose.domain.model.forecast.WeatherDescription
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteForecastsTest {
    private lateinit var database: WeatherForecastDatabase
    private lateinit var locationDao: LocationDao

    @Before
    fun setUpDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherForecastDatabase::class.java
        ).allowMainThreadQueries().build()

        locationDao = database.locations()
    }

    @Test
    fun testAddition() = runBlocking {
        val locationId: Long = locationDao.insert(
            LocationEntity(
                locationId = 1L,
                latitude = 10.0,
                longitude = 11.0,
                name = "SomeLocation",
                firstAdministrativeLevel = "",
                secondAdministrativeLevel = "",
                thirdAdministrativeLevel = "",
                fourthAdministrativeLevel = "",
                country = "FarLand",
                timezone = "auto"
            )
        )

        val dailyForecastId: Long = locationDao.insert(
            DailyForecastEntity(
                locationId = locationId,
                date = "",
                weatherDescription = WeatherDescription.CLEAR_SKY,
                maxTemperature = 10.0,
                minTemperature = 5.0,
                sunrise = "",
                sunset = ""
            )
        )

        repeat(3) {
            locationDao.insert(
                HourlyForecastEntity(
                    dailyForecastId = dailyForecastId,
                    date = "",
                    time = "",
                    weatherDescription = WeatherDescription.CLEAR_SKY,
                    temperature = 10.0,
                    isDay = true,
                )
            )
        }

        val dailyForecastCount = locationDao.countHourlyForecasts()
        assertEquals(3, dailyForecastCount)
    }

    @Test
    fun testRemovalDaily() = runBlocking {
        locationDao.deleteDailyForecastsByLocationId(locationId = 1L)

        val dailyForecastCount = locationDao.countDailyForecasts()
        assertEquals(0, dailyForecastCount)
    }

    @Test
    fun testRemovalHourly() = runBlocking {
        val dailyForecastCount = locationDao.countHourlyForecasts()
        assertEquals(0, dailyForecastCount)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}