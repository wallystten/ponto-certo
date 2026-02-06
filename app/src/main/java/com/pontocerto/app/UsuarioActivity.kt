package com.pontocerto.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        val edtCpf = findViewById<EditText>(R.id.edtUsuario)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarUsuario)

        btnSalvar.setOnClickListener {
            val cpf = edtCpf.text.toString().trim()

            // 🔐 Validação mínima (MVP seguro)
            if (cpf.length < 11) {
                Toast.makeText(
                    this,
                    "CPF inválido",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // 💾 Salva CPF como usuário logado
            StorageUtils.salvarUsuarioLogado(this, cpf)

            Toast.makeText(
                this,
                "Usuário identificado com sucesso",
                Toast.LENGTH_LONG
            ).show()

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
