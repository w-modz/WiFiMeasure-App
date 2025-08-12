package com.example.wifimeasure.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.wifimeasure.data.WiFiStats
import com.example.wifimeasure.data.calculateDistance
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDateTime

@Composable
fun NetworksContent(viewModel: WFMViewModel) {
    val wifiStats by viewModel.wifiStatsList.collectAsState()
    val distanceHistoryMap by viewModel.distanceHistoryMap.collectAsState()

    val sortedStats = wifiStats.sortedByDescending { it.rssi }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        items(sortedStats) { stat ->
            val ssid = stat.ssid.trim('"')
            val distances = distanceHistoryMap[ssid] ?: emptyList()
            val avgDistance = distances.average()
            val chartEntries = viewModel.chartDataMap.collectAsState().value[ssid] ?: emptyList()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SSID: $ssid", style = MaterialTheme.typography.titleMedium)
                    Text("RSSI: ${stat.rssi} dBm", style = MaterialTheme.typography.bodyMedium)
                    Text("Frequency: ${stat.frequency} MHz", style = MaterialTheme.typography.bodyMedium)
                    Text("Avg Distance (last ${distanceHistoryMap[ssid]?.size}): %.2f m".format(avgDistance), style = MaterialTheme.typography.bodyMedium)

                    // Key for triggering ui refresh
                    key(chartEntries) {
                        if (chartEntries.isNotEmpty()) {
                            RSSILineChart(chartEntries)
                        }
                    }

                }
            }
        }
    }
}





@Composable
fun NetworkParametersContent(viewModel: WFMViewModel) {
    val wifiInfo = viewModel.connectedWiFiInfo.collectAsState().value
    val distanceHistoryMap = viewModel.distanceHistoryMap.collectAsState().value

    if (wifiInfo != null && wifiInfo.supplicantState.name == "COMPLETED") {
        val ssid = wifiInfo.ssid.trim('"')
        val rssi = wifiInfo.rssi
        val linkSpeed = wifiInfo.linkSpeed
        val frequency = wifiInfo.frequency

        val currentDistance = calculateDistance(
            rssi = rssi.toDouble(),
            frequency = frequency.toDouble()
        )

        val avgDistance = distanceHistoryMap[ssid]?.average()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SSID: $ssid")
                Text("RSSI: $rssi dBm")
                Text("Link Speed: $linkSpeed Mbps")
                Text("Frequency: $frequency MHz")
                Text("Distance to AP: %.2f m".format(currentDistance))
                if (avgDistance != null) {
                    Text("Avg Distance (last ${distanceHistoryMap[ssid]?.size}): %.2f m".format(avgDistance))
                }
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Wi-Fi Stats: Not connected to Wi-Fi")
            }
        }
    }
}


@Composable
fun HistoryContent(viewModel: WFMViewModel) {
    val measurements by viewModel.measurementRepository
        .getAllMeasurements()
        .collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        items(measurements.sortedByDescending { it.id }) { measurement ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SSID: ${measurement.ssid}", style = MaterialTheme.typography.titleMedium)
                    Text("RSSI: ${measurement.rssi} dBm", style = MaterialTheme.typography.bodyMedium)
                    Text("Frequency: ${measurement.frequency} MHz", style = MaterialTheme.typography.bodyMedium)
                    if (measurement.linkSpeed != null)
                        Text("Link Speed: ${measurement.linkSpeed} Mbps", style = MaterialTheme.typography.bodyMedium)
                    else
                        Text("Link Speed: Unknown", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Distance to AP: %.2f m".format(
                            calculateDistance(
                                rssi = measurement.rssi,
                                frequency = measurement.frequency,
                            )),
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getAllVisibleWiFiStats(context: Context): List<WiFiStats> {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Trigger a scan (optional but may return outdated list)
    // Deprecated method used to ensure data is present on first function call
    @Suppress("DEPRECATION")
    wifiManager.startScan()

    val scanResults = wifiManager.scanResults
    // Deprecated method used to avoid initializing connectivityManager
    @Suppress("DEPRECATION")
    val connectedInfo = wifiManager.connectionInfo
    val currentSSID = connectedInfo.ssid.trim('"')

    val now = LocalDateTime.now()

    return scanResults.map { scan ->
        val isCurrent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            scan.wifiSsid.toString() == currentSSID
        } else {
            @Suppress("DEPRECATION")
            scan.SSID == currentSSID // Deprecated method used for backwards compatibility
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            WiFiStats(
                ssid = scan.wifiSsid.toString(),
                rssi = scan.level.toDouble(),
                frequency = scan.frequency.toDouble(),
                linkSpeed = if (isCurrent) connectedInfo.linkSpeed.toDouble() else null,
                timestamp = now
            )
        } else {
            @Suppress("DEPRECATION")
            WiFiStats(
                ssid = scan.SSID, // Deprecated method used for backwards compatibility
                rssi = scan.level.toDouble(),
                frequency = scan.frequency.toDouble(),
                linkSpeed = if (isCurrent) connectedInfo.linkSpeed.toDouble() else null,
                timestamp = now
            )
        }
    }
}

@Composable
fun RSSILineChart(entries: List<Entry>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    500 // Increased height for better readability
                )

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)

                val dataSet = LineDataSet(entries, "RSSI").apply {
                    color = android.graphics.Color.BLUE
                    valueTextColor = android.graphics.Color.BLACK
                    setDrawCircles(true)
                    setCircleColor(android.graphics.Color.BLUE)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                val lineData = LineData(dataSet)
                data = lineData

                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false

                // Configure X-axis for time display
                xAxis.apply {
                    isEnabled = true
                    textColor = android.graphics.Color.WHITE
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "%.0f s".format(value)
                        }
                    }
                    granularity = 1f // Tick every second
                }

                axisLeft.apply {
                    textColor = android.graphics.Color.WHITE
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "%.1f dBm".format(value)
                        }
                    }
                }

                setVisibleXRangeMaximum(100f)
                moveViewToX(entries.lastOrNull()?.x ?: 0f)

                // Refresh chart
                invalidate()
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Distance (m)").apply {
                color = android.graphics.Color.BLUE
                valueTextColor = android.graphics.Color.BLACK
                setDrawCircles(true)
                setCircleColor(android.graphics.Color.BLUE)
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.data = LineData(dataSet)
            chart.setVisibleXRangeMaximum(50f)
            chart.moveViewToX(entries.lastOrNull()?.x ?: 0f)
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}



