package com.pontocerto.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_FACE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnMarcarPonto).setOnClickListener {

            if (!PermissionUtils.hasAllPermissions(this)) {
                PermissionUtils.requestPermissions(this)
            } else {
                abrirCamera()
            }
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    private fun abrirCamera() {
        startActivityForResult(
            Intent(this, CameraActivity::class.java),
            REQUEST_FACE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionUtils.REQUEST_CODE &&
            PermissionUtils.hasAllPermissions(this)
        ) {
            abrirCamera()
        } else {
            Toast.makeText(
                this,
                "Permissões obrigatórias para marcar ponto.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FACE) {
            if (resultCode == Activity.RESULT_OK) {

                val dataHora = PontoUtils.registrarPonto()
                val registro = "$dataHora - PONTO REGISTRADO"

                StorageUtils.salvarPonto(this, registro)

                Toast.makeText(
                    this,
                    "Ponto registrado com sucesso!",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(
                    this,
                    "Falha na validação facial. Ponto não registrado.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
