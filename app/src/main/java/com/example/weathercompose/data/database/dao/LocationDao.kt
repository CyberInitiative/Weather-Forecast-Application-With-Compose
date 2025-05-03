package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weathercompose.data.database.entity.location.LocationEntity

@Dao
abstract class LocationDao {

    @Query("SELECT * FROM locations")
    abstract fun loadAll(): List<LocationEntity>

    /*
    @Transaction
    @Query("SELECT * FROM locations")
    abstract fun loadAll(): Flow<List<LocationWithDailyForecasts>>
     */

    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    abstract suspend fun load(locationId: Long): LocationEntity?

    /*
    @Transaction
    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    abstract suspend fun load(locationId: Long): LocationWithDailyForecasts?
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(location: LocationEntity): Long

    @Delete
    abstract suspend fun delete(location: LocationEntity)

    @Query("DELETE FROM locations WHERE locationId = :locationId")
    abstract suspend fun deleteLocationById(locationId: Long)

    @Query(
        """
            UPDATE locations SET forecastLastUpdateTimestamp = :timestamp 
            WHERE locationId = :locationId
        """
    )
    abstract suspend fun updateForecastLastUpdateTimestamp(locationId: Long, timestamp: Long)
}