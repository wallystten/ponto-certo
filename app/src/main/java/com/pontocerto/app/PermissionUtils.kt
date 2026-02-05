package com.pontocerto.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    const val REQUEST_CODE = 1001

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Verifica se TODAS as permissões obrigatórias foram concedidas
     */
    fun temPermissoes(activity: Activity): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Solicita TODAS as permissões obrigatórias
     */
    fun pedirPermissoes(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE
        )
    }

    /**
     * Verifica se o resultado do requestPermissions foi positivo
     */
    fun permissoesConcedidas(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) return false
        return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
}
  
