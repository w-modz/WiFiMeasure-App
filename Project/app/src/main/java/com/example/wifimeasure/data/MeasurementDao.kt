package com.example.wifimeasure.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Measurement)

    @Delete
    fun delete(item: Measurement)

    @Query("DELETE FROM Measurements")
    suspend fun deleteAll()

    @Query("SELECT * FROM Measurements")
    fun getAll(): Flow<List<Measurement>>

    @Query("SELECT * FROM Measurements WHERE ssid = :ssid")
    fun getSsid(ssid: String): Flow<List<Measurement>>

    @Query("SELECT * FROM Measurements WHERE id = :id")
    fun getId(id: Int): Flow<List<Measurement>>
}