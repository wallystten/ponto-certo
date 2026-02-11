,package com.pontocerto.app

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

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw IllegalStateException("Permissão de localização não concedida.")
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw IllegalStateException("GPS desativado.")
        }

        val location: Location? =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location == null) {
            throw IllegalStateException("Localização indisponível.")
        }

        val latitude = location.latitude
        val longitude = location.longitude

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val dataHora = sdf.format(Date())

        return "$dataHora - LAT:$latitude LON:$longitude"
    }
}
