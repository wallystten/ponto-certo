package com.pontocerto.app

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.abs
import kotlin.math.roundToInt

object FaceUtils {

    /**
     * Gera uma assinatura facial baseada em proporções do rosto.
     * Não usa imagem bruta → menos sensível a iluminação.
     */
    fun gerarAssinaturaFacial(
        bitmap: Bitmap,
        callback: (String?) -> Unit
    ) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    callback(null)
                    return@addOnSuccessListener
                }

                val face = faces.first()

                // Proporções do rosto (independentes de resolução)
                val box = face.boundingBox
                val largura = box.width().toFloat()
                val altura = box.height().toFloat()

                if (largura == 0f || altura == 0f) {
                    callback(null)
                    return@addOnSuccessListener
                }

                val proporcao = largura / altura

                val olhoEsq = face.leftEyeOpenProbability ?: 0f
                val olhoDir = face.rightEyeOpenProbability ?: 0f
                val sorriso = face.smilingProbability ?: 0f

                // Assinatura numérica estável
                val assinatura = listOf(
                    (proporcao * 100).roundToInt(),
                    (olhoEsq * 100).roundToInt(),
                    (olhoDir * 100).roundToInt(),
                    (sorriso * 100).roundToInt()
                ).joinToString("|")

                callback(assinatura)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    /**
     * Compara duas assinaturas com tolerância.
     * Isso evita falhas por pequenas variações.
     */
    fun compararAssinaturas(
        atual: String,
        salva: String
    ): Boolean {
        val a = atual.split("|").mapNotNull { it.toIntOrNull() }
        val b = salva.split("|").mapNotNull { it.toIntOrNull() }

        if (a.size != b.size) return false

        // Tolerância máxima por característica
        val tolerancia = 15

        return a.indices.all { i ->
            abs(a[i] - b[i]) <= tolerancia
        }
    }
}
