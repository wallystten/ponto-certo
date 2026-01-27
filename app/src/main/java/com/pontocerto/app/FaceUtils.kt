package com.pontocerto.app

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.security.MessageDigest

object FaceUtils {

    fun gerarAssinaturaFacial(
        bitmap: Bitmap,
        callback: (String?) -> Unit
    ) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    // Assinatura simples baseada na imagem (MVP)
                    val assinatura = hashBitmap(bitmap)
                    callback(assinatura)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    private fun hashBitmap(bitmap: Bitmap): String {
        val bytes = ByteArray(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(java.nio.ByteBuffer.wrap(bytes))

        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.joinToString("") { "%02x".format(it) }
    }

    fun compararAssinaturas(a: String, b: String): Boolean {
        // MVP: igualdade direta
        return a == b
    }
}
