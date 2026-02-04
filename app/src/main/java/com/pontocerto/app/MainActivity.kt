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

            // 🏢 PASSO 5 — verifica empresa ANTES de tudo
            if (!EmpresaStorage.existeEmpresa(this)) {
                startActivity(
                    Intent(this, EmpresaActivity::class.java)
                )
                return@setOnClickListener
            }

            // 🔎 Decide o modo facial
            val intent = Intent(this, CameraActivity::class.java)

            val modo = if (BiometriaStorage.existeCadastro(this)) {
                "VALIDACAO"
            } else {
                "CADASTRO"
            }

            intent.putExtra("MODO_FACE", modo)
            startActivityForResult(intent, REQUEST_FACE)
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
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

            val sucesso = data?.getBooleanExtra("FACE_OK", false) ?: false
            val modo = data?.getStringExtra("MODO_FACE") ?: ""

            if (resultCode == Activity.RESULT_OK && sucesso) {

                if (modo == "VALIDACAO") {
                    val dataHora = PontoUtils.registrarPonto()
                    StorageUtils.salvarPonto(this, "$dataHora - PONTO REGISTRADO")

                    Toast.makeText(
                        this,
                        "Ponto registrado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Cadastro facial concluído. Agora você pode registrar o ponto.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } else {
                Toast.makeText(
                    this,
                    "Falha na validação facial.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
