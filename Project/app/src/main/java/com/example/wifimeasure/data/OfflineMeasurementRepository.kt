package com.example.wifimeasure.data

import kotlinx.coroutines.flow.Flow

class OfflineMeasurementRepository(private val measurementDao: MeasurementDao) : MeasurementRepository {
    override fun getAllMeasurements(): Flow<List<Measurement>> {
        return measurementDao.getAll()
    }

    override suspend fun insertMeasurement(measurement: Measurement) {
        measurementDao.insert(measurement)
    }

    override suspend fun deleteAll() {
        measurementDao.deleteAll()
    }
}