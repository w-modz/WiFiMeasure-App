package com.example.wifimeasure.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wifimeasure.MeasurementApplication
import com.example.wifimeasure.data.Measurement
import com.example.wifimeasure.data.MeasurementRepository
import com.example.wifimeasure.data.WiFiStats
import com.example.wifimeasure.data.calculateDistance
import com.example.wifimeasure.ui.utils.PageType
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WFMViewModel(
                   val measurementRepository: MeasurementRepository,
                    val application: MeasurementApplication
) :  ViewModel() {

    // Refresh frequency in seconds
    val refresh_frequency: Long = 5;

    private val _uiState = MutableStateFlow(WFMUiState())
    val uiState: StateFlow<WFMUiState> = _uiState

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _wifiStatsList = MutableStateFlow<List<WiFiStats>>(emptyList())
    val wifiStatsList: StateFlow<List<WiFiStats>> = _wifiStatsList

    private val _connectedWiFiInfo = MutableStateFlow<WifiInfo?>(null)
    val connectedWiFiInfo: StateFlow<WifiInfo?> = _connectedWiFiInfo

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _distanceHistoryMap = MutableStateFlow<Map<String, List<Double>>>(emptyMap())
    val distanceHistoryMap: StateFlow<Map<String, List<Double>>> = _distanceHistoryMap

    private val _rssiHistoryMap = MutableStateFlow<Map<String, List<Double>>>(emptyMap())
    val rssiHistoryMap: StateFlow<Map<String, List<Double>>> = _rssiHistoryMap

    private val _chartDataMap = MutableStateFlow<Map<String, List<Entry>>>(emptyMap())
    val chartDataMap: StateFlow<Map<String, List<Entry>>> = _chartDataMap


    init {
        startWifiUpdates()
    }

    fun toggleUpdatesPaused() {
        _isPaused.value = !_isPaused.value
    }



    private fun updateDistanceHistoryForAllNetworks(wifiStats: List<WiFiStats>) {
        val currentMap = _distanceHistoryMap.value.toMutableMap()

        wifiStats.forEach { stat ->
            val ssid = stat.ssid.trim('"')
            val distance = calculateDistance(stat.rssi, stat.frequency)

            val historyList = currentMap[ssid]?.toMutableList() ?: mutableListOf()
            if (historyList.size >= 5) {
                historyList.removeAt(0)
            }
            historyList.add(distance)

            currentMap[ssid] = historyList
        }

        _distanceHistoryMap.value = currentMap
    }

    private fun updateRssiHistoryForAllNetworks(wifiStats: List<WiFiStats>) {
        val currentRssiMap = _rssiHistoryMap.value.toMutableMap()
        val currentChartMap = _chartDataMap.value.toMutableMap()

        wifiStats.forEach { stat ->
            val ssid = stat.ssid.trim('"')
            val rssi = stat.rssi

            val rssiHistory = currentRssiMap[ssid]?.toMutableList() ?: mutableListOf()
            if (rssiHistory.size >= 100) {
                rssiHistory.removeAt(0)
            }
            rssiHistory.add(rssi)
            currentRssiMap[ssid] = rssiHistory

            // 👇 Build chart entries using time in seconds
            val entries = rssiHistory.mapIndexed { index, value ->
                val secondsSinceStart = index * refresh_frequency.toFloat()
                Entry(secondsSinceStart, value.toFloat())
            }

            currentChartMap[ssid] = entries
        }

        _rssiHistoryMap.value = currentRssiMap
        _chartDataMap.value = currentChartMap
    }


    private fun startWifiUpdates() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        viewModelScope.launch {
            while (true) {
                if (!_isPaused.value) {
                    _wifiStatsList.value = getAllVisibleWiFiStats(context)
                    @Suppress("DEPRECATION")
                    _connectedWiFiInfo.value = wifiManager.connectionInfo // Deprecated call to avoid initializing connectivityManager
                    saveCurrentMeasurements()
                    updateDistanceHistoryForAllNetworks(_wifiStatsList.value)
                    updateRssiHistoryForAllNetworks(_wifiStatsList.value)
                }
                delay(refresh_frequency * 1000)
            }
        }
    }

    fun deleteDb()
    {
        viewModelScope.launch {
            measurementRepository.deleteAll()
        }
    }


    fun updateCurrentPage(pageType: PageType) {
        _uiState.update { it.copy(currentPage = pageType) }
    }

    private fun saveCurrentMeasurements() {
        val wifiStats = _wifiStatsList.value

        if (wifiStats.isNotEmpty()) {
            val measurements = wifiStats.map { stat ->
                Measurement(
                    ssid = stat.ssid.trim('"'),
                    rssi = stat.rssi,
                    frequency = stat.frequency,
                    linkSpeed = stat.linkSpeed.takeIf { it != -1.0 }?.toDouble()
                )
            }

            viewModelScope.launch {
                measurements.forEach { measurement ->
                    measurementRepository.insertMeasurement(measurement)
                }
            }
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MeasurementApplication)
                val measurementRepository = application.container.measurementRepository
                WFMViewModel(
                    measurementRepository = measurementRepository,
                    application = application
                )
            }
        }
    }
}