package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import android.net.Uri
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * On-device OCR via ML Kit Text Recognition (Latin script, bundled — covers
 * Vietnamese diacritics). Reads banner / sign text inside activity photos so the
 * rule engine can weave the real event name into the caption.
 *
 * Fully on-device, no permission, no model download.
 */
@Singleton
class ImageTextDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /** Returns the recognised text lines, longest first, lightly cleaned. */
    suspend fun recognizeLines(uri: Uri): List<String> = withContext(ioDispatcher) {
        runCatching {
            val image = InputImage.fromFilePath(context, uri)
            suspendCancellableCoroutine<List<String>> { cont ->
                val task = recognizer.process(image)
                task.addOnSuccessListener { result ->
                    val lines = result.textBlocks
                        .flatMap { block -> block.lines }
                        .map { it.text.trim() }
                        .filter { it.length in MIN_LINE_LEN..MAX_LINE_LEN }
                        .distinct()
                        .sortedByDescending { it.length }
                    if (cont.isActive) cont.resume(lines)
                }
                task.addOnFailureListener { error ->
                    if (cont.isActive) cont.resumeWithException(error)
                }
            }
        }.getOrElse {
            Timber.w(it, "ML Kit OCR failed for %s", uri)
            emptyList()
        }
    }

    companion object {
        private const val MIN_LINE_LEN = 4
        private const val MAX_LINE_LEN = 60
    }
}
