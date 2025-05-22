package com.example.weathercompose.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkManager(context: Context) {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _connectionState = MutableStateFlow(value = ConnectionState.DISCONNECTED)
    val connectionState: Flow<ConnectionState> get() = _connectionState

    private val networkStateCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            _connectionState.value = ConnectionState.CONNECTED
        }

        override fun onLost(network: Network) {
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }

    init {
        registerNetworkCallback()
    }

    fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        connectivityManager.registerNetworkCallback(networkRequest.build(), networkStateCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkStateCallback)
    }

    fun isInternetAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED,
    }
}