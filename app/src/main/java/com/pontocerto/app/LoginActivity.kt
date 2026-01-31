package com.pontocerto.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtCpf = findViewById<EditText>(R.id.edtCpf)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val cpf = edtCpf.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (cpf.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha CPF e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // LOGIN MOCK (simples e seguro por enquanto)
            if (cpf == "12345678900" && senha == "1234") {
                StorageUtils.salvarUsuarioLogado(this, cpf)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "CPF ou senha inválidos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

