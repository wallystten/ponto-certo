package com.pontocerto.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val txt = findViewById<TextView>(R.id.txtHistorico)
        txt.text = StorageUtils.obterHistorico(this)
    }
}
