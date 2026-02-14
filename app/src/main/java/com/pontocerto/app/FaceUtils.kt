package com.pontocerto.app

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

object FaceUtils {

    fun detectarRosto(
        bitmap: Bitmap,
        callback: (Boolean) -> Unit
    ) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                callback(faces.isNotEmpty())
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
