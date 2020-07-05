package ua.geekhub.antosiukoleksii.fitchallenge

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.widget.Toast


class ChekNetwork() {
    init {
        registerNetworkCallback()
    }
    fun registerNetworkCallback() {
        val cm:ConnectivityManager = MyApplication.appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    Log.i("MainActivity", "onAvailable!")

                    isNetworkConnected = true
                }

                override fun onLost(network: Network) {
                    Toast.makeText(MyApplication.appContext,"Network lost",Toast.LENGTH_LONG).show()
                    Log.i("MainActivity", "onLost!")
                    isNetworkConnected = false

                }
            }
        )
    }
    companion object {
        var single = ChekNetwork()

        fun getInstance(): ChekNetwork {
            if (single == null)
                single = ChekNetwork()
            return single
        }
        var isNetworkConnected = false
    }
}