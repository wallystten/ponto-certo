package com.pontocerto.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val btnMarcarPonto = findViewById<Button>(R.id.btnMarcarPonto)

        btnMarcarPonto.setOnClickListener {
            val dataHora = PontoUtils.registrarPonto()
            Toast.makeText(
                this,
                "Ponto registrado em: $dataHora",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun validarLocalizacao() {
        locationUtils.verificarLocalizacao { permitido ->
            runOnUiThread {
                if (!permitido) {
                    Toast.makeText(
                        this,
                        "Você está fora do local permitido.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
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
                    "Permissões obrigatórias para usar o app.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}
