package com.example.wifimeasure.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Measurements")
data class Measurement (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @NonNull
    @ColumnInfo(name = "ssid")
    val ssid: String,
    @NonNull
    @ColumnInfo(name = "rssi")
    val rssi: Double,
    @ColumnInfo(name = "link_speed")
    val linkSpeed: Double?,
    @NonNull
    @ColumnInfo(name = "frequency")
    val frequency: Double,
)