package com.pontocerto.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_USUARIO = 10
        private const val REQUEST_EMPRESA = 20
        private const val REQUEST_FACE = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnMarcarPonto).setOnClickListener {

            // 1️⃣ Permissões
            if (!PermissionUtils.temPermissoes(this)) {
                PermissionUtils.pedirPermissoes(this)
                return@setOnClickListener
            }

            // 2️⃣ Usuário (CPF)
            if (StorageUtils.obterUsuarioLogado(this) == null) {
                startActivityForResult(
                    Intent(this, UsuarioActivity::class.java),
                    REQUEST_USUARIO
                )
                return@setOnClickListener
            }

            // 3️⃣ Empresa
            if (!StorageUtils.existeEmpresa(this)) {
                startActivityForResult(
                    Intent(this, EmpresaActivity::class.java),
                    REQUEST_EMPRESA
                )
                return@setOnClickListener
            }

            // 4️⃣ Biometria
            iniciarFluxoFacial()
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    private fun iniciarFluxoFacial() {
        val intent = Intent(this, CameraActivity::class.java)

        val modo = if (BiometriaStorage.existeCadastro(this)) {
            "VALIDACAO"
        } else {
            "CADASTRO"
        }

        intent.putExtra("MODO_FACE", modo)
        startActivityForResult(intent, REQUEST_FACE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PermissionUtils.REQUEST_CODE &&
            PermissionUtils.permissoesConcedidas(grantResults)
        ) {
            Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_USUARIO -> {
                if (resultCode == Activity.RESULT_OK) {
                    findViewById<Button>(R.id.btnMarcarPonto).performClick()
                } else {
                    Toast.makeText(this, "CPF obrigatório.", Toast.LENGTH_LONG).show()
                }
            }

            REQUEST_EMPRESA -> {
                if (resultCode == Activity.RESULT_OK) {
                    findViewById<Button>(R.id.btnMarcarPonto).performClick()
                } else {
                    Toast.makeText(this, "Empresa obrigatória.", Toast.LENGTH_LONG).show()
                }
            }

            REQUEST_FACE -> {
                val sucesso = data?.getBooleanExtra("FACE_OK", false) ?: false
                val modo = data?.getStringExtra("MODO_FACE") ?: ""

                if (resultCode == Activity.RESULT_OK && sucesso) {

                    if (modo == "VALIDACAO") {
                        val dataHora = PontoUtils.registrarPonto()
                        StorageUtils.salvarPonto(
                            this,
                            "$dataHora - PONTO REGISTRADO"
                        )

                        Toast.makeText(
                            this,
                            "Ponto registrado com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Cadastro facial concluído. Toque novamente para bater o ponto.",
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
}
