package com.example.wifimeasure.di

import android.content.Context
import com.example.wifimeasure.data.MeasurementDatabase
import com.example.wifimeasure.data.MeasurementRepository
import com.example.wifimeasure.data.OfflineMeasurementRepository

interface AppContainer {
    val measurementRepository: MeasurementRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val measurementRepository: MeasurementRepository by lazy {
        OfflineMeasurementRepository(MeasurementDatabase.getDatabase(context).measurementDao())
    }
}