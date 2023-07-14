package ru.kheynov.todoappyandex.core.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import javax.inject.Inject

/**
 * [NetworkListener] observes network state and syncs todos when network is available
 * @param connectivityManager [ConnectivityManager]
 * @param repository [TodoItemsRepository]
 */
class NetworkListener @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val repository: TodoItemsRepository,
) {

    private val scope = CoroutineScope(SupervisorJob() + CoroutineName("NetworkListenerScope"))

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            scope.launch {
                repository.syncTodos()
            }
        }
    }

    fun start() {
        connectivityManager.requestNetwork(networkRequest, callback)
    }

    fun stop() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}
