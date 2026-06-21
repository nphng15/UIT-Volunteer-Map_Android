package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.google.mediapipe.tasks.genai.llminference.LlmInference
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
 * On-device LLM caption refinement using MediaPipe LlmInference.
 *
 * Locates a `.task` model file from one of the well-known paths in the order
 * below (first match wins). The model is loaded lazily on first refinement
 * call and cached for subsequent reuse.
 *
 * Supported model formats: Gemma 2B/3 1B, Phi-2, Falcon 1B packaged as
 * `*.task` for MediaPipe LLM Inference.
 *
 * No special permission required for either location — both are app-private
 * scopes. Drop one of these to enable:
 *   1. Internal storage (root only): filesDir/llm/{gemma.task|model.task}
 *      adb shell run-as com.example.uitvolunteermap mkdir -p files/llm
 *      adb push gemma.task /data/local/tmp/ && \
 *      adb shell run-as com.example.uitvolunteermap cp /data/local/tmp/gemma.task files/llm/
 *   2. External app-private (recommended): externalFilesDir/llm/...
 *      adb shell mkdir -p /sdcard/Android/data/com.example.uitvolunteermap/files/llm
 *      adb push gemma.task /sdcard/Android/data/com.example.uitvolunteermap/files/llm/
 */
@Singleton
class OnDeviceLlmEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val mutex = Mutex()

    @Volatile
    private var cachedEngine: LlmInference? = null

    @Volatile
    private var cachedModelPath: String? = null

    /** Locations probed for a `*.task` model, in priority order. */
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

    suspend fun refine(
        seed: CaptionSuggestion,
        labels: List<LabelInput>,
        ctx: UitContext
    ): CaptionSuggestion = withContext(Dispatchers.Default) {
        val modelFile = findModelFile() ?: return@withContext seed
        runCatching {
            val engine = obtainEngine(modelFile) ?: return@runCatching seed
            val prompt = buildPrompt(seed, labels, ctx)
            Timber.d("Gemma prompt (%d chars):\n%s", prompt.length, prompt)
            val raw = engine.generateResponse(prompt).orEmpty().trim()
            Timber.d("Gemma response (%d chars):\n%s", raw.length, raw)
            mergeWithSeed(seed, raw)
        }.getOrElse {
            Timber.w(it, "Gemma refinement failed; falling back to template seed")
            seed
        }
    }

    private suspend fun obtainEngine(modelFile: File): LlmInference? = mutex.withLock {
        val path = modelFile.absolutePath
        cachedEngine?.takeIf { cachedModelPath == path }?.let { return@withLock it }

        // Different model file from last load (or first load) — reinitialise.
        cachedEngine?.runCatching { close() }
        cachedEngine = null
        cachedModelPath = null

        runCatching {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(path)
                .setMaxTokens(MAX_TOKENS)
                .setTopK(40)
                .setTemperature(0.7f)
                .build()
            LlmInference.createFromOptions(context, options)
        }.onFailure { Timber.e(it, "Failed to load LLM model at %s", path) }
            .getOrNull()
            ?.also {
                cachedEngine = it
                cachedModelPath = path
                Timber.i("LLM engine loaded from %s", path)
            }
    }

    private fun buildPrompt(
        seed: CaptionSuggestion,
        labels: List<LabelInput>,
        ctx: UitContext
    ): String {
        val labelText = labels.take(8).joinToString(", ") { it.text }
        return buildString {
            appendLine("Bạn là quản trị fanpage Tình Nguyện UIT, viết bài Facebook bằng tiếng Việt.")
            appendLine("Hãy viết lại bài bên dưới sao cho:")
            appendLine("- Tự nhiên, ấm áp, đậm chất tuổi trẻ, không sáo rỗng")
            appendLine("- Giữ tên chương trình, tên đội hình, và các hashtag đã có")
            appendLine("- Độ dài tiêu đề tối đa 80 ký tự; nội dung 3-5 câu, có emoji nhẹ")
            appendLine("- TRẢ ĐÚNG ĐỊNH DẠNG sau, không thêm gì khác:")
            appendLine("TIÊU ĐỀ: <tiêu đề mới>")
            appendLine("NỘI DUNG: <nội dung mới>")
            appendLine()
            appendLine("Chương trình: ${ctx.resolvedProgram}")
            appendLine("Đội hình: ${ctx.resolvedTeam}")
            appendLine("Đối tượng nhận diện trong ảnh: $labelText")
            appendLine("Hashtag cần giữ: ${seed.hashtags.joinToString(" ")}")
            appendLine()
            appendLine("Bài viết gốc:")
            appendLine("TIÊU ĐỀ: ${seed.title}")
            appendLine("NỘI DUNG: ${seed.content}")
        }
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
        val MODEL_FILE_NAMES = listOf("gemma.task", "model.task")
        private const val MAX_TOKENS = 512
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
