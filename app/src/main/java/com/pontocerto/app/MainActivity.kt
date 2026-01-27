package com.pontocerto.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var locationUtils: LocationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationUtils = LocationUtils(this)

        if (!PermissionUtils.hasAllPermissions(this)) {
            PermissionUtils.requestPermissions(this)
        } else {
            validarLocalizacao()
        }
    }

    private fun validarLocalizacao() {
        locationUtils.verificarLocalizacao { permitido ->
            runOnUiThread {
                if (permitido) {
                    Toast.makeText(
                        this,
                        "Localização válida. Você pode marcar o ponto.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Você está fora do local permitido.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionUtils.REQUEST_CODE) {
            if (PermissionUtils.hasAllPermissions(this)) {
                validarLocalizacao()
            } else {
                Toast.makeText(
                    this,
                    "Permissões obrigatórias para usar o app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
