package com.pontocerto.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

<<<<<<< HEAD
        // Botão Marcar Ponto
=======
>>>>>>> 3496ed8 (build estável sem validação de localização)
        val btnMarcarPonto = findViewById<Button>(R.id.btnMarcarPonto)
        btnMarcarPonto.setOnClickListener {

            val dataHora = PontoUtils.registrarPonto()
            val registro = "$dataHora - PONTO REGISTRADO"

            StorageUtils.salvarPonto(this, registro)

            Toast.makeText(
                this,
                "Ponto registrado com sucesso!",
                Toast.LENGTH_LONG
            ).show()
        }

<<<<<<< HEAD
        // Botão Histórico
=======
>>>>>>> 3496ed8 (build estável sem validação de localização)
        val btnHistorico = findViewById<Button?>(R.id.btnHistorico)
        btnHistorico?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }
}

