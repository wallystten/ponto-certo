package com.pontocerto.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 200
    private val REQUEST_CAMERA_PERMISSION = 300
    private var modoFace = "VALIDACAO"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        modoFace = intent.getStringExtra("MODO_FACE") ?: "VALIDACAO"

        findViewById<TextView?>(R.id.txtInstrucao)?.text =
            if (modoFace == "CADASTRO") {
                "Centralize o rosto e mantenha boa iluminação"
            } else {
                "Confirme sua identidade"
            }

        findViewById<Button>(R.id.btnAbrirCamera).setOnClickListener {
            verificarPermissaoCamera()
        }
    }

    /* ===============================
       PERMISSÃO DE CÂMERA
       =============================== */

    private fun verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                abrirCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão da câmera é obrigatória.",
                    Toast.LENGTH_LONG
                ).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    /* ===============================
       CÂMERA
       =============================== */

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_IMAGE || resultCode != Activity.RESULT_OK) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val bitmap = data?.extras?.get("data") as? Bitmap
        if (bitmap == null) {
            Toast.makeText(this, "Erro ao capturar imagem.", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
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
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                    return@runOnUiThread
                }

                // ✅ MVP: rosto detectado = válido
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
                    val result = Intent()
                    result.putExtra("FACE_OK", true)
                    result.putExtra("MODO_FACE", "VALIDACAO")
                    setResult(Activity.RESULT_OK, result)
                    finish()
                }
            }
        }
    }
}
