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

        // ML Kit labels are always cheap; we use them both to seed the template
        // and (when VL is selected) to enrich hashtags from the rule engine.
        val labels = uris.flatMap { uri ->
            runCatching { labeling.label(uri) }
                .onFailure { Timber.w(it, "Skipping labels for %s", uri) }
                .getOrDefault(emptyList())
        }

        val seed = rules.apply(
            labels = labels,
            ctx = ctx,
            photoCount = uris.size,
            nonce = nonce
        )

        val final = when (mode) {
            CaptionMode.TEMPLATE_FAST -> seed
            CaptionMode.VL_GEMMA -> {
                if (llmEngine.isAvailable()) {
                    Timber.i("VL_GEMMA mode — refining caption with Gemma 3n on %d image(s)", uris.size)
                    llmEngine.refineWithImages(seed = seed, imageUris = uris, ctx = ctx)
                } else {
                    Timber.w("VL_GEMMA requested but model file missing — returning template seed")
                    seed
                }
            }
        }
        return AppResult.Success(final)
    }
}
