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

        val btnMarcarPonto = findViewById<Button>(R.id.btnMarcarPonto)
        btnMarcarPonto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, REQUEST_FACE)
        }

        val btnHistorico = findViewById<Button?>(R.id.btnHistorico)
        btnHistorico?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
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

                // 🔐 Validação facial OK → registra ponto
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
