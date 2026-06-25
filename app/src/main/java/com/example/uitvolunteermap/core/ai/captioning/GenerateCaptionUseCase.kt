package com.example.uitvolunteermap.core.ai.captioning

import android.net.Uri
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionSuggestion
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import javax.inject.Inject
import timber.log.Timber

class GenerateCaptionUseCase @Inject constructor(
    private val labeling: ImageLabelingDataSource,
    private val textRecognition: ImageTextDataSource,
    private val imageMetadata: ImageMetadataDataSource,
    private val rules: UitPostStyleRules,
    private val llmEngine: OnDeviceLlmEngine
) {

    suspend operator fun invoke(
        uris: List<Uri>,
        ctx: UitContext,
        nonce: Int = 0,
        mode: CaptionMode = CaptionMode.TEMPLATE_FAST
    ): AppResult<CaptionSuggestion> {
        if (uris.isEmpty()) {
            return AppResult.Error(AppError.Validation("Chưa có ảnh để gợi ý nội dung."))
        }

        // Three cheap on-device passes per image:
        //  - ML Kit Image Labeling   -> what is in the photo (person, food, …)
        //  - ML Kit Text Recognition -> banner/sign text (real event names)
        //  - EXIF metadata           -> capture date + GPS place
        val labels = uris.flatMap { uri ->
            runCatching { labeling.label(uri) }
                .onFailure { Timber.w(it, "Skipping labels for %s", uri) }
                .getOrDefault(emptyList())
        }
        val ocrLines = uris.flatMap { uri ->
            runCatching { textRecognition.recognizeLines(uri) }
                .onFailure { Timber.w(it, "Skipping OCR for %s", uri) }
                .getOrDefault(emptyList())
        }.distinct()

        // Use the first image that actually carries usable metadata.
        var photoMeta: PhotoMeta? = null
        for (uri in uris) {
            val meta = runCatching { imageMetadata.read(uri) }.getOrNull()
            if (meta != null && meta.hasAny) {
                photoMeta = meta
                break
            }
        }
        val enrichedCtx = ctx.copy(
            dateLabel = ctx.dateLabel ?: photoMeta?.dateLabel,
            placeName = ctx.placeName ?: photoMeta?.placeName
        )

        val seed = rules.apply(
            labels = labels,
            ctx = enrichedCtx,
            photoCount = uris.size,
            nonce = nonce,
            ocrLines = ocrLines
        )

        val final = when (mode) {
            CaptionMode.TEMPLATE_FAST -> seed
            CaptionMode.VL_GEMMA -> {
                if (llmEngine.isAvailable()) {
                    Timber.i("AI mode — refining caption with on-device LLM")
                    llmEngine.refineWithImages(seed = seed, imageUris = uris, ctx = enrichedCtx)
                } else {
                    Timber.w("AI mode requested but model file missing — returning template seed")
                    seed
                }
            }
        }
        return AppResult.Success(final)
    }
}
