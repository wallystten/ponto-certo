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

    private fun verificarPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCameraFrontal()
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

        if (requestCode == REQUEST_CAMERA_PERMISSION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCameraFrontal()
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

    private fun abrirCameraFrontal() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)

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

        // 🔥 NOVO SISTEMA SIMPLIFICADO
        FaceUtils.detectarRosto(bitmap) { rostoDetectado ->

            runOnUiThread {

                if (!rostoDetectado) {
                    Toast.makeText(
                        this,
                        "Rosto não detectado. Ajuste a posição.",
                        Toast.LENGTH_LONG
                    ).show()

                    setResult(Activity.RESULT_CANCELED)
                    finish()
                    return@runOnUiThread
                }

                val result = Intent()
                result.putExtra("FACE_OK", true)
                result.putExtra("MODO_FACE", modoFace)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
    }
}
