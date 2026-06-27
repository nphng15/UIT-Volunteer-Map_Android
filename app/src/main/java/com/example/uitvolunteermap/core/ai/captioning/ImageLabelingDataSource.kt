package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import android.net.Uri
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class ImageLabelingDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val labeler by lazy {
        ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build()
        )
    }

    suspend fun label(uri: Uri): List<LabelInput> = withContext(ioDispatcher) {
        runCatching {
            val image = InputImage.fromFilePath(context, uri)
            suspendCancellableCoroutine<List<LabelInput>> { cont ->
                val task = labeler.process(image)
                task.addOnSuccessListener { labels ->
                    val mapped = labels.map {
                        LabelInput(text = it.text, confidence = it.confidence)
                    }
                    if (cont.isActive) cont.resume(mapped)
                }
                task.addOnFailureListener { error ->
                    if (cont.isActive) cont.resumeWithException(error)
                }
            }
        }.getOrElse {
            Timber.w(it, "ML Kit labeling failed for %s", uri)
            emptyList()
        }
    }
}
