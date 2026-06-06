package com.example.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

class WifiSocketFactory(private val context: Context) : SocketFactory() {
    private val defaultFactory = getDefault()

    override fun createSocket(): Socket {
        val socket = Socket()
        bindToWifi(socket)
        return socket
    }

    override fun createSocket(host: String, port: Int): Socket {
        val socket = defaultFactory.createSocket(host, port)
        bindToWifi(socket)
        return socket
    }

    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        val socket = defaultFactory.createSocket(host, port, localHost, localPort)
        bindToWifi(socket)
        return socket
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        val socket = defaultFactory.createSocket(host, port)
        bindToWifi(socket)
        return socket
    }

    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        val socket = defaultFactory.createSocket(address, port, localAddress, localPort)
        bindToWifi(socket)
        return socket
    }

    private fun bindToWifi(socket: Socket) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = cm.allNetworks
        for (network in networks) {
            val caps = cm.getNetworkCapabilities(network)
            if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                network.bindSocket(socket)
                break
            }
        }
    }
}
