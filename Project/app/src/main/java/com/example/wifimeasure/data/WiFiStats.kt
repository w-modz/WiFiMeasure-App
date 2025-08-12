package com.example.wifimeasure.data

import java.time.LocalDateTime

data class WiFiStats(
    val ssid: String,
    val rssi: Double,
    val linkSpeed: Double?,
    val frequency: Double,
    val timestamp: LocalDateTime
)