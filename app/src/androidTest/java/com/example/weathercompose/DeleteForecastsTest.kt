package com.example.weathercompose

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteForecastsTest {
    /*
    private lateinit var database: WeatherForecastDatabase
    private lateinit var locationDao: LocationDao

    @Before
    fun setUpDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherForecastDatabase::class.java
        ).allowMainThreadQueries().build()

        locationDao = database.locationDao()
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
                timeZone = "auto"
            )
        )

        val locationFromDatabase = locationDao.load(locationId = locationId)
        assertEquals(0L, locationFromDatabase?.forecastLastUpdateTimestamp)

        val newTimestamp = System.currentTimeMillis()

        locationDao.updateForecastLastUpdateTimestamp(
            locationId = locationId,
            timestamp = newTimestamp
        )

        val updatedLocationFromDatabase = locationDao.load(locationId = locationId)
        assertEquals(newTimestamp, updatedLocationFromDatabase?.forecastLastUpdateTimestamp)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
     */
}