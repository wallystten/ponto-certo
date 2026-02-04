package com.pontocerto.app

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EmpresaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)

        val edtCodigo = findViewById<EditText>(R.id.edtCodigoEmpresa)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarEmpresa)

        btnSalvar.setOnClickListener {
            val codigo = edtCodigo.text.toString().trim()

            if (codigo.length < 4) {
                Toast.makeText(
                    this,
                    "Código de empresa inválido",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            EmpresaStorage.salvarEmpresa(this, codigo)

            Toast.makeText(
                this,
                "Empresa vinculada com sucesso",
                Toast.LENGTH_LONG
            ).show()

            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
