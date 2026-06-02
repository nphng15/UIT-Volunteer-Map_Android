package com.example.uitvolunteermap.features.checkin.presentation.hub.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Bộ điều khiển CameraX cho màn điểm danh: bind preview + chụp ảnh ra file.
 * Hỗ trợ flip giữa camera trước / sau bằng cách rebind với selector khác.
 */
class CheckinCameraController(private val context: Context) {

    enum class Facing { Back, Front }

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var previewView: PreviewView? = null
    private var currentFacing: Facing = Facing.Back

    fun bindToLifecycle(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        facing: Facing = Facing.Back
    ) {
        this.lifecycleOwner = lifecycleOwner
        this.previewView = previewView
        this.currentFacing = facing
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            doBind()
        }, ContextCompat.getMainExecutor(context))
    }

    /** Đổi camera trước/sau, giữ nguyên previewView/lifecycleOwner. */
    fun setFacing(facing: Facing) {
        if (facing == currentFacing) return
        currentFacing = facing
        doBind()
    }

    private fun doBind() {
        val provider = cameraProvider ?: return
        val owner = lifecycleOwner ?: return
        val view = previewView ?: return

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(view.surfaceProvider)
        }
        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        imageCapture = capture

        val selector = if (currentFacing == Facing.Front) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        try {
            provider.unbindAll()
            provider.bindToLifecycle(owner, selector, preview, capture)
        } catch (_: Exception) {
            // Bind thất bại (vd: không có camera) — màn sẽ hiển thị trạng thái lỗi.
        }
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
