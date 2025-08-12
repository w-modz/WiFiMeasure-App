package com.example.wifimeasure

import android.app.Application
import com.example.wifimeasure.di.AppContainer
import com.example.wifimeasure.di.AppDataContainer

class MeasurementApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}