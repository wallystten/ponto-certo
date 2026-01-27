package com.pontocerto.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        findViewById<Button>(R.id.btnAbrirCamera).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap

            FaceUtils.gerarAssinaturaFacial(bitmap) { assinaturaAtual ->
                runOnUiThread {
                    if (assinaturaAtual == null) {
                        Toast.makeText(
                            this,
                            "Rosto não detectado. Tente novamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@runOnUiThread
                    }

                    if (!BiometriaStorage.existeCadastro(this)) {
                        // PRIMEIRO USO → cadastra rosto
                        BiometriaStorage.salvarAssinatura(this, assinaturaAtual)
                        Toast.makeText(
                            this,
                            "Rosto cadastrado com sucesso.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        // COMPARAÇÃO
                        val assinaturaSalva =
                            BiometriaStorage.obterAssinatura(this)!!

                        val valido = FaceUtils.compararAssinaturas(
                            assinaturaAtual,
                            assinaturaSalva
                        )

                        if (valido) {
                            Toast.makeText(
                                this,
                                "Identidade confirmada.",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Rosto não confere. Acesso negado.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
