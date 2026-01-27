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

        val txt = findViewById<TextView>(R.id.txtHistorico)
        txt.text = StorageUtils.obterHistorico(this)

        findViewById<Button>(R.id.btnExportarCSV).setOnClickListener {
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
