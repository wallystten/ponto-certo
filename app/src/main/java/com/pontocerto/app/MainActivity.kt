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
        private const val REQUEST_EMPRESA = 200
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

            // 2️⃣ Empresa obrigatória (só uma vez)
            if (!EmpresaStorage.existeEmpresa(this)) {
                startActivityForResult(
                    Intent(this, EmpresaActivity::class.java),
                    REQUEST_EMPRESA
                )
                return@setOnClickListener
            }

            // 3️⃣ Fluxo facial
            iniciarFluxoFacial()
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    private fun iniciarFluxoFacial() {
        val intent = Intent(this, CameraActivity::class.java)

        // 🔥 REGRA FINAL: cadastro facial acontece UMA ÚNICA VEZ
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
            iniciarFluxoFacial()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            REQUEST_EMPRESA -> {
                if (resultCode == Activity.RESULT_OK) {
                    iniciarFluxoFacial()
                } else {
                    Toast.makeText(
                        this,
                        "Empresa obrigatória para continuar.",
                        Toast.LENGTH_LONG
                    ).show()
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
                        // 🔐 Cadastro facial concluído → NÃO registra ponto
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
