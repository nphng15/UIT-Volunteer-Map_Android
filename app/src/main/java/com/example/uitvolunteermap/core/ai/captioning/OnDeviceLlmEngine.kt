package com.example.uitvolunteermap.core.ai.captioning

import android.content.Context
import android.net.Uri
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
 * Optional on-device LLM refinement using MediaPipe LlmInference.
 *
 * Loads a `.task` model bundle (e.g. Qwen2.5-Instruct, SmolLM, Phi-4-mini) from
 * an app-private location. The model is text-only: it receives the ML Kit labels
 * + the rule-based seed as text and rewrites the caption. No vision input.
 *
 * The picked images are NOT sent to the model (these `.task` checkpoints are not
 * multimodal), so `refineWithImages` ignores the image URIs and works purely from
 * the seed's detected labels — the method name is kept only so the use case need
 * not change.
 *
 * No runtime permission required for either drop location — both are app-private.
 * Drop the file (renamed to one of MODEL_FILE_NAMES) here:
 *   1. External app-private (recommended, no root):
 *        /sdcard/Android/data/com.example.uitvolunteermap/files/llm/qwen.task
 *   2. Internal (debug, run-as):
 *        /data/data/com.example.uitvolunteermap/files/llm/qwen.task
 *
 * Recommended model (Apache 2.0, no HF login, multilingual incl. Vietnamese):
 *   https://huggingface.co/litert-community/Qwen2.5-1.5B-Instruct
 *   file: Qwen2.5-1.5B-Instruct_multi-prefill-seq_q8_ekv1280.task  (~1.6 GB)
 *   (the smaller Qwen2.5-0.5B .task also works if storage is tight)
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
     * Refine the rule-based seed using the on-device LLM. Image URIs are accepted
     * for API symmetry but ignored (model is text-only). Returns the seed
     * unchanged on any failure so the caller always has a usable suggestion.
     */
    suspend fun refineWithImages(
        seed: CaptionSuggestion,
        @Suppress("UNUSED_PARAMETER") imageUris: List<Uri>,
        ctx: UitContext
    ): CaptionSuggestion = withContext(Dispatchers.Default) {
        val modelFile = findModelFile() ?: return@withContext seed
        runCatching {
            val engine = obtainEngine(modelFile) ?: return@runCatching seed
            val prompt = buildPrompt(seed, ctx)
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

        cachedEngine?.runCatching { close() }
        cachedEngine = null
        cachedModelPath = null

        runCatching {
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
        ctx: UitContext
    ): String {
        val labelText = seed.rawLabels.take(5).joinToString(", ").ifBlank { "hoạt động tình nguyện" }
        // Prefill is ~90% of on-device latency, so keep the prompt SHORT, and
        // strip emojis from any text we feed in — a <1B model mangles tokens that
        // sit next to pictographs, which shows up as "typos" in the output.
        val draft = stripEmoji(seed.content).take(MAX_DRAFT_CHARS)
        // Prefill dominates latency, so keep this as terse as possible: merge the
        // facts onto few lines and feed only a short style anchor.
        return buildString {
            appendLine("Viết bài Facebook tiếng Việt (3-4 câu, ấm áp, đúng chính tả, không emoji) cho Đoàn Tình Nguyện UIT.")
            appendLine("Định dạng, không giải thích thêm:")
            appendLine("TIÊU ĐỀ: ...")
            appendLine("NỘI DUNG: ... (kết thúc bằng hashtag)")
            appendLine()
            append("Chương trình: ").append(ctx.resolvedProgram)
                .append(" | Đội: ").appendLine(ctx.resolvedTeam)
            val placeDate = listOfNotNull(
                ctx.placeName?.takeIf { it.isNotBlank() }?.let { "Địa điểm: $it" },
                ctx.dateLabel?.takeIf { it.isNotBlank() }?.let { "Thời gian: $it" }
            )
            if (placeDate.isNotEmpty()) appendLine(placeDate.joinToString(" | "))
            append("Hình ảnh: ").append(labelText)
                .append(" | Hashtag: ").appendLine(seed.hashtags.joinToString(" "))
            if (draft.isNotBlank()) appendLine("Văn phong gợi ý: $draft")
        }
    }

    /** Remove emoji / pictograph code points; keeps Vietnamese letters intact. */
    private fun stripEmoji(input: String): String {
        if (input.isEmpty()) return input
        val sb = StringBuilder(input.length)
        var i = 0
        while (i < input.length) {
            val cp = input.codePointAt(i)
            val count = Character.charCount(cp)
            val isPictograph = cp in 0x1F000..0x1FAFF ||
                cp in 0x2600..0x27BF ||   // misc symbols + dingbats
                cp in 0x2190..0x21FF ||   // arrows
                cp in 0x2300..0x23FF ||   // misc technical
                cp in 0x2B00..0x2BFF ||   // misc symbols & arrows
                cp in 0xFE00..0xFE0F ||   // variation selectors
                cp == 0x200D || cp == 0x20E3
            if (!isPictograph) sb.appendCodePoint(cp)
            i += count
        }
        return sb.toString().replace(Regex("\\s+"), " ").trim()
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
        // Captions are short; a smaller token budget cuts decode time and the
        // model stops rambling. Lower temperature = fewer garbled tokens.
        private const val MAX_TOKENS = 200
        private const val SAMPLER_TOP_K = 30
        private const val SAMPLER_TEMPERATURE = 0.4f
        private const val MAX_DRAFT_CHARS = 100
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
