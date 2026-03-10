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
    private var discoveredIp: String = "10.31.216.125" // Fallback por si todo falla

    fun init(context: Context) {}

    suspend fun discoverServer(context: Context): String = withContext(Dispatchers.IO) {
        val candidates = mutableListOf<String>()
        
        // 1. Prioridad: Hostname local y IP de emulador
        candidates.add("aye.local")
        candidates.add("10.0.2.2")
        
        // 2. Obtener la subred real de la WiFi (ej. 192.168.1)
        val localSubnet = getLocalSubnet(context)
        
        Log.d(TAG, "Iniciando escaneo ultra rápido en subred: $localSubnet")

        if (localSubnet != null) {
            // Escaneo paralelo masivo: probamos las 254 IPs al mismo tiempo
            val deferreds = (1..254).map { i ->
                async {
                    val ip = "$localSubnet.$i"
                    if (isServerReachable(ip, DEFAULT_PORT)) ip else null
                }
            }
            
            // Esperamos a que la primera que responda gane
            for (deferred in deferreds) {
                val ip = deferred.await()
                if (ip != null) {
                    discoveredIp = ip
                    Log.d(TAG, "¡Servidor de la Uni encontrado en: $ip!")
                    // Cancelamos el resto de las búsquedas para ahorrar batería
                    coroutineContext.cancelChildren()
                    return@withContext ip
                }
            }
        }

        return@withContext discoveredIp
    }

    private fun isServerReachable(ip: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            // Timeout agresivo de 300ms. En red local es más que suficiente.
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
                // Buscamos la IP WiFi típica (192.168.x.x o 10.x.x.x)
                if (ip != null && (ip.startsWith("192.168.") || ip.startsWith("10."))) {
                    if (ip.startsWith("192.168.56.")) continue // Ignorar redes virtuales
                    return ip.substringBeforeLast(".")
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    val baseUrl: String
        get() = "http://$discoveredIp:$DEFAULT_PORT/"
}
