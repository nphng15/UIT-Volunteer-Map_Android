package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
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
 * Supported model formats: any `.task` bundle produced by the LiteRT community,
 * e.g. Qwen2.5-0.5B-Instruct, SmolLM-135M-Instruct, Phi-4-mini, etc.
 * (Gemma works too but is gated behind a HuggingFace license accept.)
 *
 * Recommended model for this app (Apache 2.0, no HF login, ~547MB, Vietnamese):
 *   https://huggingface.co/litert-community/Qwen2.5-0.5B-Instruct
 *   resolve/main/Qwen2.5-0.5B-Instruct_multi-prefill-seq_q8_ekv1280.task
 *
 * No runtime permission required for either drop location — both are
 * app-private scopes. Drop the file (renamed to one of MODEL_FILE_NAMES) here:
 *   1. External app-private (recommended, no root needed):
 *        adb shell mkdir -p /sdcard/Android/data/com.example.uitvolunteermap/files/llm
 *        adb push qwen.task \
 *          /sdcard/Android/data/com.example.uitvolunteermap/files/llm/
 *   2. Internal (debug builds, requires run-as):
 *        adb push qwen.task /data/local/tmp/
 *        adb shell run-as com.example.uitvolunteermap mkdir -p files/llm
 *        adb shell run-as com.example.uitvolunteermap \
 *          cp /data/local/tmp/qwen.task files/llm/
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
            Timber.d("LLM prompt (%d chars):\n%s", prompt.length, prompt)
            val sessionOptions = LlmInferenceSession.LlmInferenceSessionOptions.builder()
                .setTopK(SAMPLER_TOP_K)
                .setTemperature(SAMPLER_TEMPERATURE)
                .build()
            val raw = LlmInferenceSession.createFromOptions(engine, sessionOptions).use { session ->
                session.addQueryChunk(prompt)
                session.generateResponse()
            }.orEmpty().trim()
            Timber.d("LLM response (%d chars):\n%s", raw.length, raw)
            mergeWithSeed(seed, raw)
        }.getOrElse {
            Timber.w(it, "LLM refinement failed; falling back to template seed")
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
            // setTopK/setTemperature moved to LlmInferenceSessionOptions in
            // MediaPipe 0.10.24+. Only model + token budget belong here; the
            // engine still needs a setMaxTopK so sessions can pick from it.
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(path)
                .setMaxTokens(MAX_TOKENS)
                .setMaxTopK(SAMPLER_TOP_K)
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
        val labelText = labels.take(6).joinToString(", ") { it.text }
        // Keep the prompt short and direct: small models (0.5B-1B) follow
        // shorter instructions much better than verbose ones.
        return buildString {
            appendLine("Bạn là người viết bài Facebook cho Đoàn Tình Nguyện UIT.")
            appendLine("Hãy viết lại bài bên dưới bằng tiếng Việt, ấm áp, tự nhiên, ngắn gọn 3-4 câu.")
            appendLine("Giữ nguyên tên chương trình, tên đội và hashtag.")
            appendLine("Trả về CHỈ theo định dạng:")
            appendLine("TIÊU ĐỀ: <tiêu đề>")
            appendLine("NỘI DUNG: <nội dung>")
            appendLine()
            appendLine("Chương trình: ${ctx.resolvedProgram}")
            appendLine("Đội: ${ctx.resolvedTeam}")
            appendLine("Trong ảnh có: $labelText")
            appendLine("Hashtag: ${seed.hashtags.joinToString(" ")}")
            appendLine()
            appendLine("Bài gốc:")
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
        val MODEL_FILE_NAMES = listOf(
            "qwen.task",
            "gemma.task",
            "smollm.task",
            "phi.task",
            "model.task"
        )
        private const val MAX_TOKENS = 512
        private const val SAMPLER_TOP_K = 40
        private const val SAMPLER_TEMPERATURE = 0.7f
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
