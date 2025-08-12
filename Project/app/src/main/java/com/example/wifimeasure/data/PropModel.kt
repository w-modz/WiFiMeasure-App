package com.example.wifimeasure.data

import kotlin.math.log
import kotlin.math.log10
import kotlin.math.pow

fun ITU (
    frequency: Double,
    N: Double = 31.0,
    distance: Double,
    Lf: Double = 16.0) : Double {
    return 20*log(frequency, 10.0) + N * log(distance, 10.0) + Lf - 28
}

fun ituDistance(L: Double, frequency: Double, N: Double = 31.0, Lf: Double = 16.0): Double {
    return 10.0.pow((L - 20 * log(frequency,10.0) - Lf + 28) / N)
}

//fun calculateDistance(rssi: Double, frequency: Double): Double {
//    var txPower = 20.0
//    if (frequency > 4800.0) // Margin of error for 5GHz
//        txPower = 23.0
//    val fspl = txPower - rssi // Conversion to power loss
//    val exponent = (fspl + 27.55 - 20 * log10(frequency)) / 20.0
//    return 10.0.pow(exponent)
//}

fun calculateDistance(rssi: Double, frequency: Double): Double {
    var txPower = 20.0
    if (frequency > 4800.0) // Margin of error for 5GHz
        txPower = 23.0
    val fspl = txPower - rssi // Conversion to power loss
    val L0 = -27.55 + 20 * log10(frequency) + 20* log10(1.0)
    val exponent = (fspl - L0) / 10.0 / 3.5
    return 10.0.pow(exponent)
}
