package com.pontocerto.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
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
            iniciarFluxoCompleto()
        }

        findViewById<Button?>(R.id.btnHistorico)?.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }
    }

    /**
     * 🔁 Fluxo único, linear e seguro
     */
    private fun iniciarFluxoCompleto() {

        // 1️⃣ Permissões primeiro
        if (!PermissionUtils.temPermissoes(this)) {
            PermissionUtils.pedirPermissoes(this)
            return
        }

        // 2️⃣ GPS obrigatório
        if (!gpsAtivo()) {
            Toast.makeText(
                this,
                "Ative o GPS para registrar o ponto.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // 3️⃣ Usuário (CPF)
        if (StorageUtils.obterUsuarioLogado(this) == null) {
            startActivityForResult(
                Intent(this, UsuarioActivity::class.java),
                REQUEST_USUARIO
            )
            return
        }

        // 4️⃣ Empresa
        if (!StorageUtils.existeEmpresa(this)) {
            startActivityForResult(
                Intent(this, EmpresaActivity::class.java),
                REQUEST_EMPRESA
            )
            return
        }

        // 5️⃣ Biometria
        iniciarFluxoFacial()
    }

    /**
     * 🔍 Verifica se GPS está ativo
     */
    private fun gpsAtivo(): Boolean {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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
            iniciarFluxoCompleto()
        } else {
            Toast.makeText(
                this,
                "Permissões obrigatórias para continuar.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(
                this,
                "Ação obrigatória cancelada.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        when (requestCode) {

            REQUEST_USUARIO,
            REQUEST_EMPRESA -> {
                iniciarFluxoCompleto()
            }

            REQUEST_FACE -> {

                val sucesso = data?.getBooleanExtra("FACE_OK", false) ?: false
                val modo = data?.getStringExtra("MODO_FACE") ?: ""

                if (!sucesso) {
                    Toast.makeText(
                        this,
                        "Falha na validação facial.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                if (modo == "VALIDACAO") {
                    try {

                        val registro = PontoUtils.registrarPonto(this)

                        StorageUtils.salvarPonto(
                            this,
                            "$registro - PONTO REGISTRADO"
                        )

                        Toast.makeText(
                            this,
                            "Ponto registrado com sucesso!",
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (e: IllegalStateException) {

                        Toast.makeText(
                            this,
                            e.message ?: "Erro ao registrar ponto.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Cadastro facial concluído. Toque novamente para bater o ponto.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
