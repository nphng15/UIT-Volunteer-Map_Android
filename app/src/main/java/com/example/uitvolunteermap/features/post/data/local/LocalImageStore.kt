package com.example.uitvolunteermap.features.post.data.local

import android.content.Context
import android.net.Uri
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Copies images picked from PhotoPicker into the app's `filesDir` so that:
 *  - The image survives after the source ContentResolver URI is revoked,
 *  - The persisted post can render the image after a process restart.
 */
@Singleton
class LocalImageStore @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val rootDir: File
        get() = File(context.filesDir, "local_posts/images").apply { mkdirs() }

    suspend fun copyToLocal(
        postId: Int,
        index: Int,
        sourceUriString: String
    ): String? = withContext(ioDispatcher) {
        runCatching {
            val source = Uri.parse(sourceUriString)
            val postDir = File(rootDir, "post_$postId").apply { mkdirs() }
            val extension = guessExtension(sourceUriString) ?: "jpg"
            val target = File(postDir, "img_$index.$extension")
            context.contentResolver.openInputStream(source)?.use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@runCatching null
            Uri.fromFile(target).toString()
        }.onFailure {
            Timber.w(it, "Failed to copy image %s", sourceUriString)
        }.getOrNull()
    }

    suspend fun deletePostDir(postId: Int) = withContext(ioDispatcher) {
        runCatching { File(rootDir, "post_$postId").deleteRecursively() }
    }

    private fun guessExtension(uriString: String): String? {
        val ext = uriString.substringAfterLast('.', "").lowercase()
        return if (ext.length in 2..4 && ext.all { it.isLetterOrDigit() }) ext else null
    }
}
