package com.example.weathercompose.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weathercompose.data.database.entity.CityEntity

@Dao
interface CityDao {

    @Query("SELECT * FROM cities")
    suspend fun loadAll(): List<CityEntity>

    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun load(cityId: Int): CityEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity)

    @Delete
    suspend fun delete(city: CityEntity)

    @Update
    suspend fun update(city: CityEntity)
}