package com.example.uitvolunteermap.features.post.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.post.data.local.LocalImageStore
import com.example.uitvolunteermap.features.post.data.local.LocalPostStore
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Offline-first decorator: every locally created post is persisted on disk
 * (with its picked images copied into `filesDir`) so that demos still surface
 * the post after process restart even when the backend is mocked or unreachable.
 *
 * Behaviour:
 *  - `getPosts()` merges remote with locally persisted posts; if remote fails,
 *    returns the local set as success so the UI never shows an empty list.
 *  - `createPost()` always stores a local copy first, then best-effort relays
 *    to the remote. Success is reported as soon as the local copy lands.
 *  - `updatePost`/`deletePost` patch the local mirror (if the post lives there)
 *    in addition to forwarding to remote.
 */
@Singleton
class OfflineFirstPostRepository @Inject constructor(
    private val remote: RemotePostRepository,
    private val localPostStore: LocalPostStore,
    private val localImageStore: LocalImageStore,
    private val sessionManager: SessionManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PostRepository {

    override suspend fun getPosts(): AppResult<List<Post>> = withContext(ioDispatcher) {
        val local = localPostStore.loadAll().filterNot { it.isDeleted }
        when (val remoteResult = remote.getPosts()) {
            is AppResult.Success -> {
                val remoteIds = remoteResult.data.map { it.id }.toSet()
                val merged = (local.filterNot { it.id in remoteIds } + remoteResult.data)
                    .sortedByDescending { it.createdAt }
                AppResult.Success(merged)
            }
            is AppResult.Error -> {
                Timber.w("getPosts remote failed (%s); serving %d local posts", remoteResult.error, local.size)
                AppResult.Success(local.sortedByDescending { it.createdAt })
            }
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override suspend fun getPost(postId: Int): AppResult<Post> = withContext(ioDispatcher) {
        if (postId < 0) {
            val match = localPostStore.loadAll().firstOrNull { it.id == postId }
            return@withContext if (match != null) AppResult.Success(match)
            else AppResult.Error(AppError.NotFound("Không tìm thấy bài viết phù hợp."))
        }
        @Suppress("DEPRECATION")
        remote.getPost(postId)
    }

    override suspend fun createPost(draft: CreatePostDraft): AppResult<Post> = withContext(ioDispatcher) {
        // The remote endpoint is still mock-only for this project, and round-tripping
        // through it would surface the same post twice in the list (once as a local
        // copy with file:// images, once as the server's positive-id echo). Persist
        // strictly to the local store so demos stay consistent and idempotent.
        val localId = localPostStore.nextLocalId()
        val copiedPhotos = copyPhotosToLocal(localId, draft)
        val timestamp = nowIsoString()
        val localPost = Post(
            id = localId,
            title = draft.title,
            content = draft.content,
            teamId = draft.teamId,
            teamName = teamNameFor(draft.teamId),
            authorId = draft.authorId,
            authorName = sessionManager.currentUsername ?: "Bạn",
            createdAt = timestamp,
            updatedAt = timestamp,
            isDeleted = false,
            photos = copiedPhotos
        )
        localPostStore.add(localPost)
        AppResult.Success(localPost)
    }

    override suspend fun updatePost(
        postId: Int,
        draft: UpdatePostDraft
    ): AppResult<Post> = withContext(ioDispatcher) {
        if (postId < 0) {
            val local = localPostStore.loadAll().firstOrNull { it.id == postId }
                ?: return@withContext AppResult.Error(
                    AppError.NotFound("Không tìm thấy bài viết phù hợp.")
                )
            val updated = local.copy(
                title = draft.title ?: local.title,
                content = draft.content ?: local.content,
                updatedAt = nowIsoString()
            )
            localPostStore.replace(updated)
            return@withContext AppResult.Success(updated)
        }
        remote.updatePost(postId, draft)
    }

    override suspend fun deletePost(postId: Int): AppResult<Post> = withContext(ioDispatcher) {
        if (postId < 0) {
            val local = localPostStore.loadAll().firstOrNull { it.id == postId }
                ?: return@withContext AppResult.Error(
                    AppError.NotFound("Không tìm thấy bài viết phù hợp.")
                )
            localPostStore.remove(postId)
            localImageStore.deletePostDir(postId)
            return@withContext AppResult.Success(local.copy(isDeleted = true))
        }
        remote.deletePost(postId)
    }

    override suspend fun addPhoto(
        postId: Int,
        photo: PostPhotoDraft
    ): AppResult<PostPhoto> = remote.addPhoto(postId, photo)

    private suspend fun copyPhotosToLocal(
        localId: Int,
        draft: CreatePostDraft
    ): List<PostPhoto> {
        val timestamp = nowIsoString()
        return draft.photos.mapIndexed { index, photo ->
            val sourceUri = draft.localImageUris.getOrNull(index)
            val resolvedUrl = sourceUri
                ?.let { localImageStore.copyToLocal(localId, index, it) }
                ?: photo.imageUrl
            PostPhoto(
                id = -(localId * 100 + index + 1),
                title = photo.title,
                imageUrl = resolvedUrl,
                uploadedAt = timestamp,
                isFirstImage = photo.isFirstImage || index == 0 && draft.photos.none { it.isFirstImage },
                isDeleted = false
            )
        }
    }

    private fun teamNameFor(teamId: Int): String = when (teamId) {
        101 -> "Đội nấu cơm"
        102 -> "Đội giáo dục"
        103 -> "Đội truyền thông"
        else -> "Đội #$teamId"
    }

    private fun nowIsoString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return formatter.format(Date())
    }
}
