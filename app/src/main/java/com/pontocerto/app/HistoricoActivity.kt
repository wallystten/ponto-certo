package com.pontocerto.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val txtHistorico = findViewById<TextView>(R.id.txtHistorico)
        val historico = StorageUtils.obterHistorico(this)

        // Exibe histórico ou mensagem padrão
        txtHistorico.text = if (historico.isNotBlank()) {
            historico
        } else {
            "Nenhum ponto registrado até o momento."
        }

        val btnExportarCSV = findViewById<Button>(R.id.btnExportarCSV)
        btnExportarCSV.setOnClickListener {

            val arquivo = ExportUtils.exportarCSV(this)

            if (arquivo != null) {
                Toast.makeText(
                    this,
                    "CSV salvo em Documents/PontoCerto",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Nenhum dado para exportar",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
