package com.pontocerto.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Mantemos o LocationUtils, mas não usamos agora
    private lateinit var locationUtils: LocationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationUtils = LocationUtils(this)

        // Verifica permissões necessárias
        if (!PermissionUtils.hasAllPermissions(this)) {
            PermissionUtils.requestPermissions(this)
        } else {
            // validarLocalizacao()  // 🔒 DESATIVADO TEMPORARIAMENTE PARA TESTES
        }

        // Botão Marcar Ponto
        val btnMarcarPonto = findViewById<Button>(R.id.btnMarcarPonto)
        btnMarcarPonto.setOnClickListener {

            // Gera data/hora do ponto
            val dataHora = PontoUtils.registrarPonto()
            val registro = "$dataHora - PONTO REGISTRADO"

            // Salva no histórico local
            StorageUtils.salvarPonto(this, registro)

            Toast.makeText(
                this,
                "Ponto registrado com sucesso!",
                Toast.LENGTH_LONG
            ).show()
        }

        // Botão Histórico (opcional no layout)
        val btnHistorico = findViewById<Button?>(R.id.btnHistorico)
        btnHistorico?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    // 🔒 Mantido para uso futuro (produção)
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
                // validarLocalizacao()  // 🔒 DESATIVADO TEMPORARIAMENTE PARA TESTES
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
