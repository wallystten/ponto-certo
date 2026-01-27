package com.pontocerto.app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationUtils(context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 📍 LOCAL FIXO DA EMPRESA (exemplo)
    private val empresaLatitude = -26.3045
    private val empresaLongitude = -48.8487
    private val raioPermitido = 100f // metros

    @SuppressLint("MissingPermission")
    fun verificarLocalizacao(callback: (Boolean) -> Unit) {
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val distancia = calcularDistancia(location)
                callback(distancia <= raioPermitido)
            } else {
                callback(false)
            }
        }
    }

    private fun calcularDistancia(locationAtual: Location): Float {
        val localEmpresa = Location("").apply {
            latitude = empresaLatitude
            longitude = empresaLongitude
        }
        return locationAtual.distanceTo(localEmpresa)
    }
}
