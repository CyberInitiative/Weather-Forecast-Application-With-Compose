package com.example.weathercompose

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathercompose.data.database.WeatherForecastDatabase
import com.example.weathercompose.data.database.dao.CityDao
import com.example.weathercompose.data.database.entity.city.CityEntity
import com.example.weathercompose.data.database.entity.forecast.DailyForecastEntity
import com.example.weathercompose.data.database.entity.forecast.HourlyForecastEntity
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
    private lateinit var cityDao: CityDao

    @Before
    fun setUpDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherForecastDatabase::class.java
        ).allowMainThreadQueries().build()

        cityDao = database.cities()
    }

    @Test
    fun testAddition() = runBlocking {
        val cityId: Long = cityDao.insert(
            CityEntity(
                cityId = 1L,
                latitude = 10.0,
                longitude = 11.0,
                name = "SomeCity",
                firstAdministrativeLevel = "",
                secondAdministrativeLevel = "",
                thirdAdministrativeLevel = "",
                fourthAdministrativeLevel = "",
                country = "FarLand",
                timezone = "auto"
            )
        )

        val dailyForecastId: Long = cityDao.insert(
            DailyForecastEntity(
                cityId = cityId,
                date = "",
                weatherDescription = WeatherDescription.CLEAR_SKY,
                maxTemperature = 10.0,
                minTemperature = 5.0,
                sunrise = "",
                sunset = ""
            )
        )

        repeat(3) {
            cityDao.insert(
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

        val dailyForecastCount = cityDao.countHourlyForecasts()
        assertEquals(3, dailyForecastCount)
    }

    @Test
    fun testRemovalDaily() = runBlocking{
        cityDao.deleteDailyForecastsByCityId(cityId = 1L)

        val dailyForecastCount = cityDao.countDailyForecasts()
        assertEquals(0, dailyForecastCount)
    }

    @Test
    fun testRemovalHourly() = runBlocking{
        val dailyForecastCount = cityDao.countHourlyForecasts()
        assertEquals(0, dailyForecastCount)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}