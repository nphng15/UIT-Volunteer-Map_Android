package com.example.uitvolunteermap.features.post.data.local

import android.content.Context
import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Simple JSON-file backed persistence for posts the user created locally.
 * Survives process restart so demos still show previously authored posts even
 * when the backend never received them.
 */
@Singleton
class LocalPostStore @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    private val mutex = Mutex()
    private val gson = Gson()
    private val file: File
        get() = File(context.filesDir, "local_posts/posts.json")

    suspend fun loadAll(): List<Post> = withContext(ioDispatcher) {
        mutex.withLock {
            runCatching {
                if (!file.exists()) return@runCatching emptyList()
                val type = object : TypeToken<List<Post>>() {}.type
                gson.fromJson<List<Post>>(file.readText(), type).orEmpty()
            }.onFailure {
                Timber.w(it, "Failed to load local posts; falling back to empty list")
            }.getOrDefault(emptyList())
        }
    }

    suspend fun add(post: Post) = withContext(ioDispatcher) {
        mutex.withLock {
            val current = readUnsafe().toMutableList()
            current.removeAll { it.id == post.id }
            current.add(0, post)
            writeUnsafe(current)
        }
    }

    suspend fun replace(post: Post) = withContext(ioDispatcher) {
        mutex.withLock {
            val current = readUnsafe().toMutableList()
            val idx = current.indexOfFirst { it.id == post.id }
            if (idx >= 0) {
                current[idx] = post
                writeUnsafe(current)
            }
        }
    }

    suspend fun remove(postId: Int) = withContext(ioDispatcher) {
        mutex.withLock {
            val current = readUnsafe().filterNot { it.id == postId }
            writeUnsafe(current)
        }
    }

    suspend fun nextLocalId(): Int = withContext(ioDispatcher) {
        mutex.withLock {
            val current = readUnsafe()
            val min = current.minOfOrNull { it.id } ?: 0
            (if (min < 0) min - 1 else -1)
        }
    }

    private fun readUnsafe(): List<Post> {
        if (!file.exists()) return emptyList()
        return runCatching {
            val type = object : TypeToken<List<Post>>() {}.type
            gson.fromJson<List<Post>>(file.readText(), type).orEmpty()
        }.getOrDefault(emptyList())
    }

    private fun writeUnsafe(posts: List<Post>) {
        runCatching {
            file.parentFile?.mkdirs()
            file.writeText(gson.toJson(posts))
        }.onFailure { Timber.w(it, "Failed to persist local posts") }
    }
}
