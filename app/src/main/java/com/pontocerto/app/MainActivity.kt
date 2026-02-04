package com.pontocerto.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_FACE = 100
        private const val REQUEST_PERMISSIONS = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnMarcarPonto = findViewById<Button>(R.id.btnMarcarPonto)
        btnMarcarPonto.setOnClickListener {
            if (temPermissoes()) {
                abrirCamera()
            } else {
                solicitarPermissoes()
            }
        }

        val btnHistorico = findViewById<Button?>(R.id.btnHistorico)
        btnHistorico?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    // 🔐 Verifica se já tem permissão
    private fun temPermissoes(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 🔔 Solicita permissão da câmera
    private fun solicitarPermissoes() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_PERMISSIONS
        )
    }

    // 📷 Abre a câmera
    private fun abrirCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, REQUEST_FACE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                abrirCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão da câmera é obrigatória para registrar o ponto.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FACE) {
            val faceOk = data?.getBooleanExtra("FACE_OK", false) ?: false

            if (resultCode == Activity.RESULT_OK && faceOk) {
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
