package com.pontocerto.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var modoFace = "VALIDACAO"
    private var validacaoRealizada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // 🔒 Bloqueia print e gravação de tela
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // 🛑 Bloqueia dispositivo comprometido
        if (SecurityUtils.dispositivoComprometido()) {
            Toast.makeText(
                this,
                "Dispositivo não seguro detectado.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        modoFace = intent.getStringExtra("MODO_FACE") ?: "VALIDACAO"

        previewView = findViewById(R.id.previewView)

        findViewById<TextView>(R.id.txtInstrucao).text =
            if (modoFace == "CADASTRO")
                "Centralize o rosto e pisque lentamente"
            else
                "Confirme identidade piscando"

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    private fun iniciarCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->

                if (validacaoRealizada) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val mediaImage = imageProxy.image
                if (mediaImage != null) {

                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    val options = FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .enableTracking()
                        .enableClassification() // 👁️ permite detectar olhos
                        .build()

                    val detector = FaceDetection.getClient(options)

                    detector.process(image)
                        .addOnSuccessListener { faces ->

                            if (faces.isNotEmpty()) {

                                val face = faces[0]

                                val olhoEsquerdo =
                                    face.leftEyeOpenProbability ?: 1f

                                val olhoDireito =
                                    face.rightEyeOpenProbability ?: 1f

                                // 👁️ Liveness: exige olho parcialmente fechado
                                if (olhoEsquerdo < 0.5f ||
                                    olhoDireito < 0.5f
                                ) {

                                    validacaoRealizada = true

                                    runOnUiThread {
                                        sucessoFacial()
                                    }
                                }
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun sucessoFacial() {

        val result = Intent()
        result.putExtra("FACE_OK", true)
        result.putExtra("MODO_FACE", modoFace)

        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
