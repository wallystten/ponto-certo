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

            // 1️⃣ Permissões primeiro
            if (!PermissionUtils.hasAllPermissions(this)) {
                PermissionUtils.requestPermissions(this)
                return@setOnClickListener
            }

            // 2️⃣ Empresa obrigatória
            if (!EmpresaStorage.existeEmpresa(this)) {
                startActivityForResult(
                    Intent(this, EmpresaActivity::class.java),
                    REQUEST_EMPRESA
                )
                return@setOnClickListener
            }

            // 3️⃣ Biometria (cadastro ou validação)
            iniciarFluxoBiometria()
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    private fun iniciarFluxoBiometria() {
        val intent = Intent(this, CameraActivity::class.java)

        // 🔐 REGRA DEFINITIVA
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

            if (PermissionUtils.hasAllPermissions(this)) {

                // volta para o fluxo normal
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

            // 🏢 retorno da empresa
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

            // 📷 retorno da biometria
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
                        // 👤 cadastro concluído
                        Toast.makeText(
                            this,
                            "Cadastro facial concluído. Agora valide novamente para registrar o ponto.",
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
