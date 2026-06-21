package com.example.uitvolunteermap.core.ai.captioning

import android.net.Uri
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
        nonce: Int = 0
    ): AppResult<CaptionSuggestion> {
        if (uris.isEmpty()) {
            return AppResult.Error(AppError.Validation("Chưa có ảnh để gợi ý nội dung."))
        }

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

        // If an on-device LLM model is present, refine the template seed with
        // it; otherwise return the rule-based seed as-is.
        val final = if (llmEngine.isAvailable()) {
            Timber.i("On-device LLM available — refining caption")
            llmEngine.refine(seed = seed, labels = labels, ctx = ctx)
        } else {
            seed
        }
        return AppResult.Success(final)
    }
}
