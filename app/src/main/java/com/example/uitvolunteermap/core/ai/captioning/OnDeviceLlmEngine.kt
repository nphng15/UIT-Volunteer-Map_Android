package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import android.net.Uri
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Vision-language refinement using Gemma 3n E2B through LiteRT-LM.
 *
 * Gemma 3n is multimodal (text + vision + audio) and is purpose-built for
 * phones; the int4-quantised `.litertlm` checkpoint runs on ~2 GB of RAM.
 *
 * The model file is NOT bundled with the APK. Drop it into one of the
 * candidate paths probed below (first match wins). Both are app-private
 * scopes — no runtime permission required.
 *
 * 1. External app-private (no root needed):
 *      /sdcard/Android/data/com.example.uitvolunteermap/files/llm/gemma3n.litertlm
 * 2. Internal (debug builds, requires run-as):
 *      /data/data/com.example.uitvolunteermap/files/llm/gemma3n.litertlm
 *
 * Download (after free HF login + accept Gemma terms — immediate, no waitlist):
 *   https://huggingface.co/google/gemma-3n-E2B-it-litert-lm
 *   file: gemma-3n-E2B-it-int4.litertlm
 */
@Singleton
class OnDeviceLlmEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val mutex = Mutex()

    @Volatile
    private var cachedEngine: Engine? = null

    @Volatile
    private var cachedModelPath: String? = null

    /** Locations probed for a `*.litertlm` model, in priority order. */
    private fun candidatePaths(): List<File> {
        val internal = File(context.filesDir, MODEL_SUBDIR)
        val externalAppPrivate = context.getExternalFilesDir(null)?.let {
            File(it, MODEL_SUBDIR)
        }
        return buildList {
            MODEL_FILE_NAMES.forEach { name -> add(File(internal, name)) }
            externalAppPrivate?.let { dir ->
                MODEL_FILE_NAMES.forEach { name -> add(File(dir, name)) }
            }
        }
    }

    fun findModelFile(): File? = candidatePaths().firstOrNull { it.exists() && it.length() > 0 }

    fun isAvailable(): Boolean = findModelFile() != null

    /**
     * Refine the rule-based seed by feeding the original picked images directly
     * into Gemma 3n. Returns the seed unchanged on any failure so the caller
     * always has a usable suggestion.
     */
    suspend fun refineWithImages(
        seed: CaptionSuggestion,
        imageUris: List<Uri>,
        ctx: UitContext
    ): CaptionSuggestion = withContext(Dispatchers.Default) {
        val modelFile = findModelFile() ?: return@withContext seed
        runCatching {
            val engine = obtainEngine(modelFile) ?: return@runCatching seed
            val cachedImages = imageUris
                .take(MAX_IMAGES_PER_PROMPT)
                .mapNotNull { cacheImageForLlm(it) }
            if (cachedImages.isEmpty()) {
                Timber.w("No images could be cached for LLM input")
                return@runCatching seed
            }

            val prompt = buildPrompt(seed, ctx)
            Timber.d("Gemma 3n prompt (%d chars, %d images):\n%s", prompt.length, cachedImages.size, prompt)

            val contentItems = buildList<Content> {
                cachedImages.forEach { file ->
                    add(Content.ImageFile(file.absolutePath))
                }
                add(Content.Text(prompt))
            }

            val raw = engine.createConversation().use { conversation ->
                conversation.sendMessage(Contents.of(*contentItems.toTypedArray())).text
            }.orEmpty().trim()

            Timber.d("Gemma 3n response (%d chars):\n%s", raw.length, raw)
            // Best-effort cleanup of cache files — engine has finished reading
            cachedImages.forEach { runCatching { it.delete() } }

            mergeWithSeed(seed, raw)
        }.getOrElse {
            Timber.w(it, "Gemma 3n refinement failed; falling back to template seed")
            seed
        }
    }

    private suspend fun obtainEngine(modelFile: File): Engine? = mutex.withLock {
        val path = modelFile.absolutePath
        cachedEngine?.takeIf { cachedModelPath == path }?.let { return@withLock it }

        // Different model file from last load (or first load) — reinitialise.
        cachedEngine?.runCatching { close() }
        cachedEngine = null
        cachedModelPath = null

        runCatching {
            val config = EngineConfig(
                modelPath = path,
                backend = Backend.CPU()
            )
            Engine(config).also { it.initialize() }
        }.onFailure { Timber.e(it, "Failed to load LLM model at %s", path) }
            .getOrNull()
            ?.also {
                cachedEngine = it
                cachedModelPath = path
                Timber.i("Gemma 3n engine loaded from %s", path)
            }
    }

    /** Copy a content:// URI to a stable file path the LiteRT-LM API can read. */
    private fun cacheImageForLlm(uri: Uri): File? = runCatching {
        val cacheDir = File(context.cacheDir, "llm_input").apply { mkdirs() }
        val target = File(cacheDir, "img_${System.nanoTime()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        } ?: return@runCatching null
        target
    }.onFailure { Timber.w(it, "Failed to cache image %s", uri) }.getOrNull()

    private fun buildPrompt(
        seed: CaptionSuggestion,
        ctx: UitContext
    ): String = buildString {
        appendLine("Bạn là người viết bài Facebook cho Đoàn Tình Nguyện UIT.")
        appendLine("Hãy nhìn ảnh và viết bài bằng tiếng Việt:")
        appendLine("- Tự nhiên, ấm áp, ngắn gọn 3-4 câu")
        appendLine("- Phải nêu rõ tên chương trình và tên đội ở câu đầu")
        appendLine("- Giữ nguyên các hashtag được liệt kê bên dưới")
        appendLine("- Trả về CHỈ theo định dạng dưới, không thêm gì khác:")
        appendLine("TIÊU ĐỀ: <tiêu đề>")
        appendLine("NỘI DUNG: <nội dung>")
        appendLine()
        appendLine("Chương trình: ${ctx.resolvedProgram}")
        appendLine("Đội: ${ctx.resolvedTeam}")
        appendLine("Hashtag bắt buộc: ${seed.hashtags.joinToString(" ")}")
    }

    private fun mergeWithSeed(seed: CaptionSuggestion, raw: String): CaptionSuggestion {
        if (raw.isBlank()) return seed
        val titleMatch = TITLE_REGEX.find(raw)?.groupValues?.getOrNull(1)?.trim()
        val bodyMatch = BODY_REGEX.find(raw)?.groupValues?.getOrNull(1)?.trim()
        val newTitle = titleMatch?.takeIf { it.isNotBlank() } ?: seed.title
        val newContent = bodyMatch?.takeIf { it.isNotBlank() } ?: seed.content
        return seed.copy(
            title = newTitle,
            content = newContent,
            refinedByLlm = true
        )
    }

    companion object {
        const val MODEL_SUBDIR = "llm"
        val MODEL_FILE_NAMES = listOf(
            "gemma3n.litertlm",
            "model.litertlm"
        )
        // Gemma 3n image encoder accepts 256/512/768 px tiles; 3 images per
        // turn comfortably fits within the model's context window.
        private const val MAX_IMAGES_PER_PROMPT = 3
        private val TITLE_REGEX = Regex(
            "TIÊU ĐỀ\\s*[:：]\\s*(.+)",
            RegexOption.IGNORE_CASE
        )
        private val BODY_REGEX = Regex(
            "NỘI DUNG\\s*[:：]\\s*([\\s\\S]+?)(?=(\\n\\s*TIÊU ĐỀ|\\z))",
            RegexOption.IGNORE_CASE
        )
    }
}
