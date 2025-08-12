package com.example.wifimeasure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Measurement::class], version = 1, exportSchema = false)
abstract class MeasurementDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var Instance: MeasurementDatabase? = null

        fun getDatabase(context: Context): MeasurementDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MeasurementDatabase::class.java, "measurements")
                    .createFromAsset("database/measurements.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}