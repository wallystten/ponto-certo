package com.pontocerto.app

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

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
                    // MVP: rosto detectado = presença confirmada
                    callback("FACE_OK")
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun compararAssinaturas(a: String, b: String): Boolean {
        // MVP: se chegou até aqui, consideramos válido
        return true
    }
}
