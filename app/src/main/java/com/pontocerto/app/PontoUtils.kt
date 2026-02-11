package com.pontocerto.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PontoUtils {

    fun registrarPonto(context: Context): String {

        // 1️⃣ Verifica permissão
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw IllegalStateException("Permissão de localização não concedida.")
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 2️⃣ Verifica se GPS está ativo
        val gpsAtivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gpsAtivo) {
            throw IllegalStateException("GPS desativado.")
        }

        // 3️⃣ Pega última localização conhecida
        val location: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0

        // 4️⃣ Data e hora
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val dataHora = sdf.format(Date())

        return "$dataHora - LAT:$latitude LON:$longitude"
    }
}
 
