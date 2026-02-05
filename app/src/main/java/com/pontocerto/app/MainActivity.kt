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

            // 🔐 PASSO 1 — permissões
            if (!PermissionUtils.temPermissoes(this)) {
                PermissionUtils.pedirPermissoes(this)
                return@setOnClickListener
            }

            // 🏢 PASSO 2 — empresa obrigatória
            if (!EmpresaStorage.existeEmpresa(this)) {
                startActivityForResult(
                    Intent(this, EmpresaActivity::class.java),
                    REQUEST_EMPRESA
                )
                return@setOnClickListener
            }

            // 📷 PASSO 3 — biometria facial
            iniciarFluxoBiometria()
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    private fun iniciarFluxoBiometria() {
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

        if (requestCode == PermissionUtils.REQUEST_CODE) {
            if (PermissionUtils.permissoesConcedidas(grantResults)) {

                if (!EmpresaStorage.existeEmpresa(this)) {
                    startActivityForResult(
                        Intent(this, EmpresaActivity::class.java),
                        REQUEST_EMPRESA
                    )
                } else {
                    iniciarFluxoBiometria()
                }

            } else {
                Toast.makeText(
                    this,
                    "Permissões obrigatórias para usar o aplicativo.",
                    Toast.LENGTH_LONG
                ).show()
            }
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
                    iniciarFluxoBiometria()
                } else {
                    Toast.makeText(
                        this,
                        "É necessário informar a empresa para continuar.",
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
}
