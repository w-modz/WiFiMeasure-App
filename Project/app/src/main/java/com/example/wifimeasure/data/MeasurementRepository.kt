package com.example.wifimeasure.data

import kotlinx.coroutines.flow.Flow


interface MeasurementRepository {

    fun getAllMeasurements(): Flow<List<Measurement>>
    suspend fun insertMeasurement(measurement: Measurement)
    suspend fun deleteAll()
}