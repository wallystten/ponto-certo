package com.pontocerto.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 200
    private var modoFace = "VALIDACAO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        modoFace = intent.getStringExtra("MODO_FACE") ?: "VALIDACAO"

        val txtInstrucao = findViewById<TextView?>(R.id.txtInstrucao)
        txtInstrucao?.text = if (modoFace == "CADASTRO") {
            "Centralize o rosto e mantenha boa iluminação"
        } else {
            "Confirme sua identidade"
        }

        findViewById<Button>(R.id.btnAbrirCamera).setOnClickListener {
            abrirCamera()
        }
    }

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_IMAGE || resultCode != Activity.RESULT_OK) return

        val bitmap = data?.extras?.get("data") as? Bitmap
        if (bitmap == null) {
            Toast.makeText(this, "Erro ao capturar imagem.", Toast.LENGTH_LONG).show()
            return
        }

        FaceUtils.gerarAssinaturaFacial(bitmap) { assinaturaAtual ->
            runOnUiThread {

                if (assinaturaAtual == null) {
                    Toast.makeText(
                        this,
                        "Rosto não detectado. Ajuste a posição e tente novamente.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runOnUiThread
                }

                if (modoFace == "CADASTRO") {
                    BiometriaStorage.salvarAssinatura(this, assinaturaAtual)

                    Toast.makeText(
                        this,
                        "Cadastro facial concluído com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()

                    val result = Intent()
                    result.putExtra("FACE_OK", true)
                    result.putExtra("MODO_FACE", "CADASTRO")
                    setResult(Activity.RESULT_OK, result)
                    finish()
                } else {
                    val assinaturaSalva = BiometriaStorage.obterAssinatura(this)

                    if (assinaturaSalva == null) {
                        Toast.makeText(this, "Biometria não encontrada.", Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                        return@runOnUiThread
                    }

                    val valido = FaceUtils.compararAssinaturas(
                        assinaturaAtual,
                        assinaturaSalva
                    )

                    if (valido) {
                        val result = Intent()
                        result.putExtra("FACE_OK", true)
                        result.putExtra("MODO_FACE", "VALIDACAO")
                        setResult(Activity.RESULT_OK, result)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Rosto não confere. Tente novamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
