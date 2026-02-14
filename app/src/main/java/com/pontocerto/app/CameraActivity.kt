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
                    .build()

                val detector = FaceDetection.getClient(options)

                detector.process(image)
                    .addOnSuccessListener { faces ->

                        if (faces.isNotEmpty()) {

                            val face = faces[0]

                            // 👁️ Liveness simplificado:
                            val olhoEsquerdo = face.leftEyeOpenProbability
                            val olhoDireito = face.rightEyeOpenProbability

                            if (olhoEsquerdo != null &&
                                olhoDireito != null &&
                                (olhoEsquerdo < 0.5f || olhoDireito < 0.5f)
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
}l
