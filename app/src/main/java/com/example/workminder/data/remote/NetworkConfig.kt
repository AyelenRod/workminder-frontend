package com.example.workminder.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.util.Log
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket

object NetworkConfig {
    private const val TAG = "NetworkConfig"
    private const val DEFAULT_PORT = 3000
    private var discoveredIp: String = com.example.workminder.BuildConfig.SERVER_IP

    fun init(context: Context) {}

    suspend fun discoverServer(context: Context): String = withContext(Dispatchers.IO) {
        val candidates = mutableListOf<String>()
        candidates.add("aye.local")
        candidates.add("10.0.2.2")
        
        val localSubnet = getLocalSubnet(context)
        if (localSubnet != null) {
            val deferreds = (1..254).map { i ->
                async {
                    val ip = "$localSubnet.$i"
                    if (isServerReachable(ip, DEFAULT_PORT)) ip else null
                }
            }
            
            for (deferred in deferreds) {
                val ip = deferred.await()
                if (ip != null) {
                    discoveredIp = ip
                    coroutineContext.cancelChildren()
                    return@withContext ip
                }
            }
        }
        discoveredIp
    }

    private fun isServerReachable(ip: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), 300)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getLocalSubnet(context: Context): String? {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val linkProps: LinkProperties = cm.getLinkProperties(cm.activeNetwork) ?: return null
            for (addr in linkProps.linkAddresses) {
                val ip = addr.address.hostAddress
                if (ip != null && (ip.startsWith("192.168.") || ip.startsWith("10."))) {
                    if (ip.startsWith("192.168.56.")) continue
                    return ip.substringBeforeLast(".")
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    val baseUrl: String
        get() = "https://workminder-backend.vercel.app/api/"
}
