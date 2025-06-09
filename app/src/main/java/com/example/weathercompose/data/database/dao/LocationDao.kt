package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weathercompose.data.database.entity.combined.LocationWithDailyForecasts
import com.example.weathercompose.data.database.entity.location.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LocationDao {
    @Query("SELECT * FROM locations")
    abstract fun findAll(): List<LocationEntity>

    @Transaction
    @Query("SELECT * FROM locations")
    abstract fun findAllLocationsWithForecasts(): Flow<List<LocationWithDailyForecasts>>

    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    abstract suspend fun findById(locationId: Long): LocationEntity?

    /*
    @Transaction
    @Query("SELECT * FROM locations WHERE locationId = :locationId")
    abstract suspend fun load(locationId: Long): LocationWithDailyForecasts?
     */

    @Query("SELECT * FROM locations WHERE isHomeLocation = 1")
    abstract suspend fun findHomeLocation(): LocationEntity?

    @Query("SELECT * FROM locations WHERE isHomeLocation = 1")
    abstract fun observeHomeLocation(): Flow<LocationEntity?>

    @Query("UPDATE locations SET isHomeLocation = :isHomeLocation WHERE locationId = :locationId")
    abstract suspend fun updateHomeLocationStatus(locationId: Long, isHomeLocation: Boolean)

    @Transaction
    open suspend fun setLocationAsHome(locationId: Long) {
        findHomeLocation()?.let { currentHome ->
            updateHomeLocationStatus(locationId = currentHome.locationId, isHomeLocation = false)
            //tapping a location already set as home will unselect it.
            if (currentHome.locationId == locationId) return
        }
        updateHomeLocationStatus(locationId = locationId, isHomeLocation = true)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(location: LocationEntity): Long

    @Delete
    abstract suspend fun delete(location: LocationEntity)

    @Query("DELETE FROM locations WHERE locationId = :locationId")
    abstract suspend fun deleteLocationById(locationId: Long)
}