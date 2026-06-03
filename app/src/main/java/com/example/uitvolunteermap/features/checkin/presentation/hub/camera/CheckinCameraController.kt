package com.example.uitvolunteermap.features.checkin.presentation.hub.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Bộ điều khiển CameraX gọn cho màn điểm danh: bind preview + chụp ảnh ra file.
 */
class CheckinCameraController(private val context: Context) {

    private var imageCapture: ImageCapture? = null

    fun bindToLifecycle(
        lifecycleOwner: androidx.lifecycle.LifecycleOwner,
        previewView: PreviewView
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            val cameraProvider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            imageCapture = capture

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
            } catch (_: Exception) {
                // Bind thất bại (vd: không có camera) — màn sẽ hiển thị trạng thái lỗi.
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /** Chụp một tấm ảnh, lưu vào cache dir, trả về File. */
    suspend fun capture(): File = suspendCancellableCoroutine { cont ->
        val capture = imageCapture
        if (capture == null) {
            cont.resumeWithException(IllegalStateException("Camera chưa sẵn sàng"))
            return@suspendCancellableCoroutine
        }

        val photoFile = File(
            context.cacheDir,
            "checkin_${System.currentTimeMillis()}.jpg"
        )
        val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cont.resume(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    cont.resumeWithException(exception)
                }
            }
        )
    }
}
