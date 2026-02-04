package com.pontocerto.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CameraActivity : AppCompatActivity() {

    private val REQUEST_IMAGE = 200
    private val REQUEST_CAMERA_PERMISSION = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        findViewById<Button>(R.id.btnAbrirCamera).setOnClickListener {
            if (temPermissaoCamera()) {
                abrirCamera()
            } else {
                pedirPermissaoCamera()
            }
        }
    }

    // 🔐 Verifica permissão
    private fun temPermissaoCamera(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 🔔 Solicita permissão
    private fun pedirPermissaoCamera() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }

    // 📷 Abre a câmera
    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION &&
            grantResults.isNotEmpty() &&
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {

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
                            "Rosto não detectado. Tente novamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        setResult(Activity.RESULT_CANCELED)
                        finish()
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

                        val resultIntent = Intent()
                        resultIntent.putExtra("FACE_OK", true)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()

                    } else {
                        // COMPARAÇÃO
                        val assinaturaSalva = BiometriaStorage.obterAssinatura(this)

                        if (assinaturaSalva == null) {
                            Toast.makeText(
                                this,
                                "Erro ao carregar biometria.",
                                Toast.LENGTH_LONG
                            ).show()
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                            return@runOnUiThread
                        }

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

                            val resultIntent = Intent()
                            resultIntent.putExtra("FACE_OK", true)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Rosto não confere. Acesso negado.",
                                Toast.LENGTH_LONG
                            ).show()

                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                    }
                }
            }
        }
    }
}
